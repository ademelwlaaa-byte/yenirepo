package com.example.data.repository

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * TimePerceptionEngine — Hikâye Takvimi Sistemi.
 * Gerçek dünya zamanından tamamen bağımsız; zaman yalnızca hikâye atlamalarıyla ilerler.
 */
object TimePerceptionEngine {

    private const val MAX_DAYS_PER_JUMP = 3650

    data class StoryTimeJumpResult(
        val daysToAdvance: Int = 0,
        val newSceneTime: String? = null
    )

    data class StoryTimeInfo(
        val storyCalendarDate: String,
        val storyDayCounter: Long = 1L,
        val currentAge: Int = 20,
        val sceneTime: String = "",
        val promptBlock: String = ""
    )

    private val VALID_SCENE_TIMES = setOf("sabah", "öğle", "akşam", "gece")

    fun validateSceneTime(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val lower = raw.lowercase(Locale("tr", "TR")).trim()
        return if (lower in VALID_SCENE_TIMES) lower else null
    }

    // ---------------------------------------------------------------------------
    // Türkçe sayı kelime ayrıştırıcı — önce bileşikler, sonra tekli
    // ---------------------------------------------------------------------------

    private data class NumberResult(val intValue: Int, val isHalf: Boolean = false)

    private fun parseTurkishNumber(text: String): NumberResult? {
        val lower = text.lowercase(Locale("tr", "TR")).trim()
        if (lower.isBlank()) return null

        // Rakam ile başlıyorsa doğrudan al
        val digitMatch = Regex("""\b(\d+)\b""").find(lower)
        if (digitMatch != null) {
            val n = digitMatch.groupValues[1].toIntOrNull() ?: return null
            return NumberResult(n)
        }

        // "bir buçuk" → 1.5 → 2 (ceil)
        if (Regex("""\bbir\s+buçuk\b""").containsMatchIn(lower)) return NumberResult(2, isHalf = true)
        // "yarım" → 0.5 → 0 gün (sadece saat bağlamında)
        if (Regex("""\byarım\b""").containsMatchIn(lower)) return NumberResult(0, isHalf = true)
        // "birkaç" → 3
        if (Regex("""\bbirkaç\b|\bbirkac\b""").containsMatchIn(lower)) return NumberResult(3)

        // Önce bileşik on-lu sayılar (uzundan kısaya eşleştir)
        val compositeMap = listOf(
            "yirmi dokuz" to 29, "yirmi sekiz" to 28, "yirmi yedi" to 27,
            "yirmi altı" to 26, "yirmi beş" to 25, "yirmi dört" to 24,
            "yirmi üç" to 23, "yirmi iki" to 22, "yirmi bir" to 21,
            "otuz bir" to 31, "otuz iki" to 32, "otuz" to 30,
            "yirmi" to 20, "on dokuz" to 19, "on sekiz" to 18,
            "on yedi" to 17, "on altı" to 16, "on beş" to 15,
            "on dört" to 14, "on üç" to 13, "on iki" to 12,
            "on bir" to 11, "kırk beş" to 45, "kırk" to 40,
            "elli" to 50, "altmış" to 60, "yetmiş" to 70,
            "seksen" to 80, "doksan" to 90, "yüz" to 100
        )
        for ((word, num) in compositeMap) {
            if (Regex("""\b${Regex.escape(word)}\b""").containsMatchIn(lower)) return NumberResult(num)
        }

        // Tekli sayılar
        val singleMap = listOf(
            "on" to 10, "dokuz" to 9, "sekiz" to 8, "yedi" to 7,
            "altı" to 6, "alti" to 6, "beş" to 5, "bes" to 5,
            "dört" to 4, "dort" to 4, "üç" to 3, "uc" to 3,
            "iki" to 2, "bir" to 1
        )
        for ((word, num) in singleMap) {
            if (Regex("""\b${Regex.escape(word)}\b""").containsMatchIn(lower)) return NumberResult(num)
        }
        return null
    }

