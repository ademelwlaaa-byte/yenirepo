package com.example.ui.screens

import com.example.ui.theme.AppColors
import com.example.ui.theme.EmochiGradients
import com.example.ui.theme.EmochiBackground
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.collectAsState
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BotEntity
import com.example.data.local.MessageEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.KeyCharacter
import com.example.ui.components.BotQuickProfileSheet
import com.example.ui.components.BotSettingsModal
import com.example.ui.components.MoodColors
import com.example.ui.components.OrbView
import com.example.ui.components.TypingDots
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.EmochiError
import com.example.ui.theme.EmochiSurface

// Helper function to format spoken dialogue and narrative actions in AI messages
fun formatNarrativeText(text: String): AnnotatedString {
    val clean = cleanCopyText(text)
    return buildAnnotatedString {
        // Regex for quoted dialogue ("..." or “...”) and asterisk actions (*...*)
        val regex = Regex("(\"[^\"]*\")|(“[^”]*”)|(«[^»]*»)|(\\*[^\\*]*\\*)")
        var lastIdx = 0
        val matches = regex.findAll(clean)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            if (start > lastIdx) {
                append(clean.substring(lastIdx, start))
            }
            val matchedStr = clean.substring(start, end)
            if (matchedStr.startsWith("*") && matchedStr.endsWith("*")) {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.TextSecondary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                ) {
                    append(matchedStr)
                }
            } else {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.PrimaryViolet, // Bright neon purple for dialogue quotes
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(matchedStr)
                }
            }
            lastIdx = end
        }
        if (lastIdx < clean.length) {
            append(clean.substring(lastIdx))
        }
    }
}

