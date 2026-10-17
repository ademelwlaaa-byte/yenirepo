package com.example.data.repository

import android.content.Context
import org.json.JSONArray
import java.util.Locale

/**
 * Cast Member Detection & Alias Resolution Engine (Otomatik Karakter Algılama).
 * Features:
 * - Turkish morphological suffix stemming (Ayla'nın -> Ayla, Kerem'e -> Kerem)
 * - Alias normalization (Doktor Kerem, Kerem, Kerem Bey -> single entity)
 * - JSON-backed blacklist with in-memory caching
 * - Code-based heuristic scoring (appearance count, dialogue share, speech cues)
 * - Active in-scene character filtering for token optimization
 */
object CastDetectorEngine {

    private val DEFAULT_FALLBACK_BLACKLIST = setOf(
        "bugün", "yarın", "dün", "sabah", "akşam", "gece", "gündüz", "şimdi", "sonra", "önce", "burada", "orada", "şurada",
        "çünkü", "sadece", "oysa", "lakin", "fakat", "ayrıca", "özellikle", "nitekim", "ancak", "belki", "böyle", "şöyle",
        "öyle", "peki", "tamam", "pekala", "lütfen", "efendim", "kullanıcı", "karakter", "insan", "adam", "kadın", "çocuk",
        "genç", "yaşlı", "polis", "garson", "hoca", "öğretmen", "müşteri", "sürücü", "asker", "kral", "kraliçe",
        "prens", "prenses", "profesör", "amiral", "komutan", "yüzbaşı", "kaptan", "istanbul", "ankara", "izmir", "türkiye",
        "dünya", "güneş", "ay", "yıldız", "sokak", "cadde", "oda", "masa", "sandalye", "kapı", "pencere", "araba", "telefon",
        "kitap", "kalem", "kılıç", "bıçak", "kalkan", "ateş", "su", "toprak", "hava", "ruh", "ışık", "karanlık", "ağaç",
        "orman", "deniz", "göl", "nehir", "dağ", "taş", "kaya", "ev", "saray", "şato", "kule", "zindan", "okul", "hastane",
        "otel", "meyhane", "restoran", "kafe", "dükkan", "pazar", "şehir", "köy", "ülke", "krallık", "imparatorluk",
        "lonca", "birlik", "takım", "grup", "sürü", "ordu", "donanma", "meclis", "pazartesi", "salı", "çarşamba", "perşembe",
        "cuma", "cumartesi", "pazar", "ocak", "şubat", "mart", "nisan", "mayıs", "haziran", "temmuz", "ağustos", "eylül",
        "ekim", "kasım", "aralık", "evvel", "ahir", "üstelik", "sanki", "hatta", "madem", "meğer", "güya", "zira", "aksi",
        "yine", "yeniden", "hemen", "derhal", "biraz", "çok", "fazla", "az", "hiç", "tüm", "bütün", "her", "kendi", "biri",
        "diğeri", "başka", "hangi", "nasıl", "neden", "niçin", "nere", "nerede", "nereden", "nereye", "kim", "kime", "kimden",
        "kimi", "gözü", "gözleri", "sesi", "yüzü", "adımları", "elleri", "bakışı", "dudakları", "başını", "elini", "arkası",
        "zorba", "soğuk", "sıcak", "büyük", "küçük", "uzun", "kısa", "sessiz", "hızlı", "yavaş", "güçlü", "zayıf",
        "evet", "hayır", "işte", "galiba", "sanırım", "bence", "sence", "zaten", "aslında", "gerçekten", "sonunda"
    )

    private val GENERIC_EXTRAS_BLACKLIST = setOf(
        "garson", "zorba", "zorba çocuk", "çocuk", "adam", "kadın", "polis", "müşteri",
        "yoldan geçen", "şoför", "taksici", "nöbetçi", "biri", "herkes", "hizmetli", "tezgahtar"
    )

    private val HONORIFICS = listOf(
        "Bey", "Hanım", "Efendi", "Ağa", "Usta", "Abi", "Abla", "Komutan",
        "Kaptan", "Doktor", "Hazretleri", "Paşa", "Hoca", "Teyze", "Amca", "Dayı", "Yenge", "Dede", "Peder"
    )

