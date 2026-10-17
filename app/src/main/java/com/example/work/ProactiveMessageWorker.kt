package com.example.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.local.MessageEntity
import com.example.data.repository.EmochiRepository
import java.util.UUID
import java.util.concurrent.TimeUnit

class ProactiveMessageWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "emochi_proactive_message"
        const val CHANNEL_ID = "emochi_proactive"
        const val CHANNEL_NAME = "Karakter Mesajları"
        const val IDLE_THRESHOLD_MS = 2 * 60 * 60 * 1000L // 2 saat

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<ProactiveMessageWorker>(45, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Karakterlerinden gelen mesajlar"
                    enableVibration(true)
                }
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.createNotificationChannel(channel)
            }
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val repository = EmochiRepository(db, applicationContext)

            val allBots = db.botDao().getAllBotsList()
            val now = System.currentTimeMillis()

            for (bot in allBots) {
                if (!bot.proactiveEnabled) continue
                val idleTime = now - bot.lastMessageTimestamp
                if (idleTime < IDLE_THRESHOLD_MS) continue

                val messages = db.messageDao().getMessagesForBotList(bot.id)
                if (messages.isEmpty()) continue

                val proactivePrompt = buildProactivePrompt(bot.aiName, bot.aiPersonality, idleTime)

                try {
                    val replyResult = repository.generateAiReply(
                        bot.copy(aiPersonality = proactivePrompt + "\n\n" + bot.aiPersonality),
                        messages.takeLast(5)
                    )
                    val replyText = replyResult.replyText
                    if (replyText.isBlank()) continue

                    val aiMsg = MessageEntity(
                        id = UUID.randomUUID().toString(),
                        botId = bot.id,
                        role = "assistant",
                        text = replyText,
                        timestamp = now,
                        provider = replyResult.usedProvider
                    )
                    repository.saveMessage(aiMsg)

                    sendNotification(
                        botId = bot.id,
                        botName = bot.aiName,
                        messagePreview = replyText.take(80)
                    )
                } catch (_: Exception) {
                    // Sessizce atla, sohbeti engelleme
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun buildProactivePrompt(name: String, personality: String, idleMs: Long): String {
        val hours = idleMs / (60 * 60 * 1000)
        return """
[PROAKTIF MESAJ - GİZLİ TALİMAT]
Kullanıcı yaklaşık $hours saattir sessiz. Sen $name karakterisin.
Karakterine uygun, kısa ve samimi bir mesaj yaz. Sanki kullanıcıya ilk sen başlatıyorsun.
Yapay veya zorlama hissettirme. Günlük hayattan bir şeyden bahset veya merak ettiğini belirt.
Maksimum 2 cümle.
        """.trimIndent()
    }

    private fun sendNotification(botId: String, botName: String, messagePreview: String) {
        createNotificationChannel(applicationContext)

        val openIntent = Intent(applicationContext, com.example.MainActivity::class.java).apply {
            action = "com.example.OPEN_CHAT"
            putExtra("botId", botId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            botId.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(botName)
            .setContentText(messagePreview)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messagePreview))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(botId.hashCode(), notification)
    }
}