@Composable
fun RpAtmosphericBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Base dark purple/black canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Hex_07030C)
        )

        // Generated atmospheric gothic wallpaper image (richer visibility)
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_chat_bg_gothic),
            contentDescription = "Gothic Chat Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.65f
        )

        // Canvas for subtle floating glowing particles / star dust
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val random = java.util.Random(42)
            for (i in 0..35) {
                val x = random.nextFloat() * width
                val y = random.nextFloat() * height
                val alpha = random.nextFloat() * 0.35f + 0.1f
                val radius = random.nextFloat() * 1.6f + 0.8f
                drawCircle(
                    color = AppColors.PurpleSoft.copy(alpha = alpha),
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
        // Very subtle vignette overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x6005030A),
                            Color(0x1005030A),
                            Color(0x7005030A)
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    bot: BotEntity,
    messages: List<MessageEntity>,
    userSettings: UserSettingsEntity?,
    isSending: Boolean,
    errorMessage: String?,
    keyCharacters: List<KeyCharacter>,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    affectionEvents: List<com.example.data.local.AffectionEventEntity> = emptyList(),
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onRegenerate: () -> Unit,
    onEditMessage: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onSaveBotProfile: (BotEntity, List<KeyCharacter>) -> Unit,
    onResetChat: (Boolean) -> Unit,
    onDeleteBot: () -> Unit,
    onSpeakText: ((String) -> Unit)? = null,
    isSpeaking: Boolean = false,
    onStopSpeaking: () -> Unit = {},
    onEnsureOpeningMessage: () -> Unit = {},
    onClearError: () -> Unit = {},
    onRetryMessage: ((String) -> Unit)? = null,
    onRetryFromBanner: (() -> Unit)? = null,
    onRewindToMessage: ((String) -> Unit)? = null,
    onCloneBot: (() -> Unit)? = null,
    streamingMsgId: String? = null,
    streamingText: String? = null
) {
    var inputText by remember { mutableStateOf("") }
    var showBotSettings by remember { mutableStateOf(false) }
    var showQuickProfile by remember { mutableStateOf(false) }
    var showBackgroundPicker by remember { mutableStateOf(false) }

    var editingMessageId by remember { mutableStateOf<String?>(null) }
    var editingText by remember { mutableStateOf("") }
    var expandedMenuMessageId by remember { mutableStateOf<String?>(null) }
    var expandedUserMenuMessageId by remember { mutableStateOf<String?>(null) }
    var rewindTargetMessageId by remember { mutableStateOf<String?>(null) }
    var isStatsExpanded by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var localSearchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val starredMessages by viewModel.starredMessages.collectAsState()
    var showStarredSheet by remember { mutableStateOf(false) }
    var activeMilestone by remember { mutableStateOf<com.example.data.local.MilestoneEntity?>(null) }
    // Reaction picker state
    var reactionPickerMessageId by remember { mutableStateOf<String?>(null) }
    val reactionEmojis = listOf("❤️", "😂", "😮", "😢", "👍", "🔥")
    val milestoneEvent = viewModel.milestoneEvent

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // Milestone event collector
    LaunchedEffect(Unit) {
        milestoneEvent.collect { milestone ->
            activeMilestone = milestone
            kotlinx.coroutines.delay(4000)
            activeMilestone = null
        }
    }

    // Stop speaking immediately when exiting chat
    DisposableEffect(Unit) {
        onDispose {
            onStopSpeaking()
        }
    }

    // Automatically ensure opening message exists if list is empty (except for book mode)
    LaunchedEffect(bot.id, messages.isEmpty()) {
        if (messages.isEmpty() && !isSending && bot.mode != "book") {
            onEnsureOpeningMessage()
        }
    }

    // Scroll to bottom on new messages
    LaunchedEffect(messages.size, isSending) {
        val totalItems = messages.size + if (isSending) 1 else 0
        if (totalItems > 0) {
            try {
                listState.animateScrollToItem(totalItems - 1)
            } catch (_: Exception) {}
        }
    }

    val isUniverse = bot.mode == "universe"
    val displayName = if (isUniverse) bot.universeName.ifBlank { "Evren" } else bot.aiName.ifBlank { "Karakter" }

    // --- Rich Animations Setup ---
    val infiniteTransition = rememberInfiniteTransition(label = "chatScreenAnimations")

    // 1. Breathing Glow Alpha Pulse (0.35f to 0.90f)
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlowAlpha"
    )

    // 2. Tactile Pulse Scale (0.96f to 1.04f)
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // 3. Rotating Sweep Angle for Neon Borders (0f to 360f)
    val sweepRotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepRotateAngle"
    )

    // Mood detection based on last AI text
    val lastAiText = messages.lastOrNull { it.role == "assistant" }?.text ?: ""
    val detectedMood = remember(lastAiText) {
        val lower = lastAiText.lowercase()
        when {
            listOf("harika", "süper", "mutlu", "güldü", "🎉", "😊").any { lower.contains(it) } -> "joy"
            listOf("canım", "değerlisin", "sarıl", "güzelim", "sevgi", "💕", "❤️").any { lower.contains(it) } -> "warm"
            listOf("üzgün", "üzülüyorum", "kötü", "yalnız", "😢").any { lower.contains(it) } -> "sad"
            listOf("tehlike", "sinirli", "öfke", "korktu").any { lower.contains(it) } -> "tense"
            listOf("merak", "acaba", "ilginç").any { lower.contains(it) } -> "curious"
            else -> "calm"
        }
    }

    val hue = MoodColors.getMoodHue(detectedMood)
    val emotionState = remember(bot.emotionState) { com.example.data.local.EmotionState.fromJson(bot.emotionState) }
    val realMoodDisplay = "${emotionState.getMoodEmoji()} ${emotionState.mood.replaceFirstChar { it.uppercase() }}"

    val lastAiIndex = messages.indexOfLast { it.role == "assistant" }

    val isBookMode = remember(bot) {
        bot.mode == "book" ||
        bot.id == "preset_aiden_mcu_cosmic" ||
        bot.aiName.contains("Kitap") ||
        bot.aiPersonality.contains("KİTAP MODU") ||
        bot.scenario.contains("KİTAP MODU")
    }

    if (isBookMode) {
        BookReaderView(
            bot = bot,
            messages = messages,
            isSending = isSending,
            errorMessage = errorMessage,
            onBack = onBack,
            onSendMessage = onSendMessage,
            onEnsureOpeningMessage = onEnsureOpeningMessage,
            onResetChat = { onResetChat(it) },
            onClearError = onClearError
        )
    } else {
    Box(modifier = Modifier.fillMaxSize()) {
        val isImeVisible = WindowInsets.isImeVisible

        // Base atmospheric wallpaper (full bleed across entire screen)
        RpAtmosphericBackground()

        // Bot background wallpaper model resolution
        val bgModel: Any = remember(bot.chatBgUrl, bot.avatarUrl, bot.id) {
            when {
                bot.chatBgUrl == "USE_AVATAR" -> {
                    when {
                        bot.avatarUrl.isNotBlank() -> bot.avatarUrl
                        bot.id.contains("aiden_zoktay") -> com.example.R.drawable.aiden_zoktay
                        bot.id.contains("aiden_dispatch") -> com.example.R.drawable.aiden_dispatch
                        bot.id.contains("aiden_joker") -> com.example.R.drawable.aiden_joker
                        bot.id.contains("aiden_doctor") -> com.example.R.drawable.aiden_doctor
                        bot.id.contains("aiden_obsidian") -> com.example.R.drawable.aiden_obsidian
                        bot.id == "starter_ayla" || bot.aiName.equals("Ayla", ignoreCase = true) -> com.example.R.drawable.img_ayla_avatar
                        bot.id == "starter_aetheria" || bot.universeName.contains("Aetheria", ignoreCase = true) -> com.example.R.drawable.img_aetheria_universe
                        else -> com.example.R.drawable.img_chat_bg_gothic
                    }
                }
                bot.chatBgUrl.isNotBlank() -> bot.chatBgUrl
                else -> com.example.R.drawable.img_chat_bg_gothic
            }
        }

        coil.compose.AsyncImage(
            model = bgModel,
            contentDescription = "Sohbet Arka Planı",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.65f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x90070512),
                            Color(0x35070512),
                            Color(0x80070512),
                            Color(0xFA070512)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    EmochiSurface.copy(alpha = 0.97f),
                                    EmochiSurface.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .statusBarsPadding()
                        .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = if (isImeVisible) 4.dp else 6.dp)
                    ) {
                        // Back button (Left) — premium glass
                        val backInteraction = remember { MutableInteractionSource() }
                        val backPressed by backInteraction.collectIsPressedAsState()
                        val backScale by animateFloatAsState(
                            targetValue = if (backPressed) 0.87f else 1f,
                            animationSpec = tween(80),
                            label = "backScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(if (isImeVisible) 34.dp else 38.dp)
                                .align(Alignment.CenterStart)
                                .graphicsLayer { scaleX = backScale; scaleY = backScale }
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1C1040), Color(0xFF0F0828))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.2.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF6D28D9))
                                        )
                                    ),
                                    RoundedCornerShape(13.dp)
                                )
                                .clickable(interactionSource = backInteraction, indication = null) { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFFCDA8FF),
                                modifier = Modifier.size(if (isImeVisible) 17.dp else 19.dp)
                            )
                        }

                        // Truly Centered Stack (Avatar + Bot Name + Mood Emoji) - Hidden when keyboard is open
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !isImeVisible,
                            enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                            exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .then(
                                        if (bot.mode == "personal") {
                                            Modifier.clickable { isStatsExpanded = !isStatsExpanded }
                                        } else Modifier
                                    )
                                    .padding(horizontal = 50.dp)
                            ) {
                                // Avatar Box
                                Box(
                                    contentAlignment = Alignment.BottomEnd,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0x30A855F7).copy(alpha = pulseGlowAlpha * 0.5f))
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .align(Alignment.Center)
                                            .clip(CircleShape)
                                            .border(
                                                width = 1.8.dp,
                                                brush = Brush.sweepGradient(
                                                    listOf(AppColors.PurpleLight, AppColors.PrimaryViolet, AppColors.PurpleAccent, AppColors.PurpleLight)
                                                ),
                                                shape = CircleShape
                                            )
                                            .padding(1.5.dp)
                                    ) {
                                        if (bot.id == "starter_ayla" || bot.aiName.equals("Ayla", ignoreCase = true)) {
                                            androidx.compose.foundation.Image(
                                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_ayla_avatar),
                                                contentDescription = displayName,
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                                            )
                                        } else if (bot.id == "starter_aetheria" || bot.universeName.contains("Aetheria", ignoreCase = true) || bot.aiName.contains("Aetheria", ignoreCase = true)) {
                                            androidx.compose.foundation.Image(
                                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_aetheria_universe),
                                                contentDescription = displayName,
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                                            )
                                        } else if (bot.avatarUrl.isNotBlank()) {
                                            com.example.ui.components.BotAvatarImage(
                                                avatarUrl = bot.avatarUrl,
                                                contentDescription = displayName,
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                fallback = { OrbView(hue = hue, size = 38.dp) }
                                            )
                                        } else {
                                            OrbView(hue = hue, size = 38.dp)
                                        }
                                    }

                                    // Online badge — pulsing glow ring + solid green dot
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(AppColors.Emerald.copy(alpha = pulseGlowAlpha * 0.55f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        colors = listOf(AppColors.LightGreen, AppColors.Emerald)
                                                    )
                                                )
                                                .border(1.5.dp, EmochiSurface, CircleShape)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // Bot Name (+ Mood Emoji on SAME LINE for Personal Mode)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = displayName,
                                        color = Color.White,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    if (bot.mode == "personal") {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSending) "✨" else emotionState.getMoodEmoji(),
                                            fontSize = 13.5.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Right Action Buttons (Wallpaper + Settings)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            AnimatedVisibility(
                                visible = !isImeVisible,
                                enter = fadeIn(tween(200)),
                                exit = fadeOut(tween(200))
                            ) {
                                val wpInteraction = remember { MutableInteractionSource() }
                                val wpPressed by wpInteraction.collectIsPressedAsState()
                                val wpScale by animateFloatAsState(
                                    targetValue = if (wpPressed) 0.88f else 1f,
                                    animationSpec = tween(80),
                                    label = "wpScale"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .graphicsLayer { scaleX = wpScale; scaleY = wpScale }
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF1C1040), Color(0xFF0F0828))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.2.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF9333EA), Color(0xFF4C1D95), Color(0xFF7C3AED))
                                                )
                                            ),
                                            RoundedCornerShape(13.dp)
                                        )
                                        .clickable(interactionSource = wpInteraction, indication = null) { showBackgroundPicker = true }
                                        .testTag("wallpaper_picker_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Wallpaper,
                                        contentDescription = "Arka Plan",
                                        tint = Color(0xFFCDA8FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Search button
                            AnimatedVisibility(visible = !isImeVisible, enter = fadeIn(tween(200)), exit = fadeOut(tween(200))) {
                                val srInteraction = remember { MutableInteractionSource() }
                                val srPressed by srInteraction.collectIsPressedAsState()
                                val srScale by animateFloatAsState(if (srPressed) 0.88f else 1f, tween(80), label = "srScale")
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .graphicsLayer { scaleX = srScale; scaleY = srScale }
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(if (showSearchBar) Brush.linearGradient(listOf(Color(0xFF3B0F70), Color(0xFF1A0D38))) else Brush.linearGradient(listOf(Color(0xFF1C1040), Color(0xFF0F0828))))
                                        .border(BorderStroke(1.2.dp, Brush.linearGradient(if (showSearchBar) listOf(Color(0xFFA855F7), Color(0xFF7C3AED)) else listOf(Color(0xFF6D28D9), Color(0xFF4C1D95), Color(0xFF7C3AED)))), RoundedCornerShape(13.dp))
                                        .clickable(interactionSource = srInteraction, indication = null) {
                                            showSearchBar = !showSearchBar
                                            if (!showSearchBar) { localSearchQuery = ""; viewModel.clearSearch() }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Search, "Ara", tint = if (showSearchBar) Color(0xFFA855F7) else Color(0xFFCDA8FF), modifier = Modifier.size(18.dp))
                                }
                            }

                            val stInteraction = remember { MutableInteractionSource() }
                            val stPressed by stInteraction.collectIsPressedAsState()
                            val stScale by animateFloatAsState(
                                targetValue = if (stPressed) 0.88f else 1f,
                                animationSpec = tween(80),
                                label = "stScale"
                            )
                            Box(
                                modifier = Modifier
                                    .size(if (isImeVisible) 34.dp else 38.dp)
                                    .graphicsLayer { scaleX = stScale; scaleY = stScale }
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1C1040), Color(0xFF0F0828))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.2.dp,
                                            Brush.linearGradient(
                                                listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF9333EA))
                                            )
                                        ),
                                        RoundedCornerShape(13.dp)
                                    )
                                    .clickable(interactionSource = stInteraction, indication = null) { showBotSettings = true }
                                    .testTag("bot_settings_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Ayarlar",
                                    tint = Color(0xFFB99AF8),
                                    modifier = Modifier.size(if (isImeVisible) 17.dp else 18.dp)
                                )
                            }
                        }
                    }

                    // Search bar (slide-in)
                    AnimatedVisibility(
                        visible = showSearchBar,
                        enter = slideInVertically(tween(220, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(220)),
                        exit = slideOutVertically(tween(180)) { -it } + fadeOut(tween(180))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF140D2E))
                                    .border(1.dp, Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95))), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                if (localSearchQuery.isEmpty()) {
                                    Text("Mesajlarda ara...", color = Color(0x60CDA8FF), fontSize = 13.sp)
                                }
                                androidx.compose.foundation.text.BasicTextField(
                                    value = localSearchQuery,
                                    onValueChange = { q ->
                                        localSearchQuery = q
                                        viewModel.setSearchQuery(q.ifBlank { null })
                                    },
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                                    singleLine = true,
                                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFFA855F7)),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (localSearchQuery.isNotEmpty()) {
                                val clrInteraction = remember { MutableInteractionSource() }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x30F87171))
                                        .border(1.dp, Color(0x60F87171), RoundedCornerShape(10.dp))
                                        .clickable(interactionSource = clrInteraction, indication = null) {
                                            localSearchQuery = ""
                                            viewModel.clearSearch()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Close, "Temizle", tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Search results summary
                    AnimatedVisibility(
                        visible = showSearchBar && searchResults.isNotEmpty(),
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(150))
                    ) {
                        Text(
                            text = "${searchResults.size} sonuç bulundu",
                            color = Color(0xFF9D74E8),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
                        )

                    // Expandable Header Stats Banner (Personal Mode Only)
                    AnimatedVisibility(
                        visible = !isImeVisible && isStatsExpanded && bot.mode == "personal",
                        enter = fadeIn(tween(250)) + expandVertically(tween(250, easing = FastOutSlowInEasing)),
                        exit = fadeOut(tween(180)) + shrinkVertically(tween(180))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF120B28), Color(0xFF0C0720))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0x809333EA), Color(0x404C1D95), Color(0x807C3AED))
                                        )
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Model pill — left
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(Color(0xFFC084FC), Color(0xFF7C3AED))
                                                )
                                            )
                                    )
                                    Text(
                                        text = userSettings?.selectedModel?.ifBlank { "Gemini 2.0 Flash" } ?: "Gemini 2.0 Flash",
                                        color = Color(0xFFCDA8FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // Stats badges — right
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Affection badge — warm pink gradient
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0x35E11D48))
                                            .border(
                                                BorderStroke(
                                                    0.8.dp,
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFFB7185), Color(0xFFE11D48))
                                                    )
                                                ),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "❤️ ${emotionState.affection}%",
                                            color = Color(0xFFFDA4AF),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    // Trust badge — cool blue gradient
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0x3538BDF8))
                                            .border(
                                                BorderStroke(
                                                    0.8.dp,
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFF38BDF8), Color(0xFF0EA5E9))
                                                    )
                                                ),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "🛡️ ${emotionState.trust}%",
                                            color = Color(0xFF7DD3FC),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isSpeaking && !isImeVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1A0A38), Color(0xFF100626))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0x809333EA), Color(0x404C1D95))
                                        )
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔊 Sesli okunuyor...",
                                color = Color(0xFFCDA8FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x30A855F7))
                                    .border(1.dp, Color(0x50A855F7), RoundedCornerShape(8.dp))
                                    .clickable { onStopSpeaking() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("⏹️ Durdur", color = Color(0xFFCDA8FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // ── Bottom bar infinite animations ──────────────────────────
                val inputInfinite = rememberInfiniteTransition(label = "inputBar")
                val rpButtonGlow by inputInfinite.animateFloat(
                    initialValue = 0.55f, targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "rpGlow"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xD0060412),
                                    Color(0xFF060412)
                                )
                            )
                        )
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                ) {
                    // ── Quick RP chips ──────────────────────────────────────
                    val quickPrompts = listOf(
                        "🎭 Sahneyi Derinleştir" to "Lütfen şu anki sahneyi ve karakterin iç dünyasını daha detaylı, atmosferik bir şekilde betimleyerek yanıt ver.",
                        "💡 Ne Yapmalıyım?" to "Karakter bana bakıp şu anda ne yapmam gerektiğiyle ilgili imalı bir öneride bulunsun.",
                        "🔥 Duyguyu Yükselt" to "Aramızdaki duygusal çekimi ve gerilimi hissettirecek şekilde davran.",
                        "🎲 Sürpriz Hamle" to "Karakter beklenmedik, şaşırtıcı bir tepki versin veya yeni bir olay başlatsın."
                    )
                    val chipScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(chipScrollState)
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPrompts.forEachIndexed { idx, (label, promptText) ->
                            val chipInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            val chipPressed by chipInteraction.collectIsPressedAsState()
                            val chipScale by animateFloatAsState(
                                targetValue = if (chipPressed) 0.94f else 1f,
                                animationSpec = tween(90),
                                label = "chipScale_$idx"
                            )
                            val chipAlpha by animateFloatAsState(
                                targetValue = if (isSending) 0.38f else 1f,
                                animationSpec = tween(200),
                                label = "chipAlpha_$idx"
                            )
                            Box(
                                modifier = Modifier
                                    .graphicsLayer { scaleX = chipScale; scaleY = chipScale; alpha = chipAlpha }
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1C1040), Color(0xFF110A2C))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            Brush.linearGradient(
                                                listOf(Color(0xFFA855F7), Color(0xFF7C3AED), Color(0xFF4C1D95))
                                            )
                                        ),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable(
                                        interactionSource = chipInteraction,
                                        indication = null,
                                        enabled = !isSending
                                    ) { onSendMessage(promptText) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = Color(0xFFCDA8FF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // ── Thin separator ──────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0x40A855F7), Color(0x20C084FC), Color.Transparent)
                                )
                            )
                    )

                    // ── Input row ───────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // +RP✦ button — glowing when active
                        val isRpMode = inputText.trim().let { it.startsWith("*") && it.endsWith("*") && it.length >= 2 }
                        val rpInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val rpPressed by rpInteraction.collectIsPressedAsState()
                        val rpScale by animateFloatAsState(
                            targetValue = if (rpPressed) 0.91f else 1f,
                            animationSpec = tween(100),
                            label = "rpScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .graphicsLayer { scaleX = rpScale; scaleY = rpScale }
                                .clip(CircleShape)
                                .background(
                                    if (isRpMode) Brush.radialGradient(
                                        listOf(Color(0xFF6D28D9), Color(0xFF3B1070))
                                    ) else Brush.verticalGradient(
                                        listOf(Color(0xFF291060), Color(0xFF150830))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        width = if (isRpMode) 2.dp else 1.5.dp,
                                        brush = Brush.sweepGradient(
                                            listOf(
                                                AppColors.PurpleLight.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.7f),
                                                AppColors.PrimaryViolet.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.5f),
                                                AppColors.PurpleAccent.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.7f),
                                                AppColors.PurpleLight.copy(alpha = if (isRpMode) 1f else rpButtonGlow * 0.7f)
                                            )
                                        )
                                    ),
                                    CircleShape
                                )
                                .clickable(interactionSource = rpInteraction, indication = null) {
                                    val trimmed = inputText.trim()
                                    if (trimmed.isBlank()) {
                                        inputText = "*...*"
                                    } else if (trimmed.startsWith("*") && trimmed.endsWith("*") && trimmed.length >= 2) {
                                        inputText = trimmed.substring(1, trimmed.length - 1)
                                    } else {
                                        inputText = "*$trimmed*"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isRpMode) "✦RP" else "+RP✦",
                                color = if (isRpMode) Color.White else AppColors.PurpleLight,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Input field — custom bordered
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (isRpMode) "Eylem veya diyalog...*" else "Bir mesaj yazın...",
                                    color = AppColors.TextSlate.copy(alpha = 0.7f),
                                    fontSize = 13.5.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(22.dp),
                            colors = customTextFieldColors(),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Send button — multi-layer premium
                        val isSendEnabled = inputText.isNotBlank() && !isSending
                        val sendInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        val sendPressed by sendInteraction.collectIsPressedAsState()
                        val sendScale by animateFloatAsState(
                            targetValue = if (sendPressed) 0.88f else 1f,
                            animationSpec = tween(90),
                            label = "sendScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .graphicsLayer { scaleX = sendScale; scaleY = sendScale },
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer pulse halo — only when enabled
                            if (isSendEnabled) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .graphicsLayer {
                                            scaleX = pulseScale
                                            scaleY = pulseScale
                                            alpha = pulseGlowAlpha * 0.5f
                                        }
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(Color(0xFFA855F7), Color(0x007C3AED))
                                            )
                                        )
                                )
                            }
                            // Button core
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSendEnabled) Brush.linearGradient(
                                            listOf(Color(0xFFBB7BFF), Color(0xFF9333EA), Color(0xFF5B21B6))
                                        ) else Brush.linearGradient(
                                            listOf(Color(0x201A0840), Color(0x200C0820))
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.5.dp,
                                            if (isSendEnabled) Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                            else Brush.linearGradient(listOf(Color(0x20A855F7), Color(0x10A855F7)))
                                        ),
                                        CircleShape
                                    )
                                    .clickable(
                                        interactionSource = sendInteraction,
                                        indication = null,
                                        enabled = isSendEnabled
                                    ) {
                                        val text = inputText
                                        inputText = ""
                                        onSendMessage(text)
                                    }
                                    .testTag("send_message_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Gönder",
                                    tint = if (isSendEnabled) Color.White else Color(0x35A78BFA),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                errorMessage?.let { err ->
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = androidx.compose.animation.slideInVertically(
                            animationSpec = tween(280, easing = FastOutSlowInEasing)
                        ) { -it } + fadeIn(tween(200)),
                        exit = androidx.compose.animation.slideOutVertically(
                            animationSpec = tween(220)
                        ) { -it } + fadeOut(tween(160))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF3D1018), Color(0xFF1F0A10))
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(Color(0xFFF87171), Color(0xFFDC2626), Color(0xFFF87171))
                                        )
                                    ),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x30F87171)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            tint = EmochiError,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = err,
                                        color = EmochiError,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (onRetryFromBanner != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0x40F87171))
                                                .border(1.dp, Color(0x60F87171), RoundedCornerShape(8.dp))
                                                .clickable { onRetryFromBanner(); onClearError() }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = "Tekrar",
                                                color = EmochiError,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x25F87171))
                                            .clickable { onClearError() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Kapat",
                                            tint = EmochiError,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                val validMessages = remember(messages) { messages.filter { it.text.isNotBlank() } }
                val lastValidAiIndex = remember(validMessages) { validMessages.indexOfLast { it.role == "assistant" } }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (validMessages.isEmpty() && !isSending) {
                        item(key = "empty_placeholder_card") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF130B2E), Color(0xFF0C0720))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.5.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF9333EA), Color(0xFF4C1D95), Color(0xFF7C3AED))
                                                )
                                            ),
                                            RoundedCornerShape(24.dp)
                                        )
                                        .padding(28.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // Orb visual
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(Color(0xFF9333EA), Color(0xFF4C1D95), Color(0xFF1A0840))
                                                    )
                                                )
                                                .border(
                                                    BorderStroke(
                                                        2.dp,
                                                        Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                                    ),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✨", fontSize = 28.sp)
                                        }
                                        Text(
                                            text = "$displayName henüz selamlamadı",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Text(
                                            text = "Karakterin selamlama mesajını başlatmak için aşağıya dokun ya da hemen yazmaya başla.",
                                            color = AppColors.TextMuted,
                                            fontSize = 12.5.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            lineHeight = 18.sp
                                        )
                                        val emptyBtnInteraction = remember { MutableInteractionSource() }
                                        val emptyBtnPressed by emptyBtnInteraction.collectIsPressedAsState()
                                        val emptyBtnScale by animateFloatAsState(
                                            targetValue = if (emptyBtnPressed) 0.94f else 1f,
                                            animationSpec = tween(90),
                                            label = "emptyBtnScale"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer { scaleX = emptyBtnScale; scaleY = emptyBtnScale }
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFBB7BFF), Color(0xFF9333EA), Color(0xFF5B21B6))
                                                    )
                                                )
                                                .border(
                                                    BorderStroke(
                                                        1.5.dp,
                                                        Brush.linearGradient(
                                                            listOf(Color(0xFFE9D5FF), Color(0xFFA855F7))
                                                        )
                                                    ),
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .clickable(
                                                    interactionSource = emptyBtnInteraction,
                                                    indication = null,
                                                    onClick = onEnsureOpeningMessage
                                                )
                                                .padding(horizontal = 22.dp, vertical = 12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Refresh,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = Color.White
                                                )
                                                Text(
                                                    "👋 Karakter İlk Mesajını Yükle",
                                                    color = Color.White,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(validMessages, key = { _, msg -> msg.id }) { idx, msg ->
                            val isUser = msg.role == "user"
                            val isLastAi = !isUser && idx == lastValidAiIndex
                            val isLastUserUnanswered = isUser && idx == validMessages.lastIndex

                            val timeFormatted = remember(msg.timestamp) {
                                if (msg.timestamp > 0L) {
                                    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                    sdf.format(java.util.Date(msg.timestamp))
                                } else ""
                            }

                            if (editingMessageId == msg.id) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF140B30), Color(0xFF0C071E))
                                            )
                                        )
                                        .border(
                                            BorderStroke(
                                                1.5.dp,
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF4C1D95))
                                                )
                                            ),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0x30A855F7)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("✏️", fontSize = 10.sp)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Mesajı Düzenle",
                                                color = Color(0xFFCDA8FF),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        OutlinedTextField(
                                            value = editingText,
                                            onValueChange = { editingText = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = customTextFieldColors(),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(0x20A855F7))
                                                    .border(1.dp, Color(0x40A855F7), RoundedCornerShape(10.dp))
                                                    .clickable { editingMessageId = null }
                                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                            ) {
                                                Text("Vazgeç", color = AppColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        Brush.linearGradient(
                                                            listOf(Color(0xFF9333EA), Color(0xFF5B21B6))
                                                        )
                                                    )
                                                    .border(
                                                        BorderStroke(
                                                            1.dp,
                                                            Brush.linearGradient(
                                                                listOf(Color(0xFFE9D5FF), Color(0xFFA855F7))
                                                            )
                                                        ),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable {
                                                        val text = editingText
                                                        editingMessageId = null
                                                        onEditMessage(msg.id, text)
                                                    }
                                                    .padding(horizontal = 18.dp, vertical = 7.dp)
                                            ) {
                                                Text("Kaydet", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            } else if (isUser) {
                                // USER BUBBLE (Right Side) matching screenshot
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    // User Name + Avatar Header
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
                                    ) {
                                        Text(
                                            text = bot.userCharName.ifBlank { "Kullanıcı" },
                                            color = Color(0xFFCDA8FF),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))
                                        // User avatar — gradient fill + sweep border
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(Color(0xFF6D28D9), Color(0xFF3B1070))
                                                    )
                                                )
                                                .border(
                                                    BorderStroke(
                                                        1.5.dp,
                                                        Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                                    ),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (bot.userCharName.ifBlank { "K" }).take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    // User Message Box — sıcak derin mor, tek temiz border
                                    val userShape = RoundedCornerShape(topStart = 20.dp, topEnd = 6.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
                                    Box(
                                        modifier = Modifier
                                            .widthIn(min = 80.dp, max = 310.dp)
                                            .border(
                                                BorderStroke(1.5.dp, EmochiGradients.chatBubbleUserBorder),
                                                userShape
                                            )
                                            .clip(userShape)
                                            .background(EmochiGradients.chatBubbleUser)
                                            .clickable {
                                                expandedUserMenuMessageId = if (expandedUserMenuMessageId == msg.id) null else msg.id
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = cleanCopyText(msg.text),
                                                color = Color.White,
                                                fontSize = 14.5.sp,
                                                lineHeight = 21.sp
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Row(
                                                modifier = Modifier.align(Alignment.End),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (timeFormatted.isNotBlank()) {
                                                    Text(
                                                        text = timeFormatted,
                                                        color = AppColors.PrimaryPurple,
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("✓✓", color = AppColors.PrimaryPurple, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (timeFormatted.isNotBlank()) "Şimdi" else "",
                                        color = AppColors.TextSlate,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )

                                    // User Message Action Pills
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .horizontalScroll(rememberScrollState())
                                            .padding(top = 3.dp, bottom = 4.dp)
                                    ) {
                                        @Composable
                                        fun ActionPill(
                                            label: String,
                                            emoji: String,
                                            isDanger: Boolean = false,
                                            onClick: () -> Unit
                                        ) {
                                            val pillInteraction = remember { MutableInteractionSource() }
                                            val pillPressed by pillInteraction.collectIsPressedAsState()
                                            val pillScale by animateFloatAsState(
                                                targetValue = if (pillPressed) 0.93f else 1f,
                                                animationSpec = tween(80),
                                                label = "pillScale"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .graphicsLayer { scaleX = pillScale; scaleY = pillScale }
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isDanger) Brush.linearGradient(
                                                            listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))
                                                        ) else Brush.linearGradient(
                                                            listOf(Color(0xFF190E35), Color(0xFF100825))
                                                        )
                                                    )
                                                    .border(
                                                        BorderStroke(
                                                            1.dp,
                                                            if (isDanger) Brush.linearGradient(
                                                                listOf(Color(0xFFF87171), Color(0xFFDC2626))
                                                            ) else Brush.linearGradient(
                                                                listOf(Color(0x90C084FC), Color(0x406B21A8))
                                                            )
                                                        ),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable(
                                                        interactionSource = pillInteraction,
                                                        indication = null,
                                                        onClick = onClick
                                                    )
                                                    .padding(horizontal = 11.dp, vertical = 6.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(emoji, fontSize = 11.sp)
                                                    Text(
                                                        label,
                                                        color = if (isDanger) Color(0xFFFCA5A5) else AppColors.TextLight,
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }

                                        ActionPill("Kopyala", "📋") {
                                            val cleanText = cleanCopyText(msg.text)
                                            clipboardManager.setText(AnnotatedString(cleanText))
                                            android.widget.Toast.makeText(context, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        ActionPill("Devam Et", "⏪") {
                                            rewindTargetMessageId = msg.id
                                        }
                                        ActionPill("•••", "") {
                                            expandedMenuMessageId = if (expandedMenuMessageId == msg.id) null else msg.id
                                        }

                                        if (expandedMenuMessageId == msg.id) {
                                            ActionPill("Düzenle", "✏️") {
                                                editingMessageId = msg.id
                                                editingText = msg.text
                                                expandedMenuMessageId = null
                                            }
                                            ActionPill("Sil", "🗑️", isDanger = true) {
                                                expandedMenuMessageId = null
                                                onDeleteMessage(msg.id)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // AI BOT BUBBLE (Left Side) matching screenshot
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Avatar — sweep border + soft glow halo
                                    Box(
                                        modifier = Modifier.size(42.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Soft glow halo behind avatar
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(Color(0x30A855F7), Color.Transparent)
                                                    )
                                                )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    BorderStroke(
                                                        1.8.dp,
                                                        Brush.sweepGradient(EmochiGradients.avatarBorderSweep)
                                                    ),
                                                    CircleShape
                                                )
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            com.example.ui.components.BotAvatarImage(
                                                avatarUrl = bot.avatarUrl.takeIf { it.isNotBlank() },
                                                contentDescription = displayName,
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                fallback = {
                                                    androidx.compose.foundation.Image(
                                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_cosmic_orb),
                                                        contentDescription = displayName,
                                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                                    )
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f, fill = false)) {
                                        // Bot Name Label
                                        Text(
                                            text = displayName,
                                            color = AppColors.PrimaryViolet,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                        )

                                        // AI Bubble Box — soğuk derin gece, subtle border
                                        val aiShape = RoundedCornerShape(topStart = 6.dp, topEnd = 22.dp, bottomStart = 22.dp, bottomEnd = 22.dp)
                                        val isThisStreaming = streamingMsgId == msg.id
                                        val displayText = if (isThisStreaming && streamingText != null) streamingText else msg.text

                                        // Cursor blink animation
                                        val cursorInfinite = rememberInfiniteTransition(label = "cursor_${msg.id}")
                                        val cursorAlpha by if (isThisStreaming) {
                                            cursorInfinite.animateFloat(
                                                initialValue = 1f, targetValue = 0f,
                                                animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
                                                label = "cursorBlink"
                                            )
                                        } else {
                                            cursorInfinite.animateFloat(
                                                initialValue = 0f, targetValue = 0f,
                                                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Restart),
                                                label = "cursorOff"
                                            )
                                        }

                                        val isSearchMatch = showSearchBar && localSearchQuery.isNotBlank() && searchResults.any { it.id == msg.id }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    BorderStroke(if (isSearchMatch) 1.5.dp else 1.dp, if (isSearchMatch) Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))) else EmochiGradients.chatBubbleAiBorder),
                                                    aiShape
                                                )
                                                .clip(aiShape)
                                                .background(if (isSearchMatch) Brush.linearGradient(listOf(Color(0xFF1F1408), Color(0xFF0F0B04))) else EmochiGradients.chatBubbleAi)
                                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = formatNarrativeText(displayText) + if (isThisStreaming) "▌".repeat(1).let { c ->
                                                        if (cursorAlpha > 0.5f) "▌" else ""
                                                    } else "",
                                                    color = AppColors.TextWhite,
                                                    fontSize = 14.5.sp,
                                                    lineHeight = 22.sp
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Row(
                                                    modifier = Modifier.align(Alignment.End),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (timeFormatted.isNotBlank()) {
                                                        Text(
                                                            text = timeFormatted,
                                                            color = AppColors.TextMuted,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                    if (!msg.provider.isNullOrBlank()) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        val providerLabel: String
                                                        val providerTextColor: Color
                                                        val providerBg: Color
                                                        val providerBorder: Color
                                                        when {
                                                            msg.provider == "llm7" -> {
                                                                providerLabel = "⚡ LLM7"; providerTextColor = Color(0xFF86EFAC)
                                                                providerBg = Color(0x2022C55E); providerBorder = Color(0x6022C55E)
                                                            }
                                                            msg.provider == "gemini" -> {
                                                                providerLabel = "✨ Gemini"; providerTextColor = Color(0xFFCDA8FF)
                                                                providerBg = Color(0x25A855F7); providerBorder = Color(0x60A855F7)
                                                            }
                                                            msg.provider == "groq" -> {
                                                                providerLabel = "🚀 Groq"; providerTextColor = Color(0xFF93C5FD)
                                                                providerBg = Color(0x203B82F6); providerBorder = Color(0x603B82F6)
                                                            }
                                                            msg.provider == "claude" -> {
                                                                providerLabel = "🧠 Claude"; providerTextColor = Color(0xFFFBBF24)
                                                                providerBg = Color(0x20F59E0B); providerBorder = Color(0x60F59E0B)
                                                            }
                                                            msg.provider == "openai" -> {
                                                                providerLabel = "🟢 OpenAI"; providerTextColor = Color(0xFF86EFAC)
                                                                providerBg = Color(0x2022C55E); providerBorder = Color(0x6022C55E)
                                                            }
                                                            msg.provider!!.startsWith("custom_") -> {
                                                                providerLabel = "🌐 Custom"; providerTextColor = Color(0xFFCDA8FF)
                                                                providerBg = Color(0x257C3AED); providerBorder = Color(0x607C3AED)
                                                            }
                                                            else -> {
                                                                providerLabel = msg.provider!!; providerTextColor = Color(0xFFCDA8FF)
                                                                providerBg = Color(0x25A855F7); providerBorder = Color(0x60A855F7)
                                                            }
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(providerBg)
                                                                .border(0.8.dp, providerBorder, RoundedCornerShape(6.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = providerLabel,
                                                                color = providerTextColor,
                                                                fontSize = 9.5.sp,
                                                                fontWeight = FontWeight.ExtraBold
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "||ı||",
                                                        color = AppColors.PrimaryPurple,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                val isFilterError = msg.text.contains("içerik kısıtlaması", ignoreCase = true) ||
                                                        msg.text.contains("içerik filtresi", ignoreCase = true) ||
                                                        msg.text.contains("content_filter", ignoreCase = true) ||
                                                        msg.text.contains("politikası", ignoreCase = true) ||
                                                        msg.text.contains("refusal", ignoreCase = true)

                                                if (msg.status == "failed" || msg.text.startsWith("⚠️")) {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(12.dp))
                                                            .background(
                                                                Brush.linearGradient(
                                                                    listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))
                                                                )
                                                            )
                                                            .border(
                                                                BorderStroke(
                                                                    1.dp,
                                                                    Brush.linearGradient(
                                                                        listOf(Color(0xFFF87171), Color(0xFFDC2626))
                                                                    )
                                                                ),
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                                    ) {
                                                        if (isFilterError) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    Text("⚠️", fontSize = 13.sp)
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text(
                                                                        "İçerik filtresi engelledi",
                                                                        color = Color(0xFFFCA5A5),
                                                                        fontSize = 11.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                                val filterBtnInteraction = remember { MutableInteractionSource() }
                                                                val filterBtnPressed by filterBtnInteraction.collectIsPressedAsState()
                                                                val filterBtnScale by animateFloatAsState(
                                                                    targetValue = if (filterBtnPressed) 0.92f else 1f,
                                                                    animationSpec = tween(80),
                                                                    label = "fbs"
                                                                )
                                                                Box(
                                                                    modifier = Modifier
                                                                        .graphicsLayer { scaleX = filterBtnScale; scaleY = filterBtnScale }
                                                                        .clip(RoundedCornerShape(10.dp))
                                                                        .background(
                                                                            Brush.linearGradient(
                                                                                listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))
                                                                            )
                                                                        )
                                                                        .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(10.dp))
                                                                        .clickable(
                                                                            interactionSource = filterBtnInteraction,
                                                                            indication = null
                                                                        ) { viewModel.retryWithSoftenedPrompt(bot.id) }
                                                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                                                ) {
                                                                    Text(
                                                                        "🛡️ Farklı Dene",
                                                                        color = Color.White,
                                                                        fontSize = 10.5.sp,
                                                                        fontWeight = FontWeight.ExtraBold
                                                                    )
                                                                }
                                                            }
                                                        } else {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    Text("⚠️", fontSize = 13.sp)
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text(
                                                                        "Mesaj iletilemedi",
                                                                        color = Color(0xFFFCA5A5),
                                                                        fontSize = 11.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                                val retryBtnInteraction = remember { MutableInteractionSource() }
                                                                val retryBtnPressed by retryBtnInteraction.collectIsPressedAsState()
                                                                val retryBtnScale by animateFloatAsState(
                                                                    targetValue = if (retryBtnPressed) 0.92f else 1f,
                                                                    animationSpec = tween(80),
                                                                    label = "rbs"
                                                                )
                                                                Box(
                                                                    modifier = Modifier
                                                                        .graphicsLayer { scaleX = retryBtnScale; scaleY = retryBtnScale }
                                                                        .clip(RoundedCornerShape(10.dp))
                                                                        .background(
                                                                            Brush.linearGradient(
                                                                                listOf(Color(0xFFDC2626), Color(0xFF991B1B))
                                                                            )
                                                                        )
                                                                        .border(1.dp, Color(0xFFF87171), RoundedCornerShape(10.dp))
                                                                        .clickable(
                                                                            interactionSource = retryBtnInteraction,
                                                                            indication = null
                                                                        ) { onRetryMessage?.invoke(msg.id) }
                                                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                                                ) {
                                                                    Text(
                                                                        "🔄 Tekrar Dene",
                                                                        color = Color.White,
                                                                        fontSize = 10.5.sp,
                                                                        fontWeight = FontWeight.ExtraBold
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                         // AI Message Action Pills
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                                            modifier = Modifier
                                                .horizontalScroll(rememberScrollState())
                                                .padding(vertical = 3.dp)
                                        ) {
                                            @Composable
                                            fun AiActionPill(
                                                label: String,
                                                emoji: String,
                                                enabled: Boolean = true,
                                                isDanger: Boolean = false,
                                                onClick: () -> Unit
                                            ) {
                                                val aiPillInteraction = remember { MutableInteractionSource() }
                                                val aiPillPressed by aiPillInteraction.collectIsPressedAsState()
                                                val aiPillScale by animateFloatAsState(
                                                    targetValue = if (aiPillPressed) 0.93f else 1f,
                                                    animationSpec = tween(80),
                                                    label = "aiPillScale"
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .graphicsLayer { scaleX = aiPillScale; scaleY = aiPillScale; alpha = if (enabled) 1f else 0.45f }
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(
                                                            if (isDanger) Brush.linearGradient(
                                                                listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))
                                                            ) else Brush.linearGradient(
                                                                listOf(Color(0xFF190E35), Color(0xFF100825))
                                                            )
                                                        )
                                                        .border(
                                                            BorderStroke(
                                                                1.dp,
                                                                if (isDanger) Brush.linearGradient(
                                                                    listOf(Color(0xFFF87171), Color(0xFFDC2626))
                                                                ) else Brush.linearGradient(
                                                                    listOf(Color(0x90C084FC), Color(0x406B21A8))
                                                                )
                                                            ),
                                                            RoundedCornerShape(12.dp)
                                                        )
                                                        .clickable(
                                                            enabled = enabled,
                                                            interactionSource = aiPillInteraction,
                                                            indication = null,
                                                            onClick = onClick
                                                        )
                                                        .padding(horizontal = 11.dp, vertical = 6.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        if (emoji.isNotEmpty()) Text(emoji, fontSize = 11.sp)
                                                        Text(
                                                            label,
                                                            color = if (isDanger) Color(0xFFFCA5A5) else AppColors.TextLight,
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                            }

                                            AiActionPill("Yeniden Oluştur", "🔄", enabled = !isSending) { onRegenerate() }
                                            AiActionPill("Devam Ettir", "▶️", enabled = !isSending) {
                                                onSendMessage("Devam et, hikayeye kaldığın yerden devam et.")
                                            }
                                            AiActionPill(
                                                if (isSpeaking) "Durdur" else "Sesli Oku",
                                                if (isSpeaking) "⏹️" else "🎙️"
                                            ) {
                                                if (isSpeaking) onStopSpeaking() else onSpeakText?.invoke(msg.text)
                                            }
                                            AiActionPill(if (msg.isStarred) "Yıldızı Kaldır" else "Yıldızla", if (msg.isStarred) "⭐" else "☆") {
                                                viewModel.toggleMessageStar(msg.id)
                                            }
                                            // Reaction button
                                            val hasReaction = msg.reaction.isNotEmpty()
                                            AiActionPill(
                                                if (hasReaction) msg.reaction else "Reaksiyon",
                                                if (hasReaction) "" else "😊"
                                            ) {
                                                reactionPickerMessageId = if (reactionPickerMessageId == msg.id) null else msg.id
                                            }
                                            AiActionPill("Kopyala", "📋") {
                                                val cleanText = cleanCopyText(msg.text)
                                                clipboardManager.setText(AnnotatedString(cleanText))
                                                android.widget.Toast.makeText(context, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                            AiActionPill("Devam Et", "⏪") { rewindTargetMessageId = msg.id }
                                            AiActionPill("•••", "") {
                                                expandedMenuMessageId = if (expandedMenuMessageId == msg.id) null else msg.id
                                            }
                                            if (expandedMenuMessageId == msg.id) {
                                                AiActionPill("Düzenle", "✏️") {
                                                    editingMessageId = msg.id
                                                    editingText = msg.text
                                                    expandedMenuMessageId = null
                                                }
                                                AiActionPill("Sil", "🗑️", isDanger = true) {
                                                    expandedMenuMessageId = null
                                                    onDeleteMessage(msg.id)
                                                }
                                            }
                                        }
                                    }

                                    // 😊 Reaction Picker Popup
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = reactionPickerMessageId == msg.id,
                                        enter = androidx.compose.animation.fadeIn(tween(150)) +
                                            androidx.compose.animation.scaleIn(tween(150), transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 1f)),
                                        exit = androidx.compose.animation.fadeOut(tween(100)) +
                                            androidx.compose.animation.scaleOut(tween(100), transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 1f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 42.dp, top = 4.dp, bottom = 4.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF1E0D40), Color(0xFF130828))))
                                                .border(1.2.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF9333EA))), RoundedCornerShape(20.dp))
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                reactionEmojis.forEach { emoji ->
                                                    val isSelected = msg.reaction == emoji
                                                    val emojiInteraction = remember { MutableInteractionSource() }
                                                    val emojiPressed by emojiInteraction.collectIsPressedAsState()
                                                    val emojiScale by animateFloatAsState(
                                                        targetValue = when {
                                                            isSelected -> 1.25f
                                                            emojiPressed -> 0.85f
                                                            else -> 1f
                                                        },
                                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                        label = "emojiScale_$emoji"
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .graphicsLayer { scaleX = emojiScale; scaleY = emojiScale }
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected)
                                                                    Brush.radialGradient(listOf(Color(0xFF9333EA), Color(0xFF4C1D95)))
                                                                else
                                                                    Brush.radialGradient(listOf(Color(0x30A855F7), Color(0x10000000)))
                                                            )
                                                            .clickable(interactionSource = emojiInteraction, indication = null) {
                                                                viewModel.setMessageReaction(msg.id, if (isSelected) "" else emoji)
                                                                reactionPickerMessageId = null
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(emoji, fontSize = 18.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (isSending) {
                        item(key = "typing_dots_indicator") {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = androidx.compose.animation.fadeIn(tween(300)) +
                                    androidx.compose.animation.slideInVertically(tween(300)) { it / 2 },
                                exit  = androidx.compose.animation.fadeOut(tween(200))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 64.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // AI avatar — small
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(EmochiGradients.chatBubbleAi)
                                            .border(
                                                BorderStroke(1.dp, Brush.sweepGradient(EmochiGradients.avatarBorderSweep)),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        com.example.ui.components.BotAvatarImage(
                                            avatarUrl = bot.avatarUrl.takeIf { it.isNotBlank() },
                                            contentDescription = displayName,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            fallback = { OrbView(hue = hue, size = 26.dp) }
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Bubble
                                    val typingShape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                                    Box(
                                        modifier = Modifier
                                            .border(BorderStroke(1.dp, EmochiGradients.chatBubbleAiBorder), typingShape)
                                            .clip(typingShape)
                                            .background(EmochiGradients.chatBubbleAi)
                                            .padding(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TypingDots()
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "$displayName yazıyor",
                                                color = AppColors.TextMuted.copy(alpha = 0.75f),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val showScrollToBottom = remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val totalItems = layoutInfo.totalItemsCount
                    if (totalItems == 0) return@derivedStateOf false

                    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
                    val itemsFromEnd = totalItems - 1 - lastVisibleItem.index

                    if (itemsFromEnd >= 2) {
                        true
                    } else if (itemsFromEnd == 1) {
                        true
                    } else {
                        // On last visible item: check if scrolled up by at least ~180px
                        val viewportEnd = layoutInfo.viewportEndOffset
                        val itemBottom = lastVisibleItem.offset + lastVisibleItem.size
                        (itemBottom - viewportEnd) > 180
                    }
                }
            }

            if (showScrollToBottom.value) {
                val totalCount = messages.size + if (isSending) 1 else 0
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF4C1D95))
                                )
                            )
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0xFFE9D5FF), Color(0xFFA855F7), Color(0xFFC084FC))
                                    )
                                ),
                                RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    if (totalCount > 0) {
                                        listState.animateScrollToItem(totalCount - 1)
                                    }
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "En aşağı in",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = "En Aşağı İn",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

    if (showBackgroundPicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showBackgroundPicker = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CardDarkBlue),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.5.dp, AppColors.PrimaryPurple),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🖼️ Arka Plan Fotoğrafı",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sohbet ekranında görünmesini istediğiniz duvar kağıdı seçeneği:",
                        color = AppColors.IndigoLight,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Button 1: Use Avatar
                    Button(
                        onClick = {
                            onSaveBotProfile(bot.copy(chatBgUrl = "USE_AVATAR"), keyCharacters)
                            showBackgroundPicker = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardPurpleDark, contentColor = AppColors.PrimaryViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("👤 Bot Avatarını Arka Plan Yap", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button 2: Default Theme
                    Button(
                        onClick = {
                            onSaveBotProfile(bot.copy(chatBgUrl = ""), keyCharacters)
                            showBackgroundPicker = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardMidnight, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("🌌 Varsayılan Temaya Dön", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button 3: Custom Gallery Photo / Profile Settings
                    Button(
                        onClick = {
                            showBackgroundPicker = false
                            showBotSettings = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryPurple, contentColor = AppColors.CardDarkBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("📸 Galeriden Fotoğraf Seç", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (rewindTargetMessageId != null) {
        val targetId = rewindTargetMessageId!!
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { rewindTargetMessageId = null },
            containerColor = AppColors.CardDarkViolet,
            title = {
                Text(
                    text = "⏪ Buradan Devam Et",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Bu noktadan devam edersen sonraki mesajlar ve onlarla ilgili hafıza/duygu değişimi SİLİNECEK, emin misin?",
                    color = AppColors.TextLight,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idToRewind = targetId
                        rewindTargetMessageId = null
                        onRewindToMessage?.invoke(idToRewind)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.ErrorRedSoft)
                ) {
                    Text("Evet, Devam Et", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
                        .clickable { rewindTargetMessageId = null }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("İptal", color = AppColors.TextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        )
    }

    if (showBotSettings) {
        BotSettingsModal(
            bot = bot,
            keyCharacters = keyCharacters,
            characterEmotions = characterEmotions,
            affectionEvents = affectionEvents,
            onDismiss = { showBotSettings = false },
            onSave = onSaveBotProfile,
            onResetChat = onResetChat,
            onDeleteBot = onDeleteBot,
            onCloneBot = onCloneBot
        )
    }

    // 🏆 Milestone Overlay
    AnimatedVisibility(
        visible = activeMilestone != null,
        enter = fadeIn(tween(400)) + scaleIn(tween(400, easing = FastOutSlowInEasing), initialScale = 0.7f),
        exit = fadeOut(tween(500)) + scaleOut(tween(300), targetScale = 0.8f)
    ) {
        val milestone = activeMilestone ?: return@AnimatedVisibility
        val infinite = rememberInfiniteTransition(label = "milestoneGlow")
        val glowAlpha by infinite.animateFloat(0.4f, 1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "mg")
        val particleProgress by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "mp")

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Semi-transparent backdrop
            Box(modifier = Modifier.fillMaxSize().background(Color(0x99000000)).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { activeMilestone = null })

            // Confetti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val colors = listOf(Color(0xFFF59E0B), Color(0xFFA855F7), Color(0xFF34D399), Color(0xFFF472B6), Color(0xFF60A5FA))
                repeat(30) { i ->
                    val hue = (i * 37f) % 360f
                    val x = size.width * ((i * 0.13f + particleProgress * 0.7f) % 1f)
                    val y = size.height * ((i * 0.17f + particleProgress * 0.4f + i * 0.03f) % 1f)
                    val r = 4f + (i % 5) * 2f
                    drawCircle(color = colors[i % colors.size].copy(alpha = 0.7f), radius = r, center = Offset(x, y))
                }
            }

            // Card
            Column(
                modifier = Modifier.padding(40.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF1F0D4A), Color(0xFF0D0820))))
                    .border(BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))), RoundedCornerShape(24.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(milestone.emoji, fontSize = 52.sp, modifier = Modifier.graphicsLayer { alpha = glowAlpha })
                Text("Yeni Kilometre Taşı!", color = Color(0xFFF59E0B), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(milestone.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text("Sevgi: ${milestone.affectionAtTime} · Güven: ${milestone.trustAtTime}", color = Color(0xFFCDA8FF), fontSize = 12.sp)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                        .clickable { activeMilestone = null }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) { Text("Harika! ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            }
        }
    }

    // ⭐ Starred Messages BottomSheet — Premium redesign
    if (showStarredSheet) {
        LaunchedEffect(showStarredSheet) { viewModel.loadStarredMessages() }
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showStarredSheet = false },
            containerColor = Color(0xFF080614),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 0.dp,
            dragHandle = {
                Box(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(40.dp).height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9))))
                    )
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
                // Premium header
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0D0820))))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.radialGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⭐", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    "Yıldızlanan Mesajlar",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.3).sp
                                )
                                Text(
                                    "${starredMessages.size} mesaj kaydedildi",
                                    color = Color(0x80CDA8FF),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        // Close button
                        val closeInteraction = remember { MutableInteractionSource() }
                        val closePressed by closeInteraction.collectIsPressedAsState()
                        val closeScale by animateFloatAsState(if (closePressed) 0.88f else 1f, tween(80), label = "cs")
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = closeScale; scaleY = closeScale }
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF2A0A0A), Color(0xFF1A0606))))
                                .border(1.dp, Brush.linearGradient(listOf(Color(0xFFF87171), Color(0xFFDC2626))), RoundedCornerShape(10.dp))
                                .clickable(interactionSource = closeInteraction, indication = null) { showStarredSheet = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                        }
                    }
                }
                // Gradient separator
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(
                    Brush.horizontalGradient(listOf(Color.Transparent, Color(0x60F59E0B), Color(0x60A855F7), Color.Transparent))
                ))

                if (starredMessages.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(72.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Brush.radialGradient(listOf(Color(0x30F59E0B), Color(0x10D97706)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⭐", fontSize = 32.sp)
                            }
                            Text("Henüz yıldızlanan mesaj yok", color = Color(0x80CDA8FF), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Mesajlara uzun basarak yıldızlayabilirsin", color = Color(0x50CDA8FF), fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(starredMessages, key = { it.id }) { starred ->
                            val isAi = starred.role == "assistant"
                            val cardInteraction = remember { MutableInteractionSource() }
                            val cardPressed by cardInteraction.collectIsPressedAsState()
                            val cardScale by animateFloatAsState(if (cardPressed) 0.97f else 1f, tween(80), label = "cardScale")

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.linearGradient(
                                        if (isAi)
                                            listOf(Color(0xFF140D2E), Color(0xFF0C0820))
                                        else
                                            listOf(Color(0xFF220A45), Color(0xFF150630))
                                    ))
                                    .border(
                                        1.2.dp,
                                        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFF59E0B))),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable(interactionSource = cardInteraction, indication = null) {}
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Header row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(
                                                modifier = Modifier.size(22.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isAi)
                                                        Brush.radialGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                                                    else
                                                        Brush.radialGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9)))),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(if (isAi) "🤖" else "👤", fontSize = 10.sp)
                                            }
                                            Text(
                                                if (isAi) bot.aiName else (bot.userCharName.ifBlank { "Sen" }),
                                                color = Color(0xFFF59E0B),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text("⭐", fontSize = 12.sp)
                                    }
                                    // Gradient divider
                                    Box(modifier = Modifier.fillMaxWidth().height(0.8.dp).background(
                                        Brush.horizontalGradient(listOf(Color.Transparent, Color(0x40F59E0B), Color.Transparent))
                                    ))
                                    // Message text
                                    Text(
                                        starred.text.take(220) + if (starred.text.length > 220) "…" else "",
                                        color = Color(0xFFE8DEFF),
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )
                                    // Unstar button
                                    val unstarInteraction = remember { MutableInteractionSource() }
                                    val unstarPressed by unstarInteraction.collectIsPressedAsState()
                                    val unstarScale by animateFloatAsState(if (unstarPressed) 0.93f else 1f, tween(80), label = "us")
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer { scaleX = unstarScale; scaleY = unstarScale }
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF2A1500), Color(0xFF1A0E00))))
                                                .border(0.8.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))), RoundedCornerShape(8.dp))
                                                .clickable(interactionSource = unstarInteraction, indication = null) {
                                                    viewModel.toggleMessageStar(starred.id)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("☆ Yıldızı Kaldır", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showQuickProfile) {
        val context = LocalContext.current
        BotQuickProfileSheet(
            bot = bot,
            keyCharacters = keyCharacters,
            characterEmotions = characterEmotions,
            affectionEvents = affectionEvents,
            onDismiss = { showQuickProfile = false },
            onSaveBot = { updatedBot ->
                onSaveBotProfile(updatedBot, keyCharacters)
            },
            onOpenFullSettings = { showBotSettings = true },
            onResetChat = { fullReset ->
                showQuickProfile = false
                onResetChat(fullReset)
            },
            onExportChat = {
                val fullText = messages.joinToString("\n\n") { "${if (it.role == "user") bot.userCharName.ifBlank { "Kullanıcı" } else displayName}: ${it.text}" }
                clipboardManager.setText(AnnotatedString(fullText))
                android.widget.Toast.makeText(context, "Tüm sohbet metni panoya kopyalandı!", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }
    } // End of else block for non-book mode
}

data class BookChoiceOption(val text: String, val rawLine: String)

fun parseBookChoiceOptions(rawText: String): List<BookChoiceOption> {
    val result = mutableListOf<BookChoiceOption>()
    val lines = rawText.split("\n")
    var inChoiceSection = false

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.startsWith("🔀")) {
            inChoiceSection = true
            continue
        }

        val isChoice = trimmed.startsWith("1️⃣") || trimmed.startsWith("2️⃣") || trimmed.startsWith("3️⃣") ||
                trimmed.startsWith("4️⃣") || trimmed.startsWith("5️⃣") || trimmed.startsWith("6️⃣") ||
                trimmed.matches(Regex("^[1-9][0-9]*[\\.\\)]\\s*.*")) ||
                (inChoiceSection && (trimmed.startsWith("-") || trimmed.startsWith("•") || trimmed.startsWith("*")))

        if (isChoice) {
            val choiceText = trimmed
                .replace(Regex("^(1️⃣|2️⃣|3️⃣|4️⃣|5️⃣|6️⃣|\\d+[\\.\\)]|[-•*])\\s*"), "")
                .trim()
            if (choiceText.length >= 2) {
                result.add(BookChoiceOption(choiceText, trimmed))
            }
        }
    }
    return result
}

