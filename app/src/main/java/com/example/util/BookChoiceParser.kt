package com.example.util

object BookChoiceParser {

    private val numberWords = mapOf(
        "bir" to 1, "birinci" to 1, "birincisini" to 1, "birinci seçenek" to 1,
        "iki" to 2, "ikinci" to 2, "ikincisini" to 2, "ikinci seçenek" to 2,
        "üç" to 3, "üçüncü" to 3, "üçüncüsünü" to 3, "üçüncü seçenek" to 3,
        "dört" to 4, "dördüncü" to 4, "dördüncüsünü" to 4,
        "one" to 1, "first" to 1, "two" to 2, "second" to 2,
        "three" to 3, "third" to 3, "four" to 4, "fourth" to 4
    )

    private val emojiNumbers = mapOf(
        "1️⃣" to 1, "2️⃣" to 2, "3️⃣" to 3, "4️⃣" to 4,
        "5️⃣" to 5, "6️⃣" to 6
    )

    /**
     * Given user input and a list of choice texts, returns the 1-based choice index
     * or null if ambiguous / no match.
     */
    fun parseChoice(userInput: String, choices: List<String>): Int? {
        val normalized = userInput.trim().lowercase()

        // 1. Emoji number match
        for ((emoji, idx) in emojiNumbers) {
            if (userInput.contains(emoji) && idx <= choices.size) return idx
        }

        // 2. Bare digit match ("1", "2", "3")
        val bareDigit = Regex("^\\s*([1-6])\\s*\$").find(normalized)
        if (bareDigit != null) {
            val n = bareDigit.groupValues[1].toIntOrNull()
            if (n != null && n <= choices.size) return n
        }

        // 3. "1." / "1)" / "#1" prefix
        val prefixDigit = Regex("^[#]?([1-6])[.):]").find(normalized)
        if (prefixDigit != null) {
            val n = prefixDigit.groupValues[1].toIntOrNull()
            if (n != null && n <= choices.size) return n
        }

        // 4. Word-based number ("birinci", "ikinci" …)
        for ((word, idx) in numberWords) {
            if (normalized.contains(word) && idx <= choices.size) return idx
        }

        // 5. Keyword / longest-common-substring match against choice texts
        var bestIdx: Int? = null
        var bestScore = 0
        choices.forEachIndexed { i, choice ->
            val score = longestCommonSubstringLength(normalized, choice.lowercase())
            if (score > bestScore && score >= 4) {
                bestScore = score
                bestIdx = i + 1
            }
        }
        if (bestIdx != null) return bestIdx

        // 6. No match → caller should ask "Hangi seçeneği kastediyorsun?"
        return null
    }

    private fun longestCommonSubstringLength(a: String, b: String): Int {
        if (a.isEmpty() || b.isEmpty()) return 0
        var max = 0
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                if (a[i - 1] == b[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1
                    if (dp[i][j] > max) max = dp[i][j]
                }
            }
        }
        return max
    }
}