    private val TITLES = listOf(
        "Bay", "Bayan", "Doktor", "Prof", "Profesör", "Dr.", "Yüzbaşı", "Kaptan", "Amiral",
        "Komutan", "Lord", "Prens", "Prenses", "Kral", "Kraliçe", "Usta", "Hoca", "Savaşçı",
        "Büyücü", "Şövalye", "Mimar", "Aziz", "Üstat", "Gözcü", "Dedektif", "Ajan", "Müfettiş",
        "Savcı", "Hâkim", "Teğmen", "Binbaşı", "Albay", "General", "Sultan", "Paşa", "Dük", "Kont",
        "Baron", "Barones", "Rahip", "Cadı", "Gölge", "Muhafız", "Hizmetkar", "Tüccar", "Eczacı", "Leydi"
    )

    private val DIALOGUE_ACTION_VERBS = listOf(
        "dedi", "sordu", "fısıldadı", "bağırdı", "haykırdı", "mırıldandı",
        "yanıtladı", "ekledi", "güldü", "sırıttı", "iç geçirdi", "kaşlarını çattı",
        "omuz silkti", "seslendi", "atıldı", "cevap verdi"
    )

    @Volatile
    private var cachedBlacklist: Set<String>? = null
    private val blacklistLock = Any()

    fun getBlacklist(context: Context? = null): Set<String> {
        cachedBlacklist?.let { return it }
        if (context == null) return DEFAULT_FALLBACK_BLACKLIST

        return synchronized(blacklistLock) {
            // Double-check: başka thread yüklemişse tekrar yükleme
            cachedBlacklist?.let { return@synchronized it }
            try {
                val jsonStr = context.assets.open("cast_blacklist.json").bufferedReader().use { it.readText() }
                val arr = JSONArray(jsonStr)
                val set = mutableSetOf<String>()
                for (i in 0 until arr.length()) {
                    val item = arr.optString(i)?.trim()?.lowercase(Locale("tr", "TR"))
                    if (!item.isNullOrBlank()) set.add(item)
                }
                val result = set + DEFAULT_FALLBACK_BLACKLIST  // her ikisini birleştir
                cachedBlacklist = result
                result
            } catch (_: Exception) {
                DEFAULT_FALLBACK_BLACKLIST
            }
        }
    }

    /**
     * Stems Turkish proper names by removing apostrophes and inflectional suffixes.
     * e.g.:
     *   "Ayla'nın" -> "Ayla"
     *   "Kerem'e" -> "Kerem"
     *   "Deniz'den" -> "Deniz"
     *   "Ahmet'te" -> "Ahmet"
     *   "Mehmet'i" -> "Mehmet"
     */
    // Türkçe hâl/iyelik eklerinin listesi — YALNIZCA apostrof sonrası parça için kullanılır
    private val TURKISH_INFLECTION_SUFFIXES = setOf(
        "nın", "nin", "nun", "nün", "ın", "in", "un", "ün",
        "a", "e", "ya", "ye",
        "da", "de", "ta", "te",
        "dan", "den", "tan", "ten",
        "nda", "nde", "ndan", "nden",
        "yla", "yle", "la", "le",
        "ı", "i", "u", "ü", "yı", "yi", "yu", "yü",
        "m", "ım", "im", "um", "üm",
        "lar", "ler",
        "lara", "lere", "lardan", "lerden", "larda", "lerde"
    )

    /**
     * Proper name stemmer: apostrof varsa ek kontrol ederek soyar.
     * Apostrof yoksa ismi olduğu gibi döndürür — kör ek tahmini yapılmaz.
     * Örnek: "Ayla’nın" → "Ayla", "Zar’thul" → "Zar’thul", "Cemile" → "Cemile".
     */
    fun stemTurkishProperName(rawName: String): String {
        val clean = rawName.trim().removeSurrounding("\"")
        if (clean.isBlank()) return ""

        val apostropheRegex = Regex("""[‘’`]""")
        if (apostropheRegex.containsMatchIn(clean)) {
            val parts = clean.split(apostropheRegex, limit = 2)
            val base = parts[0].trim()
            val suffix = if (parts.size > 1) parts[1].trim().lowercase(Locale("tr", "TR")) else ""
            // Ek geçerliyse soy; değilse ismin parçasıdır (Zar’thul gibi)
            return if (suffix in TURKISH_INFLECTION_SUFFIXES && base.length >= 2) base else clean
        }

        // Apostrof yok → olduğu gibi döndür
        return clean
    }

