package com.example.util

import com.example.data.local.MessageEntity

enum class UserMood(val label: String, val systemHint: String) {
    POSITIVE("Pozitif", "Kullanıcı şu an iyi bir ruh halinde görünüyor. Enerjik ve samimi yanıtlar ver."),
    EXCITED("Heyecanlı", "Kullanıcı heyecanlı ve coşkulu görünüyor. Aynı enerjiyi yansıt, neşeli ol."),
    NEUTRAL("Nötr", ""),
    NEGATIVE("Negatif", "Kullanıcı şu an mutsuz veya gergin görünüyor. Nazik, anlayışlı ve destekleyici ol."),
    WITHDRAWN("Mesafeli", "Kullanıcı kısa ve ilgisiz yanıtlar veriyor. Zorlamadan ilgisini çekmeye çalış.")
}

object UserSentimentAnalyzer {

    private val positiveWords = setOf(
        "mutlu", "harika", "süper", "güzel", "seviyorum", "mükemmel", "iyi", "güldüm",
        "😊", "😄", "🥰", "❤️", "💕", "✨", "🎉", "👍", "teşekkür", "sevdim",
        "eğlendim", "keyifli", "memnun", "sevinçli", "harikaydı", "bayıldım"
    )

    private val negativeWords = setOf(
        "üzgün", "kötü", "sinirli", "sıkıldım", "yoruldum", "bıktım", "nefret",
        "😢", "😭", "😡", "😤", "💔", "berbat", "korkunç", "rezalet",
        "mutsuz", "bunaldım", "yorucu", "sıkıcı", "kızgın", "bozuldum", "hayal kırıklığı"
    )

    private val excitedMarkers = setOf("!!!", "!!", "wow", "ya!!", "aman", "inanılmaz", "şaşırdım", "beklemiyordum")

    fun analyze(messages: List<MessageEntity>, windowSize: Int = 3): UserMood {
        val userMessages = messages.filter { it.role == "user" }.takeLast(windowSize)
        if (userMessages.isEmpty()) return UserMood.NEUTRAL

        var positiveScore = 0
        var negativeScore = 0
        var excitedScore = 0
        var withdrawnScore = 0

        for (msg in userMessages) {
            val text = msg.text.lowercase()
            val wordCount = text.split("\\s+".toRegex()).size

            // Very short messages signal disengagement
            if (wordCount <= 2) withdrawnScore++

            for (word in positiveWords) {
                if (text.contains(word)) positiveScore++
            }
            for (word in negativeWords) {
                if (text.contains(word)) negativeScore++
            }
            for (marker in excitedMarkers) {
                if (text.contains(marker)) excitedScore++
            }

            // Punctuation excitement
            val exclamations = text.count { it == '!' }
            if (exclamations >= 3) excitedScore++
        }

        // Withdrawn: majority short messages, no positive/negative signal
        if (withdrawnScore >= 2 && positiveScore == 0 && negativeScore == 0) return UserMood.WITHDRAWN

        val maxScore = maxOf(positiveScore, negativeScore, excitedScore)
        if (maxScore == 0) return UserMood.NEUTRAL

        return when {
            excitedScore >= maxScore && excitedScore > 0 -> UserMood.EXCITED
            positiveScore >= negativeScore && positiveScore > 0 -> UserMood.POSITIVE
            negativeScore > positiveScore -> UserMood.NEGATIVE
            else -> UserMood.NEUTRAL
        }
    }

    fun buildMoodHint(mood: UserMood): String {
        if (mood.systemHint.isBlank()) return ""
        return "\n\n[KULLANICI DUYGU SENSÖRÜ - GİZLİ]\n${mood.systemHint}"
    }
}
