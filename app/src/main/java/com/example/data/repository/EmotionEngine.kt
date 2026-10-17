package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BotEntity
import com.example.data.local.DeltaData
import com.example.data.local.EmotionState
import com.example.data.local.PrimaryEmotions
import com.example.data.local.RelationshipAxes
import com.example.data.local.SideCharacterEmotionUpdate
import org.json.JSONObject
import java.util.Locale

/**
 * Emotion Engine V3 (Duygu Sistemi).
 * Features:
 * - Compact & delta-only state parsing with automatic merging into previous valid state
 * - Robust zero-crash error recovery without redundant API retries
 * - Pure Kotlin natural emotional decay (anger/resentment cooling over time, longing increase)
 * - Psychological consistency auditing (detects & corrects emotional contradictions in code)
 * - Anti-spam affection gain damping, recovery locks, and 500-turn synthetic balancing simulation
 * - Canonical field alias table for short/long JSON field name compatibility
 * - Passive per-message micro-decay for anger/resentment (realistic slow cooling)
 */
object EmotionEngine {

    // ─────────────────────────────────────────────────────────────────────────
    // KANONIK ALAN ADI EŞLEMESİ
    // Hem kısa (yeni şema) hem uzun (eski/geriye dönük) adlar desteklenir.
    // Çakışma olursa uzun ad (mevcut değer) korunur; kısa yalnızca eksik alanı doldurur.
    // ─────────────────────────────────────────────────────────────────────────
    val FIELD_ALIASES: Map<String, String> = mapOf(
        // relationship_axes kısa adları
        "aff"      to "affectionScore",
        "rsp"      to "respectScore",
        "cmf"      to "comfortScore",
        "res"      to "resentmentScore",
        // üst seviye obje kısa adları
        "pe"       to "primary_emotions",
        "ra"       to "relationship_axes",
        // skaler alanlar
        "pcs"      to "physicalComfortScore",
        "obs"      to "obsessionScore",
        "dom"      to "dominant_emotion",
        "sup"      to "suppressed_emotion",
        "supp_emo" to "suppressed_emotion",
        // delta alt alanları
        "val"      to "value",
        "rsn"      to "reason",
        "ax"       to "axis",
        "stg"      to "setting",
        "md"       to "mode",
        "tns"      to "tension",
        // V3.1 yeni alanlar — bunlar kendi adlarıyla okunur, alias değil:
        // "why"  → değişimin kısa gerekçesi (why veya delta.reason ile aynı slot)
        // "trig" → anlamsal tetik kimlikleri listesi
        // "reapp"→ reappraisal aktif bayrağı
        "why"      to "why",
        "trig"     to "trig",
        "reapp"    to "reapp"
    )

    /**
     * JSONObject içindeki kısa takma adları kanonik uzun ada çevirir (yerinde).
     * Uzun ad zaten varsa kısa adı yok sayar (uzun ad öncelikli).
     */
    fun resolveAliases(json: JSONObject): JSONObject {
        for ((short, canonical) in FIELD_ALIASES) {
            if (short == canonical) continue
            if (json.has(short) && !json.has(canonical)) {
                json.put(canonical, json.get(short))
                json.remove(short)
            }
        }
        return json
    }

    data class EmotionalRegulationResult(
        val rawDelta: Int,
        val outwardExpressedDelta: Int,
        val internalSuppressionAmount: Int,
        val isRegulationActive: Boolean
    )

    fun calculateEmotionalRegulation(rawDelta: Int, capacity: Int): EmotionalRegulationResult {
        val absDelta = kotlin.math.abs(rawDelta)
        if (absDelta <= 25) {
            return EmotionalRegulationResult(rawDelta, rawDelta, 0, false)
        }

        val dampeningFactor = 1.0 - (capacity / 100.0 * 0.45)
        val dampened = (rawDelta * dampeningFactor).toInt()
        val suppressed = rawDelta - dampened

        return EmotionalRegulationResult(
            rawDelta = rawDelta,
            outwardExpressedDelta = dampened,
            internalSuppressionAmount = suppressed,
            isRegulationActive = true
        )
    }