    /**
     * Normalizes aliases such as "Doktor Kerem", "Kerem Bey", "Kerem" to root candidate name.
     */
    fun extractCanonicalNameAndRole(rawCandidate: String): Pair<String, String?> {
        val trimmed = rawCandidate.trim()
        var roleFromTitle: String? = null

        // Check Prefix Title: e.g. "Doktor Kerem"
        for (title in TITLES) {
            if (trimmed.startsWith("$title ", ignoreCase = true)) {
                val root = trimmed.substring(title.length).trim()
                roleFromTitle = if (title.contains("Doktor", ignoreCase = true) || title.contains("Dr.", ignoreCase = true)) "Doktor"
                else if (title.contains("Komutan", ignoreCase = true) || title.contains("Kaptan", ignoreCase = true)) "Komutan"
                else title
                return Pair(stemTurkishProperName(root), roleFromTitle)
            }
        }

        // Check Suffix Honorific: e.g. "Kerem Bey"
        for (hon in HONORIFICS) {
            if (trimmed.endsWith(" $hon", ignoreCase = true)) {
                val root = trimmed.substring(0, trimmed.length - hon.length).trim()
                roleFromTitle = if (hon == "Doktor") "Doktor" else if (hon == "Komutan" || hon == "Kaptan") "Komutan" else null
                return Pair(stemTurkishProperName(root), roleFromTitle)
            }
        }

        return Pair(stemTurkishProperName(trimmed), null)
    }

    data class CandidateScore(
        val isSignificant: Boolean,
        val role: String,
        val confidence: Int,
        val difficultyMultiplier: Double,
        val reason: String
    )

    /**
     * Code-based heuristic scoring engine for character significance.
     * Evaluates appearance frequency, dialogue verbs, titles, and speech cues without unnecessary LLM calls.
     */
    fun scoreCandidate(
        candidateName: String,
        sceneContext: String,
        appearanceCount: Int = 1,
        inferredRole: String = "Yan Karakter",
        context: Context? = null
    ): CandidateScore {
        val (canonical, titleRole) = extractCanonicalNameAndRole(candidateName)
        val clean = canonical.ifBlank { candidateName.trim() }
        val lower = clean.lowercase(Locale("tr", "TR"))
        val blacklist = getBlacklist(context)

        // 1. Generic extras and blacklist checks
        if (GENERIC_EXTRAS_BLACKLIST.contains(lower) || blacklist.contains(lower) || clean.length < 2) {
            return CandidateScore(
                isSignificant = false,
                role = "Figüran",
                confidence = 15,
                difficultyMultiplier = 1.0,
                reason = "Genel figüran veya kara liste eşleşmesi."
            )
        }

        var confidence = 40
        var role = titleRole ?: inferredRole

        // 2. Promotion: Second appearance gives immediate high confidence
        if (appearanceCount >= 2) {
            confidence = maxOf(confidence, 85)
        }

        // 3. Dialogue & Action cues: "Kerem dedi", "Kerem sordu"
        val hasSpeechCue = DIALOGUE_ACTION_VERBS.any { verb ->
            sceneContext.contains("$clean $verb", ignoreCase = true) ||
            sceneContext.contains("\" $clean", ignoreCase = true)
        }
        if (hasSpeechCue) {
            confidence += 30
        }

        // 4. Title / Honorific presence
        if (titleRole != null) {
            confidence += 20
        }

        // 5. Named entity capitalization check
        val isProperCase = clean.firstOrNull()?.isUpperCase() == true && clean.length in 3..25
        if (isProperCase) {
            confidence += 15
        }

        confidence = confidence.coerceIn(0, 100)

        // Multiplier evaluation based on role & context
        val mult = when {
            role.contains("Doktor", ignoreCase = true) || role.contains("Komutan", ignoreCase = true) || role.contains("Müdür", ignoreCase = true) -> 0.75
            role.contains("Düşman", ignoreCase = true) || role.contains("Rakip", ignoreCase = true) -> 0.50
            role.contains("Arkadaş", ignoreCase = true) || role.contains("Dost", ignoreCase = true) -> 1.15
            else -> 1.0
        }

        val isSignificant = confidence >= 50
        return CandidateScore(
            isSignificant = isSignificant,
            role = if (role.isNotBlank() && role != "Yan Karakter") role else "Yan Karakter",
            confidence = confidence,
            difficultyMultiplier = mult,
            reason = if (hasSpeechCue) "Aktif diyalog sahibi karakter" else "Sahnede adı geçen karakter"
        )
    }