    // ---------------------------------------------------------------------------
    // parseStoryTimeJump — hikâye atlama tespiti
    // ---------------------------------------------------------------------------

    fun parseStoryTimeJump(text: String): StoryTimeJumpResult {
        if (text.isBlank()) return StoryTimeJumpResult()

        // Tırnak içi diyalog ve OOC parantezleri çıkar
        val stripped = text
            .replace(Regex(""""[^"]*""""), " ")
            .replace(Regex("""«[^»]*»"""), " ")
            .replace(Regex("""\([^)]*\)"""), " ")
            .replace(Regex("""\[[^\]]*\]"""), " ")
        val lower = stripped.lowercase(Locale("tr", "TR")).trim()
        if (lower.isBlank()) return StoryTimeJumpResult()

        // --- 1. Kesinlikle ATLAma olmayan söz/plan ifadeler ---
        // "yarın" tek başına (söz), "haftaya", "gelecek ay/hafta" → ATLA
        val futurePromisePattern = Regex("""\b(yarın|yarin|haftaya|hafta\s*ya|gelecek\s+(?:ay|hafta|yıl|yil))\b""")
        if (futurePromisePattern.containsMatchIn(lower) &&
            !Regex("""\b(?:sonra|geçti|gecti|ardından|aradan)\b""").containsMatchIn(lower)) {
            return StoryTimeJumpResult()
        }

        // --- 2. Sadece saat bağlamı (gün artmaz, sceneTime güncellenir) ---
        // "birkaç saat/dakika sonra"
        val hourMinRegex = Regex(
            """\b(?:birkaç|birkac|bir|iki|üç|uc|yarım|yarim|\d+)\s+(?:saat|dakika|dk)\s*(?:sonra|geçti|gecti|ardından)\b"""
        )
        if (hourMinRegex.containsMatchIn(lower)) {
            // Scene time tahmini
            val scene = detectSceneFromContext(lower)
            return StoryTimeJumpResult(daysToAdvance = 0, newSceneTime = scene)
        }

        // Sahne geçişi ifadeleri (gün artmaz)
        val sceneShiftRegex = Regex(
            """\b(öğle(?:\s+vakti|den)?|oglen|akşamüstü|akşama\s+doğru|akşam\s+oldu|aksam\s+oldu|gece\s+oldu|gece\s+yarısı|sabah\s+oldu)\b"""
        )
        val sceneShiftMatch = sceneShiftRegex.find(lower)
        if (sceneShiftMatch != null &&
            !Regex("""\b(?:\d+|bir|iki|üç|dört|beş|altı|yedi|sekiz|dokuz|on)\s+(?:gün|hafta|ay|yıl)\b""").containsMatchIn(lower)) {
            val sceneWord = sceneShiftMatch.value
            val scene = when {
                sceneWord.contains("öğle") || sceneWord.contains("oglen") -> "öğle"
                sceneWord.contains("gece") -> "gece"
                sceneWord.contains("sabah") -> "sabah"
                else -> "akşam"
            }
            return StoryTimeJumpResult(daysToAdvance = 0, newSceneTime = scene)
        }

        // --- 3. "ertesi sabah" / "ertesi gün" / "bir sonraki sabah" → 1 gün ---
        if (Regex("""\bertesi\s+sabah\b""").containsMatchIn(lower)) {
            return StoryTimeJumpResult(daysToAdvance = 1, newSceneTime = "sabah")
        }
        if (Regex("""\b(?:ertesi\s+gün|ertesi\s+gun|sonraki\s+gün|sonraki\s+gun|bir\s+sonraki\s+sabah)\b""").containsMatchIn(lower)) {
            return StoryTimeJumpResult(daysToAdvance = 1, newSceneTime = null)
        }

        // --- 4. Gün atlamaları — kelime sınırlı, önek grubu YOK ---
        // Pattern: <sayı> <birim> <fiil>
        // Birimler: gün, hafta, ay, yıl
        // Fiiller: sonra, geçti, gecti, ardından, sonrasında

        val jumpVerb = """(?:sonra|geçti|gecti|ardından|sonrasında)"""

        // Yıl
        val yearPattern = Regex(
            """\b((?:\d+|bir\s+buçuk|birkaç|birkac|(?:yirmi\s+\w+|otuz\s+\w+|kırk|elli|on\s+\w+|bir|iki|üç|uc|dört|dort|beş|bes|altı|alti|yedi|sekiz|dokuz|on)(?:\s+\w+)?))\s+yıl\s*$jumpVerb\b"""
        )
        yearPattern.find(lower)?.let { m ->
            val num = parseTurkishNumber(m.groupValues[1]) ?: return@let
            val days = clamp(num.intValue * 365)
            return StoryTimeJumpResult(daysToAdvance = days, newSceneTime = null)
        }

        // Ay — \bay\b kelime sınırı zorunlu
        val monthPattern = Regex(
            """\b(\d+|bir\s+buçuk|birkaç|birkac|yirmi\s+\w+|otuz\s+\w+|on\s+\w+|bir|iki|üç|uc|dört|dort|beş|bes|altı|alti|yedi|sekiz|dokuz|on)\s+\bay\b\s*(?:$jumpVerb)?\b"""
        )
        monthPattern.find(lower)?.let { m ->
            val num = parseTurkishNumber(m.groupValues[1]) ?: return@let
            val days = clamp(num.intValue * 30)
            return StoryTimeJumpResult(daysToAdvance = days, newSceneTime = null)
        }

        // Hafta
        val weekPattern = Regex(
            """\b(\d+|bir\s+buçuk|birkaç|birkac|yirmi\s+\w+|otuz\s+\w+|on\s+\w+|bir|iki|üç|uc|dört|dort|beş|bes|altı|alti|yedi|sekiz|dokuz|on)\s+hafta\s*(?:$jumpVerb)?\b"""
        )
        weekPattern.find(lower)?.let { m ->
            val num = parseTurkishNumber(m.groupValues[1]) ?: return@let
            val days = if (num.isHalf) clamp((num.intValue + 0.5 * 7).toInt())
                       else clamp(num.intValue * 7)
            return StoryTimeJumpResult(daysToAdvance = days, newSceneTime = null)
        }

        // Gün
        val dayPattern = Regex(
            """\b(\d+|birkaç|birkac|yirmi\s+\w+|otuz\s+\w+|on\s+\w+|bir|iki|üç|uc|dört|dort|beş|bes|altı|alti|yedi|sekiz|dokuz|on)\s+gün\s*(?:$jumpVerb)?\b"""
        )
        dayPattern.find(lower)?.let { m ->
            val num = parseTurkishNumber(m.groupValues[1]) ?: return@let
            if (num.intValue <= 0) return@let
            val days = clamp(num.intValue)
            val scene = detectSceneFromContext(lower)
            return StoryTimeJumpResult(daysToAdvance = days, newSceneTime = scene)
        }

        return StoryTimeJumpResult()
    }

    private fun detectSceneFromContext(lower: String): String? {
        return when {
            lower.contains("sabah") -> "sabah"
            lower.contains("öğle") || lower.contains("oglen") -> "öğle"
            lower.contains("akşam") || lower.contains("aksam") -> "akşam"
            lower.contains("gece") -> "gece"
            else -> null
        }
    }

    private fun clamp(days: Int): Int {
        return if (days > MAX_DAYS_PER_JUMP) {
            android.util.Log.w("TimePerception", "Atlama $days gün → $MAX_DAYS_PER_JUMP ile kırpıldı")
            MAX_DAYS_PER_JUMP
        } else days
    }

    // ---------------------------------------------------------------------------
    // advanceCalendarDateAndCheckAge — Calendar tabanlı, UTC, DAY_OF_YEAR hatası yok
    // ---------------------------------------------------------------------------

    fun advanceCalendarDateAndCheckAge(
        currentCalendarDateStr: String,
        currentDayCounter: Long,
        elapsedDaysToAdd: Int,
        birthDateStr: String,
        initialAge: Int
    ): Triple<String, Long, Int> {
        val daysToAdd = elapsedDaysToAdd.coerceAtLeast(0)
        val newDayCounter = currentDayCounter + daysToAdd

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        val currentDate = if (currentCalendarDateStr.isNotBlank()) {
            try { sdf.parse(currentCalendarDateStr) } catch (_: Exception) { null }
        } else null

        val newCalendarDateStr = if (currentDate != null) {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.time = currentDate
            if (daysToAdd > 0) cal.add(Calendar.DAY_OF_MONTH, daysToAdd)
            sdf.format(cal.time)
        } else ""

        var newAge = initialAge
        if (birthDateStr.isNotBlank() && newCalendarDateStr.isNotBlank()) {
            try {
                val birthDate = sdf.parse(birthDateStr)
                val newDate = sdf.parse(newCalendarDateStr)
                if (birthDate != null && newDate != null) {
                    val calNew = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = newDate }
                    val calBirth = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = birthDate }

                    var age = calNew.get(Calendar.YEAR) - calBirth.get(Calendar.YEAR)
                    // Doğum günü bu yıl henüz geçmediyse bir eksilt (ay/gün karşılaştırması)
                    val newMM = calNew.get(Calendar.MONTH) * 100 + calNew.get(Calendar.DAY_OF_MONTH)
                    val birthMM = calBirth.get(Calendar.MONTH) * 100 + calBirth.get(Calendar.DAY_OF_MONTH)
                    if (newMM < birthMM) age--
                    if (age > 0) newAge = age
                }
            } catch (_: Exception) {}
        } else if (birthDateStr.isBlank()) {
            val extraYears = ((newDayCounter - 1) / 365).toInt()
            newAge = initialAge + extraYears
        }

        return Triple(newCalendarDateStr, newDayCounter, newAge)
    }