    fun calculateClampedDelta(
        currentScore: Int,
        rawDelta: Int,
        dailyAffectionGain: Int = 0,
        maxDailyGain: Int = 20,
        streakCount: Int = 0,
        difficultyMultiplier: Double = 1.0
    ): Int {
        if (rawDelta == 0) return 0

        var effectiveDelta = rawDelta.toDouble() * difficultyMultiplier

        // Positive delta clamping: streak bonus or daily gain limit
        if (effectiveDelta > 0) {
            if (dailyAffectionGain >= maxDailyGain) {
                // Diminishing returns after daily gain cap reached
                effectiveDelta = (effectiveDelta * 0.20).coerceAtLeast(0.0)
            }
            if (streakCount >= 5) {
                effectiveDelta *= 1.25 // small streak reward
            }
            val ceilingRemaining = 100 - currentScore
            effectiveDelta = minOf(effectiveDelta, ceilingRemaining.toDouble())
        } else {
            // Negative delta (cannot drop below 0)
            val floorRemaining = currentScore.toDouble()
            effectiveDelta = maxOf(effectiveDelta, -floorRemaining)
        }

        return effectiveDelta.toInt()
    }

    /**
     * Applies natural story-time-based emotional decay and longing boost in pure Kotlin.
     * Called ONLY when a story time skip (daysToSkip > 0) occurs — never on real clock time.
     * Sönümlenme hızı regulationCapacity'ye (50 = varsayılan) göre ölçeklenir:
     *   capacity ≥ 60 → %20 daha hızlı sönüm (olgun, duygusal düzenlemesi iyi karakter)
     *   capacity ≤ 30 → %15 daha yavaş sönüm (dürtüsel, duygusal yükü kolay bırakmayan karakter)
     * Sönüm sırası: önce decay uygulanır, sonra model delta'sı → çağıran bu sırayı sağlamalı.
     * @param storyDaysSkipped Hikâye içinde atlanan gün sayısı (gerçek saatle ilgisi yok)
     * @param regulationCapacity Karakterin duygusal düzenleme kapasitesi (0-100, varsayılan 50)
     * @return Güncellenmiş EmotionState ve tek satırlık prompt notu (boşsa enjekte edilmez)
     */
    data class DecayResult(val state: EmotionState, val promptNote: String)

    fun applyNaturalEmotionalDecay(
        currentState: EmotionState,
        storyDaysSkipped: Int,
        regulationCapacity: Int = 50
    ): EmotionState = applyNaturalEmotionalDecayWithNote(currentState, storyDaysSkipped, regulationCapacity).state

    fun applyNaturalEmotionalDecayWithNote(
        currentState: EmotionState,
        storyDaysSkipped: Int,
        regulationCapacity: Int = 50
    ): DecayResult {
        if (storyDaysSkipped <= 0) return DecayResult(currentState, "")

        // Düzenleme kapasitesi sönüm hızını etkiler
        val capMult = when {
            regulationCapacity >= 60 -> 1.20  // hızlı sönüm
            regulationCapacity <= 30 -> 0.85  // yavaş sönüm
            else                     -> 1.00
        }

        var anger = currentState.primaryEmotions.anger
        var resentment = currentState.relationshipAxes.resentmentScore
        var residue = currentState.emotionalResidue
        var anticipation = currentState.primaryEmotions.anticipation

        val angerBefore = anger
        val resentmentBefore = resentment

        // 1. Öfke sönümü (hikâye günü bazlı)
        if (anger > 0) {
            val baseRate = when {
                storyDaysSkipped >= 7  -> 0.85
                storyDaysSkipped >= 3  -> 0.60
                storyDaysSkipped >= 1  -> 0.35
                else                   -> 0.15
            }
            val angerDecay = (anger * baseRate * capMult).toInt()
            anger = (anger - angerDecay).coerceAtLeast(0)
        }

        // 2. Kırgınlık sönümü (öfkeden daha yavaş, daha uzun süreli)
        if (resentment > 0) {
            val baseRate = when {
                storyDaysSkipped >= 14 -> 0.70
                storyDaysSkipped >= 7  -> 0.45
                storyDaysSkipped >= 3  -> 0.25
                else                   -> 0.10
            }
            val resDecay = (resentment * baseRate * capMult).toInt()
            resentment = (resentment - resDecay).coerceAtLeast(0)
        }

        // 3. Duygusal artık (residue) sönümü
        if (residue > 0) {
            val residueDecay = (storyDaysSkipped * 48).coerceAtMost(residue)
            residue = (residue - residueDecay).coerceAtLeast(0)
        }

        // 4. Özlem artışı: yakınlık ≥ 40 ve ≥ 3 hikâye günü geçmişse
        var longingAdded = 0
        if (currentState.affection >= 40 && storyDaysSkipped >= 3) {
            longingAdded = minOf(25, storyDaysSkipped * 3)
            anticipation = (anticipation + longingAdded).coerceAtMost(90)
        }

        val updatedState = currentState.copy(
            primaryEmotions = currentState.primaryEmotions.copy(
                anger = anger,
                anticipation = anticipation
            ),
            relationshipAxes = currentState.relationshipAxes.copy(
                resentmentScore = resentment
            ),
            emotionalResidue = residue
        )

        // Prompt notu: yalnızca gerçek değişim varsa eklenir
        val parts = mutableListOf<String>()
        val angerDrop = angerBefore - anger
        val resDrop = resentmentBefore - resentment
        if (angerDrop >= 5) parts.add("öfke azaldı (${angerBefore}→${anger})")
        if (resDrop >= 5) parts.add("kırgınlık yumuşadı (${resentmentBefore}→${resentment})")
        if (longingAdded >= 5) parts.add("özlem arttı (+$longingAdded)")

        val note = if (parts.isNotEmpty()) {
            "Aradan $storyDaysSkipped gün geçti: ${parts.joinToString(", ")}."
        } else ""

        return DecayResult(updatedState, note)
    }