    // İsim hem isim hem sıradan kelime olabilir; bunlar YALNIZCA güçlü sinyal ile geçer
    val AMBIGUOUS_PROPER_NAMES = setOf(
        "deniz", "yıldız", "ay", "güneş", "ateş", "ışık", "kaya", "su", "umut", "gül",
        "bulut", "duman", "çiçek", "rüya", "ece", "nur", "nar", "dağ"
    )

    enum class SignalStrength { STRONG, WEAK }

    data class CandidateHit(
        val rawName: String,
        val canonicalName: String,
        val role: String,
        val signalStrength: SignalStrength,
        val cueSnippet: String
    )

    private val NAME_PATTERN = """[\p{Lu}\p{Lt}][\p{L}\p{M}'-]{1,29}"""

    /**
     * Detects character name candidates from combined text with signal strength classification.
     * Handles Unicode names, Turkish suffixes, newChars JSON, and double-count prevention.
     */
    fun detectCandidates(
        combinedText: String,
        blacklist: Set<String>,
        newCharsJson: String = ""
    ): List<CandidateHit> {
        val candidates = mutableMapOf<String, CandidateHit>() // canonical → best hit

        fun addCandidate(rawName: String, role: String, strength: SignalStrength, cue: String) {
            val canonical = extractCanonicalNameAndRole(rawName).first
            if (canonical.isBlank() || canonical.length < 2) return
            val lower = canonical.lowercase(Locale("tr", "TR"))
            // Ambiguous names only pass on STRONG signal
            if (lower in AMBIGUOUS_PROPER_NAMES && strength == SignalStrength.WEAK) return
            // Normal blacklist: always block (ambiguous names are also in blacklist in JSON,
            // but AMBIGUOUS_PROPER_NAMES + STRONG overrides)
            if (lower in blacklist && lower !in AMBIGUOUS_PROPER_NAMES) return
            if (lower in blacklist && lower in AMBIGUOUS_PROPER_NAMES && strength == SignalStrength.WEAK) return
            val existing = candidates[lower]
            if (existing == null || (existing.signalStrength == SignalStrength.WEAK && strength == SignalStrength.STRONG)) {
                candidates[lower] = CandidateHit(rawName, canonical, role, strength, cue)
            }
        }

        // Strip OOC parentheses and quoted dialogue for main scan (but keep explicit tags)
        val stripped = combinedText
            .replace(Regex(""""[^"]{0,400}""""), " ")
            .replace(Regex("""«[^»]{0,400}»"""), " ")
            .replace(Regex("""\([^)]{0,200}\)"""), " ")
            .replace(Regex("""\[[^\]]{0,200}\]"""), " ")

        // ── 0. newChars JSON (STRONG) ────────────────────────────────────────────
        if (newCharsJson.isNotBlank()) {
            try {
                val arr = JSONArray(newCharsJson)
                val limit = minOf(arr.length(), 5)
                for (i in 0 until limit) {
                    val item = arr.opt(i)
                    val name: String?
                    val role: String
                    when (item) {
                        is JSONObject -> {
                            name = (item.optString("n").ifBlank { item.optString("name") }).ifBlank { null }
                            role = item.optString("r").ifBlank { item.optString("role").ifBlank { "Yan Karakter" } }
                        }
                        is String -> { name = item.ifBlank { null }; role = "Yan Karakter" }
                        else -> continue
                    }
                    if (!name.isNullOrBlank()) addCandidate(name, role, SignalStrength.STRONG, "newChars:$name")
                }
            } catch (_: Exception) {}
        }

        // ── 1. Açık etiketler [CHARACTER_EMOTION: Ad | Rol] (STRONG) ────────────
        val tagRegex = Regex("""(?is)\[\[?(?:CHARACTER_EMOTION|CAST_MEMBER|CHARACTER):\s*([^\]\n|,]+)(?:[|\s,]([^\]\n]*))?]]?""")
        tagRegex.findAll(combinedText).forEach { m ->
            val name = m.groupValues[1].trim()
            val role = m.groupValues[2].trim().ifBlank { "Yan Karakter" }
            if (name.isNotBlank()) addCandidate(name, role, SignalStrength.STRONG, m.value.take(120))
        }

        // ── 2. Unvan + İsim  "Doktor Kerem" (STRONG) ───────────────────────────
        val titlePattern = Regex(
            """\b(Bay|Bayan|Doktor|Prof|Profesör|Yüzbaşı|Kaptan|Amiral|Komutan|Lord|Prens|Prenses|Kral|Kraliçe|Usta|Hoca|Savaşçı|Büyücü|Şövalye|Mimar|Aziz|Üstat|Gözcü|Dedektif|Ajan|Müfettiş|Savcı|Hâkim|Teğmen|Binbaşı|Albay|General|Sultan|Paşa|Dük|Kont|Baron|Barones|Rahip|Cadı|Gölge|Muhafız|Hizmetkar|Tüccar|Eczacı|Yargıç|Bakan|Başkan|Reis|Çavuş|Efe|Kethüda|Seyis)\s+($NAME_PATTERN)\b"""
        )
        titlePattern.findAll(stripped).forEach { m ->
            addCandidate(m.groupValues[2], m.groupValues[1], SignalStrength.STRONG, m.value.take(120))
        }

        // ── 3. İsim + Hitap  "Kerem Bey" (STRONG) ──────────────────────────────
        val honorificPattern = Regex(
            """\b($NAME_PATTERN)\s+(Bey|Hanım|Efendi|Ağa|Usta|Abi|Abla|Komutan|Kaptan|Doktor|Hazretleri|Paşa|Hoca|Teyze|Amca|Dayı|Yenge|Dede|Peder)\b"""
        )
        honorificPattern.findAll(stripped).forEach { m ->
            val name = m.groupValues[1].trim()
            addCandidate(name, m.groupValues[2], SignalStrength.STRONG, m.value.take(120))
        }

        // ── 4. Açık tanıtma  "X adında / X adlı" (STRONG) ─────────────────────
        val introPattern = Regex(
            """\b($NAME_PATTERN)\s+(adında|isminde|adlı|denen|diye\s+çağrılan|isimli|adındaki|ismindeki|arkadaşım|doktorum|yardımcım|sekreterim|muhafızım)\b"""
        )
        introPattern.findAll(stripped).forEach { m ->
            addCandidate(m.groupValues[1], "Yan Karakter", SignalStrength.STRONG, m.value.take(120))
        }

        // ── 5. Konuşma atfı (STRONG) ─────────────────────────────────────────────
        // "..." dedi Kerem  |  Kerem, "..."  |  Kerem dedi  |  "..." dedi[,] Kerem
        val speechVerbs = """(?:dedi|sordu|fısıldadı|bağırdı|güldü|mırıldandı|haykırdı|söyledi|yanıtladı|ekledi|çıkıştı|karşılık\s+verdi|seslendi|atıldı|cevap\s+verdi)"""
        val speechPost = Regex("""(?:"[^"]{3,300}"|«[^»]{3,300}»)\s*$speechVerbs\s+($NAME_PATTERN)\b""")
        val speechPre = Regex("""\b($NAME_PATTERN)\s*,\s*(?:"[^"]{3,300}"|«[^»]{3,300}»)""")
        val speechDirect = Regex("""\b($NAME_PATTERN)\s+$speechVerbs\b""")
        speechPost.findAll(combinedText).forEach { m -> addCandidate(m.groupValues[1], "Sahnede Konuşan Karakter", SignalStrength.STRONG, m.value.take(120)) }
        speechPre.findAll(combinedText).forEach { m -> addCandidate(m.groupValues[1], "Sahnede Konuşan Karakter", SignalStrength.STRONG, m.value.take(120)) }
        speechDirect.findAll(stripped).forEach { m -> addCandidate(m.groupValues[1], "Sahnede Konuşan Karakter", SignalStrength.STRONG, m.value.take(120)) }

        // ── 6. Eylem kalıpları (STRONG) ──────────────────────────────────────────
        val actionVerbs = """(?:içeri\s+girdi|kapıyı\s+çaldı|masaya\s+oturdu|elini\s+uzattı|gülümsedi|arkasını\s+döndü|ayağa\s+kalktı|bize\s+katıldı|yaklaştı|konuşmaya\s+başladı|yanımıza\s+geldi|bize\s+baktı|odaya\s+girdi|kapıdan\s+çıktı)"""
        val actionPattern = Regex("""\b($NAME_PATTERN)\s+$actionVerbs""")
        actionPattern.findAll(stripped).forEach { m -> addCandidate(m.groupValues[1], "Sahnede Eyleme Geçen Karakter", SignalStrength.STRONG, m.value.take(120)) }

        // ── 7. Apostroflu özel isim (WEAK) ───────────────────────────────────────
        // "Ayla'nın", "kerem'e" — apostrof özel isim göstergesi
        val apostrophePattern = Regex("""([\p{L}]{2,25})[''`](${TURKISH_INFLECTION_SUFFIXES.joinToString("|") { Regex.escape(it) }})\b""")
        apostrophePattern.findAll(combinedText).forEach { m ->
            val base = m.groupValues[1]
            val capitalized = base.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("tr", "TR")) else it.toString() }
            addCandidate(capitalized, "Yan Karakter", SignalStrength.WEAK, m.value.take(80))
        }

        return candidates.values.toList()
    }