    // ---------------------------------------------------------------------------
    // buildStoryTimePromptBlock — tek satır, koşullu, gerçek zaman yok
    // ---------------------------------------------------------------------------

    fun buildStoryTimePromptBlock(
        storyDayCounter: Long,
        storyCalendarDate: String,
        currentAge: Int,
        sceneTime: String = "",
        hasExplicitStoryDate: Boolean = false
    ): String {
        if (storyDayCounter <= 0L) return ""

        val validScene = validateSceneTime(sceneTime)

        val parts = mutableListOf<String>()
        parts.add("Gün #$storyDayCounter")
        if (hasExplicitStoryDate && storyCalendarDate.isNotBlank()) {
            parts.add("Tarih: $storyCalendarDate")
        }
        if (currentAge > 0) {
            parts.add("Yaş: $currentAge")
        }
        if (validScene != null) {
            parts.add("Sahne: $validScene")
        }

        val singleLine = parts.joinToString(" | ")

        val moodHint = when (validScene) {
            "sabah" -> " (sabah: sakin/mahmur ton)"
            "öğle"  -> " (öğle: canlı/odaklı ton)"
            "akşam" -> " (akşam: dinlenmiş/yumuşak ton)"
            "gece"  -> " (gece: uykulu/samimi ton)"
            else    -> ""
        }

        return "\n## HİKÂYE ZAMANI: $singleLine$moodHint\n" +
               "- KURAL: Gerçek dünya saati ve tarihi yoktur. Zaman yalnızca hikâyedeki zaman atlamalarıyla ilerler. Kullanıcı ara verse de sohbet kesintisiz devam eder.\n"
    }
}