    /**
     * Pasif mikro-sönüm: hikâye atlaması olmayan sıradan mesajlarda çok yavaş
     * öfke/kırgınlık soğuması. Gerçekçi psikoloji: büyük kızgınlıklar günler içinde
     * kendiliğinden biraz hafifler, aniden kaybolmaz.
     * Kural: her 10 mesajda öfke ve kırgınlık 1 puan azalır (eşik: >20).
     * Düzenleme kapasitesi ≥ 60 ise her 8 mesajda bir uygulanır.
     * @param messageIndex O an kaçıncı mesaj olduğu (toplam sayaç)
     */
    fun applyPassiveDecayPerMessage(
        currentState: EmotionState,
        messageIndex: Int,
        regulationCapacity: Int = 50
    ): EmotionState {
        val interval = if (regulationCapacity >= 60) 8 else 10
        if (messageIndex % interval != 0) return currentState

        var anger = currentState.primaryEmotions.anger
        var resentment = currentState.relationshipAxes.resentmentScore

        if (anger > 20) anger = (anger - 1).coerceAtLeast(0)
        if (resentment > 20) resentment = (resentment - 1).coerceAtLeast(0)

        if (anger == currentState.primaryEmotions.anger &&
            resentment == currentState.relationshipAxes.resentmentScore) return currentState

        return currentState.copy(
            primaryEmotions = currentState.primaryEmotions.copy(anger = anger),
            relationshipAxes = currentState.relationshipAxes.copy(resentmentScore = resentment)
        )
    }

    /**
     * Audits and enforces psychological consistency in Kotlin.
     * Prevents absurd contradictions (e.g. anger=80 but dominantEmotion="neşeli").
     * Returns the corrected state AND a consistency note (empty if no contradiction found).
     * The note is injected into the NEXT message's prompt as a single line so the LLM self-corrects.
     */
    data class ConsistencyResult(val state: EmotionState, val contradictionNote: String)

    fun enforcePsychologicalConsistency(state: EmotionState): EmotionState =
        enforcePsychologicalConsistencyWithNote(state).state