enum class SummaryViewMode {
    PROMPT,
    SEQUENTIAL,
    FINAL_GRID
}

enum class StepPhase {
    CARD_ONLY,
    RESULT_REVEALED
}

data class ChapterChoiceItem(
    val id: Int,
    val title: String,
    val selectedOption: String,
    val impactText: String,
    val icon: String, // "🔀", "💫", "💭"
    val isBranching: Boolean = false,
    val isRelationship: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterSummaryModal(
    messages: List<com.example.data.local.MessageEntity>,
    onDismiss: () -> Unit,
    onRestartChapter: () -> Unit,
    onNextChapter: (() -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var viewMode by remember { mutableStateOf(SummaryViewMode.PROMPT) }
    var currentIndex by remember { mutableStateOf(0) }
    var stepPhase by remember { mutableStateOf(StepPhase.CARD_ONLY) }
    var isClosing by remember { mutableStateOf(false) }

    val completedChapterNumber = remember(messages) {
        val lastAssistant = messages.lastOrNull { it.role == "assistant" || it.role == "model" }
        val txt = lastAssistant?.text ?: ""
        when {
            txt.contains("BÖLÜM 3 SONU") -> 3
            txt.contains("BÖLÜM 2 SONU") -> 2
            else -> 1
        }
    }

    // BookAudioController — modal açılırken zaten çalıyor olabilir, çift başlatma yok
    LaunchedEffect(Unit) {
        if (!com.example.util.BookAudioController.isPlaying) {
            com.example.util.BookAudioController.play(context, com.example.R.raw.bolumsonu, loop = true, volume = 0.85f)
        }
    }

    // Fade-out helper — BookAudioController stop() fadeOut içeriyor
    val safeExitWithFade = remember {
        { action: () -> Unit ->
            if (!isClosing) {
                isClosing = true
                coroutineScope.launch {
                    com.example.util.BookAudioController.stop(fadeOutMs = 1500)
                    delay(1600)
                    action()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { /* BookAudioController global — burada release etme */ }
    }

    val userMsgs = remember(messages) { messages.filter { it.role == "user" } }
    val lastAssistantMsg = remember(messages) {
        messages.lastOrNull { it.role == "assistant" || it.role == "model" }
    }
    val isChapter3 = remember(messages, lastAssistantMsg) {
        userMsgs.size >= 11 || (lastAssistantMsg?.text?.contains("BÖLÜM 3") == true)
    }
    val isChapter2 = remember(messages, lastAssistantMsg) {
        !isChapter3 && (userMsgs.size >= 5 || (lastAssistantMsg?.text?.contains("BÖLÜM 2") == true))
    }

    val choices = remember(messages, isChapter2, isChapter3) {
        val items = mutableListOf<ChapterChoiceItem>()

        if (isChapter3) {
            if (userMsgs.size >= 11) {
                val c1Msg = userMsgs.getOrNull(10)?.text ?: ""
                val isUnconditional = c1Msg.contains("2") || c1Msg.contains("Tereddütsüz")
                val isShort = c1Msg.contains("3") || c1Msg.contains("kısa")
                items.add(
                    ChapterChoiceItem(
                        id = 1,
                        title = "1. OTUZ GÜNLÜK TEKLİFE CEVAP",
                        selectedOption = if (isUnconditional) "Tereddütsüz 30 günlük deneme süresini kabul ettin."
                                        else if (isShort) "30 günü çok uzun bulup birkaç günlük kısa süre önerdin."
                                        else "30 günlük süreyi kendi çıkış şartlarınla kabul ettin.",
                        impactText = if (isUnconditional) "Steve Rogers memnun kaldı, sana karşı güveni arttı."
                                    else if (isShort) "Steve anlayış gösterdi ama ekibin temkinli yaklaşımı sürdü."
                                    else "Özgürlüğünden ödün vermeden ilk adımı attın.",
                        icon = if (isUnconditional) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isUnconditional
                    )
                )
            }

            if (userMsgs.size >= 12) {
                val c2Msg = userMsgs.getOrNull(11)?.text ?: ""
                val isEager = c2Msg.contains("2") || c2Msg.contains("İstekli")
                val isRefuse = c2Msg.contains("3") || c2Msg.contains("Reddet")
                items.add(
                    ChapterChoiceItem(
                        id = 2,
                        title = "2. ANTRENMAN SALONU DEĞERLENDİRMESİ",
                        selectedOption = if (isEager) "Steve Rogers ile antrenmana istekli katıldın."
                                        else if (isRefuse) "Fiziksel testi reddedip teorik anlatım istedin."
                                        else "Sınırlarını kendin belirlemek şartıyla temkinli kabul ettin.",
                        impactText = if (isEager) "Ekip üyelerinde büyük merak ve olumlu izlenim uyandırdın."
                                    else if (isRefuse) "Gizemli kaldın ama Steve seni yine de antrenman ortamına davet etti."
                                    else "Kontrolü elinde tutarak antrenman alanına adım attın.",
                        icon = if (isEager) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isEager
                    )
                )
            }

            if (userMsgs.size >= 13) {
                val c3Msg = userMsgs.getOrNull(12)?.text ?: ""
                val isFullPower = c3Msg.contains("3") || c3Msg.contains("Hiç geri tutma")
                val isPushHard = c3Msg.contains("2") || c3Msg.contains("zorla")
                items.add(
                    ChapterChoiceItem(
                        id = 3,
                        title = "3. STEVE'E KARŞI GÜÇ GÖSTERİMİ",
                        selectedOption = if (isFullPower) "Hiç geri tutmadın, Kırmızı Göz ivmesiyle kazandın."
                                        else if (isPushHard) "Steve'in savunmasını zorladın ama sınırda durdun."
                                        else "Kendini tamamen kontrol ettin, Steve'e zarar vermedin.",
                        impactText = if (isFullPower) "Bruce Banner dehşete düştü, Steve gücüne takdirle yaklaştı."
                                    else if (isPushHard) "Natasha ve Steve hızından ve kontrolünden etkilendi."
                                    else "Hızlı iyileşme gücün ve kontrolün Natasha'dan takdir gördü.",
                        icon = if (isFullPower) "🔀" else "💫",
                        isBranching = isFullPower,
                        isRelationship = !isFullPower
                    )
                )
            }

            if (userMsgs.size >= 14) {
                val c4Msg = userMsgs.getOrNull(13)?.text ?: ""
                val isMin = c4Msg.contains("2") || c4Msg.contains("Minimum")
                val isMax = c4Msg.contains("3") || c4Msg.contains("tam kapasite")
                items.add(
                    ChapterChoiceItem(
                        id = 4,
                        title = "4. ENERJİ TESTİ GÖSTERİMİ",
                        selectedOption = if (isMin) "Minimum kıvılcım seviyesinde güç uyguladın."
                                        else if (isMax) "Neredeyse tam kapasitede devasa enerji patlaması yarattın."
                                        else "Orta seviyede kontrollü enerji dalgası fırlattın.",
                        impactText = if (isMin) "Sınırlarını gizli tuttun, merak uyandırdın."
                                    else if (isMax) "Stark sayılara hayran kaldı, Wanda bina seviyesi potansiyelini not etti."
                                    else "Bruce Banner senin kozmik imzanın benzersiz olduğunu belirtti.",
                        icon = if (isMax) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isMax
                    )
                )
            }

            if (userMsgs.size >= 15) {
                val c5Msg = userMsgs.getOrNull(14)?.text ?: ""
                val isSerious = c5Msg.contains("3") || c5Msg.contains("ciddiye")
                val isSilent = c5Msg.contains("2") || c5Msg.contains("Sessiz")
                items.add(
                    ChapterChoiceItem(
                        id = 5,
                        title = "5. SAM WILSON'IN ŞAKASINA TEPKİ",
                        selectedOption = if (isSerious) "Şakayı ciddiye alıp soğuk durdun."
                                        else if (isSilent) "Sessiz kalıp sadece gülümsedin."
                                        else "Kuru bir espriyle masayı kahkahaya boğdun.",
                        impactText = if (isSerious) "Mesafeli tavrını korudun."
                                    else if (isSilent) "Saygıyla karşılanan mesafeli bir gülümseme bıraktın."
                                    else "Yıllar sonra ilk kez bir ekiple masada gülmenin tadını çıkardın.",
                        icon = if (!isSerious && !isSilent) "💫" else "💭",
                        isBranching = false,
                        isRelationship = !isSerious && !isSilent
                    )
                )
            }

            if (userMsgs.size >= 16) {
                val c6Msg = userMsgs.getOrNull(15)?.text ?: ""
                val isShort = c6Msg.contains("2") || c6Msg.contains("çatıya") || c6Msg.contains("Kısa")
                val isBig = c6Msg.contains("3") || c6Msg.contains("uzağa") || c6Msg.contains("Büyük")
                items.add(
                    ChapterChoiceItem(
                        id = 6,
                        title = "6. S.H.I.E.L.D. ALARM ALARMINDA KARAR",
                        selectedOption = if (isBig) "Tümünü geride bırakıp başka bir şehre ışınlandın (Büyük Kaçış)."
                                        else if (isShort) "Refleksle çatıya ışınlandın, sonra geri döndün (Kısa Kaçış)."
                                        else "Kule'de kaldın ve ekibe güvenmeyi seçtin (Normal Akış).",
                        impactText = if (isBig) "Thor seni 3 gün sonra buldu, kaçış ve dönüş deneyimin olgunlaştı."
                                    else if (isShort) "Çatıdan izledikten sonra adımlayarak aşağı indin, Wanda cesaretini övdü."
                                    else "Wanda ve Steve ile güven bağını güçlendirdin, kalmanın cesaret olduğunu hissettin.",
                        icon = "🔀",
                        isBranching = true,
                        isRelationship = !isBig
                    )
                )
            }

            if (userMsgs.size >= 17) {
                val c7Msg = userMsgs.getOrNull(16)?.text ?: ""
                val isSincere = c7Msg.contains("1") || c7Msg.contains("Tam dürüst") || c7Msg.contains("Korkutucu")
                val isJoking = c7Msg.contains("2") || c7Msg.contains("Yarım")
                items.add(
                    ChapterChoiceItem(
                        id = 7,
                        title = "7. NATASHA İLE ÇATI DİYALOGU",
                        selectedOption = if (isSincere) "Kalmanın kaybedecek bir şey biriktirmek olduğunu dürüstçe söyledin."
                                        else if (isJoking) "Manzaradan bahsettiğini söyleyerek durumu şakaya vurdun."
                                        else "Kapanıp sadece hava aldığını söyledin.",
                        impactText = if (isSincere) "Natasha Kızıl Oda geçmişini paylaşarak derin bir bağ kurdu."
                                    else if (isJoking) "Natasha hafifçe gülümsedi ve 30 günlük süreni hatırlattı."
                                    else "Natasha mesafene saygı duyarak iyi geceler diledi.",
                        icon = if (isSincere) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isSincere
                    )
                )
            }

            if (userMsgs.size >= 18) {
                val c8Msg = userMsgs.getOrNull(17)?.text ?: ""
                val isSuppress = c8Msg.contains("2") || c8Msg.contains("cebine") || c8Msg.contains("bastır")
                val isFlashback = c8Msg.contains("3") || c8Msg.contains("flashback") || c8Msg.contains("bak")
                items.add(
                    ChapterChoiceItem(
                        id = 8,
                        title = "8. KOLYE ANI & İÇ SES",
                        selectedOption = if (isSuppress) "Kolyeyi cebine koyup duygusal zayıflığı bastırdın."
                                        else if (isFlashback) "Kolyeye bakıp 20 yıl önceki patlamayı hatırladın."
                                        else "Kolyeyi elinde tutup umutla New York ışıklarını izledin.",
                        impactText = if (isSuppress) "Soğukkanlı zırhını korumaya devam ettin."
                                    else if (isFlashback) "Geçmişin yükünü kabullenerek kulede yeni bir sayfa açtın."
                                    else "Yıllar sonra ilk kez bir şeyleri kaybetmeden tutabileceğine inandın.",
                        icon = if (!isSuppress) "💫" else "💭",
                        isBranching = false,
                        isRelationship = false
                    )
                )
            }
        } else if (isChapter2) {
            // Chapter 2 Choices (Indices 4, 5, 6, 7, 8)
            if (userMsgs.size >= 5) {
                val c1Msg = userMsgs.getOrNull(4)?.text ?: ""
                val isGenerous = c1Msg.contains("3") || c1Msg.contains("fazlası") || c1Msg.contains("Cömert")
                val isRefuse = c1Msg.contains("2") || c1Msg.contains("Reddet") || c1Msg.contains("gösterme")
                items.add(
                    ChapterChoiceItem(
                        id = 1,
                        title = "1. GÜÇ GÖSTERİMİ STRATEJİSİ",
                        selectedOption = if (isGenerous) "Güç gösteriminde cömert davrandın ve katmanlı formlarından bahsettin."
                                        else if (isRefuse) "Güç gösterisini tamamen reddettin, sadece sözlerine güvenmelerini istedin."
                                        else "Küçük mavi ışık küresi oluşturdun ve temel sınırlarını açıkladın.",
                        impactText = if (isGenerous) "Tony Stark'ın ilgisini çektin ve bilimsel bir merak uyandırdın."
                                    else if (isRefuse) "Gizemin korundu ancak Steve Rogers ve Tony Stark'ın şüphesi bir nebze arttı."
                                    else "Güç gösterin kontrollü bulundu, Stark ve Rogers güçlerini gerçekçi şekilde değerlendirdi.",
                        icon = if (isGenerous) "💫" else if (isRefuse) "🔀" else "💭",
                        isBranching = isRefuse,
                        isRelationship = isGenerous
                    )
                )
            }

            if (userMsgs.size >= 6) {
                val c2Msg = userMsgs.getOrNull(5)?.text ?: ""
                val isSincere = c2Msg.contains("2") || c2Msg.contains("Ciddi") || c2Msg.contains("zor olduğunu")
                val isSilent = c2Msg.contains("3") || c2Msg.contains("Cevap verme") || c2Msg.contains("değiştir")
                items.add(
                    ChapterChoiceItem(
                        id = 2,
                        title = "2. STEVE'İN GÜVEN SÖZÜNE TEPKİ",
                        selectedOption = if (isSincere) "Güvenin senin için neden bu kadar zor olduğunu dürüstçe açıkladın."
                                        else if (isSilent) "Cevap vermeyip konuyu değiştirdin."
                                        else "Güven kelimesini sorgulayarak alaycı bir üslup takındın.",
                        impactText = if (isSincere) "Steve Rogers'ın saygısını ve güvenini kazandın."
                                    else if (isSilent) "Steve Rogers ayrılırken temkinli ve mesafeli bir tavır sergiledi."
                                    else "Geri çekilmeden sınır çizdin, mesafeli duruşunu korudun.",
                        icon = if (isSincere) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isSincere
                    )
                )
            }

            if (userMsgs.size >= 7) {
                val c3Msg = userMsgs.getOrNull(6)?.text ?: ""
                val isTalk = c3Msg.contains("2") || c3Msg.contains("Sözlü") || c3Msg.contains("sakladığını")
                val isLeave = c3Msg.contains("3") || c3Msg.contains("Mesafe") || c3Msg.contains("çık")
                items.add(
                    ChapterChoiceItem(
                        id = 3,
                        title = "3. NATASHA İLE İLİŞKİ TAVRI",
                        selectedOption = if (isTalk) "Natasha'ya sözlü karşılık verdin, onun geçmişini ima ettin."
                                        else if (isLeave) "Mesafe koyup odadan çıktın."
                                        else "Natasha'ya karşı sessiz kaldın, bakışların onda uzadı.",
                        impactText = if (isTalk) "Natasha ile güçlü ve doğrudan bir diyalog bağı kurdun."
                                    else if (isLeave) "Natasha ile arandaki mesafeyi korudun, gizemini muhafaza ettin."
                                    else "Gölgelerden gelen iki insan arasındaki sessiz anlayış derinleşti.",
                        icon = if (isTalk || !isLeave) "💫" else "💭",
                        isBranching = false,
                        isRelationship = !isLeave
                    )
                )
            }

            if (userMsgs.size >= 8) {
                val c4Msg = userMsgs.getOrNull(7)?.text ?: ""
                val isAccept = c4Msg.contains("2") || c4Msg.contains("Kabul") || c4Msg.contains("açıkça")
                val isReject = c4Msg.contains("3") || c4Msg.contains("Sert") || c4Msg.contains("kapat")
                items.add(
                    ChapterChoiceItem(
                        id = 4,
                        title = "4. WANDA'NIN İÇGÖRÜSÜNE TEPKİ",
                        selectedOption = if (isAccept) "Kayıp hissettiğini Wanda'ya açıkça kabul ettin."
                                        else if (isReject) "Sert bir şekilde konuyu kapatıp geri çekildin."
                                        else "Wanda'ya zihin okuma şüphesiyle karşılık verdin.",
                        impactText = if (isAccept) "Wanda ile aranızda derin bir duygusal empati ve bağ kuruldu."
                                    else if (isReject) "Wanda ile arandaki duygusal teması sınırlandırdın."
                                    else "Wanda anlayışla gülümsedi ve seni içgörüyle dinlemeye devam etti.",
                        icon = if (isAccept) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isAccept || !isReject
                    )
                )
            }

            if (userMsgs.size >= 9) {
                val c5Msg = userMsgs.getOrNull(8)?.text ?: ""
                val isDoubt = c5Msg.contains("2") || c5Msg.contains("şüphe") || c5Msg.contains("kaçmayı")
                val isConflicted = c5Msg.contains("3") || c5Msg.contains("Belirsiz") || c5Msg.contains("karışık") || c5Msg.contains("çatışma")
                items.add(
                    ChapterChoiceItem(
                        id = 5,
                        title = "5. PENCEREDEKİ GECE KARARI",
                        selectedOption = if (isDoubt) "Pencerede kaçış rotalarını düşünmeye devam ettin."
                                        else if (isConflicted) "İç çatışma ve derin kararsızlıkla şehre baktın."
                                        else "Pencerede temkinli bir iyimserlikle New York'u izledin.",
                        impactText = if (isDoubt) "Şüpheci ve kaçış odaklı tutumunu korudun."
                                    else if (isConflicted) "Kararsızlığını korudun, zamanın ne getireceğini beklemeye karar verdin."
                                    else "Avengers'a karşı umut dolu, olumlu bir eğilim kazandın.",
                        icon = if (!isDoubt) "💫" else "🔀",
                        isBranching = isDoubt,
                        isRelationship = false
                    )
                )
            }
        } else {
            // Chapter 1 Choices
            if (userMsgs.isNotEmpty()) {
                val c1Msg = userMsgs.getOrNull(0)?.text ?: ""
                val isC1Deep = c1Msg.contains("odaklan") || c1Msg.contains("derinden") || c1Msg.contains("2")
                items.add(
                    ChapterChoiceItem(
                        id = 1,
                        title = "1. GÖRÜYE YAKLAŞIM",
                        selectedOption = if (isC1Deep) "Görüye derinden odaklandın." else "Görüden zihnini geri çektin ve mesafeli kaldın.",
                        impactText = if (isC1Deep) {
                            "Ekstra detaylar elde ettin ancak bu zihinsel yük Aiden'a fiziksel yorgunluk ve burun kanaması verdi."
                        } else {
                            "Bu seçim Aiden'ın gücünü tasarruflu kullanma ve zihnini yıpratmama kararlılığını yansıttı, hikayeyi doğrudan değiştirmedi."
                        },
                        icon = "💭",
                        isBranching = false,
                        isRelationship = false
                    )
                )
            }

            if (userMsgs.size >= 2) {
                val c2Msg = userMsgs.getOrNull(1)?.text ?: ""
                val isC2Avoid = c2Msg.contains("Geri çekil") || c2Msg.contains("gitme") || c2Msg.contains("3")
                val isC2Observe = c2Msg.contains("gözlemle") || c2Msg.contains("uzaktan") || c2Msg.contains("2")
                val c2Selected = if (isC2Avoid) {
                    "Kule'ye girmemeyi ve geri çekilmeyi tercih ettin."
                } else if (isC2Observe) {
                    "Kule'yi önce dışarıdan gözlemledin, sonra ışınlandın."
                } else {
                    "Kule'ye direkt ışınlandın."
                }
                val c2Impact = if (isC2Avoid) {
                    "Avengers Tower'dan uzak durmayı denedin ancak S.H.I.E.L.D. uyduları uzam sapmanı tespit etti ve zorunlu bir portal dalgasıyla seni kuleye getirdi."
                } else if (isC2Observe) {
                    "Hesaplı ve temkinli bir giriş yaptın; bu tavır Natasha Romanoff'un takdirini kazandı."
                } else {
                    "Aniden ortaya çıkman odadakilerin reflekslerini tetikledi ve daha dürtüsel bir izlenim bıraktın."
                }
                items.add(
                    ChapterChoiceItem(
                        id = 2,
                        title = "2. KULE'YE GİRİŞ STRATEJİSİ",
                        selectedOption = c2Selected,
                        impactText = c2Impact,
                        icon = if (isC2Avoid) "🔀" else if (isC2Observe) "💫" else "💭",
                        isBranching = isC2Avoid,
                        isRelationship = isC2Observe
                    )
                )
            }

            if (userMsgs.size >= 3) {
                val c3Msg = userMsgs.getOrNull(2)?.text ?: ""
                val isC3Witty = c3Msg.contains("Alaycı") || c3Msg.contains("esprili") || c3Msg.contains("masraflı") || c3Msg.contains("2")
                val isC3Silent = c3Msg.contains("Sessizlik") || c3Msg.contains("Hiç isim") || c3Msg.contains("3")
                val c3Selected = if (isC3Witty) {
                    "Stark'a alaycı ve esprili bir yanıt verdin."
                } else if (isC3Silent) {
                    "Hiç isim vermeyip sessiz kaldın."
                } else {
                    "Soğuk ve mesafeli bir cevap verdin."
                }
                val c3Impact = if (isC3Witty) {
                    "Mizahi yaklaşımın Tony Stark'ın sempatisini kazandı ve ortamdaki gerilimi bir nebze düşürdü."
                } else if (isC3Silent) {
                    "Gizemli ve tekinsiz sessizliğin Steve Rogers'ın şüphesini daha da artırdı."
                } else {
                    "Bu seçim Aiden'ın zırhlarını koruma ve mesafeli durma tavrını yansıttı, hikayeyi doğrudan değiştirmedi."
                }
                items.add(
                    ChapterChoiceItem(
                        id = 3,
                        title = "3. TONY STARK İLE DİYALOG",
                        selectedOption = c3Selected,
                        impactText = c3Impact,
                        icon = if (isC3Witty || isC3Silent) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isC3Witty || isC3Silent
                    )
                )
            }

            if (userMsgs.size >= 4) {
                val c4Msg = userMsgs.getOrNull(3)?.text ?: ""
                val isC4Natasha = c4Msg.contains("Natasha") || c4Msg.contains("kızıl") || c4Msg.contains("1")
                val isC4Wanda = c4Msg.contains("Wanda") || c4Msg.contains("pencere") || c4Msg.contains("2")
                val c4Selected = if (isC4Natasha) {
                    "Bakışların Natasha Romanoff'un üzerinde kaldı."
                } else if (isC4Wanda) {
                    "Bakışların Wanda Maximoff'un üzerinde kaldı."
                } else {
                    "Kimseye özel bakış atmayıp odayı genel olarak taradın."
                }
                val c4Impact = if (isC4Natasha) {
                    "Natasha Romanoff ile aranızda sessiz ama güçlü bir diyalog ve görünmez bir bağ kuruldu."
                } else if (isC4Wanda) {
                    "Wanda Maximoff zihinsel dalgalanmanı hissetti ve aranızda ince bir merak filizlendi."
                } else {
                    "Bu seçim Aiden'ın o anki nötr ve profesyonel tavrını yansıttı, hikayeyi doğrudan değiştirmedi."
                }
                items.add(
                    ChapterChoiceItem(
                        id = 4,
                        title = "4. BAKIŞ ODAK NOKTASI",
                        selectedOption = c4Selected,
                        impactText = c4Impact,
                        icon = if (isC4Natasha || isC4Wanda) "💫" else "💭",
                        isBranching = false,
                        isRelationship = isC4Natasha || isC4Wanda
                    )
                )
            }
        }

        items
    }

    val relationshipTease = remember(userMsgs, isChapter2, isChapter3) {
        if (isChapter3) {
            "Otuz günlük deneme süresinde Avengers Tower'da yerini almaya başladın. Steve ile karşılıklı saygı, Natasha ile derin geçmiş anlayışı ve Wanda ile duygusal empati geliştirdin."
        } else if (isChapter2) {
            val c3Msg = userMsgs.getOrNull(6)?.text ?: ""
            val c4Msg = userMsgs.getOrNull(7)?.text ?: ""
            val natashaBond = c3Msg.contains("1") || c3Msg.contains("2") || c3Msg.contains("Sözlü") || c3Msg.contains("Sessiz")
            val wandaBond = c4Msg.contains("1") || c4Msg.contains("2") || c4Msg.contains("Kabul") || c4Msg.contains("zihin")
            when {
                natashaBond && wandaBond -> "Natasha ve Wanda ile derin bağlar kurdun. Kule'de hem gölgelerin anlayışını hem de duygusal sığınağı hissettin."
                natashaBond -> "Natasha Romanoff ile aranızdaki gizli bağ güçlendi. Gölgelerden gelen iki ajanın kaderi kesişiyor."
                wandaBond -> "Wanda Maximoff ile aranızda empati filizlendi. Geçmişin acılarını taşıyan iki güç sahibi birbirini anlıyor."
                else -> "Mesafeli duruşunu korudun; Avengers ekibi senin duvarlarını aşmak için zaman kolluyor."
            }
        } else {
            val c4Msg = userMsgs.getOrNull(3)?.text ?: ""
            when {
                c4Msg.contains("Natasha") || c4Msg.contains("kızıl") || c4Msg.contains("1") ->
                    "Natasha Romanoff ile aranızda özel bir bağ kuruldu. İlerleyen bölümlerde sana daha fazla güvenebilir."
                c4Msg.contains("Wanda") || c4Msg.contains("pencere") || c4Msg.contains("2") ->
                    "Wanda Maximoff'un ilgisini çektin. Sonraki bölümlerde zihinsel bağınız derinleşebilir."
                else -> null
            }
        }
    }

    // Auto Step Controller for Sequential Reveal (Life is Strange Pacing: Adım A ~1.8s, Adım B ~7.5s - 2.7s extra reading time)
    LaunchedEffect(viewMode, currentIndex) {
        if (viewMode == SummaryViewMode.SEQUENTIAL && choices.isNotEmpty()) {
            stepPhase = StepPhase.CARD_ONLY
            delay(1800) // ADIM A duration: ~1.8s (Breath & Read Action)
            if (viewMode == SummaryViewMode.SEQUENTIAL) {
                stepPhase = StepPhase.RESULT_REVEALED // ADIM B (Thread & Impact Reveal)
                delay(7500) // ADIM B duration: ~7.5s (2.7s extra time for deep impact reading)
                if (viewMode == SummaryViewMode.SEQUENTIAL) {
                    if (currentIndex < choices.lastIndex) {
                        currentIndex++
                    } else {
                        // Transition to Final Memory Thread after last choice
                        viewMode = SummaryViewMode.FINAL_GRID
                    }
                }
            }
        }
    }

    // FULL-SCREEN DIALOG WITH DARK CINEMATIC ATMOSPHERE & FADING AUDIO
    androidx.compose.ui.window.Dialog(
        onDismissRequest = { safeExitWithFade(onDismiss) },
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        val currentChoice = choices.getOrNull(currentIndex)
        val characterAccentColor = remember(currentChoice) {
            if (currentChoice != null) {
                val text = (currentChoice.selectedOption + " " + currentChoice.impactText + " " + currentChoice.title).lowercase()
                when {
                    text.contains("natasha") || text.contains("romanoff") || text.contains("kızıl") || text.contains("red") -> AppColors.ErrorRed
                    text.contains("wanda") || text.contains("scarlet") || text.contains("cadı") || text.contains("witch") -> AppColors.Rose
                    text.contains("tony") || text.contains("stark") || text.contains("zırh") || text.contains("iron") -> AppColors.WarningAmber
                    text.contains("steve") || text.contains("rogers") || text.contains("kaptan") || text.contains("captain") -> AppColors.Blue
                    else -> AppColors.DeepPurple
                }
            } else {
                AppColors.DeepPurple
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppColors.Hex_070814
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Cosmic Nebula & Character Specific Color Accent & Drifting Star Dust
                CosmicParticleBackground(accentColor = characterAccentColor)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                ) {
                    // TOP BAR: Minimal Dots Indicator & Close Button (Fixed Top Layer)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewMode == SummaryViewMode.SEQUENTIAL && choices.isNotEmpty()) {
                            // Minimal Dots Progress Indicator with Pulse & Smooth Fill (Rule 7)
                            val infiniteTransition = rememberInfiniteTransition(label = "ActiveDotPulse")
                            val activeDotScale by infiniteTransition.animateFloat(
                                initialValue = 1.0f,
                                targetValue = 1.18f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "DotPulse"
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                choices.forEachIndexed { idx, _ ->
                                    val isActive = idx == currentIndex
                                    val isPast = idx < currentIndex
                                    val targetDotSize = if (isActive) 11.dp else 6.dp
                                    val targetDotColor = when {
                                        isActive -> AppColors.PurpleSoft
                                        isPast -> AppColors.DeepPurple
                                        else -> Color(0x3594A3B8)
                                    }
                                    val animSize by androidx.compose.animation.core.animateDpAsState(
                                        targetValue = targetDotSize,
                                        animationSpec = tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                                        label = "DotSizeAnim"
                                    )
                                    val animColor by androidx.compose.animation.animateColorAsState(
                                        targetValue = targetDotColor,
                                        animationSpec = tween(300),
                                        label = "DotColorAnim"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(animSize)
                                            .graphicsLayer {
                                                if (isActive) {
                                                    scaleX = activeDotScale
                                                    scaleY = activeDotScale
                                                }
                                            }
                                            .clip(CircleShape)
                                            .background(animColor)
                                    )
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎬", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (viewMode == SummaryViewMode.PROMPT) "BÖLÜM $completedChapterNumber SEÇİM ÖZETİ" else "HAFIZA ŞERİDİ",
                                    color = AppColors.PurpleSoft,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Close (X) Button
                        IconButton(
                            onClick = { safeExitWithFade(onDismiss) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppColors.Hex_231B3D)
                                .border(1.dp, Color(0x60A78BFA), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // MAIN CONTENT AREA (PERFECTLY CENTERED VERTICALLY AND HORIZONTALLY)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        when (viewMode) {
                            // ==================== INITIAL PROMPT SCREEN ====================
                            SummaryViewMode.PROMPT -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Bu bölümdeki seçimlerini görmek ister misin?",
                                        color = Color.White,
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 31.sp
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Verdiğin kararlar ve hikayeye olan etkileri Life is Strange tarzı sinematik reveal akışıyla sunulacak.",
                                        color = AppColors.TextMuted,
                                        fontSize = 13.5.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        lineHeight = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(36.dp))

                                    Button(
                                        onClick = {
                                            currentIndex = 0
                                            stepPhase = StepPhase.CARD_ONLY
                                            viewMode = SummaryViewMode.SEQUENTIAL
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.DeepPurple),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("✨ Evet, Sırayla Göster", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.5.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    OutlinedButton(
                                        onClick = { viewMode = SummaryViewMode.FINAL_GRID },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.TextSecondary),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Hex_334155),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                    ) {
                                        Text("⚡ Atla (Direkt Hafıza Şeridi)", fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
                                    }
                                }
                            }

                            // ==================== SEQUENTIAL CINEMATIC REVEAL ====================
                            SummaryViewMode.SEQUENTIAL -> {
                                if (currentChoice != null) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CinematicSequentialReveal(
                                            choice = currentChoice,
                                            phase = stepPhase
                                        )

                                        Spacer(modifier = Modifier.height(32.dp))

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Brush.linearGradient(listOf(Color(0xFF1E0D3A), Color(0xFF130828))))
                                                .border(0.8.dp, Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9))), RoundedCornerShape(10.dp))
                                                .clickable { viewMode = SummaryViewMode.FINAL_GRID }
                                                .padding(horizontal = 14.dp, vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "⚡ Tümünü Göster / Atla",
                                                color = Color(0xB3A78BFA),
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // ==================== FINAL MEMORY THREAD TIMELINE ====================
                            SummaryViewMode.FINAL_GRID -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Kaderin Halkaları — Seçim Anıları",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "Verdiğin kararların zaman akışında bıraktığı silinmez izler:",
                                        color = AppColors.TextMuted,
                                        fontSize = 12.5.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                                    )

                                    MemoryThreadTimeline(
                                        choices = choices,
                                        relationshipTease = relationshipTease
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            safeExitWithFade {
                                                if (isChapter3) {
                                                    android.widget.Toast.makeText(context, "Bölüm 4 yapım aşamasındadır. Çok yakında yayınlanacak!", android.widget.Toast.LENGTH_LONG).show()
                                                } else {
                                                    onDismiss()
                                                    onNextChapter?.invoke()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.DeepPurple),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                if (isChapter3) "✨ Bölüm 3 Tamamlandı (Bölüm 4 Yakında)" else if (isChapter2) "🚀 Bölüm 3'e Geç" else "🚀 Bölüm 2'ye Geç",
                                                color = Color.White,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedButton(
                                        onClick = {
                                            safeExitWithFade {
                                                onDismiss()
                                                onRestartChapter()
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.PrimaryPurple),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Hex_4C1D95),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    ) {
                                        Text(
                                            if (isChapter3) "🔄 Bölüm 3'ü Tekrar Oyna" else if (isChapter2) "🔄 Bölüm 2'yi Tekrar Oyna" else "🔄 Bölüm 1'i Tekrar Oyna",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            safeExitWithFade { onDismiss() }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Hex_1E1B38),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Hex_382F5E),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = AppColors.TextSecondary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Özeti Kapat",
                                                color = AppColors.TextSecondary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class CosmicParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val baseAlpha: Float,
    val speed: Float
)

@Composable
fun CosmicParticleBackground(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CosmicParticleDrift")

    // Rule 2: Soft radial glow breath loop (scale 0.9 -> 1.05 -> 1.0, 4.5s FastOutSlowIn)
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DriftAnim"
    )

    val particles = remember {
        List(35) {
            CosmicParticle(
                xRatio = (0..100).random() / 100f,
                yRatio = (0..100).random() / 100f,
                radius = (15..45).random() / 10f,
                baseAlpha = (12..28).random() / 100f,
                speed = 0.4f + (0..100).random() / 100f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Broad Center Radial Glow with Character Accent Color & Breathing Scale (Rule 2)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color(0x208B5CF6),
                            Color(0x080F172A),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Drifting Star Dust / Cosmic Energy Particles Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                val currentY = ((p.yRatio - animProgress * p.speed * 0.35f) % 1f + 1f) % 1f
                val currentX = (p.xRatio + kotlin.math.sin((animProgress * 6.28318f + p.yRatio * 12f).toDouble()).toFloat() * 0.04f) % 1f

                drawCircle(
                    color = accentColor.copy(alpha = p.baseAlpha),
                    radius = p.radius.dp.toPx(),
                    center = Offset(currentX * width, currentY * height)
                )
            }
        }

        // 3. Top Vignette Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xEE070814), Color.Transparent)
                    )
                )
        )

        // 4. Bottom Vignette Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xEE070814))
                    )
                )
        )
    }
}

@Composable
fun PulsingLoadingText(
    modifier: Modifier = Modifier
) {
    // Rule 3: 2px fine progress line filling from left to right over 1.5s + shimmer text
    val infiniteTransition = rememberInfiniteTransition(label = "ShimmerLine")
    val lineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LineFill"
    )

    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShimmerAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(AppColors.PurpleSoft.copy(alpha = shimmerAlpha))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Sonuçlar dokunuyor...",
                color = AppColors.PurpleLight.copy(alpha = shimmerAlpha),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                letterSpacing = 0.9.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2px sleek progress fill bar (Rule 3)
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color(0x308B5CF6))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(lineProgress)
                    .background(
                        Brush.horizontalGradient(
                            listOf(AppColors.DeepPurple, AppColors.PurpleSoft)
                        )
                    )
            )
        }
    }
}

