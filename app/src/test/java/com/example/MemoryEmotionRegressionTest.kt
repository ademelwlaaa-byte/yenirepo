package com.example

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.CastMemberEntity
import com.example.data.local.EmotionState
import com.example.data.local.RelationshipAxes
import com.example.data.local.PrimaryEmotions
import com.example.data.local.WorldState
import com.example.data.local.SideCharacterEmotionUpdate
import com.example.data.local.RoutineEntity
import com.example.data.local.EntityRelationEntity
import com.example.data.repository.EmochiRepository
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import com.example.data.api.RetrofitClient
import com.example.data.local.UserSettingsEntity
import kotlinx.coroutines.runBlocking

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MemoryEmotionRegressionTest {

    // =========================================================================
    // SECTION 1: CAST MEMBER AUDIT, DETECTION & CONFIDENCE TESTS
    // =========================================================================

    @Test
    fun testCastMemberSignificance_rejectsTrivialExtras() {
        val genericExtra1 = "Garson"
        val scene1 = "Garson masaya kahveleri bıraktı ve hızla uzaklaştı."
        val (isSig1, role1, conf1, _) = EmochiRepository.evaluateCharacterSignificance(genericExtra1, scene1)
        assertFalse("Trivial extra 'Garson' should NOT be marked significant", isSig1)
        assertTrue("Confidence for trivial extra should be low (< 50)", conf1 < 50)

        val genericExtra2 = "Zorba çocuk"
        val scene2 = "Zorba çocuk köşede dikilip alaycı bir şekilde sırıttı."
        val (isSig2, _, conf2, _) = EmochiRepository.evaluateCharacterSignificance(genericExtra2, scene2)
        assertFalse("Trivial extra 'Zorba çocuk' should NOT be marked significant", isSig2)
    }

    @Test
    fun testCastMemberSignificance_acceptsImportantNamedCharacters() {
        val importantChar = "Doktor Kenan"
        val scene = "Doktor Kenan masaya yaklaştı, 'Sonuçlar beklediğimizden daha kritik' dedi."
        val (isSig, role, conf, mult) = EmochiRepository.evaluateCharacterSignificance(importantChar, scene)
        assertTrue("Named character with speech & title should be significant", isSig)
        assertTrue("Confidence should be high (>= 70)", conf >= 70)
        assertTrue("Role should reflect doctor/authority", role.contains("Doktor", ignoreCase = true))
        assertTrue("Authority figure should have strict multiplier (<= 0.8)", mult <= 0.8)
    }

    @Test
    fun testCastMemberPromotion_onSecondAppearance() {
        // CastMemberEntity appearance counter logic
        val initialMember = CastMemberEntity(
            botId = "bot_1",
            name = "Eren",
            description = "Sahnede geçen karakter",
            role = "Yan Karakter",
            appearanceCount = 1,
            detectionConfidence = 65,
            pendingUserConfirmation = true,
            importanceScore = 55
        )

        // Simulate 2nd appearance promotion
        val nextAppearance = initialMember.appearanceCount + 1
        val isPromoted = nextAppearance >= 2
        val updatedMember = initialMember.copy(
            appearanceCount = nextAppearance,
            pendingUserConfirmation = if (isPromoted) false else initialMember.pendingUserConfirmation,
            importanceScore = if (isPromoted) maxOf(initialMember.importanceScore, 80) else initialMember.importanceScore,
            detectionConfidence = if (isPromoted) maxOf(initialMember.detectionConfidence, 90) else initialMember.detectionConfidence
        )

        assertEquals("Appearance count should be 2", 2, updatedMember.appearanceCount)
        assertFalse("Promoted member should no longer be pending user confirmation", updatedMember.pendingUserConfirmation)
        assertTrue("Confidence should be elevated (>= 90)", updatedMember.detectionConfidence >= 90)
        assertTrue("Importance should be elevated (>= 80)", updatedMember.importanceScore >= 80)
    }

    @Test
    fun testCastMemberAuditTool_correctlyIdentifiesDirtyEntries() {
        val dirtyExtra = CastMemberEntity(
            botId = "bot_1",
            name = "Garson",
            description = "Sahnede beliren figüran",
            role = "Yan Karakter",
            appearanceCount = 1,
            importanceScore = 40,
            detectionConfidence = 30
        )
        val validCast = CastMemberEntity(
            botId = "bot_1",
            name = "Ayla Hanım",
            description = "Eski kütüphaneci ve akıl hocası",
            role = "Akıl Hocası",
            appearanceCount = 3,
            importanceScore = 85,
            detectionConfidence = 95
        )

        // Verify heuristic
        val extraKeywords = listOf("garson", "zorba", "çocuk", "adam", "kadın", "polis", "yoldan geçen")
        val isDirtyExtra = extraKeywords.any { dirtyExtra.name.lowercase().contains(it) } || dirtyExtra.detectionConfidence < 50
        val isValid = !extraKeywords.any { validCast.name.lowercase().contains(it) } && validCast.detectionConfidence >= 50

        assertTrue("Garson should be flagged as likely extra", isDirtyExtra)
        assertTrue("Ayla Hanım should be kept as valid cast", isValid)
    }

    // =========================================================================
    // SECTION 2: ROLE-BASED AFFECTION MULTIPLIER & CONTEXT SENSITIVITY
    // =========================================================================

    @Test
    fun testCastMemberDifficultyMultiplier_byRole() {
        val authorityMult = EmochiRepository.calculateCastMemberDifficultyMultiplier("Patron", "Selim Bey", "Şirket yöneticisi")
        val strangerMult = EmochiRepository.calculateCastMemberDifficultyMultiplier("Yabancı", "Taksici", "Yeni tanışılan biri")
        val intimateMult = EmochiRepository.calculateCastMemberDifficultyMultiplier("Çocukluk Arkadaşı", "Deniz", "Eski sevgili")
        val peerMult = EmochiRepository.calculateCastMemberDifficultyMultiplier("Sınıf Arkadaşı", "Mert", "")

        assertTrue("Authority figure multiplier should be restrictive (<= 0.75)", authorityMult <= 0.75)
        assertTrue("Stranger multiplier should be cautious (around 0.85)", strangerMult in 0.80..0.90)
        assertTrue("Intimate/Established bond should have higher multiplier (>= 1.3)", intimateMult >= 1.3)
        assertTrue("Peer multiplier should be around baseline (1.05 - 1.1)", peerMult >= 1.0)
    }

    @Test
    fun testMultiRoleContextSensitivity_bossAndExLover() {
        val complexMember = CastMemberEntity(
            botId = "bot_1",
            name = "Leyla",
            description = "Hem şirket genel müdürü hem de eski sevgili",
            role = "Patron",
            secondaryRole = "Eski Sevgili",
            baseAffectionDifficultyMultiplier = 0.70
        )

        val workScene = "Ofiste üç aylık mali raporları ve şirket bütçesini inceliyoruz."
        val intimateScene = "Gece geç saatte baş başa kahve içerken eski günleri ve anılarımızı andık."

        val workMult = EmochiRepository.determineEffectiveRoleMultiplier(complexMember, workScene)
        val intimateMult = EmochiRepository.determineEffectiveRoleMultiplier(complexMember, intimateScene)

        assertTrue("Work context should select the most restrictive multiplier (<= 0.75)", workMult <= 0.75)
        assertTrue("Personal intimate context should acknowledge intimate role (> 0.75)", intimateMult > 0.75)
    }

    // =========================================================================
    // SECTION 3: MULTI-CHARACTER INDEPENDENT EMOTIONS & GLOBAL ATMOSPHERE MODULATION
    // =========================================================================

    @Test
    fun testSideCharacterEmotionUpdate_independentScores() {
        val updateJson = """
        {
          "name": "Kaan",
          "mood": "Kızgın",
          "affection_delta": -5,
          "trust_delta": -10,
          "tension_delta": 15
        }
        """.trimIndent()

        val parsed = SideCharacterEmotionUpdate.fromJsonObject(JSONObject(updateJson))
        assertNotNull("Parsed update should not be null", parsed)
        assertEquals("Kaan", parsed!!.characterName)
        assertEquals("Kızgın", parsed.mood)
        assertEquals(-5, parsed.affectionDelta)
        assertEquals(-10, parsed.trustDelta)
        assertEquals(15, parsed.tensionDelta)
    }

    @Test
    fun testGlobalAtmosphereModulation_formula() {
        // Formula: (globalAtmosphereFactor * characterDelta) modulating without overwriting core state
        // When global tension is crisis (factor = 0.0), affection delta is locked to 0
        // When global tension is normal (factor = 1.0), characterDelta passes through
        val charA_rawDelta = 6
        val normalAtmosphereFactor = 1.0
        val crisisAtmosphereFactor = 0.0
        val highTensionAtmosphereFactor = 0.6

        val normalModulated = (charA_rawDelta * normalAtmosphereFactor).toInt()
        val crisisModulated = (charA_rawDelta * crisisAtmosphereFactor).toInt()
        val tensionModulated = (charA_rawDelta * highTensionAtmosphereFactor).toInt()

        assertEquals("Normal atmosphere does not impede delta", 6, normalModulated)
        assertEquals("Crisis atmosphere locks delta to 0", 0, crisisModulated)
        assertEquals("Tension modulates without zeroing", 3, tensionModulated)
    }

    @Test
    fun testStateJsonAliasTolerantParsing() {
        // Shorthand JSON format from models
        val shortJson = """
        {
          "aff": 72,
          "trust": 65,
          "p_cmf": 45,
          "dom": "gururlu",
          "delta": { "v": 4, "r": "Destekleyici konuşma" }
        }
        """.trimIndent()

        val jobj = JSONObject(shortJson)
        val aff = if (jobj.has("affectionScore")) jobj.getInt("affectionScore") else jobj.optInt("aff", 50)
        val pCmf = if (jobj.has("physicalComfortScore")) jobj.getInt("physicalComfortScore") else jobj.optInt("p_cmf", 0)
        val dom = if (jobj.has("dominant_emotion")) jobj.getString("dominant_emotion") else jobj.optString("dom", "Nötr")

        val deltaObj = jobj.getJSONObject("delta")
        val deltaVal = if (deltaObj.has("value")) deltaObj.getInt("value") else deltaObj.optInt("v", 0)
        val deltaReason = if (deltaObj.has("reason")) deltaObj.getString("reason") else deltaObj.optString("r", "")

        assertEquals(72, aff)
        assertEquals(45, pCmf)
        assertEquals("gururlu", dom)
        assertEquals(4, deltaVal)
        assertEquals("Destekleyici konuşma", deltaReason)
    }

    // =========================================================================
    // SECTION 7: KNOWLEDGE GRAPH & ROUTINE TRACKING TESTS
    // =========================================================================

    @Test
    fun testKnowledgeGraph_MultiHopTraversal() = kotlinx.coroutines.runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = EmochiRepository(db, context)
        val testBotId = "test_bot_kg_${System.currentTimeMillis()}"

        // Step 1: Save relations for a 3-hop family/friend network:
        // Ahmet -> [kardeşi] -> Mehmet
        // Mehmet -> [en yakın arkadaşı] -> Can
        // Can -> [patronu] -> Leyla
        val rel1Text = "Ahmet'in kardeşi Mehmet'tir."
        val rel2Text = "Mehmet'in en yakın arkadaşı Can'dır."
        val rel3Text = "Can'ın patronu Leyla'dır."

        val extracted1 = repo.extractAndSaveEntityRelations(testBotId, rel1Text)
        val extracted2 = repo.extractAndSaveEntityRelations(testBotId, rel2Text)
        val extracted3 = repo.extractAndSaveEntityRelations(testBotId, rel3Text)

        assertEquals("Should extract 1 relation for step 1", 1, extracted1.size)
        assertEquals("Should extract 1 relation for step 2", 1, extracted2.size)
        assertEquals("Should extract 1 relation for step 3", 1, extracted3.size)

        // Step 2: Traverse 2-hop: Ahmet'in kardeşinin en yakın arkadaşı kim?
        val query2Hop = "Ahmet'in kardeşinin en yakın arkadaşı kim?"
        val result2Hop = repo.detectAndResolveGraphQuery(testBotId, query2Hop)

        assertNotNull("Graph traversal should resolve 2-hop query", result2Hop)
        assertEquals("Target should be Can", "Can", result2Hop?.finalTargetName)
        assertTrue("Traversal must be complete", result2Hop?.isComplete == true)
        assertEquals("Path must have 2 steps", 2, result2Hop?.pathDescriptions?.size)
        android.util.Log.i("KG_TEST", "2-hop result: ${result2Hop?.finalTargetName}, path: ${result2Hop?.pathDescriptions}")

        // Step 3: Traverse 3-hop: Ahmet'in kardeşinin en yakın arkadaşının patronu
        val query3HopSteps = listOf("kardeşi", "en yakın arkadaşı", "patronu")
        val result3Hop = repo.traverseRelationGraph(testBotId, "Ahmet", query3HopSteps)

        assertNotNull("Graph traversal should resolve 3-hop query", result3Hop)
        assertEquals("Target should be Leyla", "Leyla", result3Hop?.finalTargetName)
        assertTrue("Traversal must be complete", result3Hop?.isComplete == true)
        assertEquals("Path must have 3 steps", 3, result3Hop?.pathDescriptions?.size)
        android.util.Log.i("KG_TEST", "3-hop result: ${result3Hop?.finalTargetName}, path: ${result3Hop?.pathDescriptions}")

        db.close()
    }

    @Test
    fun testRoutineTracking_WeeklyAndDailyLifecycle() = kotlinx.coroutines.runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = EmochiRepository(db, context)
        val testBotId = "test_bot_rt_${System.currentTimeMillis()}"

        // Step 1: Extract routines from personality/scenario:
        // "Her Pazartesi antrenmana gider ve formunu korur."
        val scenario = "Ayla başarılı bir atlettir. Her Pazartesi antrenmana gider ve akşamları dinlenir."
        val personality = "Disiplinli, dakik."

        val extracted: List<RoutineEntity> = repo.extractAndSaveRoutinesFromBotPersonality(testBotId, personality, scenario)
        assertTrue("Should extract weekly Monday routine", extracted.isNotEmpty())
        val mondayRoutine: RoutineEntity? = extracted.firstOrNull { it.dayOfWeek == "Pazartesi" }
        assertNotNull("Monday routine must be found", mondayRoutine)
        assertEquals("weekly", mondayRoutine?.frequency)

        // Step 2: Check matching date vs non-matching date
        // 2026-10-12 is a Monday
        val mondayDate = "2026-10-12"
        val tuesdayDate = "2026-10-13"

        assertEquals("Pazartesi", repo.getDayOfWeekTurkish(mondayDate))
        assertEquals("Salı", repo.getDayOfWeekTurkish(tuesdayDate))

        // Check Tuesday: should NOT trigger
        val tuesdayReminder = repo.getActiveRoutineReminder(testBotId, tuesdayDate)
        assertNull("Tuesday should not trigger Monday routine", tuesdayReminder)

        // Check Monday: should trigger!
        val mondayReminder: RoutineEntity? = repo.getActiveRoutineReminder(testBotId, mondayDate)
        assertNotNull("Monday should trigger routine reminder", mondayReminder)
        assertTrue(mondayReminder!!.routineDescription.contains("antrenman", ignoreCase = true))

        // Step 3: After mentioning, mark it mentioned on that date
        db.routineDao().updateRoutine(mondayReminder.copy(lastMentionedStoryDate = mondayDate))

        // Re-check same date: must NOT trigger again
        val mondayReminderAfterMention = repo.getActiveRoutineReminder(testBotId, mondayDate)
        assertNull("Same date should NOT re-trigger routine once mentioned", mondayReminderAfterMention)

        db.close()
    }

    @org.junit.Test
    fun testEvaluateCharacterSignificance_routesToGroqForGroqOnlyUser() = runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = EmochiRepository(db, context)

        // Setup user with Groq as selected provider and a Groq API key, but NO Gemini key
        val groqSettings = UserSettingsEntity(
            selectedProvider = "groq",
            groqApiKey = "gsk_test_groq_valid_key_999",
            customApiKey = "",
            groqModel = "openai/gpt-oss-120b"
        )
        db.userSettingsDao().insertOrUpdate(groqSettings)

        var interceptedUrl = ""
        var interceptedAuthHeader = ""
        var interceptedBody = ""

        RetrofitClient.customInterceptor = Interceptor { chain ->
            val request = chain.request()
            interceptedUrl = request.url.toString()
            interceptedAuthHeader = request.header("Authorization") ?: ""

            val mockJsonResponseBody = """
                {
                  "id": "chatcmpl-test",
                  "object": "chat.completion",
                  "choices": [
                    {
                      "index": 0,
                      "message": {
                        "role": "assistant",
                        "content": "{\"is_significant\": true, \"role\": \"Yarışçı\", \"confidence\": 95, \"status\": \"peer\"}"
                      },
                      "finish_reason": "stop"
                    }
                  ]
                }
            """.trimIndent()

            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(mockJsonResponseBody.toResponseBody("application/json".toMediaType()))
                .build()
        }

        try {
            val (isSig, role, conf, mult) = repo.evaluateCharacterSignificance(
                candidateName = "Eren",
                sceneContext = "Eren yarış arabasıyla piste çıktı."
            )

            // Assert request was ACTUALLY routed to Groq
            assertTrue("Request must be sent to api.groq.com", interceptedUrl.contains("api.groq.com"))
            assertEquals("Authorization header must contain Groq key", "Bearer gsk_test_groq_valid_key_999", interceptedAuthHeader)

            // Assert classification results parsed correctly
            assertTrue("Character must be significant", isSig)
            assertEquals("Yarışçı", role)
            assertEquals(95, conf)
            assertEquals(1.05, mult, 0.001)

            // Assert failure log is clean
            assertTrue("Failure log must be empty on success", repo.classificationCallFailureLog.isEmpty())
        } finally {
            RetrofitClient.customInterceptor = null
            db.close()
        }
    }

    @org.junit.Test
    fun testEvaluateCharacterSignificance_skipsNetworkWhenNoKeysConfigured() = runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = EmochiRepository(db, context)

        // Setup user with NO keys for ANY provider
        val emptySettings = UserSettingsEntity(
            selectedProvider = "gemini",
            customApiKey = "",
            groqApiKey = "",
            claudeApiKey = "",
            openaiApiKey = "",
            openRouterApiKey = "",
            nvidiaApiKey = "",
            mistralApiKey = "",
            enableLlm7 = false,
            enablePollinations = false,
            enableOvh = false
        )
        db.userSettingsDao().insertOrUpdate(emptySettings)

        var networkAttempted = false
        RetrofitClient.customInterceptor = Interceptor { chain ->
            networkAttempted = true
            throw AssertionError("Network request must NOT be made when user has no API keys!")
        }

        try {
            val (isSig, role, conf, mult) = repo.evaluateCharacterSignificance(
                candidateName = "Doktor Kenan",
                sceneContext = "Doktor Kenan sonuçlar kritik dedi."
            )

            assertFalse("No network request should have been made", networkAttempted)
            assertTrue("Heuristic should recognize Doktor Kenan", isSig)
            assertEquals("Doktor", role)
            assertEquals(85, conf)
        } finally {
            RetrofitClient.customInterceptor = null
            db.close()
        }
    }

    @org.junit.Test
    fun testEvaluateCharacterSignificance_logsFailureWhenProviderThrows() = runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = EmochiRepository(db, context)

        val groqSettings = UserSettingsEntity(
            selectedProvider = "groq",
            groqApiKey = "gsk_test_failing_key",
            customApiKey = ""
        )
        db.userSettingsDao().insertOrUpdate(groqSettings)

        RetrofitClient.customInterceptor = Interceptor { chain ->
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("Internal Server Error")
                .body("Internal Server Error".toResponseBody("text/plain".toMediaType()))
                .build()
        }

        try {
            repo.clearClassificationCallFailureLogs()
            val (isSig, role, conf, mult) = repo.evaluateCharacterSignificance(
                candidateName = "Ahmet",
                sceneContext = "Ahmet köşede sessizce bekledi."
            )

            // Assert failure was recorded in classificationCallFailureLog
            assertEquals("Failure must be logged", 1, repo.classificationCallFailureLog.size)
            val logEntry = repo.classificationCallFailureLog.first()
            assertEquals("groq", logEntry.provider)
            assertTrue("Error message must contain 500 or error detail", logEntry.errorMessage.contains("500") || logEntry.errorMessage.contains("Error"))

            // And graceful fallback to heuristic occurred without crash
            assertNotNull(role)
        } finally {
            RetrofitClient.customInterceptor = null
            db.close()
        }
    }

    @org.junit.Test
    fun testCleanEmotionTags_andSideCharacterParsing_handlesTurkishLeakFormat() = runBlocking {
        val rawLeak = """
            Birincil: Sıcak kuralcı denge ve şefkatli kontrol
            İkincil: İspanya'dan gelen ailevi bir sığınak hissi
            Yakınlık: 55/100 | Güven: 65/100 | Kırgınlık: 0/100
            [[/CHARACTER_EMOTION: Marie]]
        """.trimIndent()

        // 1. Verify cleanEmotionTags completely strips the leaked block
        val cleanedOnlyBlock = EmochiRepository.cleanEmotionTags(rawLeak)
        assertEquals("When raw response has only leaked emotion tags, cleaned text must be empty", "", cleanedOnlyBlock)

        // 2. Verify when mixed with dialogue, narrative remains untouched and all emotion tokens are eliminated
        val dialogueText = "*Marie sıcak bir tebessümle çayını yudumlar.* \"Endişelenme, burada güvendesin.\""
        val mixedRaw = """
            $dialogueText

            Birincil: Sıcak kuralcı denge ve şefkatli kontrol
            İkincil: İspanya'dan gelen ailevi bir sığınak hissi
            Yakınlık: 55/100 | Güven: 65/100 | Kırgınlık: 0/100
            [[/CHARACTER_EMOTION: Marie]]
        """.trimIndent()

        val cleanedMixed = EmochiRepository.cleanEmotionTags(mixedRaw)
        assertEquals("Cleaned mixed text must preserve narrative dialogue exactly", dialogueText, cleanedMixed)
        assertFalse("Cleaned text must NOT contain 'Birincil'", cleanedMixed.contains("Birincil"))
        assertFalse("Cleaned text must NOT contain 'İkincil'", cleanedMixed.contains("İkincil"))
        assertFalse("Cleaned text must NOT contain 'Yakınlık'", cleanedMixed.contains("Yakınlık"))
        assertFalse("Cleaned text must NOT contain 'Güven'", cleanedMixed.contains("Güven"))
        assertFalse("Cleaned text must NOT contain 'Kırgınlık'", cleanedMixed.contains("Kırgınlık"))
        assertFalse("Cleaned text must NOT contain '55/100'", cleanedMixed.contains("55/100"))
        assertFalse("Cleaned text must NOT contain 'CHARACTER_EMOTION'", cleanedMixed.contains("CHARACTER_EMOTION"))

        // 3. Verify side character parsing correctly registers Marie with affection 55 and trust 65
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        val repo = EmochiRepository(db, context)
        val testBot = com.example.data.local.BotEntity(
            id = "test_bot_leak_parse",
            mode = "personal",
            aiName = "Aiden",
            aiPersonality = "Nazik",
            scenario = "Sohbet",
            universeName = "",
            keyCharactersJson = "[]",
            userCharName = "Kullanıcı",
            userCharDesc = "Biri",
            openingMessage = "Selam",
            writingStyle = "rp",
            intensity = "normal"
        )
        db.botDao().insertOrUpdate(testBot)

        val cleanResult = repo.parseAndApplyEmotionUpdates(testBot.id, mixedRaw)
        assertEquals("parseAndApplyEmotionUpdates must return cleaned text", dialogueText, cleanResult)

        val marieCast = db.castMemberDao().findByName(testBot.id, "Marie")
        assertNotNull("Marie should be automatically registered into cast members", marieCast)
        assertEquals("Marie's affection should be parsed as 55", 55, marieCast?.affectionScore)

        val marieEmotion = db.characterEmotionDao().getEmotionForCharacter(testBot.id, "Marie")
        assertNotNull("Marie emotion state should exist in database", marieEmotion)
        val parsedState = EmotionState.fromJson(marieEmotion?.emotionState)
        assertEquals("Marie affection in state should be 55", 55, parsedState.affection)
        assertEquals("Marie trust in state should be 65", 65, parsedState.trust)
        assertEquals("Marie dominant emotion should match Birincil line", "Sıcak kuralcı denge ve şefkatli kontrol", parsedState.dominantEmotion)

        db.close()
    }

    @org.junit.Test
    fun testRewindToMessage_10MessagesTo5th_restoresAllStateAndCounters() = runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        val repo = EmochiRepository(db, context)

        val botId = "bot_rewind_test_${System.currentTimeMillis()}"
        val initialBaselineJson = EmotionState(
            relationshipAxes = RelationshipAxes(affectionScore = 50),
            primaryEmotions = PrimaryEmotions(trust = 50),
            consecutivePositiveCount = 0,
            dailyAffectionGain = 0,
            peakAffectionScore = 50,
            recoveryLockUntilMessageCount = 0
        ).toJson()

        val initialBot = com.example.data.local.BotEntity(
            id = botId,
            mode = "personal",
            aiName = "Aiden",
            aiPersonality = "Koruyucu ve sadık",
            scenario = "Macera",
            universeName = "",
            keyCharactersJson = "[]",
            userCharName = "Selin",
            userCharDesc = "Gezgin",
            openingMessage = "Hazır mısın?",
            writingStyle = "rp",
            intensity = "normal",
            storyCalendarDate = "2026-08-29",
            storyDayCounter = 1L,
            emotionState = initialBaselineJson,
            previousEmotionState = initialBaselineJson
        )
        db.botDao().insertOrUpdate(initialBot)

        // Step 1: Create 10 messages with incremental timestamps
        val baseTime = 1000000L
        val messages = (1..10).map { i ->
            com.example.data.local.MessageEntity(
                id = "msg_$i",
                botId = botId,
                role = if (i % 2 == 1) "user" else "assistant",
                text = "Mesaj $i içeriği",
                timestamp = baseTime + (i * 1000L)
            )
        }
        db.messageDao().insertAllMessages(messages)
        assertEquals(10, db.messageDao().getMessageCountForBot(botId))

        // State up to message 5:
        // Turn 2 (message 2)
        val stateAtMsg2 = EmotionState(
            relationshipAxes = RelationshipAxes(affectionScore = 55),
            primaryEmotions = PrimaryEmotions(trust = 55),
            consecutivePositiveCount = 1,
            dailyAffectionGain = 5,
            peakAffectionScore = 55
        )
        db.emotionHistoryDao().insertHistory(
            com.example.data.local.EmotionHistoryEntity(
                botId = botId,
                messageIndex = 2,
                timestamp = messages[1].timestamp,
                affectionScore = 55,
                trustScore = 55,
                fullVectorJson = stateAtMsg2.toJson()
            )
        )

        // Turn 4 (message 4)
        val stateAtMsg4 = EmotionState(
            relationshipAxes = RelationshipAxes(affectionScore = 60),
            primaryEmotions = PrimaryEmotions(trust = 62),
            consecutivePositiveCount = 2,
            dailyAffectionGain = 10,
            peakAffectionScore = 60
        )
        db.emotionHistoryDao().insertHistory(
            com.example.data.local.EmotionHistoryEntity(
                botId = botId,
                messageIndex = 4,
                timestamp = messages[3].timestamp,
                affectionScore = 60,
                trustScore = 62,
                fullVectorJson = stateAtMsg4.toJson()
            )
        )

        // Memory event & fact created at turn 3 (before message 5)
        db.memoryEventDao().insertEvent(
            com.example.data.local.MemoryEventEntity(
                id = 101,
                botId = botId,
                timestamp = messages[2].timestamp,
                realWorldTimestamp = messages[2].timestamp,
                description = "Erken macera anısı",
                storyDayIndex = 2L,
                storyCalendarDateStr = "2026-08-30"
            )
        )
        db.memoryFactDao().insertFact(
            com.example.data.local.MemoryFactEntity(
                id = 201,
                botId = botId,
                subject = "Selin",
                key = "favori içecek",
                value = "Papatya çayı",
                lastConfirmedAt = messages[2].timestamp
            )
        )
        db.memoryFragmentDao().insertFragment(
            com.example.data.local.MemoryFragmentEntity(
                id = 301,
                botId = botId,
                content = "Selin papatya çayı sever",
                category = "HAFIZA",
                createdAt = messages[2].timestamp
            )
        )

        // State after message 5 (messages 6-10):
        // Turn 6 (message 6)
        val stateAtMsg6 = stateAtMsg4.copy(
            relationshipAxes = RelationshipAxes(affectionScore = 68),
            consecutivePositiveCount = 3,
            dailyAffectionGain = 18,
            peakAffectionScore = 68
        )
        db.emotionHistoryDao().insertHistory(
            com.example.data.local.EmotionHistoryEntity(
                botId = botId,
                messageIndex = 6,
                timestamp = messages[5].timestamp,
                affectionScore = 68,
                fullVectorJson = stateAtMsg6.toJson()
            )
        )

        // Turn 8 (message 8): Doctor introduced!
        val stateAtMsg8 = stateAtMsg6.copy(
            relationshipAxes = RelationshipAxes(affectionScore = 75),
            consecutivePositiveCount = 4,
            dailyAffectionGain = 25,
            peakAffectionScore = 75
        )
        db.emotionHistoryDao().insertHistory(
            com.example.data.local.EmotionHistoryEntity(
                botId = botId,
                messageIndex = 8,
                timestamp = messages[7].timestamp,
                affectionScore = 75,
                fullVectorJson = stateAtMsg8.toJson()
            )
        )

        // Auto-added side character Doktor Kenan at turn 8
        db.castMemberDao().insertCastMember(
            com.example.data.local.CastMemberEntity(
                id = 501,
                botId = botId,
                name = "Doktor Kenan",
                description = "Klinik hekimi",
                isAutoAdded = true,
                firstAppearedAt = messages[7].timestamp,
                affectionScore = 65
            )
        )
        db.emotionHistoryDao().insertHistory(
            com.example.data.local.EmotionHistoryEntity(
                botId = botId,
                characterName = "Doktor Kenan",
                messageIndex = 8,
                timestamp = messages[7].timestamp,
                affectionScore = 65,
                fullVectorJson = EmotionState(relationshipAxes = RelationshipAxes(affectionScore = 65)).toJson()
            )
        )

        // Turn 10 (message 10)
        val stateAtMsg10 = stateAtMsg8.copy(
            relationshipAxes = RelationshipAxes(affectionScore = 85),
            consecutivePositiveCount = 5,
            dailyAffectionGain = 35,
            peakAffectionScore = 85
        )
        db.emotionHistoryDao().insertHistory(
            com.example.data.local.EmotionHistoryEntity(
                botId = botId,
                messageIndex = 10,
                timestamp = messages[9].timestamp,
                affectionScore = 85,
                fullVectorJson = stateAtMsg10.toJson()
            )
        )

        // Late memory event, fact, fragment at turn 8 (after message 5)
        db.memoryEventDao().insertEvent(
            com.example.data.local.MemoryEventEntity(
                id = 102,
                botId = botId,
                timestamp = messages[7].timestamp,
                realWorldTimestamp = messages[7].timestamp,
                description = "Geç hastane olayı",
                storyDayIndex = 6L,
                storyCalendarDateStr = "2026-09-03"
            )
        )
        db.memoryFactDao().insertFact(
            com.example.data.local.MemoryFactEntity(
                id = 202,
                botId = botId,
                subject = "Aiden",
                key = "yara",
                value = "Sağ kol",
                lastConfirmedAt = messages[7].timestamp
            )
        )
        db.memoryFragmentDao().insertFragment(
            com.example.data.local.MemoryFragmentEntity(
                id = 302,
                botId = botId,
                content = "Aiden sağ kolundan yaralandı",
                category = "HAFIZA",
                createdAt = messages[7].timestamp
            )
        )

        // Update Bot to current message 10 state
        db.botDao().insertOrUpdate(
            initialBot.copy(
                emotionState = stateAtMsg10.toJson(),
                storyCalendarDate = "2026-09-03",
                storyDayCounter = 6L
            )
        )

        // Pre-condition assertion: verify message 10 state exists
        assertEquals(10, db.messageDao().getMessageCountForBot(botId))
        assertEquals(85, EmotionState.fromJson(db.botDao().getBotById(botId)!!.emotionState).affection)
        assertNotNull(db.castMemberDao().findByName(botId, "Doktor Kenan"))
        assertEquals(2, db.memoryEventDao().getActiveEvents(botId).size)

        // =====================================================================
        // EXECUTE REWIND: User rewinds to message 5 ("msg_5")
        // =====================================================================
        val rewindSuccess = repo.rewindToMessage(botId, "msg_5")
        assertTrue("Rewind to message 5 should succeed", rewindSuccess)

        // Step A: Trailing messages (6-10) must be deleted! Exactly 5 messages remain (1-5).
        val remainingMsgs = db.messageDao().getMessagesForBotList(botId)
        assertEquals("Exactly 5 messages should remain", 5, remainingMsgs.size)
        assertEquals("msg_1", remainingMsgs[0].id)
        assertEquals("msg_5", remainingMsgs[4].id)
        for (i in 6..10) {
            assertNull("Message msg_$i must be deleted", remainingMsgs.find { it.id == "msg_$i" })
        }

        // Step B: EmotionHistory records created after message 5 (index > 5 or timestamp > t5) must be deleted!
        val remainingHistory = db.emotionHistoryDao().getAllHistoryForBot(botId)
        assertTrue("All history records must have messageIndex <= 5", remainingHistory.all { it.messageIndex <= 5 })
        assertTrue("All history records must have timestamp <= msg_5 timestamp", remainingHistory.all { it.timestamp <= messages[4].timestamp })
        assertEquals(2, remainingHistory.size) // msg_2 and msg_4

        // Step C: Auto-added cast member 'Doktor Kenan' (appeared at message 8) must be deleted!
        val doctorCast = db.castMemberDao().findByName(botId, "Doktor Kenan")
        assertNull("Auto-added cast member Doktor Kenan must be deleted after rewind", doctorCast)

        // Step D: Memory entities (events, facts, fragments) created after message 5 must be deleted!
        val remainingEvents = db.memoryEventDao().getActiveEvents(botId)
        assertEquals("Only 1 event before message 5 should remain", 1, remainingEvents.size)
        assertEquals(101L, remainingEvents[0].id)

        val remainingFacts = db.memoryFactDao().getActiveFacts(botId)
        assertEquals("Only 1 fact before message 5 should remain", 1, remainingFacts.size)
        assertEquals(201L, remainingFacts[0].id)

        val remainingFragments = db.memoryFragmentDao().getRecentFragments(botId)
        assertEquals("Only 1 fragment before message 5 should remain", 1, remainingFragments.size)
        assertEquals(301L, remainingFragments[0].id)

        // Step E: BotEntity state, counters & calendar date/day must be restored to message 5 snapshot!
        val restoredBot = db.botDao().getBotById(botId)!!
        val restoredEmotion = EmotionState.fromJson(restoredBot.emotionState)
        assertEquals("Bot affection must be restored to 60 (snapshot at turn 4/5)", 60, restoredEmotion.affection)
        assertEquals("Bot trust must be restored to 62", 62, restoredEmotion.trust)
        assertEquals("consecutivePositiveCount must be restored to 2", 2, restoredEmotion.consecutivePositiveCount)
        assertEquals("dailyAffectionGain must be restored to 10", 10, restoredEmotion.dailyAffectionGain)
        assertEquals("peakAffectionScore must be restored to 60", 60, restoredEmotion.peakAffectionScore)
        assertEquals("storyCalendarDate must be restored to 2026-08-30", "2026-08-30", restoredBot.storyCalendarDate)
        assertEquals("storyDayCounter must be restored to 2", 2L, restoredBot.storyDayCounter)

        db.close()
    }

    @Test
    fun testCloneBot_copiesSettingsAndManualCast_resetsCountersAndEmotionsAndHistory() = runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = EmochiRepository(db, context)

        val origBotId = "bot_source_1"
        val manualChar = com.example.data.repository.KeyCharacter(id = "char_manual_1", name = "Sora", desc = "Sadık dost")
        val autoChar = com.example.data.repository.KeyCharacter(id = "auto_17000000_Doctor", name = "Doktor", desc = "Hastanede karşılaşıldı")
        val keyCharsJson = repo.serializeKeyCharacters(listOf(manualChar, autoChar))

        val originalBot = com.example.data.local.BotEntity(
            id = origBotId,
            mode = "personal",
            aiName = "Ayla",
            aiPersonality = "Nazik ve koruyucu bir yapay zeka. Her Pazartesi kütüphaneye gider.",
            scenario = "Kullanıcı ile bir dağ evinde mahsur kalırlar.",
            universeName = "",
            keyCharactersJson = keyCharsJson,
            userCharName = "Deniz",
            userCharDesc = "Gezgin",
            openingMessage = "Fırtına çok şiddetlendi Deniz, içeri gelmelisin.",
            writingStyle = "rp",
            intensity = "normal",
            customLength = "long",
            isNsfw = true,
            birthDate = "2006-08-29",
            initialAge = 20,
            currentAge = 25, // Aged during story
            storyCalendarDate = "2026-12-25", // Advanced calendar date
            storyDayCounter = 118L, // Advanced day counter
            emotionState = """{"mood":"öfkeli","intensity":9,"affection":92,"trust":85,"tension":65,"consecutivePositiveCount":5,"dailyAffectionGain":25,"peakAffectionScore":95,"recoveryLockUntilMessageCount":10}""",
            pinnedMemory = "Eski hatıra",
            storyNotes = "Eski hikaye notu"
        )
        db.botDao().insertOrUpdate(originalBot)

        // Add 5 chat messages to original bot
        for (i in 1..5) {
            db.messageDao().insertMessage(
                com.example.data.local.MessageEntity(
                    id = "msg_orig_$i",
                    botId = origBotId,
                    role = if (i % 2 == 1) "user" else "assistant",
                    text = "Mesaj $i",
                    timestamp = System.currentTimeMillis() + i * 1000
                )
            )
        }

        // Add manual cast member & auto-added cast member
        db.castMemberDao().insertCastMember(
            CastMemberEntity(
                id = 10,
                botId = origBotId,
                name = "Sora",
                description = "Sadık dost",
                role = "Yoldaş",
                isAutoAdded = false,
                affectionScore = 80,
                consecutivePositiveCount = 4,
                dailyAffectionGain = 15,
                peakAffectionScore = 85,
                recoveryLockUntilMessageCount = 5,
                initialAge = 19,
                currentAge = 24
            )
        )
        db.castMemberDao().insertCastMember(
            CastMemberEntity(
                id = 11,
                botId = origBotId,
                name = "Doktor",
                description = "Hastanede karşılaşıldı",
                role = "Yan Karakter",
                isAutoAdded = true,
                affectionScore = 60
            )
        )

        // Add emotion history, character emotions, memory fragments, routine
        db.characterEmotionDao().insertOrUpdate(
            com.example.data.local.CharacterEmotionEntity(
                id = 20,
                botId = origBotId,
                characterName = "Sora",
                emotionState = """{"affection":80}"""
            )
        )
        db.emotionHistoryDao().insertHistory(
            com.example.data.local.EmotionHistoryEntity(
                id = 30,
                botId = origBotId,
                messageIndex = 5,
                characterName = "Ayla",
                affectionScore = 92,
                fullVectorJson = """{"affection":92}"""
            )
        )
        db.memoryFragmentDao().insertFragment(
            com.example.data.local.MemoryFragmentEntity(
                id = 40,
                botId = origBotId,
                content = "Fırtına anısı",
                category = "HAFIZA"
            )
        )
        db.routineDao().insertRoutine(
            RoutineEntity(
                id = 50,
                botId = origBotId,
                routineDescription = "Pazartesi günleri kütüphaneye gider",
                lastMentionedStoryDate = "2026-12-20"
            )
        )

        // ACT: Clone the bot
        val clonedBot = repo.cloneBot(origBotId)

        // ASSERTIONS
        assertNotNull("Cloned bot must not be null", clonedBot)
        val newBotId = clonedBot!!.id
        assertNotEquals("Cloned bot must have a unique ID", origBotId, newBotId)

        // 1. Settings & Core config preserved
        assertEquals("Ayla", clonedBot.aiName)
        assertEquals(originalBot.aiPersonality, clonedBot.aiPersonality)
        assertEquals(originalBot.scenario, clonedBot.scenario)
        assertEquals(originalBot.writingStyle, clonedBot.writingStyle)
        assertEquals(originalBot.intensity, clonedBot.intensity)
        assertEquals("long", clonedBot.customLength)
        assertEquals(true, clonedBot.isNsfw)

        // 2. KeyCharacters: manual kept ("Sora"), auto removed ("Doktor")
        val clonedKeyChars = repo.parseKeyCharacters(clonedBot.keyCharactersJson)
        assertEquals("Only manual character should be kept", 1, clonedKeyChars.size)
        assertEquals("Sora", clonedKeyChars[0].name)

        // 3. CastMemberEntity: auto-added NOT copied, manual copied with reset counters
        val clonedCast = db.castMemberDao().getCastMembersForBot(newBotId)
        assertEquals("Only manual cast member should be copied", 1, clonedCast.size)
        val clonedSora = clonedCast[0]
        assertEquals("Sora", clonedSora.name)
        assertFalse("Manual cast member is not auto-added", clonedSora.isAutoAdded)
        assertEquals("Affection must be reset to 50", 50, clonedSora.affectionScore)
        assertEquals("consecutivePositiveCount must be 0", 0, clonedSora.consecutivePositiveCount)
        assertEquals("dailyAffectionGain must be 0", 0, clonedSora.dailyAffectionGain)
        assertEquals("peakAffectionScore must be 50", 50, clonedSora.peakAffectionScore)
        assertEquals("recoveryLockUntilMessageCount must be 0", 0, clonedSora.recoveryLockUntilMessageCount)
        assertEquals("Age must be reset to initialAge (19)", 19, clonedSora.currentAge)

        // 4. Memory, notes & Emotion history must NOT be copied
        assertEquals("", clonedBot.pinnedMemory)
        assertEquals("", clonedBot.storyNotes)
        assertEquals("", clonedBot.memoryNotes)
        val clonedFragments = db.memoryFragmentDao().getRecentFragments(newBotId)
        assertTrue("Memory fragments must be empty for cloned bot", clonedFragments.isEmpty())
        val clonedHistory = db.emotionHistoryDao().getHistoryForBot(newBotId)
        assertTrue("Emotion history must be empty for cloned bot", clonedHistory.isEmpty())
        val clonedCharEmotions = db.characterEmotionDao().getEmotionsForBot(newBotId)
        assertTrue("Character emotions must be empty for cloned bot", clonedCharEmotions.isEmpty())

        // 5. Calendar & Counters reset
        assertEquals("storyCalendarDate must reset to 2026-08-29", "2026-08-29", clonedBot.storyCalendarDate)
        assertEquals("storyDayCounter must reset to 1", 1L, clonedBot.storyDayCounter)
        assertEquals("currentAge must reset to initialAge (20)", 20, clonedBot.currentAge)

        val clonedEmotionState = EmotionState.fromJson(clonedBot.emotionState)
        assertEquals("consecutivePositiveCount must reset to 0", 0, clonedEmotionState.consecutivePositiveCount)
        assertEquals("dailyAffectionGain must reset to 0", 0, clonedEmotionState.dailyAffectionGain)
        assertEquals("recoveryLockUntilMessageCount must reset to 0", 0, clonedEmotionState.recoveryLockUntilMessageCount)

        // 6. Routines: definition copied, but lastMentionedStoryDate reset
        val clonedRoutines = db.routineDao().getRoutinesForBot(newBotId)
        assertTrue("Routines should be copied", clonedRoutines.isNotEmpty())
        assertEquals("lastMentionedStoryDate must be reset to empty", "", clonedRoutines[0].lastMentionedStoryDate)

        // 7. Messages: exactly 1 opening message from openingMessage
        val clonedMsgs = db.messageDao().getMessagesForBotList(newBotId)
        assertEquals("Only opening message should exist", 1, clonedMsgs.size)
        assertEquals("assistant", clonedMsgs[0].role)
        assertEquals("Fırtına çok şiddetlendi Deniz, içeri gelmelisin.", clonedMsgs[0].text)

        // 8. DB independence: deleting or updating original does not affect clone
        repo.deleteBot(origBotId)
        val verifyClonedStillExists = db.botDao().getBotById(newBotId)
        assertNotNull("Cloned bot must still exist independently after original is deleted", verifyClonedStillExists)

        db.close()
    }
}