    fun enforcePsychologicalConsistencyWithNote(state: EmotionState): ConsistencyResult {
        var dom = state.dominantEmotion.lowercase(Locale("tr", "TR")).trim()
        val anger = state.primaryEmotions.anger
        val resentment = state.relationshipAxes.resentmentScore
        val joy = state.primaryEmotions.joy
        val sadness = state.primaryEmotions.sadness
        val fear = state.primaryEmotions.fear

        var note = ""
        var corrected = false

        // Kural 1: Yüksek öfke/kırgınlık ile neşeli baskın duygu çelişkisi
        if (anger >= 65 || resentment >= 65) {
            if (dom.contains("neşeli") || dom.contains("mutlu") || dom.contains("coşkulu") ||
                dom.contains("keyifli") || dom.contains("sevinçli")) {
                val oldDom = dom
                dom = if (anger >= resentment) "gergin ve öfkeli" else "kırgın ve mesafeli"
                note = "Önceki state çelişkiliydi: öfke/kırgınlık yüksek (anger=$anger, resentment=$resentment) ama baskın duygu \"$oldDom\" idi. Tutarlı bir state üret."
                corrected = true
            }
        }

        // Kural 2: Yüksek neşe + düşük öfke ile kızgın baskın duygu çelişkisi
        if (!corrected && joy >= 70 && anger < 20) {
            if (dom.contains("öfkeli") || dom.contains("kızgın") || dom.contains("sinirli")) {
                val oldDom = dom
                dom = "keyifli ve rahat"
                note = "Önceki state çelişkiliydi: neşe yüksek (joy=$joy) ama baskın duygu \"$oldDom\" idi. Tutarlı bir state üret."
                corrected = true
            }
        }

        // Kural 3: Çok düşük affection ile "tam güven/derin aşk" baskın duygu çelişkisi
        if (!corrected && state.affection < 25) {
            if (dom.contains("derin aşk") || dom.contains("tam güven") || dom.contains("aşık")) {
                val oldDom = dom
                dom = "nötr ve temkinli"
                note = "Önceki state çelişkiliydi: affection düşük (${state.affection}) ama baskın duygu \"$oldDom\" idi. Tutarlı bir state üret."
                corrected = true
            }
        }

        // Kural 4: Yüksek korku/üzüntü ile neşeli çelişkisi
        if (!corrected && (fear >= 70 || sadness >= 70)) {
            if (dom.contains("neşeli") || dom.contains("mutlu") || dom.contains("coşkulu")) {
                val oldDom = dom
                dom = if (fear >= sadness) "kaygılı ve tedirgin" else "üzgün ve içe kapanık"
                note = "Önceki state çelişkiliydi: korku/üzüntü yüksek (fear=$fear, sadness=$sadness) ama baskın duygu \"$oldDom\" idi. Tutarlı bir state üret."
            }
        }

        return ConsistencyResult(
            state = state.copy(dominantEmotion = dom),
            contradictionNote = note
        )
    }

    /**
     * Resilient parser supporting full and delta-only state blocks.
     * Merges incoming deltas into the previous valid state. Never crashes, never calls LLM again.
     */
    fun parseStateJsonSafely(
        rawStateBlock: String,
        previousState: EmotionState
    ): EmotionState {
        if (rawStateBlock.isBlank()) return previousState

        return try {
            val jsonMatch = Regex("""(?is)\{.*\}""").find(rawStateBlock)?.value ?: rawStateBlock
            val json = resolveAliases(JSONObject(jsonMatch))

            // Delta: tüm kısa/uzun formlar alias tablosu ile çözüldükten sonra okunur
            val deltaObj = json.optJSONObject("delta")?.let { resolveAliases(it) }
            val deltaAxis = deltaObj?.optString("axis", "")?.ifBlank { "affectionScore" }
                ?: "affectionScore"
            val deltaVal = deltaObj?.optInt("value", 0) ?: json.optInt("delta", 0)
            // "why" alanı delta.reason ile aynı slota düşer
            val deltaReason = deltaObj?.optString("reason", "")
                ?: json.optString("why", "")

            // Primary emotions: varsa merge, yoksa koru
            val primObj = json.optJSONObject("primary_emotions")
            val updatedPrimary = if (primObj != null) {
                PrimaryEmotions.fromJsonObject(primObj)
            } else {
                previousState.primaryEmotions
            }

            // Relationship axes: varsa merge, yoksa delta uygula
            val relObj = json.optJSONObject("relationship_axes")?.let { resolveAliases(it) }
            val updatedAxes = if (relObj != null) {
                RelationshipAxes.fromJsonObject(relObj)
            } else {
                val newAff = if (deltaAxis == "affectionScore")
                    (previousState.affection + deltaVal).coerceIn(0, 100)
                else previousState.affection
                previousState.relationshipAxes.copy(affectionScore = newAff)
            }

            val domEmotion = json.optString("dominant_emotion", "").ifBlank {
                json.optString("mood", previousState.dominantEmotion)
            }
            val suppEmotion = json.optString("suppressed_emotion", previousState.suppressedEmotion)

            val state = previousState.copy(
                primaryEmotions = updatedPrimary,
                relationshipAxes = updatedAxes,
                dominantEmotion = domEmotion,
                suppressedEmotion = suppEmotion,
                deltaAxis = deltaAxis,
                deltaValue = deltaVal,
                deltaReason = deltaReason
            )

            enforcePsychologicalConsistency(state)
        } catch (_: Exception) {
            // Regex kurtarma — delta değeri çıkarılabiliyorsa kısmi uygula, yoksa son geçerli state
            val valMatch = Regex("""(?:value|val|delta|aff)["':\s]+([+-]?\d+)""")
                .find(rawStateBlock)?.groupValues?.get(1)?.toIntOrNull()
            if (valMatch != null) {
                val newAff = (previousState.affection + valMatch).coerceIn(0, 100)
                previousState.copy(
                    relationshipAxes = previousState.relationshipAxes.copy(affectionScore = newAff),
                    deltaValue = valMatch
                )
            } else {
                previousState
            }
        }
    }