@Composable
fun CinematicSequentialReveal(
    choice: ChapterChoiceItem,
    phase: StepPhase
) {
    // Screen Shake effect for Branching choices
    val shakeAnim = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(phase) {
        if (phase == StepPhase.RESULT_REVEALED && choice.isBranching) {
            repeat(3) {
                shakeAnim.animateTo(4f, androidx.compose.animation.core.tween(30))
                shakeAnim.animateTo(-4f, androidx.compose.animation.core.tween(30))
            }
            shakeAnim.animateTo(0f, androidx.compose.animation.core.tween(30))
        }
    }

    // Rule 4: Overshoot Elastic Icon Reveal (scale 0 -> 1.35 -> 1.0)
    val iconScaleAnim = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(phase) {
        if (phase == StepPhase.RESULT_REVEALED) {
            iconScaleAnim.snapTo(0f)
            iconScaleAnim.animateTo(
                targetValue = 1.35f,
                animationSpec = tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )
            iconScaleAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(160, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )
        }
    }

    val impactColor = when {
        choice.isBranching -> AppColors.WarningAmber // Gold / Amber
        choice.isRelationship -> AppColors.PinkLight // Pink / Magenta
        else -> AppColors.TextSecondary // Slate / Neutral
    }

    // Rule 5: Mood Shift Smooth Color Accent (800ms transition)
    val animatedImpactColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (phase == StepPhase.RESULT_REVEALED) impactColor else AppColors.DeepPurple,
        animationSpec = tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "MoodShiftColor"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(x = shakeAnim.value.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. ADIM A: Small, subtle top title (e.g., "4. BAKIŞ ODAK NOKTASI")
        Text(
            text = choice.title.uppercase(),
            color = AppColors.PrimaryPurple,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. ADIM A (Rule 1 & 8): Selected Option (opacity 0->1, y: +25px->0, 600ms ease-out, 150ms delay)
        // Rule 8: Exit with scale 1->0.95 + fade out
        androidx.compose.animation.AnimatedContent(
            targetState = choice.selectedOption,
            transitionSpec = {
                (androidx.compose.animation.fadeIn(animationSpec = tween(600, delayMillis = 150, easing = androidx.compose.animation.core.FastOutSlowInEasing)) +
                        androidx.compose.animation.slideInVertically(
                            initialOffsetY = { 25 },
                            animationSpec = tween(600, delayMillis = 150, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                        ) +
                        androidx.compose.animation.scaleIn(initialScale = 0.96f, animationSpec = tween(600, delayMillis = 150))) togetherWith
                        (androidx.compose.animation.fadeOut(animationSpec = tween(300, easing = androidx.compose.animation.core.FastOutLinearInEasing)) +
                                androidx.compose.animation.scaleOut(targetScale = 0.95f, animationSpec = tween(300)))
            },
            label = "ActionTextAnim"
        ) { text ->
            Text(
                text = text,
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (phase == StepPhase.CARD_ONLY) {
            // 3. ADIM A: Vivid Pulsing Loading Indicator Text
            PulsingLoadingText()
        } else {
            // GEÇİŞ & ADIM B: Light Thread + Impact Reveal
            androidx.compose.animation.AnimatedVisibility(
                visible = true,
                enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(600)) +
                        androidx.compose.animation.expandVertically(animationSpec = androidx.compose.animation.core.tween(600))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dikey Işık Huzmesi (Connecting Light Thread)
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(44.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(AppColors.DeepPurple, animatedImpactColor)
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pulse Icon & Impact Tag & Consequence Text (Floating freely in space)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = choice.icon,
                                fontSize = 23.sp,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = iconScaleAnim.value
                                    scaleY = iconScaleAnim.value
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    choice.isBranching -> "💥 BÜYÜK DALLANMA ETKİSİ"
                                    choice.isRelationship -> "💕 İLİŞKİ DOKUNUŞU"
                                    else -> "💭 TAVIR ETKİSİ"
                                },
                                color = animatedImpactColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = choice.impactText,
                            color = AppColors.TextWhite,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 23.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryThreadTimeline(
    choices: List<ChapterChoiceItem>,
    relationshipTease: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        // Continuous Vertical Thread Line
        Box(
            modifier = Modifier
                .padding(start = 11.dp, top = 12.dp, bottom = 12.dp)
                .width(2.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        listOf(AppColors.DeepPurple, AppColors.PrimaryViolet, Color(0x308B5CF6))
                    )
                )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            choices.forEachIndexed { idx, choice ->
                // Staggered animated appearance
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(idx * 90L)
                    isVisible = true
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = isVisible,
                    enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) +
                            androidx.compose.animation.slideInVertically(animationSpec = androidx.compose.animation.core.tween(400)) { 20 }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Node Dot on Timeline
                        val nodeColor = when {
                            choice.isBranching -> AppColors.WarningAmber
                            choice.isRelationship -> AppColors.PinkLight
                            else -> AppColors.PrimaryPurple
                        }

                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(nodeColor)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Timeline Item Content (Clean, unboxed composition)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = choice.title,
                                color = AppColors.PrimaryPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = choice.selectedOption,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(choice.icon, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = choice.impactText,
                                    color = AppColors.TextSecondary,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            if (relationshipTease != null) {
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(choices.size * 90L + 100L)
                    isVisible = true
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = isVisible,
                    enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) +
                            androidx.compose.animation.slideInVertically(animationSpec = androidx.compose.animation.core.tween(400)) { 20 }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.Pink)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔮 İLERLEYEN BÖLÜM ETKİSİ",
                                color = AppColors.PinkLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = relationshipTease,
                                color = AppColors.Hex_FBCFE8,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryChoiceCard(
    title: String,
    choiceText: String,
    impact: String,
    icon: String = "💭",
    isBranching: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isBranching) AppColors.Hex_2C192E else AppColors.Hex_181728),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isBranching) AppColors.RoseSoft else AppColors.Hex_382F58
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = if (isBranching) AppColors.RoseLight else AppColors.PrimaryPurple,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            Text(
                text = choiceText,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = if (isBranching) AppColors.Hex_3B1220 else AppColors.Hex_121220,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
                Text(
                    text = impact,
                    color = if (isBranching) AppColors.Hex_FECDD3 else AppColors.TextSecondary,
                    fontSize = 12.5.sp,
                    lineHeight = 17.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun BookReaderView(
    bot: com.example.data.local.BotEntity,
    messages: List<com.example.data.local.MessageEntity>,
    isSending: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onEnsureOpeningMessage: () -> Unit,
    onResetChat: (Boolean) -> Unit,
    onClearError: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var fontSizeSp by remember { mutableStateOf(16.sp) }
    var showSummaryModal by remember { mutableStateOf(false) }
    val listState = rememberSaveable(saver = androidx.compose.foundation.lazy.LazyListState.Saver) {
        androidx.compose.foundation.lazy.LazyListState()
    }
    var lastProcessedMsgCount by rememberSaveable { mutableIntStateOf(0) }

    // BookAudioController ile müzik yönetimi — race condition yok
    val isAmbientMusicPlaying by remember { derivedStateOf { com.example.util.BookAudioController.isPlaying } }

    fun toggleAmbientMusic() {
        com.example.util.BookAudioController.toggle(context, com.example.R.raw.bolumsonu)
    }

    DisposableEffect(Unit) {
        onDispose { com.example.util.BookAudioController.release() }
    }

    LaunchedEffect(messages.size, isSending) {
        if (messages.isNotEmpty()) {
            if (messages.size > lastProcessedMsgCount && lastProcessedMsgCount > 0) {
                listState.animateScrollToItem(messages.lastIndex)
            }
            lastProcessedMsgCount = messages.size
        }
    }

    val lastAssistantMsg = remember(messages) {
        messages.lastOrNull { it.role == "assistant" || it.role == "model" }
    }

    val activeChoices = remember(lastAssistantMsg?.text) {
        if (lastAssistantMsg != null) parseBookChoiceOptions(lastAssistantMsg.text) else emptyList()
    }

    val isChapterCompleted = remember(lastAssistantMsg?.text) {
        val txt = lastAssistantMsg?.text ?: ""
        txt.contains("BÖLÜM 1 SONU") || txt.contains("BÖLÜM 2 SONU") || txt.contains("BÖLÜM 3 SONU") ||
        txt.contains("BÖLÜM TAMAMLANDI") || txt.contains("Tebrikler! Bölüm") ||
        (txt.contains("BÖLÜM") && txt.contains("SONU"))
    }

    // Bölüm tamamlandığında müziği başlat — BookAudioController ile tekli player garantisi
    LaunchedEffect(isChapterCompleted) {
        if (isChapterCompleted && !com.example.util.BookAudioController.isPlaying) {
            com.example.util.BookAudioController.play(context, com.example.R.raw.bolumsonu, loop = true, volume = 0.85f)
        }
    }

    val completedChapterTitle = remember(lastAssistantMsg?.text) {
        val txt = lastAssistantMsg?.text ?: ""
        when {
            txt.contains("BÖLÜM 3 SONU") -> "📖 BÖLÜM 3 TAMAMLANDI"
            txt.contains("BÖLÜM 2 SONU") -> "📖 BÖLÜM 2 TAMAMLANDI"
            else -> "📖 BÖLÜM 1 TAMAMLANDI"
        }
    }

    val completedChapterDesc = remember(lastAssistantMsg?.text) {
        val txt = lastAssistantMsg?.text ?: ""
        when {
            txt.contains("BÖLÜM 3 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 3'ün sonuna ulaştın."
            txt.contains("BÖLÜM 2 SONU") -> "Tebrikler! Verdiğin kararlarla Bölüm 2'nin sonuna ulaştın."
            else -> "Tebrikler! Verdiğin kararlarla Bölüm 1'in sonuna ulaştın."
        }
    }

    if (showSummaryModal) {
        ChapterSummaryModal(
            messages = messages,
            onDismiss = { showSummaryModal = false },
            onRestartChapter = { onResetChat(true) },
            onNextChapter = {
                showSummaryModal = false
                val userCount = messages.filter { it.role == "user" }.size
                val lastMsgText = lastAssistantMsg?.text ?: ""
                if (lastMsgText.contains("BÖLÜM 3 SONU") || userCount >= 19) {
                    android.widget.Toast.makeText(context, "Bölüm 4 yapım aşamasındadır. Çok yakında yayınlanacak!", android.widget.Toast.LENGTH_LONG).show()
                } else if (lastMsgText.contains("BÖLÜM 2 SONU") || userCount in 5..10) {
                    onSendMessage("Bölüm 3: Deneme Süresi'ne başla")
                } else {
                    onSendMessage("Bölüm 2: Şartlar'a başla")
                }
            }
        )
    }

    // Bölüm sonu sinematik overlay state
    var showChapterEndOverlay by remember { mutableStateOf(false) }
    LaunchedEffect(isChapterCompleted) {
        if (isChapterCompleted) showChapterEndOverlay = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Hex_0C0D17)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // ── TOP BOOK NAVIGATION BAR — Premium Glassmorphic ──────────────
        val chapterNum = remember(messages) {
            val txt = messages.lastOrNull { it.role == "assistant" || it.role == "model" }?.text ?: ""
            when {
                txt.contains("BÖLÜM 3") -> 3
                txt.contains("BÖLÜM 2") -> 2
                else -> 1
            }
        }
        val musicPulse = rememberInfiniteTransition(label = "musicPulse")
        val musicGlow by musicPulse.animateFloat(0.4f, 1f,
            infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "mg")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF0A0818), Color(0xFF1A0D3A))))
        ) {
            // Subtle bottom separator
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).align(Alignment.BottomCenter)
                .background(Brush.linearGradient(listOf(Color.Transparent, Color(0x60A78BFA), Color.Transparent))))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: back + title
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    val backScale = remember { androidx.compose.animation.core.Animatable(1f) }
                    val backScope = rememberCoroutineScope()
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF1A1035), Color(0xFF0D0820))))
                            .border(1.dp, Brush.linearGradient(listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))), RoundedCornerShape(10.dp))
                            .graphicsLayer { scaleX = backScale.value; scaleY = backScale.value }
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                backScope.launch {
                                    backScale.animateTo(0.88f, tween(80))
                                    backScale.animateTo(1f, tween(120))
                                }
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Book icon container
                    Box(
                        modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📖", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bot.aiName.ifBlank { "Kozmik Sürükleniş" },
                            color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
                            maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("İnteraktif Roman", color = Color(0xFF9F7AEA), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(Color(0xFF6D28D9)))
                            Text("Bölüm $chapterNum / 3", color = Color(0xFFCDA8FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Right: font size controls + music + reset
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Font size A- A+
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A1035))
                            .border(1.dp, Color(0xFF2D1F50), RoundedCornerShape(8.dp))
                    ) {
                        Box(modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            fontSizeSp = if (fontSizeSp.value > 14f) (fontSizeSp.value - 2f).sp else 14.sp
                        }.padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Text("A⁻", color = Color(0xFF9F7AEA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color(0xFF2D1F50)).align(Alignment.CenterVertically))
                        Box(modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            fontSizeSp = if (fontSizeSp.value < 22f) (fontSizeSp.value + 2f).sp else 22.sp
                        }.padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Text("A⁺", color = Color(0xFF9F7AEA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Music toggle
                    val musicScale = remember { androidx.compose.animation.core.Animatable(1f) }
                    val musicScope = rememberCoroutineScope()
                    Box(
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                            .background(if (isAmbientMusicPlaying) Color(0xFF2D0F40) else Color(0xFF1A1035))
                            .border(1.dp,
                                if (isAmbientMusicPlaying) Color(0xFFD946EF).copy(alpha = musicGlow) else Color(0xFF2D1F50),
                                RoundedCornerShape(8.dp))
                            .graphicsLayer { scaleX = musicScale.value; scaleY = musicScale.value }
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                musicScope.launch {
                                    musicScale.animateTo(0.85f, tween(80))
                                    musicScale.animateTo(1f, tween(120))
                                }
                                toggleAmbientMusic()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isAmbientMusicPlaying) "🎵" else "🔇", fontSize = 15.sp)
                    }

                    // Reset
                    val resetScale = remember { androidx.compose.animation.core.Animatable(1f) }
                    val resetScope = rememberCoroutineScope()
                    Box(
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A0A0A))
                            .border(1.dp, Color(0xFF7F1D1D).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .graphicsLayer { scaleX = resetScale.value; scaleY = resetScale.value }
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                resetScope.launch {
                                    resetScale.animateTo(0.85f, tween(80))
                                    resetScale.animateTo(1f, tween(120))
                                }
                                onResetChat(true)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(17.dp))
                    }
                }
            }
        }

        // Error banner
        errorMessage?.let { err ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF2D0A0A), Color(0xFF1A0808))))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⚠️ $err", color = Color(0xFFFCA5A5), fontSize = 12.sp, modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(24.dp).clip(CircleShape)
                    .background(Color(0xFF3D1010))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClearError() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(14.dp))
                }
            }
        }

        // ── NOVEL CANVAS BODY ────────────────────────────────────────────
        val novelInfinite = rememberInfiniteTransition(label = "novelBg")
        val novelDrift by novelInfinite.animateFloat(0f, 1f,
            infiniteRepeatable(tween(18000, easing = LinearEasing)), label = "nd")

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, Brush.linearGradient(listOf(Color(0x40A78BFA), Color(0x206D28D9), Color(0x40A78BFA))), RoundedCornerShape(18.dp))
        ) {
            // Canlı nebula arka plan
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(Color(0xFF0A0A18))
                // 3 yavaş nebula blob
                val blobs = listOf(
                    Triple(0.2f, 0.3f, Color(0xFF1A0A2E)),
                    Triple(0.7f, 0.6f, Color(0xFF0D1A2E)),
                    Triple(0.5f, 0.85f, Color(0xFF1A0D2E))
                )
                blobs.forEachIndexed { i, (bx, by, col) ->
                    val cx = size.width * (bx + kotlin.math.sin(novelDrift * 2 * Math.PI.toFloat() + i) * 0.05f)
                    val cy = size.height * (by + kotlin.math.cos(novelDrift * 2 * Math.PI.toFloat() + i * 1.3f) * 0.04f)
                    drawCircle(color = col.copy(alpha = 0.6f), radius = size.minDimension * 0.4f, center = Offset(cx, cy),
                        blendMode = BlendMode.Screen)
                }
                // Üst ve alt vignette
                drawRect(Brush.verticalGradient(listOf(Color(0xFF050510), Color.Transparent, Color.Transparent, Color(0xFF050510))))
            }

            if (messages.isEmpty() && !isSending) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize().padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(20.dp))
                        .background(Brush.radialGradient(listOf(Color(0xFF4C1D95), Color(0xFF1A0D3A)))),
                        contentAlignment = Alignment.Center) {
                        Text("📖", fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text("Kitap Sayfası Hazırlanıyor", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bölüm 1 metnini başlatmak için dokunun.", color = Color(0xFF9F7AEA), fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(22.dp))
                    val startScale = remember { androidx.compose.animation.core.Animatable(1f) }
                    val startScope = rememberCoroutineScope()
                    Box(modifier = Modifier.clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95))))
                        .graphicsLayer { scaleX = startScale.value; scaleY = startScale.value }
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            startScope.launch { startScale.animateTo(0.93f, tween(80)); startScale.animateTo(1f, tween(120)) }
                            onEnsureOpeningMessage()
                        }.padding(horizontal = 24.dp, vertical = 12.dp)) {
                        Text("📖 Bölüm 1'i Başlat", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(messages, key = { _, msg -> msg.id }) { idx, msg ->
                        // Aşamalı fade-in
                        val visible = remember { androidx.compose.animation.core.MutableTransitionState(false).apply { targetState = true } }
                        AnimatedVisibility(visibleState = visible,
                            enter = fadeIn(tween(350, delayMillis = minOf(idx * 40, 300))) +
                                    slideInVertically(tween(350, delayMillis = minOf(idx * 40, 300))) { it / 3 }
                        ) {
                            if (msg.role == "user") {
                                // ── Kullanıcı seçim kartı ──
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                    Box(modifier = Modifier.fillMaxWidth(0.95f).clip(RoundedCornerShape(14.dp))
                                        .background(Brush.linearGradient(listOf(Color(0xFF1A1218), Color(0xFF120D1A))))
                                        .border(1.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(30.dp).clip(CircleShape)
                                                .background(Brush.radialGradient(listOf(Color(0xFFF59E0B), Color(0xFF92400E)))),
                                                contentAlignment = Alignment.Center) {
                                                Text("✦", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("VERİLEN KARAR", color = Color(0xFFF59E0B), fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                                                Text(
                                                    text = msg.text.replace(Regex("^(1️⃣|2️⃣|3️⃣|4️⃣|\\d+[\\.\\)]|[-•*])\\s*"), ""),
                                                    color = Color(0xFFFDE68A), fontSize = (fontSizeSp.value - 1f).sp,
                                                    fontWeight = FontWeight.SemiBold, lineHeight = (fontSizeSp.value * 1.4f).sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // ── AI hikaye metni ──
                                val msgChoices = parseBookChoiceOptions(msg.text)
                                val rawChoiceLines = msgChoices.map { it.rawLine.trim() }.toSet()
                                val paragraphs = msg.text.split("\n\n")
                                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    paragraphs.forEach { paragraph ->
                                        val trimmed = paragraph.trim()
                                        val lines = trimmed.lines().map { it.trim() }
                                        val isChoiceBlock = lines.all { line ->
                                            line.isBlank() || rawChoiceLines.contains(line) || line.startsWith("🔀") ||
                                            line.startsWith("---") || line.matches(Regex("^(1️⃣|2️⃣|3️⃣|4️⃣|5️⃣|6️⃣|\\d+[\\.\\)]|[-•*])\\s*.*"))
                                        }
                                        if (isChoiceBlock && msgChoices.isNotEmpty()) return@forEach
                                        if (trimmed.isBlank()) return@forEach

                                        when {
                                            // Bölüm tamamlandı başlığı
                                            trimmed.contains("BÖLÜM") && (trimmed.contains("TAMAMLANDI") || trimmed.contains("SONU")) -> {
                                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center) {
                                                    Text(trimmed.replace("#", "").trim(),
                                                        color = Color(0xFF34D399), fontSize = (fontSizeSp.value + 5f).sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        lineHeight = (fontSizeSp.value * 1.5f).sp)
                                                }
                                            }
                                            // Bölüm başlığı
                                            trimmed.startsWith("#") || (trimmed.startsWith("📖") && trimmed.contains("BÖLÜM")) -> {
                                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp)
                                                        .background(Brush.linearGradient(listOf(Color.Transparent, Color(0x80A78BFA), Color.Transparent))))
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(trimmed.replace("#", "").trim(),
                                                        color = Color(0xFFC4B5FD), fontSize = (fontSizeSp.value + 5f).sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        lineHeight = (fontSizeSp.value * 1.5f).sp)
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp)
                                                        .background(Brush.linearGradient(listOf(Color.Transparent, Color(0x80A78BFA), Color.Transparent))))
                                                }
                                            }
                                            // Scene break
                                            trimmed == "---" || trimmed == "---\n---" -> {
                                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalAlignment = Alignment.CenterVertically) {
                                                    repeat(3) {
                                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape)
                                                            .background(Color(0xFF6D28D9).copy(alpha = 0.7f)))
                                                        if (it < 2) Spacer(modifier = Modifier.width(8.dp))
                                                    }
                                                }
                                            }
                                            // Diyalog (tırnak ile başlıyorsa)
                                            trimmed.startsWith("\"") || trimmed.startsWith("*") || trimmed.startsWith("_") -> {
                                                Row(modifier = Modifier.fillMaxWidth()) {
                                                    Box(modifier = Modifier.width(2.dp).fillMaxHeight()
                                                        .background(Color(0xFF7C3AED).copy(alpha = 0.5f)))
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(trimmed.removePrefix("*").removeSuffix("*").trim(),
                                                        color = Color(0xFFE2D9F3), fontSize = fontSizeSp,
                                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                        lineHeight = (fontSizeSp.value * 1.6f).sp)
                                                }
                                            }
                                            // Normal paragraf
                                            else -> {
                                                Text(trimmed, color = Color(0xFFD4C8E8), fontSize = fontSizeSp,
                                                    lineHeight = (fontSizeSp.value * 1.65f).sp,
                                                    fontWeight = FontWeight.Normal)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sayfa yazılıyor — canvas animasyonu
                    if (isSending) {
                        item(key = "sending_book_progress") {
                            val pageInfinite = rememberInfiniteTransition(label = "pageFlip")
                            val pageRot by pageInfinite.animateFloat(-12f, 12f,
                                infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pr")
                            val dotAlpha1 by pageInfinite.animateFloat(0.2f, 1f,
                                infiniteRepeatable(tween(500, 0), RepeatMode.Reverse), label = "d1")
                            val dotAlpha2 by pageInfinite.animateFloat(0.2f, 1f,
                                infiniteRepeatable(tween(500, 170), RepeatMode.Reverse), label = "d2")
                            val dotAlpha3 by pageInfinite.animateFloat(0.2f, 1f,
                                infiniteRepeatable(tween(500, 340), RepeatMode.Reverse), label = "d3")

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF0F0D20), Color(0xFF1A1535))))
                                .border(1.dp, Color(0x50A78BFA), RoundedCornerShape(14.dp))
                                .padding(18.dp), contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                    // Dönen sayfa ikonu
                                    Canvas(modifier = Modifier.size(32.dp).graphicsLayer { rotationY = pageRot }) {
                                        drawRoundRect(Color(0xFF4C1D95), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f),
                                            size = size * 0.85f, topLeft = Offset(size.width * 0.075f, size.height * 0.075f))
                                        drawLine(Color(0xFF9F7AEA).copy(alpha = 0.6f),
                                            Offset(size.width * 0.25f, size.height * 0.35f),
                                            Offset(size.width * 0.75f, size.height * 0.35f), strokeWidth = 1.5f)
                                        drawLine(Color(0xFF9F7AEA).copy(alpha = 0.4f),
                                            Offset(size.width * 0.25f, size.height * 0.5f),
                                            Offset(size.width * 0.75f, size.height * 0.5f), strokeWidth = 1.2f)
                                        drawLine(Color(0xFF9F7AEA).copy(alpha = 0.3f),
                                            Offset(size.width * 0.25f, size.height * 0.65f),
                                            Offset(size.width * 0.6f, size.height * 0.65f), strokeWidth = 1f)
                                    }
                                    Column {
                                        Text("Kararın işleniyor...", color = Color(0xFFCDA8FF), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf(dotAlpha1, dotAlpha2, dotAlpha3).forEach { alpha ->
                                                Box(modifier = Modifier.size(5.dp).clip(CircleShape)
                                                    .background(Color(0xFF7C3AED).copy(alpha = alpha)))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Seçim kartları — animasyonlu premium
                    if (activeChoices.isNotEmpty() && !isSending) {
                        item(key = "interactive_choices_section") {
                            val choiceInfinite = rememberInfiniteTransition(label = "choicePulse")
                            val choiceBorderAlpha by choiceInfinite.animateFloat(0.5f, 1f,
                                infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "cb")

                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clip(RoundedCornerShape(18.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF0F0D1F), Color(0xFF17122E))))
                                .border(1.5.dp, Brush.linearGradient(listOf(
                                    Color(0xFF7C3AED).copy(alpha = choiceBorderAlpha),
                                    Color(0xFF4F46E5).copy(alpha = choiceBorderAlpha)
                                )), RoundedCornerShape(18.dp))
                                .padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                                        .background(Brush.radialGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))),
                                        contentAlignment = Alignment.Center) {
                                        Text("🔀", fontSize = 12.sp)
                                    }
                                    Text("SENİN KARARIN", color = Color(0xFFA78BFA), fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1A1035))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)) {
                                        Text("${activeChoices.size} seçenek", color = Color(0xFF7C3AED), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                activeChoices.forEachIndexed { choiceIdx, choice ->
                                    val choiceScale = remember { androidx.compose.animation.core.Animatable(1f) }
                                    val choiceScope = rememberCoroutineScope()
                                    val emoji = listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣")[choiceIdx.coerceAtMost(3)]

                                    Box(modifier = Modifier.fillMaxWidth().graphicsLayer { scaleX = choiceScale.value; scaleY = choiceScale.value }
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(listOf(Color(0xFF1A1530), Color(0xFF110D25))))
                                        .border(1.dp, Brush.linearGradient(listOf(Color(0xFF4C1D95), Color(0xFF2D1F50))), RoundedCornerShape(12.dp))
                                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                            choiceScope.launch {
                                                choiceScale.animateTo(0.95f, tween(80))
                                                choiceScale.animateTo(1f, tween(150))
                                            }
                                            onSendMessage(choice.rawLine)
                                        }.padding(14.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(emoji, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(choice.text, color = Color(0xFFEDE9FE), fontSize = (fontSizeSp.value - 0.5f).sp,
                                                fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f),
                                                lineHeight = (fontSizeSp.value * 1.4f).sp)
                                            Icon(Icons.Default.ChevronRight, contentDescription = null,
                                                tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bölüm sonu — premium card (overlay ayrıca açılıyor)
                    if (isChapterCompleted && !isSending) {
                        item(key = "chapter_end_card") {
                            val chapterEndScale = remember { androidx.compose.animation.core.Animatable(0.85f) }
                            LaunchedEffect(Unit) { chapterEndScale.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }
                            val endInfinite = rememberInfiniteTransition(label = "chEnd")
                            val endGlow by endInfinite.animateFloat(0.5f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "eg")

                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp)
                                .graphicsLayer { scaleX = chapterEndScale.value; scaleY = chapterEndScale.value }
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF0A2010), Color(0xFF0D1A0D))))
                                .border(1.5.dp, Brush.linearGradient(listOf(
                                    Color(0xFF34D399).copy(alpha = endGlow),
                                    Color(0xFF10B981).copy(alpha = endGlow)
                                )), RoundedCornerShape(20.dp))
                                .padding(20.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(completedChapterTitle, color = Color(0xFF6EE7B7), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                    Text(completedChapterDesc, color = Color(0xFFD1FAE5), fontSize = 13.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 19.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val summaryBtnScale = remember { androidx.compose.animation.core.Animatable(1f) }
                                    val summaryScope = rememberCoroutineScope()
                                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                        .background(Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857))))
                                        .graphicsLayer { scaleX = summaryBtnScale.value; scaleY = summaryBtnScale.value }
                                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                            summaryScope.launch { summaryBtnScale.animateTo(0.95f, tween(80)); summaryBtnScale.animateTo(1f, tween(120)) }
                                            showSummaryModal = true
                                        }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                            Text("✨ SEÇİM ÖZETİNİ GÖR", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } // end Column

    // ── SİNEMATİK BÖLÜM SONU OVERLAY ────────────────────────────────
    AnimatedVisibility(
        visible = showChapterEndOverlay,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(400))
    ) {
        val confettiColors = listOf(
            Color(0xFF7C3AED), Color(0xFFA78BFA), Color(0xFF34D399),
            Color(0xFFFBBF24), Color(0xFFF472B6), Color(0xFF60A5FA),
            Color(0xFFE879F9), Color(0xFF4ADE80)
        )
        data class ConfettiParticle(val x: Float, val y: Float, val vx: Float, val vy: Float,
            val size: Float, val rotation: Float, val vr: Float, val color: Color, val shape: Int)

        val overlayInfinite = rememberInfiniteTransition(label = "overlayAnim")
        val overlayTick by overlayInfinite.animateFloat(0f, 1f,
            infiniteRepeatable(tween(50, easing = LinearEasing)), label = "ot")

        val particles = remember {
            List(55) {
                ConfettiParticle(
                    x = (0..100).random() / 100f,
                    y = -(0..60).random() / 100f,
                    vx = ((-3)..3).random() / 300f,
                    vy = (4..10).random() / 250f,
                    size = (6..14).random().toFloat(),
                    rotation = (0..360).random().toFloat(),
                    vr = ((-5)..5).random().toFloat(),
                    color = confettiColors.random(),
                    shape = (0..2).random()
                )
            }
        }
        val particleStates = remember { particles.map { androidx.compose.animation.core.Animatable(0f) }.toMutableList() }
        LaunchedEffect(Unit) {
            particleStates.forEachIndexed { i, anim ->
                launch { anim.animateTo(1f, tween(3500 + i * 20, easing = LinearEasing)) }
            }
        }

        val emojiScale = remember { androidx.compose.animation.core.Animatable(0f) }
        val cardSlide = remember { androidx.compose.animation.core.Animatable(120f) }
        LaunchedEffect(Unit) {
            delay(200)
            launch { emojiScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
            launch { cardSlide.animateTo(0f, tween(450, easing = FastOutSlowInEasing)) }
        }

        val overlayGlow by overlayInfinite.animateFloat(0.6f, 1f,
            infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "og")

        Box(modifier = Modifier.fillMaxSize()) {
            // Backdrop
            Box(modifier = Modifier.fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xCC0A0518), Color(0xEE000005)))))

            // Konfeti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val t = overlayTick
                particles.forEachIndexed { i, p ->
                    val progress = particleStates[i].value
                    val cx = (p.x + p.vx * progress * 80) * size.width
                    val cy = (p.y + p.vy * progress * 80) * size.height
                    if (cy < 0 || cy > size.height || cx < 0 || cx > size.width) return@forEachIndexed
                    val alpha = if (progress > 0.75f) 1f - (progress - 0.75f) * 4f else 1f
                    val rot = (p.rotation + p.vr * progress * 800) * (Math.PI / 180).toFloat()
                    when (p.shape) {
                        0 -> drawCircle(p.color.copy(alpha = alpha.coerceIn(0f, 1f)), p.size, Offset(cx, cy))
                        1 -> {
                            rotate(degrees = rot * 57.3f, pivot = Offset(cx, cy)) {
                                drawRect(p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                                    Offset(cx - p.size / 2, cy - p.size / 4),
                                    androidx.compose.ui.geometry.Size(p.size, p.size / 2))
                            }
                        }
                        else -> {
                            rotate(degrees = rot * 57.3f, pivot = Offset(cx, cy)) {
                                drawLine(p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                                    Offset(cx - p.size / 2, cy), Offset(cx + p.size / 2, cy), strokeWidth = 2.5f)
                            }
                        }
                    }
                }
            }

            // Merkezi kart
            Box(modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.85f)
                .graphicsLayer { translationY = cardSlide.value }
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF0F0A20), Color(0xFF1A1035))))
                .border(2.dp, Brush.linearGradient(listOf(
                    Color(0xFF7C3AED).copy(alpha = overlayGlow),
                    Color(0xFF34D399).copy(alpha = overlayGlow),
                    Color(0xFF7C3AED).copy(alpha = overlayGlow)
                )), RoundedCornerShape(24.dp))
                .padding(28.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Emoji
                    Box(modifier = Modifier.size(80.dp).graphicsLayer { scaleX = emojiScale.value; scaleY = emojiScale.value }
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF4C1D95), Color(0xFF1A0D3A)))),
                        contentAlignment = Alignment.Center) {
                        Text("📖", fontSize = 38.sp)
                    }
                    Text(completedChapterTitle, color = Color(0xFF6EE7B7), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text("Tüm kararların kaydedildi. Seçimlerin hikayeni şekillendirdi.",
                        color = Color(0xFFBBB0D8), fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 19.sp)

                    Spacer(modifier = Modifier.height(4.dp))

                    // Seçim özeti butonu
                    val overlayBtn1Scale = remember { androidx.compose.animation.core.Animatable(1f) }
                    val overlayScope1 = rememberCoroutineScope()
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857))))
                        .graphicsLayer { scaleX = overlayBtn1Scale.value; scaleY = overlayBtn1Scale.value }
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            overlayScope1.launch { overlayBtn1Scale.animateTo(0.94f, tween(80)); overlayBtn1Scale.animateTo(1f, tween(120)) }
                            showChapterEndOverlay = false
                            showSummaryModal = true
                        }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                        Text("✨ Seçim Özetini Gör", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }

                    // Kapat
                    val overlayBtn2Scale = remember { androidx.compose.animation.core.Animatable(1f) }
                    val overlayScope2 = rememberCoroutineScope()
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A1035))
                        .border(1.dp, Color(0xFF3D2D60), RoundedCornerShape(14.dp))
                        .graphicsLayer { scaleX = overlayBtn2Scale.value; scaleY = overlayBtn2Scale.value }
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            overlayScope2.launch { overlayBtn2Scale.animateTo(0.94f, tween(80)); overlayBtn2Scale.animateTo(1f, tween(120)) }
                            showChapterEndOverlay = false
                        }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Şimdi Değil", color = Color(0xFF9F7AEA), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }

    } // end outer Box
}

fun cleanCopyText(rawText: String): String {
    return com.example.data.repository.EmochiRepository.cleanEmotionTags(rawText)
}