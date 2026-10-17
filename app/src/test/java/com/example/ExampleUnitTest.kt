package com.example

import com.example.data.local.CastMemberEntity
import com.example.data.local.EmotionState
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.EmochiRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EmochiCoreSystemRegressionTest {

    // =========================================================================
    // TEST 1 (SORUN 1): YAN KARAKTER DUYGU GÜNCELLEMESİ KORUMA & ASİMETRİ KURALLARI
    // =========================================================================
    @Test
    fun testSideCharacterEmotionClamping_preventsInstantRomance() {
        // Bir model yan karaktere tek seferde +50 veya +100 delta vermeye çalışsa bile
        // calculateClampedDelta bunu güvenli günlük/mesaj tavanıyla (max 4-8) sınırlandırmalıdır.
        val prevAffection = 20
        val rawInsaneDelta = 80 // Modelin sınırsız göndermeye çalıştığı aşırı delta

        val clampedDelta = EmochiRepository.calculateClampedDelta(
            prevAffection = prevAffection,
            rawDelta = rawInsaneDelta,
            totalMsgCount = 2,
            baseMultiplier = 0.85, // Yabancı / temkinli rol
            combinedContextMult = 1.0,
            personalityAffectionMult = 1.0,
            recoveryLockUntilMessageCount = 0,
            dailyAffectionGain = 0,
            consecutivePositiveCount = 0,
            regCount = 0,
            isEstablishedRomantic = false
        )

        assertTrue("Ham delta 80 olsa bile tavan 3'ü (erken mesaj kuralı) geçemez", clampedDelta <= 3)
        assertEquals("İlk 5 mesajda romantik olmayan bağ için delta 3 olmalıdır", 3, clampedDelta)

        // İlerleyen mesajlarda bile tavan asla 8'i geçemez
        val matureClampedDelta = EmochiRepository.calculateClampedDelta(
            prevAffection = prevAffection,
            rawDelta = rawInsaneDelta,
            totalMsgCount = 20,
            baseMultiplier = 1.0,
            combinedContextMult = 1.0,
            personalityAffectionMult = 1.0,
            recoveryLockUntilMessageCount = 0,
            dailyAffectionGain = 0,
            consecutivePositiveCount = 0,
            regCount = 0,
            isEstablishedRomantic = false
        )
        assertTrue("Olgun sohbette bile tek mesajda artış 8 ile sınırlı kalmalıdır", matureClampedDelta <= 8)
    }

    @Test
    fun testSideCharacter_recoveryLockSlowsAffectionGain() {
        // Büyük düşüş (-15 veya altı) sonrası iyileşme kilidi varsa yakınlık kazanımı %50 yavaşlamalı
        val clampedDeltaWithLock = EmochiRepository.calculateClampedDelta(
            prevAffection = 40,
            rawDelta = 10,
            totalMsgCount = 10,
            baseMultiplier = 1.0,
            combinedContextMult = 1.0,
            personalityAffectionMult = 1.0,
            recoveryLockUntilMessageCount = 25, // Kilit aktif (10 < 25)
            dailyAffectionGain = 0,
            consecutivePositiveCount = 0,
            regCount = 0,
            isEstablishedRomantic = false
        )

        assertEquals("Kilit altındayken normal tavan (8) yarıya (4) düşmelidir", 4, clampedDeltaWithLock)
    }

    // =========================================================================
    // TEST 2 (SORUN 3): CastMemberEntity ROL-BAZLI ZORLUK ÇARPANI
    // =========================================================================
    @Test
    fun testCastMemberEntity_roleDifficultyMultipliers() {
        val authorityMember = CastMemberEntity(
            botId = "bot_1",
            name = "Müdür Selim",
            description = "Şirket genel müdürü",
            role = "Patron",
            baseAffectionDifficultyMultiplier = 0.70
        )
        val friendMember = CastMemberEntity(
            botId = "bot_1",
            name = "Can",
            description = "Eski çocukluk arkadaşı",
            role = "Çocukluk Arkadaşı",
            baseAffectionDifficultyMultiplier = 1.40
        )

        val authorityDelta = EmochiRepository.calculateClampedDelta(
            prevAffection = 30,
            rawDelta = 10,
            totalMsgCount = 15,
            baseMultiplier = authorityMember.baseAffectionDifficultyMultiplier,
            combinedContextMult = 1.0,
            personalityAffectionMult = 1.0,
            recoveryLockUntilMessageCount = 0,
            dailyAffectionGain = 0,
            consecutivePositiveCount = 0
        )

        val friendDelta = EmochiRepository.calculateClampedDelta(
            prevAffection = 30,
            rawDelta = 10,
            totalMsgCount = 15,
            baseMultiplier = friendMember.baseAffectionDifficultyMultiplier,
            combinedContextMult = 1.0,
            personalityAffectionMult = 1.0,
            recoveryLockUntilMessageCount = 0,
            dailyAffectionGain = 0,
            consecutivePositiveCount = 0
        )

        assertTrue("Patron için çarpan (0.70) yakınlık artışını arkadaşa (1.40) göre daha yavaş tutmalıdır", authorityDelta < friendDelta)
    }

    // =========================================================================
    // TEST 3 (SORUN 5): SELECTED MODEL YERİNE SAĞLAYICIYA ÖZEL PINNED MODEL KULLANIMI
    // =========================================================================
    @Test
    fun testCentralizedModelResolver_usesProviderSpecificPinnedModels() {
        // Groq seçiliyken groqModel dönmeli, selectedModel eski bir değerde kalsa bile
        val groqSettings = UserSettingsEntity(
            selectedProvider = "groq",
            selectedModel = "gemini-2.0-flash", // Yanlış/eski global model
            groqModel = "openai/gpt-oss-120b"
        )
        val resolvedGroq = EmochiRepository.getEffectiveModelForProvider(groqSettings)
        assertEquals("Groq seçiliyken groqModel kullanılmalı", "openai/gpt-oss-120b", resolvedGroq)

        // OpenAI seçiliyken openaiModel dönmeli
        val openaiSettings = UserSettingsEntity(
            selectedProvider = "openai",
            selectedModel = "gemini-2.0-flash",
            openaiModel = "gpt-4o"
        )
        val resolvedOpenai = EmochiRepository.getEffectiveModelForProvider(openaiSettings)
        assertEquals("OpenAI seçiliyken openaiModel kullanılmalı", "gpt-4o", resolvedOpenai)

        // Gemini seçiliyken geminiModel dönmeli
        val geminiSettings = UserSettingsEntity(
            selectedProvider = "gemini",
            selectedModel = "gpt-4o",
            geminiModel = "gemini-2.0-flash"
        )
        val resolvedGemini = EmochiRepository.getEffectiveModelForProvider(geminiSettings)
        assertEquals("Gemini seçiliyken geminiModel kullanılmalı", "gemini-2.0-flash", resolvedGemini)

        // OpenRouter seçiliyken openRouterModel dönmeli
        val openRouterSettings = UserSettingsEntity(
            selectedProvider = "openrouter",
            openRouterModel = "deepseek/deepseek-chat"
        )
        val resolvedOpenRouter = EmochiRepository.getEffectiveModelForProvider(openRouterSettings)
        assertEquals("OpenRouter seçiliyken openRouterModel kullanılmalı", "deepseek/deepseek-chat", resolvedOpenRouter)
    }

    // =========================================================================
    // TEST 4 (SORUN 4): SAĞLAYICI AÇIK/KAPALI (ENABLED) KONTROLÜ
    // =========================================================================
    @Test
    fun testProviderEnabledFlags_filtersDisabledProvidersFromFallback() {
        val settingsWithDisabledLlm7 = UserSettingsEntity(
            selectedProvider = "gemini",
            enableAutoFallback = true,
            enableLlm7 = true,
            llm7Enabled = false, // Kullanıcı LLM7'yi kapattı
            enablePollinations = true,
            pollinationsEnabled = true,
            openRouterEnabled = false // Kullanıcı OpenRouter'ı kapattı
        )

        // Katman 1 simülasyonu
        val layer1Keys = listOf("pollinations", "llm7", "ovh").filter { key ->
            when (key) {
                "pollinations" -> (settingsWithDisabledLlm7.enablePollinations && settingsWithDisabledLlm7.pollinationsEnabled) && settingsWithDisabledLlm7.selectedProvider != "pollinations"
                "llm7" -> (settingsWithDisabledLlm7.enableLlm7 && settingsWithDisabledLlm7.llm7Enabled) && settingsWithDisabledLlm7.selectedProvider != "llm7"
                "ovh" -> (settingsWithDisabledLlm7.enableOvh && settingsWithDisabledLlm7.ovhEnabled) && settingsWithDisabledLlm7.selectedProvider != "ovh"
                else -> false
            }
        }

        assertFalse("LLM7 kapalı olduğunda Katman 1 listesinde yer almamalıdır", layer1Keys.contains("llm7"))
        assertTrue("Pollinations açık olduğunda Katman 1 listesinde yer almalıdır", layer1Keys.contains("pollinations"))

        // Katman 3 simülasyonu
        val layer3Keys = listOf("openrouter", "nvidia", "mistral").filter { key ->
            when (key) {
                "openrouter" -> settingsWithDisabledLlm7.openRouterEnabled && settingsWithDisabledLlm7.selectedProvider != "openrouter" && settingsWithDisabledLlm7.openRouterApiKey.isNotBlank()
                "nvidia" -> settingsWithDisabledLlm7.nvidiaEnabled && settingsWithDisabledLlm7.selectedProvider != "nvidia" && settingsWithDisabledLlm7.nvidiaApiKey.isNotBlank()
                "mistral" -> settingsWithDisabledLlm7.mistralEnabled && settingsWithDisabledLlm7.selectedProvider != "mistral" && settingsWithDisabledLlm7.mistralApiKey.isNotBlank()
                else -> false
            }
        }
        assertFalse("OpenRouter kapalı olduğunda Katman 3 fallback listesine giremez", layer3Keys.contains("openrouter"))
    }
}
