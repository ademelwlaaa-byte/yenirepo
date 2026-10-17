package com.example.ui.screens

import com.example.ui.theme.AppColors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import com.example.data.local.BotEntity
import com.example.data.local.EmotionState
import com.example.data.local.SensitiveTriggerEntity
import com.example.data.local.WorldAtmosphere
import com.example.data.repository.BaselineEmotionEngine
import com.example.data.repository.KeyCharacter
import com.example.ui.components.AppBackground
import com.example.ui.components.OrbView
import com.example.ui.theme.EmochiError
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

@Composable
fun CreateBotWizardScreen(
    onBack: () -> Unit,
    onFinish: (BotEntity) -> Unit,
    onGenerateOpening: suspend (BotEntity) -> String,
    onAnalyzeBaseline: (suspend (BotEntity) -> BaselineEmotionEngine.BaselineAnalysisResult)? = null
) {
    var stepIdx by remember { mutableIntStateOf(0) }

    var mode by remember { mutableStateOf("personal") }
    var aiName by remember { mutableStateOf("") }
    var aiPersonality by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var pinnedMemory by remember { mutableStateOf("") }
    var scenario by remember { mutableStateOf("") }
    var universeName by remember { mutableStateOf("") }
    var userCharName by remember { mutableStateOf("") }
    var userCharDesc by remember { mutableStateOf("") }
    var openingMessage by remember { mutableStateOf("") }
    var writingStyle by remember { mutableStateOf("rp") }
    var intensity by remember { mutableStateOf("normal") }
    var isPublic by remember { mutableStateOf(true) }

    var storyStartDate by remember { mutableStateOf("") }

    var keyCharacters by remember { mutableStateOf<List<KeyCharacter>>(emptyList()) }

    // Baseline analysis state (step 4 — review before writing style)
    var analysisResult by remember { mutableStateOf<BaselineEmotionEngine.BaselineAnalysisResult?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisError by remember { mutableStateOf<String?>(null) }
    var userEditedFields by remember { mutableStateOf<Set<String>>(emptySet()) }
    var editedTriggers by remember { mutableStateOf<List<SensitiveTriggerEntity>?>(null) }
    // Local editable copies shown in the review step
    var reviewRelType by remember { mutableStateOf("") }
    var reviewAttachment by remember { mutableStateOf("secure") }
    var reviewSpeechStyle by remember { mutableStateOf("") }
    var reviewDominantEmotion by remember { mutableStateOf("") }
    var reviewTriggers by remember { mutableStateOf<List<SensitiveTriggerEntity>>(emptyList()) }
    var reviewBigFive by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var reviewAxes by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    var isGeneratingOpening by remember { mutableStateOf(false) }
    var genError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { inputUri ->
            scope.launch {
                // Compress → local file, then convert to Base64 data URI for portable storage
                val localPath = com.example.util.ImageStorageManager.compressAndSaveImage(context, inputUri)
                val base64Uri = com.example.util.ImageStorageManager.toBase64DataUri(context, localPath)
                avatarUrl = base64Uri ?: localPath
            }
        }
    }

    val totalSteps = 6 // +1 for baseline review step (step 4, 0-indexed)

    fun buildCurrentDraft(): BotEntity {
        val serializedCast = if (keyCharacters.isEmpty()) "[]" else {
            val array = org.json.JSONArray()
            for (char in keyCharacters) {
                val obj = org.json.JSONObject()
                obj.put("id", char.id)
                obj.put("name", char.name)
                obj.put("desc", char.desc)
                array.put(obj)
            }
            array.toString()
        }
        val baselineEmotion = EmotionState.calculateBaselineEmotionState(
            aiName = if (mode == "personal") aiName.trim() else universeName.trim(),
            personality = aiPersonality.trim(),
            scenario = scenario.trim(),
            userCharName = userCharName.trim(),
            userCharDesc = userCharDesc.trim()
        ).toJson()

        val initialWorldAtmosphere = if (mode == "universe") {
            val intensityInt = when (intensity) {
                "düşük" -> 3
                "yüksek" -> 8
                else -> 5
            }
            WorldAtmosphere(
                mood = "sakin ve beklentili",
                intensity = intensityInt,
                currentEvent = "Açılış sahnesi ve hikayenin başlangıcı",
                macroAtmosphere = "${universeName.trim()} evreninin genel düzeni ve toplumsal kuralları hakim.",
                microAtmosphere = "Hikayenin başladığı fiziki mekan ve başlangıç ortamı."
            ).toJson()
        } else ""

        return BotEntity(
            id = UUID.randomUUID().toString(),
            mode = mode,
            aiName = aiName.trim(),
            aiPersonality = aiPersonality.trim(),
            avatarUrl = avatarUrl.trim(),
            pinnedMemory = pinnedMemory.trim(),
            scenario = scenario.trim(),
            universeName = universeName.trim(),
            keyCharactersJson = serializedCast,
            userCharName = userCharName.trim().ifBlank { "Kullanıcı" },
            userCharDesc = userCharDesc.trim(),
            openingMessage = openingMessage.trim(),
            writingStyle = writingStyle,
            intensity = intensity,
            customLength = "default",
            isNsfw = true,
            isPublic = isPublic,
            emotionState = baselineEmotion,
            previousEmotionState = baselineEmotion,
            worldAtmosphere = initialWorldAtmosphere,
            storyCalendarDate = storyStartDate.trim(),
            hasExplicitStoryDate = storyStartDate.trim().isNotBlank(),
            updatedAt = System.currentTimeMillis()
        )
    }

    val canNext = when (stepIdx) {
        0 -> true
        1 -> if (mode == "personal") aiName.isNotBlank() && aiPersonality.isNotBlank() else universeName.isNotBlank()
        2 -> scenario.isNotBlank() && userCharName.isNotBlank()
        3 -> openingMessage.isNotBlank()
        4 -> !isAnalyzing // analysis step: can proceed while loading (skippable) or after result
        5 -> true
        else -> true
    }

    // Glow and pulse animations for cosmic effects
    val infiniteTransition = rememberInfiniteTransition(label = "wizardAnimations")
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulse"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AppBackground()

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                val backInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val backPressed by backInteraction.collectIsPressedAsState()
                val backScale by animateFloatAsState(
                    targetValue = if (backPressed) 0.87f else 1f,
                    animationSpec = tween(80), label = "wizardBackScale"
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Premium back button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .graphicsLayer { scaleX = backScale; scaleY = backScale }
                                .clip(RoundedCornerShape(13.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF1C1040), Color(0xFF0F0828))))
                                .border(
                                    BorderStroke(1.2.dp, Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF6D28D9)))),
                                    RoundedCornerShape(13.dp)
                                )
                                .clickable(interactionSource = backInteraction, indication = null) {
                                    if (stepIdx > 0) stepIdx-- else onBack()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFFCDA8FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Bot Oluştur",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Animated step counter badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Brush.linearGradient(listOf(Color(0xFF3B1070), Color(0xFF1E0850))))
                                        .border(BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9)))), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${stepIdx + 1} / $totalSteps",
                                        color = Color(0xFFCDA8FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step progress dots with animated fill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(totalSteps) { idx ->
                            val isActive = idx == stepIdx
                            val isDone = idx < stepIdx
                            val dotWidth by animateFloatAsState(
                                targetValue = if (isActive) 28f else 8f,
                                animationSpec = tween(300, easing = FastOutSlowInEasing),
                                label = "dotW_$idx"
                            )
                            Box(
                                modifier = Modifier
                                    .height(5.dp)
                                    .width(dotWidth.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        when {
                                            isActive -> Brush.horizontalGradient(listOf(Color(0xFFC084FC), Color(0xFF7C3AED)))
                                            isDone   -> Brush.horizontalGradient(listOf(Color(0xFF6D28D9), Color(0xFF4C1D95)))
                                            else     -> Brush.horizontalGradient(listOf(Color(0x30A855F7), Color(0x20A855F7)))
                                        }
                                    )
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Geri Pill Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .border(
                                BorderStroke(1.2.dp, Color(0x607C3AED)),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .background(Color(0x4013082A))
                            .clickable {
                                if (stepIdx > 0) stepIdx-- else onBack()
                            }
                            .padding(horizontal = 22.dp, vertical = 11.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = AppColors.PurpleLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Geri",
                                color = AppColors.PurpleLight,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Right İleri / Tamamla Pill Button
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = if (canNext) buttonPulseScale else 1f
                                scaleY = if (canNext) buttonPulseScale else 1f
                            }
                            .clip(RoundedCornerShape(22.dp))
                            .border(
                                BorderStroke(
                                    width = 1.3.dp,
                                    brush = if (canNext) {
                                        Brush.sweepGradient(
                                            listOf(AppColors.White, AppColors.PurpleLight, AppColors.PrimaryViolet, AppColors.White)
                                        )
                                    } else {
                                        Brush.linearGradient(listOf(Color(0x307C3AED), Color(0x203B0764)))
                                    }
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .background(
                                if (canNext) {
                                    Brush.horizontalGradient(
                                        listOf(AppColors.PurpleSoft, AppColors.PurpleDark, AppColors.PurpleDarkest)
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(Color(0x403B0764), Color(0x301E1035))
                                    )
                                }
                            )
                            .clickable(enabled = canNext) {
                                if (stepIdx == 3) {
                                    // Moving to analysis step: trigger analysis
                                    stepIdx++
                                    val draft = buildCurrentDraft()
                                    scope.launch {
                                        isAnalyzing = true
                                        analysisError = null
                                        try {
                                            val res = onAnalyzeBaseline?.invoke(draft)
                                                ?: BaselineEmotionEngine.calculateDeterministicFallback(
                                                    botId = draft.id, aiName = draft.aiName,
                                                    personality = draft.aiPersonality, scenario = draft.scenario,
                                                    userCharName = draft.userCharName, userCharDesc = draft.userCharDesc
                                                )
                                            analysisResult = res
                                            reviewRelType = res.relationshipType
                                            reviewAttachment = res.personalityProfile.attachmentStyle
                                            reviewSpeechStyle = res.speechPattern
                                            reviewDominantEmotion = res.baselineEmotionState.dominantEmotion
                                            reviewTriggers = res.sensitiveTriggers.toMutableList()
                                            reviewBigFive = mapOf(
                                                "openness" to res.personalityProfile.openness,
                                                "conscientiousness" to res.personalityProfile.conscientiousness,
                                                "extraversion" to res.personalityProfile.extraversion,
                                                "agreeableness" to res.personalityProfile.agreeableness,
                                                "neuroticism" to res.personalityProfile.neuroticism
                                            )
                                            reviewAxes = mapOf(
                                                "affection" to res.baselineEmotionState.affection,
                                                "respect" to res.baselineEmotionState.relationshipAxes.respectScore,
                                                "comfort" to res.baselineEmotionState.relationshipAxes.comfortScore,
                                                "resentment" to res.baselineEmotionState.relationshipAxes.resentmentScore
                                            )
                                        } catch (_: Exception) {
                                            analysisError = "Analiz başarısız. Otomatik tahmin kullanıldı."
                                            val fb = BaselineEmotionEngine.calculateDeterministicFallback(
                                                botId = draft.id, aiName = draft.aiName,
                                                personality = draft.aiPersonality, scenario = draft.scenario,
                                                userCharName = draft.userCharName, userCharDesc = draft.userCharDesc
                                            )
                                            analysisResult = fb
                                            reviewRelType = fb.relationshipType
                                            reviewAttachment = fb.personalityProfile.attachmentStyle
                                            reviewSpeechStyle = fb.speechPattern
                                            reviewDominantEmotion = fb.baselineEmotionState.dominantEmotion
                                            reviewTriggers = fb.sensitiveTriggers.toMutableList()
                                            reviewBigFive = mapOf(
                                                "openness" to fb.personalityProfile.openness,
                                                "conscientiousness" to fb.personalityProfile.conscientiousness,
                                                "extraversion" to fb.personalityProfile.extraversion,
                                                "agreeableness" to fb.personalityProfile.agreeableness,
                                                "neuroticism" to fb.personalityProfile.neuroticism
                                            )
                                            reviewAxes = mapOf(
                                                "affection" to fb.baselineEmotionState.affection,
                                                "respect" to fb.baselineEmotionState.relationshipAxes.respectScore,
                                                "comfort" to fb.baselineEmotionState.relationshipAxes.comfortScore,
                                                "resentment" to fb.baselineEmotionState.relationshipAxes.resentmentScore
                                            )
                                        } finally {
                                            isAnalyzing = false
                                        }
                                    }
                                } else if (stepIdx < totalSteps - 1) {
                                    stepIdx++
                                } else {
                                    // Last step: apply analysis result to draft
                                    val base = buildCurrentDraft()
                                    val res = analysisResult
                                    val finalBot = if (res != null) {
                                        val axes = com.example.data.local.RelationshipAxes(
                                            affectionScore  = reviewAxes["affection"] ?: res.baselineEmotionState.affection,
                                            respectScore    = reviewAxes["respect"] ?: res.baselineEmotionState.relationshipAxes.respectScore,
                                            comfortScore    = reviewAxes["comfort"] ?: res.baselineEmotionState.relationshipAxes.comfortScore,
                                            resentmentScore = reviewAxes["resentment"] ?: res.baselineEmotionState.relationshipAxes.resentmentScore
                                        )
                                        val emotions = res.baselineEmotionState.primaryEmotions
                                        val editedState = res.baselineEmotionState.copy(
                                            relationshipAxes = axes,
                                            dominantEmotion = reviewDominantEmotion,
                                            speechPattern = reviewSpeechStyle,
                                            primaryEmotions = emotions
                                        )
                                        val emotionJson = editedState.toJson()
                                        base.copy(
                                            emotionState = emotionJson,
                                            previousEmotionState = emotionJson,
                                            baseAffectionDifficulty = res.difficultyMultiplier,
                                            emotionalRegulationCapacity = res.regulationCapacity,
                                            baselineEmotionAnalysisJson = res.rawJson.ifBlank { "" },
                                            baselineAnalysisSource = if (userEditedFields.isNotEmpty()) "user_edited" else res.analysisSource,
                                            baselineUserEditedFields = userEditedFields.joinToString(",")
                                        )
                                    } else { base }
                                    onFinish(finalBot)
                                }
                            }
                            .padding(horizontal = 26.dp, vertical = 11.dp)
                            .testTag("wizard_next_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (stepIdx == totalSteps - 1) "Tamamla" else "İleri",
                                color = if (canNext) Color.White else Color(0x60A78BFA),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (stepIdx == totalSteps - 1) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = if (canNext) Color.White else Color(0x60A78BFA),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // 3D Glowing Purple Orb Widget
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AppColors.PrimaryViolet.copy(alpha = pulseGlowAlpha * 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    OrbView(hue = 275f, size = 56.dp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                when (stepIdx) {
                    0 -> {
                        // Step 1: Mode Selection matching screenshot 1/5
                        Text(
                            text = "Nasıl bir etkileşim istersiniz?",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI karakter tipini ve hikaye yapısını seçin.",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        WizardModeCard(
                            title = "Kişisel Karakter",
                            subtitle1 = "Sana özel bire bir sohbet.",
                            subtitle2 = "Tek ve sabit bir karakterle gerçek zamanlı bağ.",
                            iconVector = Icons.Default.Person,
                            isSelected = mode == "personal",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { mode = "personal" }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        WizardModeCard(
                            title = "Evren / Hikaye (RP)",
                            subtitle1 = "Kurgusal bir dünyada geçen roleplay.",
                            subtitle2 = "AI evrenin anlatıcısı olur ve kadroyu yönetir.",
                            iconVector = Icons.Default.Book,
                            isSelected = mode == "universe",
                            glowAlpha = pulseGlowAlpha,
                            onClick = { mode = "universe" }
                        )
                    }

                    1 -> {
                        // Step 2: Universe & Characters matching screenshot 2/5
                        if (mode == "personal") {
                            Text(
                                text = "Karakter Detayları",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Karşınızdaki karakterin resmini, adını ve kişiliğini tanımlayın.",
                                color = AppColors.TextMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Avatar Upload Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0x800E0620))
                                    .border(1.2.dp, Color(0x507C3AED), RoundedCornerShape(18.dp))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(AppColors.Hex_23104D)
                                            .border(1.5.dp, AppColors.PrimaryViolet, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        com.example.ui.components.BotAvatarImage(
                                            avatarUrl = avatarUrl.takeIf { it.isNotBlank() },
                                            contentDescription = "Avatar",
                                            modifier = Modifier.fillMaxSize(),
                                            fallback = {
                                                Icon(
                                                    Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = AppColors.PrimaryViolet,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            }
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Bot Profil Fotoğrafı",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Galeriden kapak resmi yükle",
                                            color = AppColors.TextMuted,
                                            fontSize = 11.5.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        Brush.horizontalGradient(
                                                            listOf(AppColors.PurpleAccent, AppColors.PurpleDarkest)
                                                        )
                                                    )
                                                    .clickable { photoPickerLauncher.launch("image/*") }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    "🖼️ Fotoğraf Seç",
                                                    fontSize = 11.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            if (avatarUrl.isNotBlank()) {
                                                Box(
                                                    modifier = Modifier
                                                        .height(28.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Brush.linearGradient(listOf(Color(0xFF1A0000), Color(0xFF100000))))
                                                        .border(0.8.dp, EmochiError.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                        .clickable { avatarUrl = "" }
                                                        .padding(horizontal = 10.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("Sil", fontSize = 11.sp, color = EmochiError, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Karakter Adı *",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            com.example.ui.components.EmochiTextField(
                                value = aiName,
                                onValueChange = { aiName = it },
                                placeholder = "ör. Aiden",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_name_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                "Kişilik ve Özellikler *",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            com.example.ui.components.EmochiTextField(
                                value = aiPersonality,
                                onValueChange = { aiPersonality = it },
                                placeholder = "Nazik mi, korumacı mı, esprili mi, konuşma tarzı nasıl...",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .testTag("ai_personality_input"),
                                singleLine = false,
                                maxLines = 6
                            )
                        } else {
                            Text(
                                text = "Evren ve Karakter Kadrosu",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kurgusal dünyanın adını ve ana karakter kadrosunu belirleyin.",
                                color = AppColors.TextMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                "Evren Adı *",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            com.example.ui.components.EmochiTextField(
                                value = universeName,
                                onValueChange = { universeName = it },
                                placeholder = "Ör. Karanlık Akademi",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                "Ana Karakter Kadrosu (Opsiyonel)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            keyCharacters.forEachIndexed { idx, char ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    com.example.ui.components.EmochiTextField(
                                        value = char.name,
                                        onValueChange = { newName ->
                                            keyCharacters = keyCharacters.toMutableList().apply {
                                                this[idx] = this[idx].copy(name = newName)
                                            }
                                        },
                                        placeholder = "İsim",
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    com.example.ui.components.EmochiTextField(
                                        value = char.desc,
                                        onValueChange = { newDesc ->
                                            keyCharacters = keyCharacters.toMutableList().apply {
                                                this[idx] = this[idx].copy(desc = newDesc)
                                            }
                                        },
                                        placeholder = "Tanım",
                                        modifier = Modifier.weight(1.8f),
                                        singleLine = true
                                    )
                                    IconButton(onClick = {
                                        keyCharacters = keyCharacters.toMutableList().apply { removeAt(idx) }
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Sil", tint = EmochiError, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Add character outline button matching screenshot 2/5
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        BorderStroke(1.2.dp, Color(0x607C3AED)),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .background(Color(0x3013082A))
                                    .clickable {
                                        keyCharacters = keyCharacters + KeyCharacter(name = "", desc = "")
                                    }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = AppColors.PrimaryViolet,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Karakter Ekle",
                                        color = AppColors.PrimaryViolet,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // Step 3: Scenario & User Character matching screenshot 3/5
                        Text(
                            text = "Senaryo & Kendi Karakteriniz",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sahneye giriş bağlamını ve kendi karakterinizi tanımlayın.",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            "Senaryo / Bağlam *",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        com.example.ui.components.EmochiTextField(
                            value = scenario,
                            onValueChange = { scenario = it },
                            placeholder = "Neredesiniz, aranızdaki ilişki ne, gerilim veya durum nasıl başladı...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("scenario_input"),
                            singleLine = false,
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            "Sizin Canlandırdığınız Karakterin Adı *",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        com.example.ui.components.EmochiTextField(
                            value = userCharName,
                            onValueChange = { userCharName = it },
                            placeholder = "ör. Zeynep",
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_char_name_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            "Karakterinizin Kısa Açıklaması (Opsiyonel)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        com.example.ui.components.EmochiTextField(
                            value = userCharDesc,
                            onValueChange = { userCharDesc = it },
                            placeholder = "Yaş, görünüş, kişilik özellikleri...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            singleLine = false,
                            maxLines = 4
                        )
                    }

                    3 -> {
                        // Step 4: Opening Scene matching screenshot 4/5
                        Text(
                            text = "Başlangıç Sahnesi",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sohbet bu açılış mesajı/sahnesi ile başlayacak.",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            com.example.ui.components.EmochiTextField(
                                value = openingMessage,
                                onValueChange = { if (it.length <= 1000) openingMessage = it },
                                placeholder = "Açılış mesajını kendiniz yazın veya aşağıdaki yapay zeka butonunu kullanın...",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .testTag("opening_message_input"),
                                singleLine = false,
                                maxLines = 7
                            )

                            // Counter 0/1000 inside bottom right
                            Text(
                                text = "${openingMessage.length}/1000",
                                color = AppColors.TextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 14.dp, bottom = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // AI Auto-write Button matching screenshot 4/5
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    BorderStroke(1.3.dp, AppColors.PurpleAccent),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(Color(0x801A0D38))
                                .clickable(enabled = !isGeneratingOpening) {
                                    scope.launch {
                                        isGeneratingOpening = true
                                        genError = null
                                        try {
                                            val generated = onGenerateOpening(buildCurrentDraft())
                                            openingMessage = generated
                                        } catch (e: Exception) {
                                            genError = e.message ?: "Oluşturulamadı."
                                        } finally {
                                            isGeneratingOpening = false
                                        }
                                    }
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isGeneratingOpening) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    com.example.ui.components.EmochiLoadingSpinner(size = 18.dp, small = true)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Yapay Zeka Yazıyor...",
                                        color = AppColors.PrimaryViolet,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AppColors.PrimaryViolet,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "AI Otomatik Yazsın",
                                        color = AppColors.PrimaryViolet,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        genError?.let { err ->
                            Text(
                                text = err,
                                color = EmochiError,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    4 -> {
                        // Step 5: Baseline Emotion Profile Review — Premium redesign
                        // Header card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF1E0B3A), Color(0xFF100820))))
                                .border(
                                    1.5.dp,
                                    Brush.sweepGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFFA855F7), Color(0xFF9333EA))),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(18.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Box(
                                    modifier = Modifier.size(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Brush.radialGradient(listOf(Color(0xFF7C3AED), Color(0xFF3B1065)))),
                                    contentAlignment = Alignment.Center
                                ) { Text("🧬", fontSize = 24.sp) }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Duygusal Profil",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.3).sp
                                    )
                                    Text(
                                        "Başlangıç duygu durumunu incele ve düzenle",
                                        color = Color(0x80CDA8FF),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (isAnalyzing) {
                            Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    com.example.ui.components.EmochiLoadingSpinner(size = 36.dp)
                                    Spacer(Modifier.height(12.dp))
                                    Text("Yapay Zeka Analiz Ediyor…", color = AppColors.TextMuted, fontSize = 13.sp)
                                }
                            }
                        } else {
                            val res = analysisResult
                            if (analysisError != null) {
                                Text(
                                    text = "⚠ ${analysisError} (Otomatik tahmin uygulandı)",
                                    color = EmochiError,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                )
                            }
                            if (res != null && res.confidence < 60) {
                                Text(
                                    text = "ℹ Güven düşük (${res.confidence}/100) — profili kontrol etmeni öneririz.",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                )
                            }
                            if (res != null) {
                                // Analysis source badge — premium pill
                                val (sourceBadge, sourceColor) = when (res.analysisSource) {
                                    "llm"     -> Pair("🤖 Yapay Zeka Analizi", Color(0xFF10B981))
                                    "keyword" -> Pair("📝 Otomatik Tahmin", Color(0xFFF59E0B))
                                    else      -> Pair("✏ Kullanıcı Düzenlemesi", Color(0xFF60A5FA))
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                            .background(sourceColor.copy(alpha = 0.12f))
                                            .border(0.8.dp, sourceColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) { Text(sourceBadge, color = sourceColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                                    if (res.confidence < 60) {
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFF59E0B).copy(alpha = 0.12f))
                                                .border(0.8.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) { Text("⚠ Güven: ${res.confidence}/100", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                                    }
                                }
                                Spacer(Modifier.height(12.dp))

                                // Section card helper
                                @Composable
                                fun SectionCard(title: String, emoji: String, content: @Composable () -> Unit) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                                            .border(1.dp, Brush.linearGradient(listOf(Color(0xFF3B1065), Color(0xFF2E1052))), RoundedCornerShape(16.dp))
                                            .padding(14.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text(emoji, fontSize = 14.sp)
                                                Text(title, color = Color(0xFFCDA8FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(Modifier.height(10.dp))
                                            content()
                                        }
                                    }
                                }

                                // Relationship type selector
                                SectionCard("İlişki Türü", "💞") {
                                    val relTypes = BaselineEmotionEngine.VALID_REL_TYPES.toList()
                                    val relLabels = mapOf(
                                        "parent" to "Ebeveyn", "sibling" to "Kardeş", "friend" to "Arkadaş",
                                        "romantic" to "Sevgili", "ex_romantic" to "Eski Sevgili",
                                        "conflicted_romantic" to "Çatışmalı Sevgili", "rival" to "Rakip",
                                        "enemy" to "Düşman", "stranger" to "Yabancı", "authority" to "Otorite",
                                        "mentor" to "Mentor", "subordinate" to "Ast", "companion_creature" to "Yaratık/Evcil",
                                        "other" to "Diğer"
                                    )
                                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(relTypes) { rt ->
                                            val isSelected = reviewRelType == rt
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(
                                                        if (isSelected)
                                                            Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                                                        else
                                                            Brush.linearGradient(listOf(Color(0xFF1A0D35), Color(0xFF0E0820)))
                                                    )
                                                    .border(
                                                        if (isSelected) 1.5.dp else 1.dp,
                                                        if (isSelected)
                                                            Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF7C3AED)))
                                                        else
                                                            Brush.linearGradient(listOf(Color(0x40C084FC), Color(0x204C1D95))),
                                                        RoundedCornerShape(14.dp)
                                                    )
                                                    .clickable { reviewRelType = rt; userEditedFields = userEditedFields + "relationshipType" }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    relLabels[rt] ?: rt,
                                                    color = if (isSelected) Color.White else Color(0xB0CDA8FF),
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))

                                // Big Five sliders
                                SectionCard("Kişilik (Big Five)", "🧠") {
                                    val bigFiveLabels = listOf(
                                        "openness" to "Açıklık",
                                        "conscientiousness" to "Sorumluluk",
                                        "extraversion" to "Dışadönüklük",
                                        "agreeableness" to "Uyumluluk",
                                        "neuroticism" to "Nevrotiklik"
                                    )
                                    bigFiveLabels.forEach { (key, label) ->
                                        val value = reviewBigFive[key] ?: 50
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Text(label, color = Color(0x90CDA8FF), fontSize = 11.sp, modifier = Modifier.width(100.dp))
                                            Slider(
                                                value = value.toFloat(),
                                                onValueChange = {
                                                    reviewBigFive = reviewBigFive + (key to it.toInt())
                                                    userEditedFields = userEditedFields + "bigFive"
                                                },
                                                valueRange = 0f..100f,
                                                modifier = Modifier.weight(1f),
                                                colors = SliderDefaults.colors(
                                                    thumbColor = AppColors.PrimaryViolet,
                                                    activeTrackColor = AppColors.PurpleLight,
                                                    inactiveTrackColor = Color(0x30A855F7)
                                                )
                                            )
                                            Box(
                                                modifier = Modifier.width(34.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0x30A855F7))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                                contentAlignment = Alignment.Center
                                            ) { Text("$value", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))

                                // Attachment style
                                SectionCard("Bağlanma Stili", "🔗") {
                                    val attachStyles = listOf("secure" to "Güvenli", "anxious" to "Kaygılı", "avoidant" to "Kaçınan", "disorganized" to "Düzensiz")
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                        attachStyles.forEach { (key, label) ->
                                            val isSel = reviewAttachment == key
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isSel) Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                                                        else Brush.linearGradient(listOf(Color(0xFF1A0D35), Color(0xFF0E0820)))
                                                    )
                                                    .border(
                                                        if (isSel) 1.5.dp else 1.dp,
                                                        if (isSel) Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF7C3AED)))
                                                        else Brush.linearGradient(listOf(Color(0x40C084FC), Color(0x204C1D95))),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { reviewAttachment = key; userEditedFields = userEditedFields + "attachmentStyle" }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    label,
                                                    color = if (isSel) Color.White else Color(0xB0CDA8FF),
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))

                                // Dominant emotion
                                SectionCard("Baskın Duygu", "💫") {
                                com.example.ui.components.EmochiTextField(
                                    value = reviewDominantEmotion,
                                    onValueChange = { reviewDominantEmotion = it; userEditedFields = userEditedFields + "dominantEmotion" },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                    // removed old OutlinedTextField colors block below
                                )
                                }
                                Spacer(Modifier.height(8.dp))

                                // Speech style
                                SectionCard("Konuşma Tarzı", "🗣️") {
                                com.example.ui.components.EmochiTextField(
                                    value = reviewSpeechStyle,
                                    onValueChange = { reviewSpeechStyle = it; userEditedFields = userEditedFields + "speechStyle" },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = false,
                                    minLines = 2,
                                    maxLines = 3
                                ) }
                                Spacer(Modifier.height(8.dp))

                                // Sensitive triggers
                                SectionCard("Hassas Konular", "⚡") {
                                reviewTriggers.forEachIndexed { idx, trig ->
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        com.example.ui.components.EmochiTextField(
                                            value = trig.triggerTopic,
                                            onValueChange = { v ->
                                                reviewTriggers = reviewTriggers.toMutableList().also { it[idx] = trig.copy(triggerTopic = v) }
                                                userEditedFields = userEditedFields + "sensitiveTriggers"
                                            },
                                            modifier = Modifier.weight(1f),
                                            placeholder = "Konu",
                                            singleLine = true
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Close, contentDescription = "Sil",
                                            tint = EmochiError, modifier = Modifier.size(20.dp).clickable {
                                                if (reviewTriggers.size > 1) {
                                                    reviewTriggers = reviewTriggers.toMutableList().also { it.removeAt(idx) }
                                                    userEditedFields = userEditedFields + "sensitiveTriggers"
                                                }
                                            }
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                                if (reviewTriggers.size < 3) {
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF1E0D3A), Color(0xFF130828))))
                                            .border(0.8.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9))), RoundedCornerShape(10.dp))
                                            .clickable {
                                                reviewTriggers = reviewTriggers + SensitiveTriggerEntity(
                                                    botId = "", triggerTopic = "", sourceDescription = "", intensityMultiplier = 1.8
                                                )
                                                userEditedFields = userEditedFields + "sensitiveTriggers"
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) { Text("+ Konu Ekle", color = AppColors.PrimaryViolet, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                                }
                                }  // end SectionCard "Hassas Konular"
                                Spacer(Modifier.height(14.dp))

                                // Reanalyze / reset buttons
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = {
                                            val draft = buildCurrentDraft()
                                            scope.launch {
                                                isAnalyzing = true
                                                analysisError = null
                                                userEditedFields = emptySet()
                                                try {
                                                    val r = onAnalyzeBaseline?.invoke(draft)
                                                        ?: BaselineEmotionEngine.calculateDeterministicFallback(
                                                            botId = draft.id, aiName = draft.aiName,
                                                            personality = draft.aiPersonality, scenario = draft.scenario,
                                                            userCharName = draft.userCharName, userCharDesc = draft.userCharDesc
                                                        )
                                                    analysisResult = r
                                                    reviewRelType = r.relationshipType
                                                    reviewAttachment = r.personalityProfile.attachmentStyle
                                                    reviewSpeechStyle = r.speechPattern
                                                    reviewDominantEmotion = r.baselineEmotionState.dominantEmotion
                                                    reviewTriggers = r.sensitiveTriggers.toMutableList()
                                                    reviewBigFive = mapOf(
                                                        "openness" to r.personalityProfile.openness,
                                                        "conscientiousness" to r.personalityProfile.conscientiousness,
                                                        "extraversion" to r.personalityProfile.extraversion,
                                                        "agreeableness" to r.personalityProfile.agreeableness,
                                                        "neuroticism" to r.personalityProfile.neuroticism
                                                    )
                                                    reviewAxes = mapOf(
                                                        "affection" to r.baselineEmotionState.affection,
                                                        "respect" to r.baselineEmotionState.relationshipAxes.respectScore,
                                                        "comfort" to r.baselineEmotionState.relationshipAxes.comfortScore,
                                                        "resentment" to r.baselineEmotionState.relationshipAxes.resentmentScore
                                                    )
                                                } catch (_: Exception) {
                                                    analysisError = "Yeniden analiz başarısız."
                                                } finally { isAnalyzing = false }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF1E0D3A), Color(0xFF130828))))
                                            .border(0.8.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9))), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) { Text("Yeniden Analiz Et", color = AppColors.PrimaryViolet, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }

                                    TextButton(
                                        onClick = {
                                            val fb = BaselineEmotionEngine.calculateDeterministicFallback(
                                                botId = "", aiName = aiName, personality = aiPersonality,
                                                scenario = scenario, userCharName = userCharName, userCharDesc = userCharDesc
                                            )
                                            analysisResult = fb
                                            reviewRelType = fb.relationshipType
                                            reviewAttachment = fb.personalityProfile.attachmentStyle
                                            reviewSpeechStyle = fb.speechPattern
                                            reviewDominantEmotion = fb.baselineEmotionState.dominantEmotion
                                            reviewTriggers = fb.sensitiveTriggers.toMutableList()
                                            reviewBigFive = mapOf(
                                                "openness" to fb.personalityProfile.openness,
                                                "conscientiousness" to fb.personalityProfile.conscientiousness,
                                                "extraversion" to fb.personalityProfile.extraversion,
                                                "agreeableness" to fb.personalityProfile.agreeableness,
                                                "neuroticism" to fb.personalityProfile.neuroticism
                                            )
                                            reviewAxes = mapOf(
                                                "affection" to fb.baselineEmotionState.affection,
                                                "respect" to fb.baselineEmotionState.relationshipAxes.respectScore,
                                                "comfort" to fb.baselineEmotionState.relationshipAxes.comfortScore,
                                                "resentment" to fb.baselineEmotionState.relationshipAxes.resentmentScore
                                            )
                                            userEditedFields = emptySet()
                                            analysisError = null
                                        },
                                        modifier = Modifier.weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                                            .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) { Text("Varsayılana Dön", color = AppColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                                }
                                Spacer(Modifier.height(8.dp))
                            } else {
                                // Still no result after loading (shouldn't happen, but guard)
                                Text("Analiz bekleniyor…", color = AppColors.TextMuted, fontSize = 13.sp)
                            }
                        }
                    }

                    5 -> {
                        // Step 6: Writing Style & Tone matching screenshot 5/5
                        Text(
                            text = "Yazım Tarzı & Ton",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yanıtların biçimini ve tematik yoğunluğunu özelleştirin.",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            "Yazım Formatı",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        WizardOptionCard(
                            title = "RP / Anlatı Tarzı",
                            desc = "Üçüncü şahıs roman anlatımı. Ortam ve mimik betimlemeleri + diyaloglar.",
                            iconVector = Icons.Default.Book,
                            isSelected = writingStyle == "rp",
                            onClick = { writingStyle = "rp" }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        WizardOptionCard(
                            title = "Sade Sohbet",
                            desc = "Doğal mesajlaşma tarzı. Kısa ve samimi yanıtlar.",
                            iconVector = Icons.Default.Person,
                            isSelected = writingStyle == "chat",
                            onClick = { writingStyle = "chat" }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            "Tema Yoğunluğu",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        WizardOptionCard(
                            title = "Normal",
                            desc = "Dengeli ve yumuşak ton.",
                            iconVector = Icons.Default.Settings,
                            isSelected = intensity == "normal",
                            onClick = { intensity = "normal" }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        WizardOptionCard(
                            title = "Yoğun",
                            desc = "Gerilimi ve karanlık temalar yumuşatılmadan aktarılır (Ginsel içerik hariç).",
                            iconVector = Icons.Default.AutoAwesome,
                            isSelected = intensity == "intense",
                            onClick = { intensity = "intense" }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            "Hikâye Başlangıç Tarihi (İsteğe Bağlı)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Boş bırakırsanız bot yalnızca Gün #N sayacını kullanır, gerçek tarih göstermez.",
                            color = AppColors.TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        com.example.ui.components.EmochiTextField(
                            value = storyStartDate,
                            onValueChange = { storyStartDate = it },
                            placeholder = "ör: 2045-06-15",
                            label = "yyyy-AA-gg",
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            "Görünürlük & Yayınlama",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        WizardOptionCard(
                            title = "🌐 Herkese Açık (Keşfette Yayınla)",
                            desc = "Botunuz Velora Keşfet sekmesinde tüm toplulukla paylaşılır.",
                            iconVector = Icons.Default.Public,
                            isSelected = isPublic,
                            onClick = { isPublic = true }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        WizardOptionCard(
                            title = "🔒 Sadece Bana Özel",
                            desc = "Bot sadece sizin cihazınızda ve sohbetlerinizde gözükür.",
                            iconVector = Icons.Default.Lock,
                            isSelected = !isPublic,
                            onClick = { isPublic = false }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardModeCard(
    title: String,
    subtitle1: String,
    subtitle2: String,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    glowAlpha: Float,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "modeCardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        // Outer glow when selected — using glowAlpha
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(cardShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                AppColors.PrimaryViolet.copy(alpha = glowAlpha * 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 1.8.dp else 1.dp,
                    brush = if (isSelected) {
                        Brush.sweepGradient(
                            listOf(
                                AppColors.PurpleLight,
                                AppColors.PrimaryViolet,
                                AppColors.PurpleAccent,
                                AppColors.PurpleLight
                            )
                        )
                    } else {
                        Brush.linearGradient(listOf(Color(0x307C3AED), Color(0x203B0764)))
                    },
                    shape = cardShape
                )
                .clip(cardShape)
                .background(
                    if (isSelected) {
                        Brush.verticalGradient(listOf(Color(0x952E105C), Color(0x95150833)))
                    } else {
                        Brush.verticalGradient(listOf(Color(0x800E0620), Color(0x80080314)))
                    }
                )
                .clickable(interactionSource = interaction, indication = null) { onClick() }
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon box — glow ring when selected
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected)
                                Brush.radialGradient(listOf(Color(0xFF3B1065), Color(0xFF1A0840)))
                            else
                                Brush.radialGradient(listOf(Color(0xFF1A0840), Color(0xFF0C0420)))
                        )
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected)
                                Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFF7C3AED)))
                            else
                                Brush.linearGradient(listOf(Color(0x307C3AED), Color(0x203B0764))),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFFCDA8FF) else Color(0x80A78BFA),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle1,
                        color = if (isSelected) Color(0xFFB99AF8) else AppColors.TextMuted,
                        fontSize = 12.5.sp,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = subtitle2,
                        color = if (isSelected) Color(0xFFB99AF8) else AppColors.TextMuted,
                        fontSize = 12.5.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Animated check vs arrow
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0x50A78BFA),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WizardOptionCard(
    title: String,
    desc: String,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.8.dp else 1.dp,
                brush = if (isSelected) {
                    Brush.sweepGradient(
                        listOf(
                            AppColors.PurpleLight,
                            AppColors.PrimaryViolet,
                            AppColors.PurpleAccent,
                            AppColors.PurpleLight
                        )
                    )
                } else {
                    Brush.linearGradient(listOf(Color(0x307C3AED), Color(0x203B0764)))
                },
                shape = cardShape
            )
            .clip(cardShape)
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        listOf(Color(0x902E105C), Color(0x90150833))
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(Color(0x800E0620), Color(0x80080314))
                    )
                }
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected)
                            Brush.radialGradient(listOf(Color(0xFF3B1065), Color(0xFF1E0840)))
                        else
                            Brush.radialGradient(listOf(Color(0xFF1A0D35), Color(0xFF0E0620)))
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        brush = if (isSelected)
                            Brush.sweepGradient(listOf(
                                Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFFA855F7), Color(0xFF9333EA)
                            ))
                        else
                            Brush.linearGradient(listOf(Color(0x507C3AED), Color(0x302E1065))),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFFCDA8FF) else Color(0x80A78BFA),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = desc,
                    color = AppColors.TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Checkmark Radio Button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AppColors.PurpleAccent else Color.Transparent)
                    .border(
                        width = 1.5.dp,
                        color = if (isSelected) AppColors.PrimaryViolet else Color(0x607C3AED),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun customWizardTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0x900E0622),
    unfocusedContainerColor = Color(0x800E0620),
    focusedBorderColor = AppColors.PrimaryViolet,
    unfocusedBorderColor = Color(0x507C3AED),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = AppColors.PrimaryViolet
)