    /**
     * Word-boundary aware name mention check, with suffix/alias tolerance.
     * Prevents "Ali" matching "Alışveriş".
     */
    fun isNameMentionedInText(name: String, text: String): Boolean {
        if (name.isBlank() || text.isBlank()) return false
        val lowerText = text.lowercase(Locale("tr", "TR"))
        val nameLower = name.lowercase(Locale("tr", "TR"))
        val stem = stemTurkishProperName(name).lowercase(Locale("tr", "TR"))

        // Word boundary match for base name and stem
        val candidates = setOfNotNull(
            nameLower.takeIf { it.length >= 2 },
            stem.takeIf { it.length >= 2 && it != nameLower }
        )
        for (c in candidates) {
            if (Regex("""\b${Regex.escape(c)}""").containsMatchIn(lowerText)) return true
        }

        // Also match apostrophe-inflected forms: "Kerem'e", "Kerem'in" etc.
        if (Regex("""\b${Regex.escape(nameLower)}[''`]""").containsMatchIn(lowerText)) return true
        if (stem != nameLower && Regex("""\b${Regex.escape(stem)}[''`]""").containsMatchIn(lowerText)) return true
        return false
    }

    /**
     * Token-subset alias check: "Kerem" ⊂ "Kerem Aydın" → same person.
     * Returns true only if match is unambiguous (single direction, symmetric).
     */
    fun isLikelyAlias(candidate: String, existingName: String): Boolean {
        val c = stemTurkishProperName(candidate).lowercase(Locale("tr", "TR"))
        val e = stemTurkishProperName(existingName).lowercase(Locale("tr", "TR"))
        if (c == e) return true
        val cTokens = c.split(Regex("""\s+""")).filter { it.length >= 2 }.toSet()
        val eTokens = e.split(Regex("""\s+""")).filter { it.length >= 2 }.toSet()
        // Subset in one direction only (Kerem ⊂ Kerem Aydın)
        return (cTokens.isNotEmpty() && eTokens.containsAll(cTokens)) ||
               (eTokens.isNotEmpty() && cTokens.containsAll(eTokens))
    }

    /**
     * Filters cast members to only include those active in the current scene/window (last 3 messages).
     * Uses word-boundary matching to prevent "Ali" ↔ "Alışveriş" false positives.
     * importanceScore >= 90: always included (core cast).
     */
    fun filterActiveInSceneCastMembers(
        allCastMembers: List<com.example.data.local.CastMemberEntity>,
        recentMessagesText: String
    ): List<com.example.data.local.CastMemberEntity> {
        if (allCastMembers.isEmpty()) return emptyList()

        return allCastMembers.filter { member ->
            member.importanceScore >= 90 ||
            isNameMentionedInText(member.name, recentMessagesText)
        }
    }
}