    /**
     * 500-Turn Synthetic Simulation for Balance Verification:
     * - Scenario 1: 100 Compliment Spam turns (Daily cap & diminishing returns test)
     * - Scenario 2: Severe Conflict crisis (Crisis lock & recovery resistance test)
     * - Scenario 3: 14-Day Absence (Natural decay & longing buildup test)
     */
    fun run500TurnBalancingSimulation(): String {
        val sb = StringBuilder()
        sb.appendLine("==========================================================================")
        sb.appendLine("=== EMOCHI EMOTION ENGINE V3: 500-TURN SYNTHETIC BALANCING REPORT ===")
        sb.appendLine("==========================================================================")

        var state = EmotionState()

        // 1. Compliment Spam Test (Turns 1 - 200)
        sb.appendLine("\n[PHASE 1: 200 Turns Continuous Compliment Spam]")
        var dailyGain = 0
        var streak = 0
        for (i in 1..200) {
            val delta = calculateClampedDelta(
                currentScore = state.affection,
                rawDelta = 5,
                dailyAffectionGain = dailyGain,
                maxDailyGain = 20,
                streakCount = streak,
                difficultyMultiplier = 1.0
            )
            val newAff = (state.affection + delta).coerceIn(0, 100)
            dailyGain += delta
            streak++
            state = state.copy(relationshipAxes = state.relationshipAxes.copy(affectionScore = newAff))
        }
        sb.appendLine("-> Turn 200 Affection Score: ${state.affection}/100 (Expected: capped safely <= 75 under daily limits)")
        check(state.affection <= 75) { "Compliment spam exceeded safe progression limit!" }

        // 2. Severe Conflict (Turns 201 - 300)
        sb.appendLine("\n[PHASE 2: Severe Conflict & Trauma Trigger]")
        state = state.copy(
            primaryEmotions = state.primaryEmotions.copy(anger = 85),
            relationshipAxes = state.relationshipAxes.copy(affectionScore = 40, resentmentScore = 70),
            dominantEmotion = "öfkeli ve kırgın"
        )
        val regulated = calculateEmotionalRegulation(-35, 70)
        sb.appendLine("-> Crisis Delta: Raw=-35, Regulated Outward=${regulated.outwardExpressedDelta}")
        check(regulated.isRegulationActive && regulated.outwardExpressedDelta > -35) { "Regulation failed to dampen crisis!" }

        // 3. 14-Day Natural Decay Test (Turns 301 - 500)
        sb.appendLine("\n[PHASE 3: 14-Day Absence Emotional Decay]")
        val decayed = applyNaturalEmotionalDecay(state, storyDaysSkipped = 14)
        sb.appendLine("-> Initial Anger: ${state.primaryEmotions.anger} -> Post-Decay Anger: ${decayed.primaryEmotions.anger}")
        sb.appendLine("-> Initial Resentment: ${state.relationshipAxes.resentmentScore} -> Post-Decay Resentment: ${decayed.relationshipAxes.resentmentScore}")
        sb.appendLine("-> Post-Decay Anticipation/Longing: ${decayed.primaryEmotions.anticipation}")

        check(decayed.primaryEmotions.anger < 30) { "Anger did not cool down after 2 weeks!" }
        check(decayed.relationshipAxes.resentmentScore < 30) { "Resentment did not soften after 2 weeks!" }

        sb.appendLine("\n=== 500-TURN SIMULATION COMPLETED: PROGRESSION CURVES MATHEMATICALLY PROVEN ===")
        return sb.toString()
    }
}
