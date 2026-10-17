package com.example.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.local.EmotionHistoryEntity
import com.example.data.local.EmotionState
import com.example.data.local.MessageEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.EmochiRepository
import com.example.data.repository.EmotionEngine

/**
 * Arka plan duygu denetimi — her ~50 mesajda bir tetiklenir, state belirgin kaymışsa.
 * LLM'e "duygu gidişatı sahneyle tutarlı mı?" diye sorar, sınırlı düzeltme önerir.
 * Hata veya API anahtarı yoksa sessizce atlanır, sohbeti etkilemez.
 * Denetim: yalnızca oturum dışında (aktif sohbet yokken) çalışır.
 */
class BackgroundEmotionAuditWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val botId = inputData.getString("botId") ?: return Result.success()

        // Aktif sohbet varsa bu botu atla
        if (botId == EmochiRepository.activeBotId) return Result.success()

        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val bot = db.botDao().getBotById(botId) ?: return Result.success()
            val settings = db.userSettingsDao().getUserSettings() ?: return Result.success()

            // API anahtarı yoksa sessizce atla
            val apiKey = settings.apiKey.ifBlank { return Result.success() }

            val recentHistory = db.emotionHistoryDao().getRecentHistoryForBot(botId, limit = 20)
            if (recentHistory.size < 5) return Result.success()

            val recentMessages = db.messageDao().getMessagesForBotList(botId).takeLast(15)
            val currentState = EmotionState.fromJson(bot.emotionState)

            // Audit prompt: LLM'e mevcut state + son mesajları ver, tutarlılık değerlendirmesi iste
            val historySnapshot = buildHistorySnapshot(recentHistory)
            val messagesSnapshot = buildMessagesSnapshot(recentMessages)
            val currentStateJson = currentState.toJson()

            val auditSystemPrompt = """
Sen bir duygu tutarlılığı denetçisisin. Aşağıdaki verilerle bir karakterin duygu gidişatını değerlendir.
Yalnızca GERÇEK tutarsızlıkları bildir — küçük dalgalanmalar normal kabul edilir.
Cevabını yalnızca şu JSON formatında ver, başka hiçbir şey yazma:
{"consistent": true/false, "correction": {"field": "affectionScore/anger/trust/...", "suggested": <0-100>, "reason": "kısa açıklama"} veya null}
""".trimIndent()

            val auditUserMessage = """
Son 20 mesajın duygu geçmişi:
$historySnapshot

Son 15 mesaj metni:
$messagesSnapshot

Mevcut duygu state'i:
$currentStateJson

Bu veriler tutarlı mı? Büyük bir kayma veya gerçeklikten kopma var mı?
""".trimIndent()

            val repository = EmochiRepository(db, applicationContext)
            val result = repository.callLlmForAudit(
                settings = settings,
                systemPrompt = auditSystemPrompt,
                userMessage = auditUserMessage,
                botId = botId
            )

            if (result.isNotBlank()) {
                applyAuditCorrection(db, bot.id, currentState, result)
            }

            Result.success()
        } catch (_: Exception) {
            // Sessizce atla — sohbeti etkileme
            Result.success()
        }
    }

    private fun buildHistorySnapshot(history: List<EmotionHistoryEntity>): String {
        return history.joinToString("\n") { h ->
            "msg#${h.messageIndex}: aff=${h.affectionScore} trust=${h.trustScore} anger=${h.resentmentScore} dom=${h.dominantEmotion}"
        }
    }

    private fun buildMessagesSnapshot(messages: List<MessageEntity>): String {
        return messages.joinToString("\n") { m ->
            "[${m.role}]: ${m.text.take(120).replace("\n", " ")}"
        }
    }

    private suspend fun applyAuditCorrection(
        db: AppDatabase,
        botId: String,
        currentState: EmotionState,
        rawResult: String
    ) {
        try {
            val jsonMatch = Regex("""(?is)\{.*\}""").find(rawResult)?.value ?: return
            val json = org.json.JSONObject(jsonMatch)
            if (json.optBoolean("consistent", true)) return

            val corrObj = json.optJSONObject("correction") ?: return
            val field = corrObj.optString("field", "")
            val suggested = corrObj.optInt("suggested", -1)
            val reason = corrObj.optString("reason", "arka plan denetimi")
            if (field.isBlank() || suggested < 0) return

            // Güvenlik: tek seferde en fazla ±15 düzeltme, kullanıcı verisi silinmez
            val MAX_CORRECTION = 15
            val correctedState = when (field) {
                "affectionScore" -> {
                    val current = currentState.affection
                    val clamped = suggested.coerceIn(
                        (current - MAX_CORRECTION).coerceAtLeast(0),
                        (current + MAX_CORRECTION).coerceAtMost(100)
                    )
                    currentState.copy(
                        relationshipAxes = currentState.relationshipAxes.copy(affectionScore = clamped)
                    )
                }
                "anger" -> {
                    val current = currentState.primaryEmotions.anger
                    val clamped = suggested.coerceIn(
                        (current - MAX_CORRECTION).coerceAtLeast(0),
                        (current + MAX_CORRECTION).coerceAtMost(100)
                    )
                    currentState.copy(
                        primaryEmotions = currentState.primaryEmotions.copy(anger = clamped)
                    )
                }
                "trust" -> {
                    val current = currentState.primaryEmotions.trust
                    val clamped = suggested.coerceIn(
                        (current - MAX_CORRECTION).coerceAtLeast(0),
                        (current + MAX_CORRECTION).coerceAtMost(100)
                    )
                    currentState.copy(
                        primaryEmotions = currentState.primaryEmotions.copy(trust = clamped)
                    )
                }
                "resentmentScore" -> {
                    val current = currentState.relationshipAxes.resentmentScore
                    val clamped = suggested.coerceIn(
                        (current - MAX_CORRECTION).coerceAtLeast(0),
                        (current + MAX_CORRECTION).coerceAtMost(100)
                    )
                    currentState.copy(
                        relationshipAxes = currentState.relationshipAxes.copy(resentmentScore = clamped)
                    )
                }
                else -> return
            }

            val bot = db.botDao().getBotById(botId) ?: return
            db.botDao().insertOrUpdate(
                bot.copy(emotionState = correctedState.toJson())
            )

            // Geçmişe kayıt: gerekçe dominantEmotion prefix'i ile loglanır
            android.util.Log.i("EmotionAudit", "[$botId] Audit correction: $field $suggested ($reason)")
            val totalMsgCount = db.messageDao().getMessageCountForBot(botId)
            db.emotionHistoryDao().insertHistory(
                EmotionHistoryEntity(
                    botId = botId,
                    timestamp = System.currentTimeMillis(),
                    messageIndex = totalMsgCount,
                    affectionScore = correctedState.affection,
                    respectScore = correctedState.relationshipAxes.respectScore,
                    comfortScore = correctedState.relationshipAxes.comfortScore,
                    resentmentScore = correctedState.relationshipAxes.resentmentScore,
                    trustScore = correctedState.primaryEmotions.trust,
                    physicalComfortScore = correctedState.physicalComfortScore,
                    obsessionScore = correctedState.obsessionScore,
                    dominantEmotion = "[audit] ${correctedState.dominantEmotion}",
                    fullVectorJson = correctedState.toJson()
                )
            )
        } catch (_: Exception) {}
    }
}
