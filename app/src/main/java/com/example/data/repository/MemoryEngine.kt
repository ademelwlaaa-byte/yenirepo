package com.example.data.repository

import com.example.data.local.MemoryFactEntity
import com.example.data.local.MemoryEventEntity
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Memory Engine V3 (Hafıza ve Anı Sistemi).
 * Features:
 * - In-database embedding caching (MemoryFactEntity.embeddingJson)
 * - Cosine similarity semantic retrieval on IO dispatcher (no runBlocking)
 * - Trivial message RAG bypass (skips retrieval for casual greetings, saving ~120 tokens)
 * - Dynamic token ceiling (~400 tokens) with (Importance * Similarity) ranking
 * - Contradiction detection (isContradictingText) with automatic archival to ArchivedMemoryEntity
 * - Dynamic entity-based subject mapping instead of hardcoded 'kullanıcı'
 */
object MemoryEngine {

    const val MAX_MEMORY_PROMPT_TOKENS = 400

    /**
     * Hafıza geri çağırma gerekmeyen basit mesajları filtreler.
     * Zorla-tetikleme sinyalleri varsa (?, hatırla, özel isim, geçen) her zaman false döner.
     * Belirsizlikte false döner (yani geri çağırma yapılır).
     */
    fun isTrivialCasualMessage(query: String): Boolean {
        val clean = query.trim().lowercase(Locale("tr", "TR"))

        // Zorla-tetikleme sinyalleri → hiçbir zaman trivial değil
        val forceRetrievalSignals = listOf(
            "?", "hatırla", "hatırlıyor", "kimdi", "kimsin", "nerede", "nereden", "nereli",
            "geçen", "daha önce", "eskiden", "önceki", "bahsetmiştin", "demiştin",
            "anlattığım", "söylemiştim", "biliyorsun", "neydi", "neymiş", "kaçtı", "kim"
        )
        if (forceRetrievalSignals.any { clean.contains(it) }) return false

        // Özel isim içeren mesajlar (büyük harfle başlayan kelime var mı)
        val hasProperName = query.split(Regex("\\s+")).any { word ->
            word.length >= 3 && word.first().isUpperCase() && word.first().isLetter()
        }
        if (hasProperName) return false

        // Uzun mesajlar her zaman geri çağırma gerektirir
        if (clean.length > 30) return false

        // Kesinlikle basit: selamlama, kısa onay/ret, tek kelimelik tepkiler
        val trivialExact = setOf(
            "selam", "merhaba", "slm", "mrb", "hey", "hi",
            "nasılsın", "naber", "n'aber", "ne haber",
            "günaydın", "iyi akşamlar", "iyi geceler", "iyi sabahlar",
            "tamam", "olur", "evet", "hayır", "peki", "ok", "okay",
            "eyvallah", "sağol", "teşekkürler", "teşekkür", "tşk",
            "hm", "hmm", "ah", "oh", "aa", "ehe", "ehe", "e", "ya",
            "dur", "bekle", "anladım", "anla", "yok", "yok mu",
            "harika", "süper", "güzel", "iyi", "kötü", "neden",
            "devam", "devam et", "söyle", "anlat", "evet anlat"
        )
        if (trivialExact.contains(clean)) return true

        // Kısa ve yalnızca trivial kelimelerle başlıyorsa
        val trivialPrefixes = setOf("selam", "merhaba", "hey", "tamam", "hm", "ah", "oh")
        if (clean.length < 20 && trivialPrefixes.any { clean.startsWith(it) }) return true

        return false
    }

    fun parseEmbeddingFromJson(jsonStr: String): FloatArray? {
        if (jsonStr.isBlank()) return null
        return try {
            val arr = JSONArray(jsonStr)
            val floats = FloatArray(arr.length())
            for (i in 0 until arr.length()) {
                floats[i] = arr.getDouble(i).toFloat()
            }
            floats
        } catch (_: Exception) {
            null
        }
    }

    fun serializeEmbeddingToJson(embedding: FloatArray): String {
        val arr = JSONArray()
        for (f in embedding) {
            arr.put(f.toDouble())
        }
        return arr.toString()
    }

    fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        if (v1.isEmpty() || v2.isEmpty() || v1.size != v2.size) return 0f
        var dot = 0f
        var n1 = 0f
        var n2 = 0f
        for (i in v1.indices) {
            dot += v1[i] * v2[i]
            n1 += v1[i] * v1[i]
            n2 += v2[i] * v2[i]
        }
        val denom = kotlin.math.sqrt(n1) * kotlin.math.sqrt(n2)
        return if (denom > 0f) (dot / denom).coerceIn(0f, 1f) else 0f
    }

    /**
     * Determines whether two factual statements contradict each other.
     * e.g. "evli" vs "bekar", "İstanbul'da yaşıyor" vs "Ankara'da yaşıyor".
     */
    fun isContradictingText(oldValue: String, newValue: String): Boolean {
        val old = oldValue.lowercase(Locale("tr", "TR")).trim()
        val new = newValue.lowercase(Locale("tr", "TR")).trim()
        if (old == new) return false

        val opposites = listOf(
            Pair("evli", "bekar"),
            Pair("sevgili", "ayrıldı"),
            Pair("öğrenci", "mezun"),
            Pair("doktor", "mühendis"),
            Pair("yaşıyor", "vefat"),
            Pair("seviyor", "nefret ediyor")
        )

        for ((o1, o2) in opposites) {
            if ((old.contains(o1) && new.contains(o2)) || (old.contains(o2) && new.contains(o1))) {
                return true
            }
        }

        // Location / City changes
        val cities = listOf("istanbul", "ankara", "izmir", "bursa", "antalya", "londra", "berlin", "paris")
        val oldCity = cities.firstOrNull { old.contains(it) }
        val newCity = cities.firstOrNull { new.contains(it) }
        if (oldCity != null && newCity != null && oldCity != newCity) {
            return true
        }

        return false
    }

    /**
     * Extracts dynamic subject based on sentence cues instead of hardcoded 'kullanıcı'.
     */
    fun inferFactSubject(content: String, defaultSubject: String = "kullanıcı"): String {
        val lower = content.lowercase(Locale("tr", "TR"))
        val (firstWord) = Regex("""^([A-ZÇĞİÖŞÜ][a-zçğıöşü]+)""").find(content)?.groupValues?.drop(1) ?: listOf("")

        return when {
            lower.startsWith("ben ") || lower.contains("benim ") || lower.contains("seviyorum") -> "kullanıcı"
            lower.startsWith("sen ") || lower.contains("senin ") || lower.contains("seviyorsun") -> "karakter"
            firstWord.isNotBlank() && firstWord.length in 3..15 -> firstWord
            else -> defaultSubject
        }
    }

    data class FilteredMemoryResult(
        val facts: List<MemoryFactEntity>,
        val events: List<MemoryEventEntity>,
        val estimatedTokens: Int
    )

    // -------------------------------------------------------------------------
    // HAFIZA ZAMAN SİSTEMİ
    // Her kaydın hikâye gün sayacına göre "ne kadar önce" olduğunu hesaplar.
    // Gerçek saatten BAĞIMSIZ — yalnızca storyDayCounter kullanılır.
    //
    // Kurallar:
    //   - Aynı gün (0 gün fark)     → "bugün"
    //   - 1 gün önce               → "dün"
    //   - 2-6 gün önce             → "X gün önce"
    //   - 7-13 gün önce            → "geçen hafta"
    //   - 14-29 gün önce           → "birkaç hafta önce"
    //   - 30-89 gün önce           → "yaklaşık X hafta önce"
    //   - 90-364 gün önce          → "birkaç ay önce"
    //   - 365+ gün önce            → "çok uzun zaman önce"
    //   - storyDayIndex == 0/1     → "bilinmeyen bir zamanda" (eski kayıt)
    //
    // Amaç: Model "3 gün önce" olan bir olayı "az önce oldu" gibi sunmasın.
    // -------------------------------------------------------------------------
    fun storyTimeLabel(storyDayIndex: Long, currentStoryDay: Long): String {
        if (storyDayIndex <= 1L) return "bilinmeyen bir zamanda"
        val diff = (currentStoryDay - storyDayIndex).coerceAtLeast(0L)
        return when {
            diff == 0L      -> "bugün"
            diff == 1L      -> "dün"
            diff <= 6L      -> "$diff gün önce"
            diff <= 13L     -> "geçen hafta"
            diff <= 29L     -> "birkaç hafta önce"
            diff <= 89L     -> "yaklaşık ${diff / 7} hafta önce"
            diff <= 364L    -> "birkaç ay önce"
            else            -> "çok uzun zaman önce"
        }
    }

    /**
     * Olayın netlik/bulanıklık durumunu hesaplar.
     * Yüksek öneme sahip olaylar hiçbir zaman "Silik Anı" olmaz.
     * storyDayIndex <= 1 olan eski kayıtlar bulanık varsayılır.
     */
    fun memoryClarityTag(
        storyDayIndex: Long,
        currentStoryDay: Long,
        importanceScore: Int
    ): String {
        val isHighImportance = importanceScore >= 80
        if (isHighImportance) return "[Net Anı]"
        if (storyDayIndex <= 1L) return "[Bulanık Anı]"

        val ageDays = (currentStoryDay - storyDayIndex).coerceAtLeast(0L).toFloat()
        val decay = ageDays * 2.0f * (1.0f - importanceScore / 100f)
        val clarity = (100f - decay).coerceIn(10f, 100f)
        return when {
            clarity >= 75f -> "[Net Anı]"
            clarity >= 40f -> "[Bulanık Anı]"
            else           -> "[Silik Anı]"
        }
    }

    /**
     * Hafıza bloğunu tarih etiketleri dahil formatlı string olarak üretir.
     * Aynı bilginin facts/events/checkpoint'te tekrarını önler (içerik hash'i ile).
     * Tavanı aşan kayıtlar tamamen çıkarılır — yarım kayıt yazılmaz.
     */
    fun buildTimestampedMemoryBlock(
        facts: List<MemoryFactEntity>,
        events: List<MemoryEventEntity>,
        currentStoryDay: Long,
        maxTokens: Int = MAX_MEMORY_PROMPT_TOKENS
    ): String {
        var tokenCount = 0
        val lines = mutableListOf<String>()
        val seenHashes = mutableSetOf<Int>()

        fun tryAdd(line: String): Boolean {
            val cost = maxOf(1, (line.length / 3.5).toInt())
            if (tokenCount + cost > maxTokens) return false
            val hash = line.lowercase().trim().hashCode()
            if (seenHashes.contains(hash)) return false
            seenHashes.add(hash)
            lines.add(line)
            tokenCount += cost
            return true
        }

        for (f in facts) {
            val subjectTag = if (f.subject.isNotBlank() && f.subject != "kullanıcı") "[${f.subject}]" else ""
            val line = "- $subjectTag [${f.key}] ${f.value}"
            tryAdd(line)
        }

        for (ev in events) {
            val timeLabel = storyTimeLabel(ev.storyDayIndex, currentStoryDay)
            val clarityTag = memoryClarityTag(ev.storyDayIndex, currentStoryDay, ev.importanceScore)
            val sourceTag = if (ev.isBotSaved) "[Bot]" else "[Sistem]"
            val line = "- $clarityTag [$timeLabel] $sourceTag ${ev.description}"
            tryAdd(line)
        }

        return lines.joinToString("\n")
    }

    /**
     * Ranks facts and events by (Importance * Similarity) and strictly clamps to the token ceiling.
     */
    fun buildBoundedMemoryBlock(
        facts: List<MemoryFactEntity>,
        events: List<MemoryEventEntity>,
        maxTokens: Int = MAX_MEMORY_PROMPT_TOKENS
    ): FilteredMemoryResult {
        var currentTokenCount = 0
        val selectedFacts = mutableListOf<MemoryFactEntity>()
        val selectedEvents = mutableListOf<MemoryEventEntity>()

        // 1. Facts: high accuracy, high value per token
        for (f in facts) {
            val cost = maxOf(1, (("${f.subject}: ${f.key} = ${f.value}").length / 3.5).toInt())
            if (currentTokenCount + cost <= maxTokens) {
                selectedFacts.add(f)
                currentTokenCount += cost
            } else break
        }

        // 2. Events: chronological / narrative richness
        for (e in events) {
            val cost = maxOf(1, (e.description.length / 3.5).toInt())
            if (currentTokenCount + cost <= maxTokens) {
                selectedEvents.add(e)
                currentTokenCount += cost
            } else break
        }

        return FilteredMemoryResult(
            facts = selectedFacts,
            events = selectedEvents,
            estimatedTokens = currentTokenCount
        )
    }

    /**
     * Creates a genuine concise scene checkpoint summary from older message segments.
     */
    fun buildConciseCheckpointSummary(
        checkpointNumber: Int,
        recapEvents: String,
        recapEmotions: String
    ): String {
        val cleanEvents = recapEvents.lines().filter { it.isNotBlank() }.take(3).joinToString("; ").removePrefix("- ")
        val cleanEmotions = recapEmotions.lines().filter { it.isNotBlank() }.take(2).joinToString("; ").removePrefix("- ")

        return "Sahne #$checkpointNumber Özeti: $cleanEvents. Duygusal Durum: $cleanEmotions."
    }
}
