package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.BotEntity
import com.example.data.local.MessageEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatExporter {

    enum class ExportFormat { TEXT, JSON }
    enum class DateRange { ALL, LAST_7_DAYS, LAST_30_DAYS }

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))
    private val fileNameFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun export(
        context: Context,
        bot: BotEntity,
        messages: List<MessageEntity>,
        format: ExportFormat,
        dateRange: DateRange
    ) {
        val filtered = filterByRange(messages, dateRange)
        val content = when (format) {
            ExportFormat.TEXT -> buildTextExport(bot, filtered)
            ExportFormat.JSON -> buildJsonExport(bot, filtered)
        }
        val ext = if (format == ExportFormat.TEXT) "txt" else "json"
        val fileName = "emochi_${bot.aiName.take(20).replace(" ", "_")}_${fileNameFormatter.format(Date())}.$ext"

        val cacheDir = File(context.cacheDir, "exports").also { it.mkdirs() }
        val file = File(cacheDir, fileName)
        file.writeText(content, Charsets.UTF_8)

        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = if (format == ExportFormat.TEXT) "text/plain" else "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Emochi — ${bot.aiName} sohbeti")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Sohbeti Paylaş").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun filterByRange(messages: List<MessageEntity>, range: DateRange): List<MessageEntity> {
        val now = System.currentTimeMillis()
        return when (range) {
            DateRange.ALL -> messages
            DateRange.LAST_7_DAYS -> messages.filter { now - it.timestamp <= 7 * 24 * 60 * 60 * 1000L }
            DateRange.LAST_30_DAYS -> messages.filter { now - it.timestamp <= 30 * 24 * 60 * 60 * 1000L }
        }
    }

    private fun buildTextExport(bot: BotEntity, messages: List<MessageEntity>): String {
        val sb = StringBuilder()
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine("  Emochi — ${bot.aiName}")
        sb.appendLine("  Dışa aktarıldı: ${dateFormatter.format(Date())}")
        sb.appendLine("  Toplam mesaj: ${messages.size}")
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine()
        for (msg in messages) {
            val sender = if (msg.role == "assistant") "🤖 ${bot.aiName}" else "👤 Sen"
            val time = dateFormatter.format(Date(msg.timestamp))
            sb.appendLine("[$time] $sender")
            sb.appendLine(msg.text)
            sb.appendLine()
        }
        return sb.toString()
    }

    private fun buildJsonExport(bot: BotEntity, messages: List<MessageEntity>): String {
        val root = JSONObject()
        root.put("exportedAt", dateFormatter.format(Date()))
        root.put("botName", bot.aiName)
        root.put("botMode", bot.mode)
        root.put("messageCount", messages.size)
        val arr = JSONArray()
        for (msg in messages) {
            val obj = JSONObject()
            obj.put("id", msg.id)
            obj.put("role", msg.role)
            obj.put("text", msg.text)
            obj.put("timestamp", msg.timestamp)
            obj.put("timestampFormatted", dateFormatter.format(Date(msg.timestamp)))
            obj.put("provider", msg.provider ?: "")
            obj.put("isStarred", msg.isStarred)
            arr.put(obj)
        }
        root.put("messages", arr)
        return root.toString(2)
    }
}