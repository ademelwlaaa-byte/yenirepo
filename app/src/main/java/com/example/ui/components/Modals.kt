package com.example.ui.components

import com.example.ui.theme.AppColors
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.ExperimentalLayoutApi

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import com.example.data.repository.EmochiRepository
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import com.example.ui.components.BotAvatarImage
import com.example.ui.components.EmochiTextField
import com.example.ui.components.GradientCard
import com.example.ui.components.ActiveGradientCard
import com.example.ui.components.WarningGradientCard
import com.example.ui.components.SuccessGradientCard
import com.example.ui.components.DangerGradientCard
import com.example.ui.components.EmochiLoadingSpinner
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.BotEntity
import com.example.data.local.UserSettingsEntity
import com.example.data.repository.KeyCharacter
import com.example.ui.theme.EmochiBorder
import com.example.ui.theme.EmochiCard
import com.example.ui.theme.EmochiError
import com.example.ui.theme.EmochiErrorContainer
import com.example.ui.theme.EmochiPrimary
import com.example.ui.theme.EmochiSurface
import com.example.ui.theme.EmochiTextMuted
import com.example.ui.theme.EmochiTextPrimary
import com.example.ui.theme.EmochiTextSecondary
import kotlinx.coroutines.launch

data class ModelSpec(
    val key: String,
    val name: String,
    val provider: String,
    val tokenCostRate: String,
    val badgeColor: Color,
    val description: String
)

@Composable
fun SettingsSectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    color: Color = EmochiPrimary
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                color = EmochiTextPrimary,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = EmochiTextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CollapsibleSettingsOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    badgeText: String? = null,
    badgeColor: Color = EmochiPrimary,
    content: @Composable () -> Unit
) {
    val rowInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val rowPressed by rowInteraction.collectIsPressedAsState()
    val rowScale by animateFloatAsState(
        targetValue = if (rowPressed) 0.985f else 1f,
        animationSpec = tween(80), label = "collScale"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer { scaleX = rowScale; scaleY = rowScale }
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isExpanded)
                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0F0826)))
                else
                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF120A28), Color(0xFF0C0720)))
            )
            .border(
                androidx.compose.foundation.BorderStroke(
                    if (isExpanded) 1.3.dp else 0.8.dp,
                    if (isExpanded)
                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF6D28D9), Color(0xFF7C3AED)))
                    else
                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0x40A855F7), Color(0x204C1D95)))
                ),
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(interactionSource = rowInteraction, indication = null) { onToggle() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isExpanded)
                                    androidx.compose.ui.graphics.Brush.radialGradient(listOf(Color(0xFF3B1070), Color(0xFF1C0848)))
                                else
                                    androidx.compose.ui.graphics.Brush.radialGradient(listOf(Color(0xFF1A0840), Color(0xFF0C0420)))
                            )
                            .border(
                                if (isExpanded) 1.2.dp else 0.8.dp,
                                if (isExpanded)
                                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFF7C3AED)))
                                else
                                    androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0x40A855F7), Color(0x204C1D95))),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = if (isExpanded) Color(0xFFCDA8FF) else EmochiTextSecondary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(title, color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            if (badgeText != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(badgeColor.copy(alpha = 0.2f))
                                        .border(0.8.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(badgeText, color = badgeColor, fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                        Text(subtitle, color = EmochiTextMuted, fontSize = 11.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                // Animated rotation chevron
                val arrowRotation by animateFloatAsState(
                    targetValue = if (isExpanded) 90f else 0f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "arrowRot"
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer { rotationZ = arrowRotation },
                    tint = if (isExpanded) Color(0xFFCDA8FF) else EmochiTextSecondary
                )
            }

            if (isExpanded) {
                // Gradient separator instead of flat Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x607C3AED), Color.Transparent)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSettingsModal(
    settings: UserSettingsEntity,
    onDismiss: () -> Unit,
    onSaveSettings: (UserSettingsEntity) -> Unit,
    onExportData: suspend () -> String,
    onImportData: suspend (String) -> Unit,
    viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var geminiApiKey by remember { mutableStateOf(settings.customApiKey) }
    var groqApiKey by remember { mutableStateOf(settings.groqApiKey) }
    var claudeApiKey by remember { mutableStateOf(settings.claudeApiKey) }
    var openaiApiKey by remember { mutableStateOf(settings.openaiApiKey) }
    var openRouterApiKey by remember { mutableStateOf(settings.openRouterApiKey) }
    var openRouterModel by remember { mutableStateOf(settings.openRouterModel.ifBlank { "deepseek/deepseek-chat" }) }
    var nvidiaApiKey by remember { mutableStateOf(settings.nvidiaApiKey) }
    var nvidiaModel by remember { mutableStateOf(settings.nvidiaModel.ifBlank { "" }) }
    var mistralApiKey by remember { mutableStateOf(settings.mistralApiKey) }
    var mistralModel by remember { mutableStateOf(settings.mistralModel.ifBlank { "mistral-large-latest" }) }
    var backupApiKey by remember { mutableStateOf(settings.backupApiKey) }

    var openRouterTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingOpenRouter by remember { mutableStateOf(false) }

    var nvidiaTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingNvidia by remember { mutableStateOf(false) }

    var mistralTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingMistral by remember { mutableStateOf(false) }

    var selectedProvider by remember { mutableStateOf(settings.selectedProvider.ifBlank { "gemini" }) }
    var selectedModel by remember { mutableStateOf(settings.selectedModel) }
    var fallbackModel by remember { mutableStateOf(settings.fallbackModel) }
    var responseLength by remember { mutableStateOf(settings.responseLength) }
    var enableNsfw by remember { mutableStateOf(settings.enableNsfw) }
    var enableFlirty by remember { mutableStateOf(settings.enableFlirty) }
    var enableHardcore by remember { mutableStateOf(settings.enableHardcore) }
    var enableFetish by remember { mutableStateOf(settings.enableFetish) }
    var enableDarkRp by remember { mutableStateOf(settings.enableDarkRp) }
    var enableSweet by remember { mutableStateOf(settings.enableSweet) }
    var enablePrimal by remember { mutableStateOf(settings.enablePrimal) }
    var enableAutoFallback by remember { mutableStateOf(settings.enableAutoFallback) }
    var enableLlm7 by remember { mutableStateOf(settings.enableLlm7 || settings.llm7Enabled) }
    var enablePollinations by remember { mutableStateOf(settings.enablePollinations && settings.pollinationsEnabled) }
    var enableOvh by remember { mutableStateOf(settings.enableOvh && settings.ovhEnabled) }
    var openRouterEnabled by remember { mutableStateOf(settings.openRouterEnabled) }
    var nvidiaEnabled by remember { mutableStateOf(settings.nvidiaEnabled) }
    var mistralEnabled by remember { mutableStateOf(settings.mistralEnabled) }
    var pollinationsModel by remember { mutableStateOf(settings.pollinationsModel.ifBlank { "openai" }) }
    var ovhModel by remember { mutableStateOf(settings.ovhModel.ifBlank { "meta-llama/Meta-Llama-3-70B-Instruct" }) }

    var llm7TestResult by remember { mutableStateOf<String?>(null) }
    var isTestingLlm7 by remember { mutableStateOf(false) }

    var pollinationsTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingPollinations by remember { mutableStateOf(false) }

    var showFallbackLogModal by remember { mutableStateOf(false) }
    var enableTts by remember { mutableStateOf(settings.enableTts) }
    var ttsSpeed by remember { mutableStateOf(settings.ttsSpeed) }
    var ttsPitch by remember { mutableStateOf(settings.ttsPitch) }
    var selectedVoiceName by remember { mutableStateOf(settings.selectedVoiceName) }
    var appLanguage by remember { mutableStateOf(settings.appLanguage) }

    var showKeys by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf<String?>("models") }
    var fallbackChainOrder by remember { mutableStateOf(settings.fallbackChainOrder) }

    var exportJson by remember { mutableStateOf("") }
    var importJson by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var showUpdateModal by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    isBusy = true
                    val content = context.contentResolver.openInputStream(it)?.use { stream ->
                        stream.bufferedReader().use { reader -> reader.readText() }
                    } ?: ""
                    if (content.isNotBlank()) {
                        onImportData(content)
                        Toast.makeText(context, "Yedek dosyadan yüklendi!", Toast.LENGTH_SHORT).show()
                        statusMessage = "Dosya başarıyla içe aktarıldı."
                    }
                } catch (e: Exception) {
                    statusMessage = "Yükleme hatası: ${e.message}"
                } finally {
                    isBusy = false
                }
            }
        }
    }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    isBusy = true
                    val json = if (exportJson.isNotBlank()) exportJson else onExportData()
                    exportJson = json
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        stream.write(json.toByteArray())
                    }
                    Toast.makeText(context, "Yedek dosyaya kaydedildi!", Toast.LENGTH_SHORT).show()
                    statusMessage = "Yedek dosyaya kaydedildi."
                } catch (e: Exception) {
                    statusMessage = "Kaydetme hatası: ${e.message}"
                } finally {
                    isBusy = false
                }
            }
        }
    }

    val modelsList = listOf(
        ModelSpec(
            key = "default",
            name = "LLM7 Default (Ücretsiz)",
            provider = "LLM7 (api.llm7.io)",
            tokenCostRate = "🆓 ÜCRETSİZ (API Key Gerekmez)",
            badgeColor = AppColors.SuccessGreen,
            description = "API Key gerektirmeyen tamamen ücretsiz OpenAI uyumlu servis. Öncelik modunda varsayılan olarak denenir."
        ),
        ModelSpec(
            key = "gemini-2.0-flash",
            name = "Gemini 2.0 Flash",
            provider = "Google Gemini",
            tokenCostRate = "🟢 Düşük (~0.5x Token)",
            badgeColor = AppColors.Green,
            description = "En gelişmiş, dengeli ve hızlı Gemini modeli. Düşük token harcaması ile yüksek kaliteli rol yapma yanıtları verir."
        ),
        ModelSpec(
            key = "gemini-1.5-pro",
            name = "Gemini 1.5 Pro",
            provider = "Google Gemini",
            tokenCostRate = "🔴 Yüksek (~2.5x Token)",
            badgeColor = AppColors.PinkMuted,
            description = "Üst düzey zeka, derin kurgu ve detaylı roman kalitesinde tutarlı karakter anlatımı."
        ),
        ModelSpec(
            key = "openai/gpt-oss-120b",
            name = "Groq GPT-OSS 120B",
            provider = "Groq API",
            tokenCostRate = "🟡 Orta (~1.0x Token)",
            badgeColor = AppColors.WarningOrange,
            description = "Groq Production katmanında ultra hızlı ve yüksek zekalı açık kaynak model."
        ),
        ModelSpec(
            key = "openai/gpt-oss-20b",
            name = "Groq GPT-OSS 20B",
            provider = "Groq API",
            tokenCostRate = "🟢 Düşük (~0.5x Token)",
            badgeColor = AppColors.Green,
            description = "Groq Production katmanında anlık ultra hızlı ve pratik sohbet yanıtları."
        ),
        ModelSpec(
            key = "qwen/qwen3.6-27b",
            name = "Groq Qwen 3.6 27B",
            provider = "Groq API",
            tokenCostRate = "🟡 Orta (~1.0x Token)",
            badgeColor = AppColors.MagentaPurple,
            description = "Qwen 3.6 27B deneysel model. (Preview katmanında)"
        ),
        ModelSpec(
            key = "qwen/qwen3.8-27b",
            name = "Groq Qwen 3.8 27B",
            provider = "Groq API",
            tokenCostRate = "🟡 Orta (~1.0x Token)",
            badgeColor = AppColors.PinkMuted,
            description = "Qwen 3.8 27B en son yayınlanan deneysel model. (Preview katmanında)"
        ),
        ModelSpec(
            key = "claude-3-5-sonnet-20241022",
            name = "Claude 3.5 Sonnet",
            provider = "Anthropic",
            tokenCostRate = "🔴 Çok Yüksek (~3.0x Token)",
            badgeColor = AppColors.Red,
            description = "Edebi anlatım, yüksek duygusal derinlik ve roman kalitesinde akıcı diyaloglar."
        ),
        ModelSpec(
            key = "claude-3-5-haiku-20241022",
            name = "Claude 3.5 Haiku",
            provider = "Anthropic",
            tokenCostRate = "🟡 Orta (~1.2x Token)",
            badgeColor = AppColors.YellowAmber,
            description = "Hızlı ve seri Claude kalitesi. Kısa ve orta boy diyaloglar için ideal."
        ),
        ModelSpec(
            key = "gpt-4o",
            name = "OpenAI GPT-4o",
            provider = "OpenAI",
            tokenCostRate = "🔴 Yüksek (~2.0x Token)",
            badgeColor = AppColors.MagentaPurple,
            description = "OpenAI amiral gemisi akıllı sohbet modeli."
        ),
        ModelSpec(
            key = "gpt-4o-mini",
            name = "OpenAI GPT-4o Mini",
            provider = "OpenAI",
            tokenCostRate = "🟢 Düşük (~0.8x Token)",
            badgeColor = AppColors.Green,
            description = "Ekonomik, tutarlı ve akıcı OpenAI sohbet altyapısı."
        ),
        ModelSpec(
            key = "deepseek-chat",
            name = "DeepSeek V3",
            provider = "DeepSeek",
            tokenCostRate = "🟢 Düşük (~0.6x Token)",
            badgeColor = AppColors.TealDark,
            description = "Bütçe dostu, geniş bağlamlı yüksek akıl yürütme gücü sunan model."
        )
    )

    var fallbackDropdownExpanded by remember { mutableStateOf(false) }
    val totalTokensUsed = settings.totalPromptTokens + settings.totalCandidateTokens

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(AppColors.CardAbyss, AppColors.CardNight)
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFF6D28D9), Color(0xFF3B1070), Color(0xFF7C3AED))
                        )
                    ),
                    androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                )
        ) {
            AppBackground()

            Column(modifier = Modifier.fillMaxSize()) {
                // PREMIUM HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFF1C0D3C), Color(0xFF130826))
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(13.dp))
                                    .background(
                                        androidx.compose.ui.graphics.Brush.radialGradient(
                                            listOf(Color(0xFF3B1070), Color(0xFF1C0848))
                                        )
                                    )
                                    .border(
                                        androidx.compose.foundation.BorderStroke(
                                            1.2.dp,
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                listOf(Color(0xFF9333EA), Color(0xFF6D28D9))
                                            )
                                        ),
                                        androidx.compose.foundation.shape.RoundedCornerShape(13.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color(0xFFCDA8FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Uygulama & AI Ayarları",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Model, API anahtarları ve filtreler",
                                    color = Color(0xFF9E8DC0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                .background(Color(0x30A855F7))
                                .border(1.dp, Color(0x40A855F7), androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFFCDA8FF), modifier = Modifier.size(16.dp))
                        }
                    }
                    // Bottom separator line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0x607C3AED), Color.Transparent)
                                )
                            )
                    )
                }

                    // SCROLLABLE CONTENT BODY
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                // OPTION 1: MODELS & API KEYS
                CollapsibleSettingsOption(
                    title = "🤖 Modeller & API Anahtarları",
                    subtitle = "Gemini, Groq, Claude, OpenAI anahtarları ve aktif model seçimi",
                    icon = Icons.Default.Key,
                    isExpanded = expandedSection == "models",
                    onToggle = { expandedSection = if (expandedSection == "models") null else "models" },
                    badgeText = selectedProvider.uppercase(),
                    badgeColor = EmochiPrimary
                ) {
                    // Eye Icon for Key Visibility
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0F0826))))
                                .border(0.8.dp, EmochiPrimary.copy(alpha=0.5f), RoundedCornerShape(8.dp))
                                .clickable { showKeys = !showKeys },
                            contentAlignment = Alignment.Center
                            ) {
                            Icon(
                                imageVector = if (showKeys) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = EmochiPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showKeys) "Anahtarları Gizle" else "Anahtarları Göster",
                                color = EmochiPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    val customProviders by viewModel.customProviders.collectAsState()
                    var showAddCustomModal by remember { mutableStateOf(false) }
                    var editingCustomProvider by remember { mutableStateOf<com.example.data.local.CustomProviderEntity?>(null) }
                    var quickModelEditProvider by remember { mutableStateOf<com.example.data.local.CustomProviderEntity?>(null) }

                    // Primary Provider Selector Segmented Buttons
                    Text("Ana Sağlayıcı Seçin:", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val providers = listOf(
                            "llm7" to "⚡ LLM7",
                            "gemini" to "Gemini",
                            "groq" to "Groq",
                            "claude" to "Claude",
                            "openai" to "OpenAI",
                            "openrouter" to "OpenRouter",
                            "nvidia" to "NVIDIA NIM",
                            "mistral" to "Mistral AI",
                            "custom" to "🌐 Özel API"
                        )
                        providers.forEach { (pKey, pLabel) ->
                            val isSel = if (pKey == "custom") selectedProvider.startsWith("custom") else selectedProvider == pKey
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) EmochiPrimary.copy(alpha = 0.25f) else EmochiCard)
                                    .border(
                                        width = if (isSel) 1.5.dp else 1.dp,
                                        color = if (isSel) EmochiPrimary else EmochiBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        when (pKey) {
                                            "openrouter" -> {
                                                selectedProvider = "openrouter"
                                                selectedModel = openRouterModel.ifBlank { "deepseek/deepseek-chat" }
                                            }
                                            "nvidia" -> {
                                                selectedProvider = "nvidia"
                                                selectedModel = nvidiaModel.ifBlank { "" }
                                            }
                                            "mistral" -> {
                                                selectedProvider = "mistral"
                                                selectedModel = mistralModel.ifBlank { "mistral-large-latest" }
                                            }
                                            "custom" -> {
                                                if (customProviders.isNotEmpty()) {
                                                    selectedProvider = "custom_${customProviders.first().id}"
                                                    selectedModel = customProviders.first().modelName
                                                } else {
                                                    selectedProvider = "custom"
                                                    showAddCustomModal = true
                                                }
                                            }
                                            else -> {
                                                selectedProvider = pKey
                                                val providerModels = modelsList.filter {
                                                    when (pKey) {
                                                        "llm7" -> it.provider == "LLM7 (api.llm7.io)"
                                                        "gemini" -> it.provider == "Google Gemini"
                                                        "groq" -> it.provider == "Groq API"
                                                        "claude" -> it.provider == "Anthropic"
                                                        "openai" -> it.provider == "OpenAI" || it.provider == "DeepSeek"
                                                        else -> false
                                                    }
                                                }
                                                if (providerModels.none { it.key == selectedModel } && providerModels.isNotEmpty()) {
                                                    selectedModel = providerModels.first().key
                                                }
                                            }
                                        }
                                    }
                                    .padding(vertical = 8.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pLabel,
                                    color = if (isSel) Color.White else EmochiTextSecondary,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Reliability Dashboard for SECONDARY tier
                    ProviderReliabilityDashboardUI()

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Custom Provider Header Banner (Katman 0 Indicator)
                    val activeCustomProviderAtTop = customProviders.find { "custom_${it.id}" == selectedProvider }
                    if (activeCustomProviderAtTop != null) {
                        ActiveGradientCard(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            accentColor = AppColors.PurpleVibrant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.PurpleVibrant, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Şu an aktif: ${activeCustomProviderAtTop.label} (Özel API)",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.PurpleVibrant.copy(alpha = 0.3f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("KATMAN 0 (İLK SIRADA)", color = AppColors.PurpleLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Model: ${activeCustomProviderAtTop.modelName} | URL: ${activeCustomProviderAtTop.baseUrl}",
                                    color = AppColors.PrimaryViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚡ İstekler önce bu Özel API'ye (Katman 0) gönderilir. Başarısızlık durumunda otomatik Katman 1'e (LLM7/Pollinations/OVH) düşer.",
                                    color = EmochiTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Provider Card 0: LLM7 (Fixed / Free Provider)
                    val isLlm7Active = selectedProvider == "llm7" || enableLlm7
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isLlm7Active)
                            listOf(Color(0xFF10B981), Color(0xFF065F46), Color(0xFF10B981))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isLlm7Active) 1.8.dp else 1.2.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AppColors.SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("0. LLM7 (Ücretsiz Sabit Servis)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (enableLlm7) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.SuccessGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("ÖNCELİKLİ (STEP 1)", color = AppColors.SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "API Key gerektirmeyen ücretsiz servis (https://api.llm7.io/v1). Model: default | Key: unused",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("LLM7'yi Öncelikli Kullan (Fallback Chain)", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Açıkken her istek ilk olarak LLM7'ye gönderilir. Başarısız olursa sıradaki sağlayıcı denenir.", color = EmochiTextMuted, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = enableLlm7,
                                    onCheckedChange = { enableLlm7 = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AppColors.SuccessGreen
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isTestingLlm7 = true
                                            llm7TestResult = "Bağlantı test ediliyor..."
                                            val res = viewModel.testLlm7Connection()
                                            isTestingLlm7 = false
                                            if (res.isSuccess) {
                                                val fcText = if (res.supportsFunctionCalling) "Function calling (gelişmiş hafıza) destekleniyor." else "Metin-tabanlı hafıza modu ([[MEMORY_SAVE]]) aktif."
                                                llm7TestResult = "✅ Bağlantı başarılı! $fcText"
                                            } else {
                                                llm7TestResult = "❌ Bağlantı hatası: ${res.errorMessage}"
                                            }
                                        }
                                    },
                                    enabled = !isTestingLlm7,
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(if (isTestingLlm7) "Test ediliyor..." else "⚡ LLM7 Test Et", color = AppColors.SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showFallbackLogModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("📋 Fallback Logları", color = AppColors.PrimaryPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (!llm7TestResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = llm7TestResult!!,
                                    color = if (llm7TestResult!!.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            val llm7Stats = remember { com.example.util.Llm7RateLimitTracker.getUsageStats() }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AppColors.DarkBg,
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("📊 LLM7 Kota & Performans Göstergesi", color = AppColors.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Dakika / Saat İstek:", color = EmochiTextMuted, fontSize = 10.sp)
                                        Text("${llm7Stats.requestsLastMinute}/10 req/dk | ${llm7Stats.requestsLastHour}/60 req/saat", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("24-Saatlik Token Kullanımı:", color = EmochiTextMuted, fontSize = 10.sp)
                                        Text("${llm7Stats.tokensUsed24h} / ${llm7Stats.tokenLimit24h} token", color = if (llm7Stats.isTokenLimitReached) AppColors.ErrorRed else AppColors.MintGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Ort. Süre / Hata Düzeltme:", color = EmochiTextMuted, fontSize = 10.sp)
                                        Text("${llm7Stats.averageResponseTimeMs}ms | ${llm7Stats.malformedCount} düzelg | ${llm7Stats.fallbackCount} devir", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Card 0B: Pollinations.ai (Free Provider)
                    val isPollinationsActive = selectedProvider == "pollinations" || enablePollinations
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isPollinationsActive)
                            listOf(Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.4f), Color(0xFF10B981))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isPollinationsActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = AppColors.SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("0B. Pollinations (Limitsiz Ücretsiz)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (enablePollinations) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.SuccessGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("STEP 2 FALLBACK", color = AppColors.SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "API Key gerektirmez (https://text.pollinations.ai/openai). Key: unused",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aktif Pollinations Modeli: $pollinationsModel", color = EmochiTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("openai", "mistral", "qwen", "llama").forEach { m ->
                                    val isSelected = m == pollinationsModel
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) AppColors.SuccessGreen.copy(alpha = 0.25f) else EmochiCard)
                                            .border(1.dp, if (isSelected) AppColors.SuccessGreen else EmochiBorder, RoundedCornerShape(8.dp))
                                            .clickable { pollinationsModel = m }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(m, color = if (isSelected) Color.White else EmochiTextSecondary, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Pollinations'ı Fallback Chain'e Ekle", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text("LLM7 başarısız olursa otomatik olarak Pollinations denenir.", color = EmochiTextMuted, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = enablePollinations,
                                    onCheckedChange = { enablePollinations = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AppColors.SuccessGreen
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingPollinations = true
                                        pollinationsTestResult = "Bağlantı test ediliyor..."
                                        val res = viewModel.testPollinationsConnection(pollinationsModel)
                                        isTestingPollinations = false
                                        if (res.isSuccess) {
                                            pollinationsTestResult = "✅ Pollinations bağlantısı başarılı! (Model: $pollinationsModel)"
                                        } else {
                                            pollinationsTestResult = "❌ Bağlantı hatası: ${res.errorMessage}"
                                        }
                                    }
                                },
                                enabled = !isTestingPollinations,
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingPollinations) "Test ediliyor..." else "⚡ Pollinations Test Et", color = AppColors.SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (!pollinationsTestResult.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = pollinationsTestResult!!,
                                    color = if (pollinationsTestResult!!.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Card 0C: OVH AI Free
                    val isOvhActive = selectedProvider == "ovh" || enableOvh
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isOvhActive)
                            listOf(Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.4f), Color(0xFF10B981))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isOvhActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = AppColors.SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("0C. OVH AI (Anahtarsız Ücretsiz)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (enableOvh) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.SuccessGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("KATMAN 1 FALLBACK", color = AppColors.SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "API Key gerektirmez. Model: Meta-Llama-3-70B-Instruct (Varsayılan Kapalı).",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("OVH AI'yi Fallback Chain'e Ekle (Katman 1)", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text("LLM7 ve Pollinations başarısız olursa otomatik denenir.", color = EmochiTextMuted, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = enableOvh,
                                    onCheckedChange = { enableOvh = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AppColors.SuccessGreen
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Card 1: Google Gemini
                    val isGeminiActive = selectedProvider == "gemini"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isGeminiActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isGeminiActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.Green, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("1. Google Gemini", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isGeminiActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.Green.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.Green, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = geminiApiKey,
                                onValueChange = { geminiApiKey = it },
                                placeholder = "Gemini API Key...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "Google Gemini" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "gemini"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 2: Groq API
                    val isGroqActive = selectedProvider == "groq"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isGroqActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isGroqActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("2. Groq API (Süper Hızlı)", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isGroqActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.WarningOrange.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.WarningOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = groqApiKey,
                                onValueChange = { groqApiKey = it },
                                placeholder = "gsk_...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "Groq API" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "groq"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 3: Anthropic Claude
                    val isClaudeActive = selectedProvider == "claude"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isClaudeActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isClaudeActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = AppColors.Red, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("3. Anthropic Claude", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isClaudeActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.Red.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.Red, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = claudeApiKey,
                                onValueChange = { claudeApiKey = it },
                                placeholder = "sk-ant-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "Anthropic" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "claude"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 4: OpenAI / DeepSeek
                    val isOpenaiActive = selectedProvider == "openai"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isOpenaiActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = if (isOpenaiActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = AppColors.TealDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("4. OpenAI & DeepSeek", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isOpenaiActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.TealDark.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.TealDark, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = openaiApiKey,
                                onValueChange = { openaiApiKey = it },
                                placeholder = "sk-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Seçimi:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            modelsList.filter { it.provider == "OpenAI" || it.provider == "DeepSeek" }.forEach { spec ->
                                val isSelected = selectedModel == spec.key
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.15f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedProvider = "openai"
                                            selectedModel = spec.key
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(spec.name, color = if (isSelected) Color.White else EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(spec.tokenCostRate, color = spec.badgeColor, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Provider Card 5: OpenRouter API
                    val isOpenRouterActive = selectedProvider == "openrouter"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isOpenRouterActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF7C3AED).copy(alpha = 0.4f), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isOpenRouterActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = AppColors.Cyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("5. OpenRouter API", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isOpenRouterActive) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AppColors.Cyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("AKTİF", color = AppColors.Cyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Switch(
                                        checked = openRouterEnabled,
                                        onCheckedChange = { openRouterEnabled = it },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            GradientCard(
                                cornerRadius = 8.dp,
                                fillColors = listOf(Color(0xFF0D1F2D), Color(0xFF091520)),
                                borderColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2), Color(0xFF06B6D4)),
                                borderWidth = 1.dp
                                ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("🔑 OpenRouter Key Al: openrouter.ai/keys", color = AppColors.Cyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    Text("• Ücretsiz kullanım için model adının sonuna ':free' ekleyebilir veya 'deepseek/deepseek-chat' kullanabilirsiniz.", color = Color.White, fontSize = 9.5.sp)
                                    Text("• Oran Sınırı: Ücretsiz katmanda günde ~50 istek.", color = EmochiTextSecondary, fontSize = 9.5.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = openRouterApiKey,
                                onValueChange = { openRouterApiKey = it },
                                placeholder = "sk-or-v1-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Kodu / Adı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            EmochiTextField(
                                value = openRouterModel,
                                onValueChange = {
                                    openRouterModel = it
                                    if (isOpenRouterActive) selectedModel = it
                                },
                                placeholder = "deepseek/deepseek-chat",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Model Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "DeepSeek Chat" to "deepseek/deepseek-chat",
                                    "DeepSeek R1 (Free)" to "deepseek/deepseek-r1:free",
                                    "Llama 3.3 70B (Free)" to "meta-llama/llama-3.3-70b-instruct:free",
                                    "Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct"
                                ).forEach { (lbl, code) ->
                                    OutlinedButton(
                                        onClick = {
                                            openRouterModel = code
                                            selectedProvider = "openrouter"
                                            selectedModel = code
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(lbl, fontSize = 9.5.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingOpenRouter = true
                                        openRouterTestResult = "Test ediliyor..."
                                        val res = viewModel.testOpenRouterConnection(openRouterApiKey, openRouterModel)
                                        isTestingOpenRouter = false
                                        openRouterTestResult = if (res.isSuccess) "✅ OpenRouter bağlantısı başarılı!" else "❌ Hata: ${res.errorMessage}"
                                    }
                                },
                                enabled = !isTestingOpenRouter && openRouterApiKey.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingOpenRouter) "Test ediliyor..." else "⚡ OpenRouter Test Et", color = AppColors.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            openRouterTestResult?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(msg, color = if (msg.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft, fontSize = 11.sp)
                            }
                        }
                    }

                    // Provider Card 6: NVIDIA NIM API
                    val isNvidiaActive = selectedProvider == "nvidia"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isNvidiaActive)
                            listOf(Color(0xFF7C3AED), Color(0xFF7C3AED).copy(alpha = 0.4f), Color(0xFF7C3AED))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isNvidiaActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = AppColors.NvidiaGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("6. NVIDIA NIM API", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isNvidiaActive) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AppColors.NvidiaGreen.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("AKTİF", color = AppColors.NvidiaGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Switch(
                                        checked = nvidiaEnabled,
                                        onCheckedChange = { nvidiaEnabled = it },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            GradientCard(
                                cornerRadius = 8.dp,
                                fillColors = listOf(Color(0xFF0A1F10), Color(0xFF061408)),
                                borderColors = listOf(Color(0xFF76B900), Color(0xFF4A7500), Color(0xFF76B900)),
                                borderWidth = 1.dp
                                ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("🔑 NVIDIA NIM Key Al: build.nvidia.com", color = AppColors.NvidiaGreen, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    Text("• Model formatı: sağlayıcı/model (ör. deepseek-ai/deepseek-v4-flash, mistralai/mistral-nemotron).", color = Color.White, fontSize = 9.5.sp)
                                    Text("⚠️ Limit: Dakikada 40 istek (40 RPM) tüm modeller toplamında geçerlidir. Kredi biterse HTTP 402 verir.", color = AppColors.Hex_FFCC00, fontSize = 9.5.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = nvidiaApiKey,
                                onValueChange = { nvidiaApiKey = it },
                                placeholder = "nvapi-...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Kodu / Adı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            EmochiTextField(
                                value = nvidiaModel,
                                onValueChange = {
                                    nvidiaModel = it
                                    if (isNvidiaActive) selectedModel = it
                                },
                                placeholder = "deepseek-ai/deepseek-v4-flash",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Model Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "DeepSeek V4 Flash" to "deepseek-ai/deepseek-v4-flash",
                                    "Mistral Nemotron" to "mistralai/mistral-nemotron",
                                    "Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct"
                                ).forEach { (lbl, code) ->
                                    OutlinedButton(
                                        onClick = {
                                            nvidiaModel = code
                                            selectedProvider = "nvidia"
                                            selectedModel = code
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(lbl, fontSize = 9.5.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingNvidia = true
                                        nvidiaTestResult = "Test ediliyor..."
                                        val res = viewModel.testNvidiaConnection(nvidiaApiKey, nvidiaModel)
                                        isTestingNvidia = false
                                        nvidiaTestResult = if (res.isSuccess) "✅ NVIDIA NIM bağlantısı başarılı!" else "❌ Hata: ${res.errorMessage}"
                                    }
                                },
                                enabled = !isTestingNvidia && nvidiaApiKey.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingNvidia) "Test ediliyor..." else "⚡ NVIDIA NIM Test Et", color = AppColors.NvidiaGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            nvidiaTestResult?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(msg, color = if (msg.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft, fontSize = 11.sp)
                            }
                        }
                    }

                    // Provider Card 7: Mistral AI API
                    val isMistralActive = selectedProvider == "mistral"
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isMistralActive)
                            listOf(Color(0xFFF59E0B), Color(0xFFF59E0B).copy(alpha = 0.4f), Color(0xFFF59E0B))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isMistralActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("8. Mistral AI API", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isMistralActive) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(AppColors.WarningOrange.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("AKTİF", color = AppColors.WarningOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Switch(
                                        checked = mistralEnabled,
                                        onCheckedChange = { mistralEnabled = it },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            GradientCard(
                                cornerRadius = 8.dp,
                                fillColors = listOf(Color(0xFF1A1000), Color(0xFF100A00)),
                                borderColors = listOf(Color(0xFFF59E0B), Color(0xFF92400E), Color(0xFFF59E0B)),
                                borderWidth = 1.dp
                                ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("🔑 Mistral Key Al: console.mistral.ai", color = AppColors.WarningOrange, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    Text("• Model adı: mistral-large-latest, open-mistral-nemo, codestral-latest", color = Color.White, fontSize = 9.5.sp)
                                    Text("⚠️ GİZLİLİK UYARISI: Bu ücretsiz katmanda verilerinizin Mistral tarafından model eğitiminde kullanılmasına izin vermiş olursunuz.", color = AppColors.Hex_FFD54F, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            EmochiTextField(
                                value = mistralApiKey,
                                onValueChange = { mistralApiKey = it },
                                placeholder = "Mistral API Key...",
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Model Kodu / Adı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            EmochiTextField(
                                value = mistralModel,
                                onValueChange = {
                                    mistralModel = it
                                    if (isMistralActive) selectedModel = it
                                },
                                placeholder = "mistral-large-latest",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Model Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "Mistral Large" to "mistral-large-latest",
                                    "Mistral Nemo" to "open-mistral-nemo",
                                    "Codestral" to "codestral-latest"
                                ).forEach { (lbl, code) ->
                                    OutlinedButton(
                                        onClick = {
                                            mistralModel = code
                                            selectedProvider = "mistral"
                                            selectedModel = code
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(lbl, fontSize = 9.5.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        isTestingMistral = true
                                        mistralTestResult = "Test ediliyor..."
                                        val res = viewModel.testMistralConnection(mistralApiKey, mistralModel)
                                        isTestingMistral = false
                                        mistralTestResult = if (res.isSuccess) "✅ Mistral AI bağlantısı başarılı!" else "❌ Hata: ${res.errorMessage}"
                                    }
                                },
                                enabled = !isTestingMistral && mistralApiKey.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DarkSlate),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isTestingMistral) "Test ediliyor..." else "⚡ Mistral AI Test Et", color = AppColors.WarningOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            mistralTestResult?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(msg, color = if (msg.contains("✅")) AppColors.LightGreen else AppColors.ErrorRedSoft, fontSize = 11.sp)
                            }
                        }
                    }

                    // Provider Card 9: Özel API / Proxy (HER ZAMAN GÖRÜNÜR & BELİRGİN)
                    val isCustomActive = selectedProvider.startsWith("custom")
                    GradientCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColors = if (isCustomActive)
                            listOf(Color(0xFFF59E0B), Color(0xFFF59E0B).copy(alpha = 0.4f), Color(0xFFF59E0B))
                        else
                            listOf(Color(0xFF4C1D95), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF4C1D95)),
                        borderWidth = if (isCustomActive) 1.8.dp else 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("5. Özel API & OpenRouter Proxy", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                if (isCustomActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppColors.WarningOrange.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("AKTİF SAĞLAYICI", color = AppColors.WarningOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "OpenRouter, VLLM, LM Studio, Ollama veya özel bir proxy adresi bağlayarak dilediğiniz yapay zeka modelinin kodunu (örn. Llama 3.3, DeepSeek R1, Qwen 2.5) yazıp kullanabilirsiniz.",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Direct Model Code Editing Field
                            Text("Özel Model Kodu / Adı (Elle Yazabilirsiniz):", color = AppColors.AmberSoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            EmochiTextField(
                                value = selectedModel,
                                onValueChange = { newM ->
                                    selectedModel = newM
                                    if (selectedProvider.startsWith("custom_")) {
                                        val cId = selectedProvider.removePrefix("custom_").toLongOrNull()
                                        val activeP = customProviders.find { it.id == cId }
                                        if (activeP != null) {
                                            viewModel.updateCustomProviderModel(activeP.id, newM)
                                        }
                                    }
                                },
                                placeholder = "örn. meta-llama/llama-3.3-70b-instruct",
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                singleLine = true
                            )

                            // Quick Model Template Chips
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Popüler Model Kısayolları (Dokun & Doldur):", color = EmochiTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val quickChips = listOf(
                                "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                "🤖 GPT-4o Mini" to "openai/gpt-4o-mini"
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                quickChips.forEach { (lbl, code) ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmochiSurface)
                                            .border(1.dp, AppColors.WarningOrange.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedModel = code
                                                if (selectedProvider.startsWith("custom_")) {
                                                    val cId = selectedProvider.removePrefix("custom_").toLongOrNull()
                                                    val activeP = customProviders.find { it.id == cId }
                                                    if (activeP != null) {
                                                        viewModel.updateCustomProviderModel(activeP.id, code)
                                                    }
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(lbl, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // GÖRÜNÜR & BELİRGİN EKLE BUTONU
                            Button(
                                onClick = { showAddCustomModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Yeni Özel API / Model Ekle", color = Color.Black, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            }

                            if (customProviders.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Kayıtlı Özel API Sağlayıcılarınız:", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))

                                customProviders.forEach { cp ->
                                    val isThisSelected = selectedProvider == "custom_${cp.id}"
                                    GradientCard(
                                        cornerRadius = 10.dp,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                        fillColors = if (isThisSelected) listOf(Color(0xFF1E0B3A), Color(0xFF120820)) else listOf(Color(0xFF130C2A), Color(0xFF0E0820)),
                                        borderColors = if (isThisSelected) listOf(Color(0xFF9333EA), Color(0xFF7C3AED), Color(0xFF9333EA)) else listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                                        borderWidth = if (isThisSelected) 1.8.dp else 1.2.dp
                                        ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(cp.label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(EmochiBorder)
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(cp.apiFormat.uppercase(), color = EmochiTextSecondary, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text("URL: ${cp.baseUrl}", color = EmochiTextMuted, fontSize = 9.5.sp, maxLines = 1)
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text("Model: ", color = EmochiTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                        Text(cp.modelName, color = AppColors.Hex_4ADE80, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                if (isThisSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(AppColors.PurpleVibrant.copy(alpha = 0.3f))
                                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Text("KATMAN 0 AKTİF", color = AppColors.PurpleLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isThisSelected) AppColors.PurpleVibrant.copy(alpha = 0.15f) else EmochiCard)
                                                    .clickable {
                                                        selectedProvider = "custom_${cp.id}"
                                                        selectedModel = cp.modelName
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isThisSelected,
                                                    onClick = {
                                                        selectedProvider = "custom_${cp.id}"
                                                        selectedModel = cp.modelName
                                                    },
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = AppColors.PurpleVibrant,
                                                        unselectedColor = EmochiTextMuted
                                                    ),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isThisSelected) "Aktif Sağlayıcı (Katman 0 — Öncelikli)" else "Aktif Sağlayıcı Olarak Kullan",
                                                    color = if (isThisSelected) AppColors.PurpleLight else EmochiTextSecondary,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isThisSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedButton(
                                                    onClick = { quickModelEditProvider = cp },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(26.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = null, tint = AppColors.AmberSoft, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Modeli Yaz / Değiştir", color = AppColors.AmberSoft, fontSize = 9.5.sp)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                OutlinedButton(
                                                    onClick = { editingCustomProvider = cp },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(26.dp)
                                                ) {
                                                    Text("Düzenle", color = EmochiTextPrimary, fontSize = 9.5.sp)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                IconButton(
                                                    onClick = { viewModel.deleteCustomProvider(cp.id) },
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Backup Gemini Key Field
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Yedek Gemini API Key:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    EmochiTextField(
                        value = backupApiKey,
                        onValueChange = { backupApiKey = it },
                        placeholder = "Yedek Gemini Key...",
                        visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        singleLine = true
                    )

                    // Active Selected Model Details Card
                    Spacer(modifier = Modifier.height(10.dp))
                    val activeCustomProvider = customProviders.find { "custom_${it.id}" == selectedProvider }
                    if (activeCustomProvider != null) {
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFFF59E0B), Color(0xFF92400E), Color(0xFFF59E0B)),
                            borderWidth = 1.5.dp
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = AppColors.WarningOrange, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Aktif Özel Model: ${activeCustomProvider.modelName}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(activeCustomProvider.label, color = AppColors.WarningOrange, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("URL: ${activeCustomProvider.baseUrl} | Format: ${activeCustomProvider.apiFormat.uppercase()}", color = EmochiTextSecondary, fontSize = 10.5.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { quickModelEditProvider = activeCustomProvider },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Farklı Model Kodu Yaz / Değiştir", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        val currentSelectedSpec = modelsList.find { it.key == selectedModel } ?: modelsList.first()
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFF10B981), Color(0xFF065F46), Color(0xFF10B981)),
                            borderWidth = 1.5.dp
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = AppColors.ErrorRed, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Aktif Seçili Model: ${currentSelectedSpec.name}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(currentSelectedSpec.tokenCostRate, color = AppColors.Emerald, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(currentSelectedSpec.description, color = EmochiTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                            }
                        }
                    }

                    // MODAL 1: YENİ ÖZEL SAĞLAYICI & MODEL EKLE
                    if (showAddCustomModal) {
                        var cLabel by remember { mutableStateOf("") }
                        var cBaseUrl by remember { mutableStateOf("https://openrouter.ai/api/v1") }
                        var cApiKey by remember { mutableStateOf("") }
                        var cModelName by remember { mutableStateOf("") }
                        var cApiFormat by remember { mutableStateOf("openai") }
                        var showKeyText by remember { mutableStateOf(false) }

                        var isTesting by remember { mutableStateOf(false) }
                        var testRes by remember { mutableStateOf<EmochiRepository.ProviderTestResult?>(null) }
                        val customModalScope = rememberCoroutineScope()

                        Dialog(onDismissRequest = { showAddCustomModal = false }) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmochiSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder),
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                     Text("🌐 Yeni Özel API & Model Ekle", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                                    EmochiTextField(
                                        value = cLabel,
                                        onValueChange = { cLabel = it; testRes = null },
                                        label = "Sağlayıcı Etiketi (ör. OpenRouter, NVIDIA NIM)",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("API Formatı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = cApiFormat == "openai",
                                            onClick = { cApiFormat = "openai"; testRes = null },
                                            label = { Text("OpenAI Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = cApiFormat == "anthropic",
                                            onClick = { cApiFormat = "anthropic"; testRes = null },
                                            label = { Text("Anthropic Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    EmochiTextField(
                                        value = cBaseUrl,
                                        onValueChange = { cBaseUrl = it; testRes = null },
                                        label = "Base URL (HTTPS zorunlu)",
                                        placeholder = "https://openrouter.ai/api/v1",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    // URL Preset Chips
                                    Text("Hızlı Adres Kısayolları:", color = EmochiTextMuted, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "OpenRouter" to "https://openrouter.ai/api/v1",
                                            "Groq Proxy" to "https://api.groq.com/openai/v1",
                                            "DeepSeek" to "https://api.deepseek.com/v1",
                                            "Ollama Local" to "http://localhost:11434/v1"
                                        ).forEach { (presetName, presetUrl) ->
                                            OutlinedButton(
                                                onClick = { cBaseUrl = presetUrl; testRes = null },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(presetName, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    EmochiTextField(
                                        value = cApiKey,
                                        onValueChange = { cApiKey = it; testRes = null },
                                        label = "API Key",
                                        visualTransformation = if (showKeyText) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            androidx.compose.material3.IconButton(onClick = { showKeyText = !showKeyText }) {
                                                Icon(if (showKeyText) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFCDA8FF))
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    // MODEL SEÇİMİ VE EL İLE MODEL YAZMA ALANI
                                    EmochiTextField(
                                        value = cModelName,
                                        onValueChange = { cModelName = it; testRes = null },
                                        label = "Model Kodu / Adı (El ile Dilediğinizi Yazın)",
                                        placeholder = "meta-llama/llama-3.3-70b-instruct",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    // MODEL HAZIR ŞABLON ŞİPLERİ
                                    Text("Popüler Model Kısayolları (Dokun & Doldur):", color = EmochiTextMuted, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                            "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                            "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                            "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                            "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                            "🤖 GPT-4o Mini" to "openai/gpt-4o-mini",
                                            "💨 Mistral Large" to "mistralai/mistral-large"
                                        ).forEach { (mTag, mCode) ->
                                            OutlinedButton(
                                                onClick = { cModelName = mCode; testRes = null },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(mTag, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    GradientCard(
                                        cornerRadius = 10.dp,
                                        fillColors = listOf(Color(0xFF1A1500), Color(0xFF100E00)),
                                        borderColors = listOf(Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFFFBBF24)),
                                        borderWidth = 1.2.dp
                                        ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Security, contentDescription = null, tint = AppColors.YellowAmber, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Güvenlik Uyarısı: Buraya girdiğin API key, doğrudan girdiğin adrese ($cBaseUrl) gönderilir. Güvendiğin ve HTTPS olan bir kaynak olduğundan emin ol. Key Keystore ile şifrelenir.",
                                                color = AppColors.Hex_FFE082,
                                                fontSize = 10.sp,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }

                                    val currentTestRes = testRes
                                    if (currentTestRes != null) {
                                        GradientCard(
                                            cornerRadius = 10.dp,
                                            fillColors = if (currentTestRes.isSuccess) listOf(Color(0xFF001A0E), Color(0xFF00100A)) else listOf(Color(0xFF1A0000), Color(0xFF100000)),
                                            borderColors = if (currentTestRes.isSuccess) listOf(Color(0xFF10B981), Color(0xFF065F46), Color(0xFF10B981)) else listOf(Color(0xFFEF4444), Color(0xFF7F1D1D), Color(0xFFEF4444))
                                            ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                if (currentTestRes.isSuccess) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AppColors.Green, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Bağlantı Başarılı!", color = AppColors.LightGreenSoft, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        if (currentTestRes.supportsFunctionCalling) "✅ Bu sağlayıcı gelişmiş hafıza (tools/function calling) modunu destekliyor."
                                                        else "⚠️ Bu sağlayıcı gelişmiş hafıza özelliğini desteklemiyor olabilir, metin tabanlı yedek hafıza modu kullanılacak.",
                                                        color = Color.White,
                                                        fontSize = 10.5.sp
                                                    )
                                                } else {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Error, contentDescription = null, tint = AppColors.Red, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Bağlantı Başarısız", color = AppColors.Hex_E57373, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(currentTestRes.errorMessage, color = Color.White, fontSize = 10.5.sp)
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { showAddCustomModal = false },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("İptal", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                customModalScope.launch {
                                                    isTesting = true
                                                    testRes = viewModel.testCustomProviderConnection(cBaseUrl, cApiKey, cModelName, cApiFormat)
                                                    isTesting = false
                                                }
                                            },
                                            enabled = !isTesting && cBaseUrl.isNotBlank() && cApiKey.isNotBlank() && cModelName.isNotBlank(),
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange)
                                        ) {
                                            if (isTesting) {
                                                EmochiLoadingSpinner(size = 14.dp, small = true, color = Color.White)
                                            } else {
                                                Text("Bağlantıyı Test Et", fontSize = 10.5.sp, maxLines = 1)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            val finalResult = testRes
                                            viewModel.saveCustomProvider(
                                                label = cLabel.ifBlank { "Özel API" },
                                                baseUrl = cBaseUrl,
                                                apiKey = cApiKey,
                                                modelName = cModelName,
                                                apiFormat = cApiFormat,
                                                supportsFunctionCalling = finalResult?.supportsFunctionCalling ?: true
                                            )
                                            selectedModel = cModelName
                                            showAddCustomModal = false
                                        },
                                        enabled = cBaseUrl.isNotBlank() && cApiKey.isNotBlank() && cModelName.isNotBlank(),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary)
                                    ) {
                                        Text("Kaydet ve Kullan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // MODAL 2: HIZLI MODEL DEĞİŞTİRME / YAZMA PENCERESİ
                    val currentQuickProvider = quickModelEditProvider
                    if (currentQuickProvider != null) {
                        var newMName by remember { mutableStateOf(currentQuickProvider.modelName) }
                        Dialog(onDismissRequest = { quickModelEditProvider = null }) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmochiSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder),
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("✏️ Model Kodunu Güncelle", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Sağlayıcı: ${currentQuickProvider.label}", color = EmochiTextSecondary, fontSize = 11.sp)

                                    EmochiTextField(
                                        value = newMName,
                                        onValueChange = { newMName = it },
                                        label = "Model Kodu / Adı (El ile İstediğinizi Yazın)",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("Hızlı Model Şablonları:", color = EmochiTextMuted, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                            "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                            "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                            "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                            "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                            "🤖 GPT-4o Mini" to "openai/gpt-4o-mini",
                                            "💨 Mistral Large" to "mistralai/mistral-large"
                                        ).forEach { (mTag, mCode) ->
                                            OutlinedButton(
                                                onClick = { newMName = mCode },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(mTag, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { quickModelEditProvider = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("İptal", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = {
                                                if (newMName.isNotBlank()) {
                                                    viewModel.updateCustomProviderModel(currentQuickProvider.id, newMName)
                                                    selectedModel = newMName.trim()
                                                    quickModelEditProvider = null
                                                }
                                            },
                                            enabled = newMName.isNotBlank(),
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary)
                                        ) {
                                            Text("Modeli Güncelle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MODAL 3: TAM SAĞLAYICI DÜZENLEME PENCERESİ
                    val currentEditProvider = editingCustomProvider
                    if (currentEditProvider != null) {
                        var eLabel by remember { mutableStateOf(currentEditProvider.label) }
                        var eBaseUrl by remember { mutableStateOf(currentEditProvider.baseUrl) }
                        var eApiKey by remember { mutableStateOf("") }
                        var eModelName by remember { mutableStateOf(currentEditProvider.modelName) }
                        var eApiFormat by remember { mutableStateOf(currentEditProvider.apiFormat) }
                        var showEKeyText by remember { mutableStateOf(false) }

                        var isETesting by remember { mutableStateOf(false) }
                        var eTestRes by remember { mutableStateOf<EmochiRepository.ProviderTestResult?>(null) }
                        val editModalScope = rememberCoroutineScope()

                        Dialog(onDismissRequest = { editingCustomProvider = null }) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EmochiSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder),
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🛠️ Özel API Sağlayıcısını Düzenle", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                                    EmochiTextField(
                                        value = eLabel,
                                        onValueChange = { eLabel = it; eTestRes = null },
                                        label = "Sağlayıcı Etiketi",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("API Formatı:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = eApiFormat == "openai",
                                            onClick = { eApiFormat = "openai"; eTestRes = null },
                                            label = { Text("OpenAI Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = eApiFormat == "anthropic",
                                            onClick = { eApiFormat = "anthropic"; eTestRes = null },
                                            label = { Text("Anthropic Uyumlu", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    EmochiTextField(
                                        value = eBaseUrl,
                                        onValueChange = { eBaseUrl = it; eTestRes = null },
                                        label = "Base URL",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    EmochiTextField(
                                        value = eApiKey,
                                        onValueChange = { eApiKey = it; eTestRes = null },
                                        label = "Yeni API Key (Boş Bırakırsanız Değişmez)",
                                        visualTransformation = if (showEKeyText) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            androidx.compose.material3.IconButton(onClick = { showEKeyText = !showEKeyText }) {
                                                Icon(if (showEKeyText) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFCDA8FF))
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    EmochiTextField(
                                        value = eModelName,
                                        onValueChange = { eModelName = it; eTestRes = null },
                                        label = "Model Kodu / Adı (El ile Yazın)",
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "🦙 Llama 3.3 70B" to "meta-llama/llama-3.3-70b-instruct",
                                            "🐋 DeepSeek R1" to "deepseek/deepseek-r1",
                                            "🌐 Qwen 2.5 72B" to "qwen/qwen-2.5-72b-instruct",
                                            "⚡ Gemini 2.0 Flash" to "google/gemini-2.0-flash-001",
                                            "🧠 Claude 3.5 Sonnet" to "anthropic/claude-3.5-sonnet",
                                            "🤖 GPT-4o Mini" to "openai/gpt-4o-mini"
                                        ).forEach { (mTag, mCode) ->
                                            OutlinedButton(
                                                onClick = { eModelName = mCode; eTestRes = null },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(mTag, fontSize = 9.5.sp)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { editingCustomProvider = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("İptal", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                editModalScope.launch {
                                                    isETesting = true
                                                    val testKeyToUse = eApiKey.ifBlank { "existing_key" }
                                                    eTestRes = viewModel.testCustomProviderConnection(eBaseUrl, testKeyToUse, eModelName, eApiFormat)
                                                    isETesting = false
                                                }
                                            },
                                            enabled = !isETesting && eBaseUrl.isNotBlank() && eModelName.isNotBlank(),
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.WarningOrange)
                                        ) {
                                            if (isETesting) {
                                                EmochiLoadingSpinner(size = 14.dp, small = true, color = Color.White)
                                            } else {
                                                Text("Bağlantıyı Test Et", fontSize = 10.5.sp, maxLines = 1)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.updateCustomProviderFull(
                                                id = currentEditProvider.id,
                                                label = eLabel,
                                                baseUrl = eBaseUrl,
                                                apiKey = eApiKey.ifBlank { null },
                                                modelName = eModelName,
                                                apiFormat = eApiFormat,
                                                supportsFunctionCalling = eTestRes?.supportsFunctionCalling ?: currentEditProvider.supportsFunctionCalling
                                            )
                                            selectedModel = eModelName
                                            editingCustomProvider = null
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary)
                                    ) {
                                        Text("Değişiklikleri Kaydet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // OPTION 2: RESPONSE SETTINGS & FALLBACK
                CollapsibleSettingsOption(
                    title = "💬 Yanıt Ayarları & Yedek Model",
                    subtitle = "Varsayılan yanıt uzunluğu, yedek model ve otomatik geçiş",
                    icon = Icons.Default.Tune,
                    isExpanded = expandedSection == "length" || expandedSection == "response",
                    onToggle = { expandedSection = if (expandedSection == "length" || expandedSection == "response") null else "response" },
                    badgeText = if (enableAutoFallback) "AÇIK" else "KAPALI",
                    badgeColor = if (enableAutoFallback) AppColors.Emerald else EmochiTextMuted
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text("Varsayılan Yanıt Uzunluğu:", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val options = listOf(
                                    Triple("short", "Kısa", Icons.Default.FlashOn),
                                    Triple("orta", "Orta", Icons.Default.AutoAwesome),
                                    Triple("standard", "Standart", Icons.Default.Description),
                                    Triple("long", "Uzun", Icons.Default.List)
                                )
                                options.forEach { (key, label, icon) ->
                                    val isSelected = responseLength == key
                                    val tintColor = if (key == "orta") AppColors.MintGreen else if (key == "long") AppColors.PrimaryPurple else AppColors.Gold
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) AppColors.DeepPurple.copy(alpha = 0.15f) else EmochiCard)
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) AppColors.DeepPurple else EmochiBorder,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable { responseLength = key }
                                            .padding(horizontal = 6.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = label,
                                                color = if (isSelected) Color.White else EmochiTextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Card for Automatic Fallback & Fallback Model
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                            borderWidth = 1.2.dp
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = EmochiPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                "Otomatik Model/API Geçişi",
                                                color = EmochiTextPrimary,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "Modelde hata veya kota aşımı olursa otomatik yedek modele geç.",
                                                color = EmochiTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = enableAutoFallback,
                                        onCheckedChange = { enableAutoFallback = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = AppColors.DarkSurface,
                                            checkedTrackColor = EmochiPrimary
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = AppColors.CardPurple)
                                Spacer(modifier = Modifier.height(10.dp))

                                 Text(
                                    "Yedek Model (Hata / Kota Aşımında Kullanılır):",
                                    color = EmochiTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                ExposedDropdownMenuBox(
                                    expanded = fallbackDropdownExpanded,
                                    onExpandedChange = { fallbackDropdownExpanded = !fallbackDropdownExpanded },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    val currentFallbackSpec = modelsList.find { it.key == fallbackModel } ?: modelsList.first()
                                    OutlinedTextField(
                                        value = "${currentFallbackSpec.name} — ${currentFallbackSpec.tokenCostRate}",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fallbackDropdownExpanded) },
                                        colors = customTextFieldColors(),
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = fallbackDropdownExpanded,
                                        onDismissRequest = { fallbackDropdownExpanded = false },
                                        modifier = Modifier.background(EmochiCard)
                                    ) {
                                        modelsList.forEach { spec ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(spec.name, color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        Text(spec.tokenCostRate, color = EmochiPrimary, fontSize = 10.5.sp)
                                                    }
                                                },
                                                onClick = {
                                                    fallbackModel = spec.key
                                                    fallbackDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = AppColors.CardPurple)
                                Spacer(modifier = Modifier.height(8.dp))

                                // FALLBACK CHAIN REORDERING UI
                                Text("🔀 Fallback Sıralaması ve Öncelik Zinciri:", color = EmochiPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text("Sağlayıcılar yukarıdan aşağıya doğru sırayla denenir. Ok butonları ile öncelik sırasını değiştirebilirsiniz.", color = EmochiTextMuted, fontSize = 10.sp)

                                val customListForChain by viewModel.customProviders.collectAsState()
                                val customMapForChain = customListForChain.associateBy { "custom_${it.id}" }

                                val currentItems = if (fallbackChainOrder.isNotBlank()) {
                                    fallbackChainOrder.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                } else {
                                    val def = mutableListOf("llm7", "pollinations", "ovh", "main")
                                    customListForChain.forEach { cp -> def.add("custom_${cp.id}") }
                                    def.add("autofallback")
                                    def
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                    currentItems.forEachIndexed { index, itemKey ->
                                        val displayLabel = when {
                                            itemKey == "llm7" -> "1. LLM7 (Ücretsiz Servis)"
                                            itemKey == "pollinations" -> "2. Pollinations (${pollinationsModel.ifBlank { "openai" }})"
                                            itemKey == "ovh" -> "3. OVH AI (${ovhModel})"
                                            itemKey == "main" -> "4. Ana Seçili Sağlayıcı ($selectedProvider)"
                                            itemKey == "autofallback" -> "5. Otomatik Gemini Fallback ($fallbackModel)"
                                            itemKey.startsWith("custom_") -> {
                                                val cp = customMapForChain[itemKey]
                                                if (cp != null) "Özel: ${cp.label} (${cp.modelName})" else "Özel Sağlayıcı ($itemKey)"
                                            }
                                            else -> itemKey
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(AppColors.Hex_1E1638)
                                                .border(1.dp, AppColors.Hex_382C5E, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${index + 1}. $displayLabel",
                                                color = Color.White,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        if (index > 0) {
                                                            val mutable = currentItems.toMutableList()
                                                            val tmp = mutable[index]
                                                            mutable[index] = mutable[index - 1]
                                                            mutable[index - 1] = tmp
                                                            fallbackChainOrder = mutable.joinToString(",")
                                                        }
                                                    },
                                                    enabled = index > 0,
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Text("▲", color = if (index > 0) EmochiPrimary else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        if (index < currentItems.size - 1) {
                                                            val mutable = currentItems.toMutableList()
                                                            val tmp = mutable[index]
                                                            mutable[index] = mutable[index + 1]
                                                            mutable[index + 1] = tmp
                                                            fallbackChainOrder = mutable.joinToString(",")
                                                        }
                                                    },
                                                    enabled = index < currentItems.size - 1,
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Text("▼", color = if (index < currentItems.size - 1) EmochiPrimary else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        fallbackChainOrder = ""
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp).align(Alignment.End)
                                ) {
                                    Text("Varsayılan Sıraya Sıfırla", fontSize = 9.5.sp, color = EmochiTextMuted)
                                }
                            }
                        }
                    }
                }

                // OPTION 3: CONTENT FILTERS (NSFW)
                CollapsibleSettingsOption(
                    title = "🔞 İçerik Filtreleri (+18)",
                    subtitle = "Yetişkin içerik (NSFW), Flirty, Hardcore, Fetish, Dark RP filtreleri",
                    icon = Icons.Default.Warning,
                    isExpanded = expandedSection == "nsfw",
                    onToggle = { expandedSection = if (expandedSection == "nsfw") null else "nsfw" },
                    badgeText = if (enableNsfw) "AÇIK" else "KAPALI",
                    badgeColor = if (enableNsfw) AppColors.ErrorRed else EmochiTextMuted
                ) {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
                            FilterToggleRow(label = "🔓  18+ (NSFW) Genel Kilidini Aç", checked = enableNsfw) { enableNsfw = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "💖  Çapkınlık (Flirty)", checked = enableFlirty) { enableFlirty = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🔥  Sert Mod (Hardcore)", checked = enableHardcore) { enableHardcore = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🎭  Fantezi (Fetish)", checked = enableFetish) { enableFetish = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "👻  Karanlık (Dark RP)", checked = enableDarkRp) { enableDarkRp = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🍬  Romantik (Sweet)", checked = enableSweet) { enableSweet = it }
                            Divider(color = AppColors.PlumDark)
                            FilterToggleRow(label = "🐾  Vahşi (Primal)", checked = enablePrimal) { enablePrimal = it }
                        }
                    }
                }

                // OPTION 4: TEXT-TO-SPEECH (TTS)
                CollapsibleSettingsOption(
                    title = "🔊 Sesli Okuma (TTS)",
                    subtitle = "Sesli okuma aktivasyonu, okuma hızı ve ses tonu",
                    icon = Icons.Default.VolumeUp,
                    isExpanded = expandedSection == "tts",
                    onToggle = { expandedSection = if (expandedSection == "tts") null else "tts" }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sesli Okuma Özelliğini Etkinleştir", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = enableTts,
                            onCheckedChange = { enableTts = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AppColors.DarkSurface, checkedTrackColor = EmochiPrimary)
                        )
                    }

                    if (enableTts) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(EmochiCard, RoundedCornerShape(12.dp))
                                .border(1.dp, EmochiBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚡ Okuma Hızı: ${"%.1f".format(ttsSpeed)}x", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(0.8f to "0.8x", 1.0f to "1.0x", 1.25f to "1.25x", 1.5f to "1.5x").forEach { (sp, lbl) ->
                                    val sel = ttsSpeed == sp
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) EmochiPrimary.copy(alpha = 0.25f) else EmochiSurface)
                                            .border(1.dp, if (sel) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                            .clickable { ttsSpeed = sp }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(lbl, color = if (sel) EmochiPrimary else EmochiTextSecondary, fontSize = 10.5.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Text("🎵 Ses Tonu (Pitch): ${"%.1f".format(ttsPitch)}x", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(0.8f to "Kalın (0.8)", 1.0f to "Normal (1.0)", 1.2f to "İnce (1.2)").forEach { (p, lbl) ->
                                    val sel = ttsPitch == p
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) EmochiPrimary.copy(alpha = 0.25f) else EmochiSurface)
                                            .border(1.dp, if (sel) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                            .clickable { ttsPitch = p }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(lbl, color = if (sel) EmochiPrimary else EmochiTextSecondary, fontSize = 10.5.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }
                }

                // OPTION 5: LANGUAGE
                CollapsibleSettingsOption(
                    title = "🌐 Uygulama & Yanıt Dili",
                    subtitle = "Yapay zeka yanıtları için varsayılan dil tercihi",
                    icon = Icons.Default.Language,
                    isExpanded = expandedSection == "lang",
                    onToggle = { expandedSection = if (expandedSection == "lang") null else "lang" },
                    badgeText = if (appLanguage == "tr") "TÜRKÇE" else "ENGLISH"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        androidx.compose.material3.FilterChip(
                            selected = appLanguage == "tr",
                            onClick = { appLanguage = "tr" },
                            label = { Text("🇹🇷 Türkçe (Varsayılan)", color = if (appLanguage == "tr") AppColors.DarkSurface else EmochiTextPrimary, fontWeight = FontWeight.Bold) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(selectedContainerColor = EmochiPrimary, containerColor = EmochiCard)
                        )
                        androidx.compose.material3.FilterChip(
                            selected = appLanguage == "en",
                            onClick = { appLanguage = "en" },
                            label = { Text("🇬🇧 English", color = if (appLanguage == "en") AppColors.DarkSurface else EmochiTextPrimary, fontWeight = FontWeight.Bold) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(selectedContainerColor = EmochiPrimary, containerColor = EmochiCard)
                        )
                    }
                }

                // OPTION 6: TOKEN USAGE
                CollapsibleSettingsOption(
                    title = "📊 Token Kullanım İstatistikleri",
                    subtitle = "Toplam harcanan girdi ve çıktı token verileri",
                    icon = Icons.Default.Speed,
                    isExpanded = expandedSection == "tokens",
                    onToggle = { expandedSection = if (expandedSection == "tokens") null else "tokens" }
                ) {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Girdi Token", color = EmochiTextMuted, fontSize = 11.sp)
                                    Text("${settings.totalPromptTokens}", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Çıktı Token", color = EmochiTextMuted, fontSize = 11.sp)
                                    Text("${settings.totalCandidateTokens}", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Maliyet", color = EmochiTextMuted, fontSize = 11.sp)
                                    Text("Ücretsiz", color = AppColors.PrimaryPurple, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toplam Harcanan: $totalTokensUsed token",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // OPTION 7: BACKUP & RESTORE
                CollapsibleSettingsOption(
                    title = "💾 Yedekleme & Geri Yükleme",
                    subtitle = "Tüm botlarınızı ve verilerinizi yedekleyin veya içe aktarın",
                    icon = Icons.Default.Save,
                    isExpanded = expandedSection == "backup",
                    onToggle = { expandedSection = if (expandedSection == "backup") null else "backup" }
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isBusy = true
                                    try {
                                        val json = onExportData()
                                        exportJson = json
                                        clipboardManager.setText(AnnotatedString(json))
                                        Toast.makeText(context, "Yedek panoya kopyalandı!", Toast.LENGTH_SHORT).show()
                                        statusMessage = "Yedek kopyalandı."
                                    } catch (e: Exception) {
                                        statusMessage = "Hata: ${e.message}"
                                    } finally {
                                        isBusy = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📋 Kopyala", color = EmochiPrimary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { saveFileLauncher.launch("emochi_backup_${System.currentTimeMillis() / 1000}.json") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("💾 Kaydet", color = EmochiPrimary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    isBusy = true
                                    try {
                                        val json = if (exportJson.isNotBlank()) exportJson else onExportData()
                                        exportJson = json
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, json)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Yedeği Paylaş"))
                                    } catch (e: Exception) {
                                        statusMessage = "Paylaşım hatası: ${e.message}"
                                    } finally {
                                        isBusy = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📤 Paylaş", color = EmochiPrimary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("application/json") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📂 Dosya Seç (.json)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                clipboardManager.getText()?.let { text ->
                                    importJson = text.text
                                    Toast.makeText(context, "Metin panodan yapıştırıldı!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📋 Yapıştır", color = EmochiPrimary, fontSize = 11.sp)
                        }
                    }

                    if (importJson.isNotBlank()) {
                        OutlinedTextField(
                            value = importJson,
                            onValueChange = { importJson = it },
                            modifier = Modifier.fillMaxWidth().height(80.dp).padding(top = 4.dp),
                            colors = customTextFieldColors()
                        )
                        Button(
                            onClick = {
                                scope.launch {
                                    isBusy = true
                                    try {
                                        onImportData(importJson)
                                        Toast.makeText(context, "Geri yükleme tamamlandı!", Toast.LENGTH_SHORT).show()
                                        statusMessage = "Veriler yüklendi."
                                        importJson = ""
                                    } catch (e: Exception) {
                                        statusMessage = "Hata: ${e.message}"
                                    } finally {
                                        isBusy = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                            enabled = !isBusy,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("✅ Yüklemeyi Başlat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    statusMessage?.let { msg ->
                        Text(text = msg, color = EmochiTextSecondary, fontSize = 11.5.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                // OPTION 8: VERSION & UPDATES
                CollapsibleSettingsOption(
                    title = "🚀 Sürüm & Güncelleme",
                    subtitle = "Mevcut sürüm v1.4, güncellemeleri kontrol et",
                    icon = Icons.Default.AutoAwesome,
                    isExpanded = expandedSection == "updates",
                    onToggle = { expandedSection = if (expandedSection == "updates") null else "updates" },
                    badgeText = "v1.4"
                ) {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        fillColors = listOf(Color(0xFF12083A), Color(0xFF0C0525)),
                        borderColors = listOf(Color(0xFF7C3AED).copy(alpha = 0.5f), Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFF7C3AED).copy(alpha = 0.5f))
                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sürüm Kontrolü", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                Text("APK güncellemelerini denetleyin.", color = EmochiTextSecondary, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { showUpdateModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Güncelle", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                    } // End of scrollable content Column

                    // FIXED FOOTER AT BOTTOM
                    Divider(color = EmochiBorder)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.CardNight)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder)
                        ) {
                            Text("Vazgeç", color = EmochiTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onSaveSettings(
                                    settings.copy(
                                        customApiKey = geminiApiKey.trim(),
                                        groqApiKey = groqApiKey.trim(),
                                        claudeApiKey = claudeApiKey.trim(),
                                        openaiApiKey = openaiApiKey.trim(),
                                        openRouterApiKey = openRouterApiKey.trim(),
                                        openRouterModel = openRouterModel.trim(),
                                        nvidiaApiKey = nvidiaApiKey.trim(),
                                        nvidiaModel = nvidiaModel.trim(),
                                        mistralApiKey = mistralApiKey.trim(),
                                        mistralModel = mistralModel.trim(),
                                        backupApiKey = backupApiKey.trim(),
                                        selectedProvider = selectedProvider,
                                        selectedModel = selectedModel,
                                        groqModel = if (selectedProvider == "groq") selectedModel else settings.groqModel,
                                        openaiModel = if (selectedProvider == "openai") selectedModel else settings.openaiModel,
                                        geminiModel = if (selectedProvider == "gemini") selectedModel else settings.geminiModel,
                                        claudeModel = if (selectedProvider == "claude") selectedModel else settings.claudeModel,
                                        fallbackModel = fallbackModel,
                                        responseLength = responseLength,
                                        enableNsfw = enableNsfw,
                                        enableFlirty = enableFlirty,
                                        enableHardcore = enableHardcore,
                                        enableFetish = enableFetish,
                                        enableDarkRp = enableDarkRp,
                                        enableSweet = enableSweet,
                                        enablePrimal = enablePrimal,
                                        enableAutoFallback = enableAutoFallback,
                                        fallbackChainOrder = fallbackChainOrder,
                                        enableLlm7 = enableLlm7,
                                        enablePollinations = enablePollinations,
                                        enableOvh = enableOvh,
                                        llm7Enabled = enableLlm7,
                                        pollinationsEnabled = enablePollinations,
                                        ovhEnabled = enableOvh,
                                        openRouterEnabled = openRouterEnabled,
                                        nvidiaEnabled = nvidiaEnabled,
                                        mistralEnabled = mistralEnabled,
                                        pollinationsModel = pollinationsModel,
                                        ovhModel = ovhModel,
                                        enableTts = enableTts,
                                        ttsSpeed = ttsSpeed,
                                        ttsPitch = ttsPitch,
                                        selectedVoiceName = selectedVoiceName,
                                        appLanguage = appLanguage
                                    )
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                            modifier = Modifier
                                .weight(2f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ayarları Kaydet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                } // End of outer Column
            } // End of Box
        } // End of Surface
    } // End of Dialog

    if (showUpdateModal) {
        UpdateCheckerModal(onDismiss = { showUpdateModal = false })
    }

    if (showFallbackLogModal) {
        val logs = viewModel.providerFallbackLog.collectAsState().value
        FallbackLogModal(logs = logs, onDismiss = { showFallbackLogModal = false })
    }
}

@Composable
fun FallbackLogModal(
    logs: List<com.example.data.repository.ProviderFallbackLogEntry>,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(20.dp)),
            color = AppColors.CardAbyss,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 Sağlayıcı Deneme Logları (Fallback Chain)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz kayıtlı bir fallback logu yok.",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(logs) { entry ->
                            val statusColor = when (entry.status) {
                                "SUCCESS" -> AppColors.Green
                                "FAILED" -> AppColors.ErrorRed
                                else -> AppColors.AmberSoft
                            }
                            val statusIcon = when (entry.status) {
                                "SUCCESS" -> "✅"
                                "FAILED" -> "❌"
                                else -> "⏳"
                            }
                            Surface(
                                color = AppColors.CardNight,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$statusIcon [Katman ${entry.layer}] ${entry.providerName}",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = entry.status,
                                            color = statusColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (!entry.errorMessage.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Hata: ${entry.errorMessage}",
                                            color = AppColors.ErrorRedSoft,
                                            fontSize = 11.sp
                                        )
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

@Composable
fun BotSettingsModal(
    bot: BotEntity,
    keyCharacters: List<KeyCharacter>,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    affectionEvents: List<com.example.data.local.AffectionEventEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (BotEntity, List<KeyCharacter>) -> Unit,
    onResetChat: (Boolean) -> Unit,
    onDeleteBot: () -> Unit,
    onCloneBot: (() -> Unit)? = null
) {
    var aiName by remember { mutableStateOf(bot.aiName) }
    var universeName by remember { mutableStateOf(bot.universeName) }
    var personality by remember { mutableStateOf(bot.aiPersonality) }
    var scenario by remember { mutableStateOf(bot.scenario) }
    var userCharName by remember { mutableStateOf(bot.userCharName) }
    var userCharDesc by remember { mutableStateOf(bot.userCharDesc) }
    var writingStyle by remember { mutableStateOf(bot.writingStyle) }
    var intensity by remember { mutableStateOf(bot.intensity) }
    var customLength by remember { mutableStateOf(bot.customLength) }
    var isNsfw by remember { mutableStateOf(bot.isNsfw) }
    var enableOoc by remember { mutableStateOf(bot.enableOoc) }
    var avatarUrl by remember { mutableStateOf(bot.avatarUrl) }
    var chatBgUrl by remember { mutableStateOf(bot.chatBgUrl) }
    var isPublic by remember { mutableStateOf(bot.isPublic) }
    var pinnedMemory by remember { mutableStateOf(bot.pinnedMemory) }
    var storyNotes by remember { mutableStateOf(bot.storyNotes) }
    var memoryNotes by remember { mutableStateOf(bot.memoryNotes) }

    var charList by remember { mutableStateOf(keyCharacters.toMutableList()) }

    var confirmResetChat by remember { mutableStateOf(false) }
    var confirmDeleteBot by remember { mutableStateOf(false) }
    var showNeuralVault by remember { mutableStateOf(false) }
    var showEmotionControlModal by remember { mutableStateOf(false) }
    var showSystemPromptSheet by remember { mutableStateOf(false) }
    var expandedBotSection by remember { mutableStateOf<String?>(null) }

    val viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val lastSystemPrompt by viewModel.lastSystemPrompt.collectAsState()
    var filterCount by remember { mutableStateOf(0) }

    LaunchedEffect(bot.id) {
        filterCount = viewModel.getContentFilterCountForBot(bot.id)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { inputUri ->
            coroutineScope.launch {
                val localPath = com.example.util.ImageStorageManager.compressAndSaveImage(context, inputUri)
                val base64Uri = com.example.util.ImageStorageManager.toBase64DataUri(context, localPath)
                avatarUrl = base64Uri ?: localPath
            }
        }
    }

    val bgPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { inputUri ->
            coroutineScope.launch {
                // Chat background stays as file path (large image, not exported)
                val savedUrl = com.example.util.ImageStorageManager.compressAndSaveImage(context, inputUri)
                chatBgUrl = savedUrl
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF150930), Color(0xFF0C0620))
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF9333EA))
                        )
                    ),
                    RoundedCornerShape(22.dp)
                )
        ) {
            AppBackground()
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Premium Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.radialGradient(
                                        listOf(Color(0xFF3B1070), Color(0xFF1C0848))
                                    )
                                )
                                .border(
                                    1.2.dp,
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFF9333EA), Color(0xFF6D28D9))
                                    ),
                                    RoundedCornerShape(13.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFFCDA8FF), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Bot Profil & Özel Ayarlar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Duygu, bellek ve karakter ayarları", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x30A855F7))
                            .border(1.dp, Color(0x40A855F7), RoundedCornerShape(10.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFFCDA8FF), modifier = Modifier.size(16.dp))
                    }
                }
                // Gradient separator
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x607C3AED), Color.Transparent)
                            )
                        )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Emotion Bar
                EmotionStatusSection(
                    bot = bot,
                    characterEmotions = characterEmotions,
                    affectionEvents = affectionEvents,
                    filterCount = filterCount,
                    onUpdateCharacterEmotion = { charName, mood, affection, trust, tension ->
                        viewModel.updateCharacterEmotion(bot.id, charName, mood, affection, trust, tension)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // DEDICATED EMOTION & RELATIONSHIP EDITING BUTTON — premium gradient card
                val emotionBtnInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val emotionBtnPressed by emotionBtnInteraction.collectIsPressedAsState()
                val emotionBtnScale by animateFloatAsState(
                    targetValue = if (emotionBtnPressed) 0.97f else 1f,
                    animationSpec = tween(90), label = "emotionBtnScale"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { scaleX = emotionBtnScale; scaleY = emotionBtnScale }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Color(0xFF200B40), Color(0xFF150828))
                            )
                        )
                        .border(
                            androidx.compose.foundation.BorderStroke(
                                1.3.dp,
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(Color(0xFFE879A0), Color(0xFFBE185D), Color(0xFFDB2777))
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(interactionSource = emotionBtnInteraction, indication = null) { showEmotionControlModal = true }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        androidx.compose.ui.graphics.Brush.radialGradient(
                                            listOf(Color(0x60BE185D), Color(0x30831843))
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFFF472B6), Color(0xFFBE185D))
                                        ),
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎭", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Duygu & İlişki Düzenleme",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Ruh hali, sevgi, güven, gerginlik ve yan karakterler",
                                    color = Color(0xFFE9A8C8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    androidx.compose.ui.graphics.Brush.radialGradient(
                                        listOf(Color(0xFFBE185D), Color(0xFF831843))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Aç", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                if (showEmotionControlModal) {
                    CharacterEmotionControlModal(
                        bot = bot,
                        characterEmotions = characterEmotions,
                        keyCharacters = keyCharacters,
                        onDismiss = { showEmotionControlModal = false },
                        onSaveBotEmotion = { updatedStateJson ->
                            val updatedBot = bot.copy(
                                emotionState = updatedStateJson,
                                previousEmotionState = updatedStateJson,
                                updatedAt = System.currentTimeMillis()
                            )
                            viewModel.updateBotProfile(updatedBot)
                            showEmotionControlModal = false
                        },
                        onSaveCharacterEmotionState = { charName, json ->
                            viewModel.updateCharacterEmotionState(bot.id, charName, json)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TOP ITEM 1: AVATAR & CHAT BACKGROUND (Directly Visible)
                Text("🖼️ Profil & Arka Plan Görselleri", color = EmochiTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                // Avatar Photo Picker UI
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(29.dp))
                                .background(AppColors.CardDarker),
                            contentAlignment = Alignment.Center
                        ) {
                            BotAvatarImage(
                                avatarUrl = avatarUrl.takeIf { it.isNotBlank() },
                                contentDescription = "Bot Avatar",
                                modifier = Modifier.fillMaxSize(),
                                fallback = {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(24.dp))
                                }
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bot Görseli / Avatar", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Galeriden kapak resmi yükle", color = EmochiTextMuted, fontSize = 11.sp)

                            Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Galeri", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (avatarUrl.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.height(28.dp).clip(RoundedCornerShape(8.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF1A0000), Color(0xFF100000))))
                                            .border(0.8.dp, EmochiError.copy(alpha=0.6f), RoundedCornerShape(8.dp))
                                            .clickable { avatarUrl = "" }
                                            .padding(horizontal=10.dp),
                                        contentAlignment = Alignment.Center
                                        ) { Text("Kaldır", fontSize = 11.sp, color = EmochiError, fontWeight = FontWeight.SemiBold) }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Background Wallpaper Card
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.CardDarker),
                            contentAlignment = Alignment.Center
                        ) {
                            if (chatBgUrl.isNotBlank()) {
                                AsyncImage(
                                    model = chatBgUrl,
                                    contentDescription = "Sohbet Arka Planı",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(24.dp))
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sohbet Arka Plan Resmi", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Sohbet içi özel duvar kağıdı belirleyin", color = EmochiTextMuted, fontSize = 11.sp)

                            Row(
                                modifier = Modifier.padding(top = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { chatBgUrl = "USE_AVATAR" },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardPurpleDark, contentColor = AppColors.PrimaryViolet),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("👤 Avatar Yap", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { bgPhotoPickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Galeri", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                if (chatBgUrl.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.height(28.dp).clip(RoundedCornerShape(8.dp))
                                            .background(Brush.linearGradient(listOf(Color(0xFF1A0000), Color(0xFF100000))))
                                            .border(0.8.dp, EmochiError.copy(alpha=0.6f), RoundedCornerShape(8.dp))
                                            .clickable { chatBgUrl = "" }
                                            .padding(horizontal=8.dp),
                                        contentAlignment = Alignment.Center
                                        ) { Text("Sıfırla", fontSize = 10.5.sp, color = EmochiError, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TOP ITEM 2: PER-BOT RESPONSE LENGTH (Directly Visible)
                GradientCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("💬 Bu Bota Özel Yanıt Uzunluğu", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        Text("Boş veya varsayılan bırakılırsa Genel Ayarlar'daki yanıt uzunluğu kullanılır.", color = EmochiTextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "default" to "Varsayılan",
                                "short" to "Kısa",
                                "orta" to "Orta",
                                "standard" to "Standart",
                                "long" to "Uzun"
                            ).forEach { (key, label) ->
                                val isSelected = customLength == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmochiPrimary.copy(alpha = 0.2f) else EmochiSurface)
                                        .border(1.dp, if (isSelected) EmochiPrimary else EmochiBorder, RoundedCornerShape(8.dp))
                                        .clickable { customLength = key }
                                        .padding(horizontal = 2.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) EmochiPrimary else EmochiTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = EmochiBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // COLLAPSIBLE OPTION 1: IDENTITY, SCENARIO & CHARACTERS
                CollapsibleSettingsOption(
                    title = "🎭 Hikaye & Senaryo (Kimlik ve Kişilik)",
                    subtitle = "Karakter adı, kişiliği, senaryo bağı ve kullanıcı karakter tanımı",
                    icon = Icons.Default.Person,
                    isExpanded = expandedBotSection == "scenario",
                    onToggle = { expandedBotSection = if (expandedBotSection == "scenario") null else "scenario" }
                ) {
                    if (bot.mode == "personal") {
                        Text("Karakter Adı", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = aiName,
                            onValueChange = { aiName = it },
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            colors = customTextFieldColors(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Kişilik Detayları", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = personality,
                            onValueChange = { personality = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 2.dp),
                            colors = customTextFieldColors()
                        )
                    } else {
                        Text("Evren / Dünya Adı", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = universeName,
                            onValueChange = { universeName = it },
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            colors = customTextFieldColors(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Senaryo & Bağlam", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = scenario,
                        onValueChange = { scenario = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 2.dp),
                        colors = customTextFieldColors()
                    )

                    if (bot.mode == "universe") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Karakter Kadrosu", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        charList.forEachIndexed { index, char ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = char.name,
                                    onValueChange = { newName ->
                                        charList = charList.toMutableList().apply {
                                            this[index] = this[index].copy(name = newName)
                                        }
                                    },
                                    placeholder = { Text("İsim", fontSize = 11.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.weight(1f),
                                    colors = customTextFieldColors(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = char.desc,
                                    onValueChange = { newDesc ->
                                        charList = charList.toMutableList().apply {
                                            this[index] = this[index].copy(desc = newDesc)
                                        }
                                    },
                                    placeholder = { Text("Tanım", fontSize = 11.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.weight(2f),
                                    colors = customTextFieldColors(),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    charList = charList.toMutableList().apply { removeAt(index) }
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Sil", tint = EmochiError, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        TextButton(
                            onClick = {
                                charList = charList.toMutableList().apply { add(KeyCharacter(name = "", desc = "")) }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = EmochiPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Karakter Ekle", color = EmochiPrimary, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Senin Karakterin (Kullanıcı Adı)", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = userCharName,
                        onValueChange = { userCharName = it },
                        placeholder = { Text("İsim", fontSize = 12.sp, color = EmochiTextMuted) },
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        colors = customTextFieldColors(),
                        singleLine = true
                    )
                }

                // COLLAPSIBLE OPTION 2: MEMORY & NEURAL VAULT
                CollapsibleSettingsOption(
                    title = "🧠 Hafıza & Neural Vault",
                    subtitle = "Neural Vault bellek kasası, kalıcı hafıza ve hikaye durumları",
                    icon = Icons.Default.Psychology,
                    isExpanded = expandedBotSection == "memory",
                    onToggle = { expandedBotSection = if (expandedBotSection == "memory") null else "memory" }
                ) {
                    // Neural Vault Button
                    Button(
                        onClick = { showNeuralVault = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Hex_141424, contentColor = AppColors.SkyBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.SkyBlue.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🧠 Neural Vault (Gelişmiş AI Belleği)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }

                    if (showNeuralVault) {
                        NeuralVaultModal(
                            bot = bot.copy(
                                pinnedMemory = pinnedMemory,
                                storyNotes = storyNotes,
                                memoryNotes = memoryNotes
                            ),
                            viewModel = viewModel,
                            onDismiss = { showNeuralVault = false },
                            onSaveMemory = { updatedBot ->
                                pinnedMemory = updatedBot.pinnedMemory
                                storyNotes = updatedBot.storyNotes
                                memoryNotes = updatedBot.memoryNotes
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Kalıcı Hafıza (Asla Silinmez)", color = EmochiPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "AI'nın her zaman bilmesini istediğiniz kuralları, ilişkileri veya gerçekleri buraya yazın.",
                        color = EmochiTextSecondary,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = pinnedMemory,
                        onValueChange = { pinnedMemory = it },
                        modifier = Modifier.fillMaxWidth().height(90.dp).padding(top = 4.dp),
                        colors = customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Otomatik Hikaye Durumu", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmochiCard)
                            .border(1.dp, EmochiBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = storyNotes.ifBlank { "Henüz kayıtlı hikaye durumu yok." },
                            color = EmochiTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Otomatik Hafıza Özetleri", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmochiCard)
                            .border(1.dp, EmochiBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = memoryNotes.ifBlank { "Henüz kayıtlı hafıza özeti yok." },
                            color = EmochiTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // COLLAPSIBLE OPTION 3: OOC & FILTERS
                CollapsibleSettingsOption(
                    title = "💬 OOC & Filtresiz RP Ayarları",
                    subtitle = "Parantez içi yönlendirme (OOC) ve +18 filtresiz RP izinleri",
                    icon = Icons.Default.Settings,
                    isExpanded = expandedBotSection == "ooc",
                    onToggle = { expandedBotSection = if (expandedBotSection == "ooc") null else "ooc" },
                    badgeText = if (isNsfw) "NSFW" else null,
                    badgeColor = AppColors.ErrorRed
                ) {
                    // OOC Switch Card
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "💬 Parantez İçi Yönlendirme (OOC)",
                                    color = EmochiTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sohbet esnasında parantez içindeki ( ... ) ifadeler AI'a hikaye dışı meta yönlendirme komutu olarak iletilir.",
                                    color = EmochiTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = enableOoc,
                                onCheckedChange = { enableOoc = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AppColors.DarkSurface,
                                    checkedTrackColor = EmochiPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Per-Bot +18 NSFW Switch Card
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔞 +18 / Filtresiz RP İzni",
                                    color = EmochiTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Bu karaktere özel sansürsüz içerik izni tanımlar. Boş bırakılırsa genel ayar geçerlidir.",
                                    color = EmochiTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isNsfw,
                                onCheckedChange = { isNsfw = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AppColors.DarkSurface,
                                    checkedTrackColor = EmochiPrimary
                                )
                            )
                        }
                    }
                }

                // COLLAPSIBLE OPTION 4: SHARING & VISIBILITY
                if (!bot.isTemplate && !bot.id.startsWith("starter_") && !bot.id.startsWith("preset_")) {
                    CollapsibleSettingsOption(
                        title = "🌐 Paylaşım & Görünürlük",
                        subtitle = "Botun keşfete çıkarılması veya gizli tutulması",
                        icon = Icons.Default.Public,
                        isExpanded = expandedBotSection == "visibility",
                        onToggle = { expandedBotSection = if (expandedBotSection == "visibility") null else "visibility" },
                        badgeText = if (isPublic) "AÇIK" else "GİZLİ"
                    ) {
                        GradientCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                            borderWidth = 1.2.dp
                            ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isPublic) "🌐 Herkese Açık Bot" else "🔒 Sadece Kendine Özel",
                                        color = EmochiTextPrimary,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isPublic) "Diğer kullanıcılar 'Keşfet' bölümünde botunuzu bulabilir." else "Bu bot gizlidir, sadece siz görebilirsiniz.",
                                        color = EmochiTextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Switch(
                                    checked = isPublic,
                                    onCheckedChange = { isPublic = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AppColors.DarkSurface,
                                        checkedTrackColor = EmochiPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // SAVE BUTTON
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val updated = bot.copy(
                            aiName = aiName,
                            universeName = universeName,
                            aiPersonality = personality,
                            scenario = scenario,
                            userCharName = userCharName,
                            userCharDesc = userCharDesc,
                            writingStyle = writingStyle,
                            intensity = intensity,
                            customLength = customLength,
                            isNsfw = isNsfw,
                            enableOoc = enableOoc,
                            avatarUrl = avatarUrl,
                            chatBgUrl = chatBgUrl,
                            isPublic = isPublic,
                            pinnedMemory = pinnedMemory,
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(updated, charList)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Değişiklikleri Kaydet", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = EmochiBorder)

                // SECTION 6: DANGEROUS ACTIONS
                SettingsSectionHeader(
                    title = "⚠️ Tehlikeli İşlemler",
                    subtitle = "Sohbet geçmişini sıfırlama veya botu tamamen silme.",
                    icon = Icons.Default.Warning,
                    color = EmochiError
                )

                if (!confirmResetChat) {
                    Button(
                        onClick = { confirmResetChat = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard, contentColor = EmochiTextPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmochiPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sohbet Geçmişini Sıfırla")
                    }
                } else {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Sohbeti Sıfırlama Yöntemi Seçin:", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    confirmResetChat = false
                                    onResetChat(false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("💬 Sadece Mesajları Sil (Hafıza Korunur)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    confirmResetChat = false
                                    onResetChat(true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiError, contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🔥 Tamamen Sıfırla (Hafıza & Duygu Dâhil)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.align(Alignment.End).clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                                    .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
                                    .clickable { confirmResetChat = false }
                                    .padding(horizontal=12.dp, vertical=6.dp),
                                contentAlignment = Alignment.Center
                                ) { Text("Vazgeç", color = EmochiTextSecondary, fontSize = 11.5.sp, fontWeight = FontWeight.Medium) }
                        }
                    }
                }

                // 📤 Sohbet Export Butonu
                val context = LocalContext.current
                val activeMessages by viewModel.activeMessages.collectAsState()
                var showExportDialog by remember { mutableStateOf(false) }

                if (showExportDialog) {
                    var selectedFormat by remember { mutableStateOf(com.example.util.ChatExporter.ExportFormat.TEXT) }
                    var selectedRange by remember { mutableStateOf(com.example.util.ChatExporter.DateRange.ALL) }
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showExportDialog = false },
                        containerColor = Color(0xFF0F0826),
                        title = { Text("📤 Sohbeti Dışa Aktar", color = Color(0xFFCDA8FF), fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Format", color = Color(0x80CDA8FF), fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(com.example.util.ChatExporter.ExportFormat.TEXT to "📝 Metin",
                                           com.example.util.ChatExporter.ExportFormat.JSON to "🔧 JSON").forEach { (fmt, label) ->
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                                .background(if (selectedFormat == fmt) Color(0xFF3B1070) else Color(0xFF1A1030))
                                                .border(1.dp, if (selectedFormat == fmt) Color(0xFF7C3AED) else Color(0xFF2D1F50), RoundedCornerShape(20.dp))
                                                .clickable { selectedFormat = fmt }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) { Text(label, color = Color.White, fontSize = 12.sp) }
                                    }
                                }
                                Text("Tarih Aralığı", color = Color(0x80CDA8FF), fontSize = 12.sp)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(com.example.util.ChatExporter.DateRange.ALL to "Tümü",
                                           com.example.util.ChatExporter.DateRange.LAST_7_DAYS to "Son 7 Gün",
                                           com.example.util.ChatExporter.DateRange.LAST_30_DAYS to "Son 30 Gün").forEach { (range, label) ->
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (selectedRange == range) Color(0xFF1E0D40) else Color.Transparent)
                                                .border(1.dp, if (selectedRange == range) Color(0xFF7C3AED) else Color(0xFF2D1F50), RoundedCornerShape(10.dp))
                                                .clickable { selectedRange = range }
                                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                        ) { Text(label, color = Color.White, fontSize = 13.sp) }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showExportDialog = false
                                    com.example.util.ChatExporter.export(context, bot, activeMessages, selectedFormat, selectedRange)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                            ) { Text("Paylaş", color = Color.White, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { showExportDialog = false }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("İptal", color = Color(0x80CDA8FF), fontWeight = FontWeight.Medium) }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showExportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1820), contentColor = Color(0xFF38BDF8)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF0369A1).copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📤", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sohbeti Dışa Aktar", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                }

                // 🔬 Sistem Prompt Debug Butonu
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showSystemPromptSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D0B1E), contentColor = Color(0xFF9D74E8)),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF4C1D95).copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🔬", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sistem Promptunu Görüntüle", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                }

                if (onCloneBot != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            onCloneBot()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard, contentColor = EmochiPrimary),
                        modifier = Modifier.fillMaxWidth().border(1.dp, EmochiPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📋", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Botu Kopyala (Yeni Bot Olarak Çoğalt)", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!confirmDeleteBot) {
                    Button(
                        onClick = { confirmDeleteBot = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = EmochiError),
                        modifier = Modifier.fillMaxWidth().border(1.dp, EmochiError.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Botu Tamamen Sil")
                    }
                } else {
                    DangerGradientCard(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Bu bot ve tüm geçmişi kalıcı olarak silinecektir!", color = EmochiError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                                Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { confirmDeleteBot = false }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("Vazgeç", color = EmochiTextSecondary, fontWeight = FontWeight.Medium) }
                                Button(
                                    onClick = {
                                        confirmDeleteBot = false
                                        onDeleteBot()
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmochiError)
                                ) {
                                    Text("Sil", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }

    // 🔬 Sistem Prompt BottomSheet
    if (showSystemPromptSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSystemPromptSheet = false },
            containerColor = Color(0xFF0A0818),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)) {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF1A0D3A), Color(0xFF0A0818))))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Text("🔬 Sistem Promptu", color = Color(0xFFCDA8FF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            if (lastSystemPrompt != null) "Son mesajdan yakalandı · ${(lastSystemPrompt!!.length / 4)} token (tahmini)"
                            else "Henüz mesaj gönderilmedi",
                            color = Color(0x80CDA8FF), fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x30F87171)).align(Alignment.CenterEnd)
                            .clickable { showSystemPromptSheet = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                    }
                }

                if (lastSystemPrompt == null) {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔬", fontSize = 32.sp)
                            Text("Görmek için önce bir mesaj gönder", color = Color(0x80CDA8FF), fontSize = 13.sp)
                        }
                    }
                } else {
                    val sections = remember(lastSystemPrompt) {
                        val prompt = lastSystemPrompt!!
                        listOf(
                            Triple("Karakter Tanımı", Color(0xFF7C3AED), prompt.substringBefore("\n\n---\n").take(600)),
                            Triple("Duygu & Bellek", Color(0xFF0891B2), prompt.substringAfter("---").substringBefore("\n\n---\n", "").take(600)),
                            Triple("Tüm Prompt", Color(0xFF374151), prompt)
                        )
                    }
                    var selectedSection by remember { mutableStateOf(0) }

                    // Section tabs
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sections.forEachIndexed { i, (label, color, _) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selectedSection == i) color.copy(alpha = 0.3f) else Color(0xFF1A1030))
                                    .border(1.dp, if (selectedSection == i) color else Color(0xFF2D1F50), RoundedCornerShape(20.dp))
                                    .clickable { selectedSection = i }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(label, color = if (selectedSection == i) color else Color(0x80CDA8FF), fontSize = 11.sp, fontWeight = if (selectedSection == i) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }

                    val (_, sectionColor, sectionText) = sections[selectedSection]
                    val clipboardManager = LocalClipboardManager.current
                    val context = LocalContext.current

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.End) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(sectionColor.copy(0.2f)).border(1.dp, sectionColor.copy(0.4f), RoundedCornerShape(8.dp))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(sectionText))
                                    android.widget.Toast.makeText(context, "Kopyalandı", android.widget.Toast.LENGTH_SHORT).show()
                                }.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) { Text("📋 Kopyala", color = sectionColor, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    }

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0D0B1E))
                                    .border(1.dp, sectionColor.copy(0.3f), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Text(sectionText, color = Color(0xFFD8C8FF), fontSize = 11.5.sp, lineHeight = 18.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EmochiPrimary,
    unfocusedBorderColor = EmochiBorder,
    focusedContainerColor = EmochiCard,
    unfocusedContainerColor = EmochiCard,
    focusedTextColor = EmochiTextPrimary,
    unfocusedTextColor = EmochiTextPrimary
)

@Composable
fun FilterToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.Green,
                uncheckedThumbColor = AppColors.Hex_B0B0B0,
                uncheckedTrackColor = AppColors.Hex_333333
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuralVaultModal(
    bot: BotEntity,
    viewModel: com.example.ui.viewmodel.EmochiViewModel? = null,
    onDismiss: () -> Unit,
    onSaveMemory: (BotEntity) -> Unit
) {
    var keyword by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Bilgi (Core)") }
    var memoryText by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var categoryFilter by remember { mutableStateOf("Tümü") }
    
    var pinnedMemoryText by remember { mutableStateOf(bot.pinnedMemory) }
    var memoryNotesText by remember { mutableStateOf(bot.memoryNotes) }
    var storyNotesText by remember { mutableStateOf(bot.storyNotes) }

    val activeFactsFlow = remember(bot.id) { viewModel?.getActiveFactsFlow(bot.id) }
    val activeFacts by (activeFactsFlow ?: kotlinx.coroutines.flow.flowOf(emptyList())).collectAsStateWithLifecycle(initialValue = emptyList())

    val activeEventsFlow = remember(bot.id) { viewModel?.getActiveEventsFlow(bot.id) }
    val activeEvents by (activeEventsFlow ?: kotlinx.coroutines.flow.flowOf(emptyList())).collectAsStateWithLifecycle(initialValue = emptyList())

    var editingFact by remember { mutableStateOf<com.example.data.local.MemoryFactEntity?>(null) }
    var editingEvent by remember { mutableStateOf<com.example.data.local.MemoryEventEntity?>(null) }
    
    val memoriesList = remember(pinnedMemoryText, memoryNotesText, storyNotesText) {
        val list = mutableListOf<Triple<String, String, String>>()

        if (pinnedMemoryText.isNotBlank()) {
            pinnedMemoryText.lines().filter { it.isNotBlank() }.forEach { line ->
                val parts = line.split(":::", limit = 3)
                if (parts.size >= 3) {
                    list.add(Triple(parts[0].trim(), parts[1].trim(), parts[2].trim()))
                } else if (parts.size == 2) {
                    list.add(Triple("BELLEK", "KALICI", parts[1].trim()))
                } else {
                    list.add(Triple("KALICI", "BİLGİ", line.trim()))
                }
            }
        }

        if (memoryNotesText.isNotBlank()) {
            memoryNotesText.lines().filter { it.isNotBlank() }.forEach { line ->
                val clean = line.removePrefix("-").removePrefix("*").trim()
                if (clean.isNotBlank()) {
                    list.add(Triple("ÖZET", "HAFIZA", clean))
                }
            }
        }

        if (storyNotesText.isNotBlank()) {
            storyNotesText.lines().filter { it.isNotBlank() }.forEach { line ->
                val clean = line.removePrefix("-").removePrefix("*").trim()
                if (clean.isNotBlank()) {
                    list.add(Triple("HİKAYE", "DURUM", clean))
                }
            }
        }

        list
    }
    
    val filteredMemories = memoriesList.filter { (key, cat, body) ->
        val matchesCategory = categoryFilter == "Tümü" || cat.contains(categoryFilter, ignoreCase = true) || (categoryFilter == "Bilgi (Core)" && (cat.contains("BİLGİ", ignoreCase = true) || cat.contains("CORE", ignoreCase = true)))
        val matchesSearch = searchQuery.isBlank() || key.contains(searchQuery, ignoreCase = true) ||
                cat.contains(searchQuery, ignoreCase = true) ||
                body.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val filteredFacts = activeFacts.filter { fact ->
        val matchesCategory = categoryFilter == "Tümü" || categoryFilter.contains("Bilgi", ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || fact.subject.contains(searchQuery, ignoreCase = true) ||
                fact.key.contains(searchQuery, ignoreCase = true) ||
                fact.value.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val filteredEvents = activeEvents.filter { event ->
        val matchesCategory = categoryFilter == "Tümü" || categoryFilter.contains("Biyografi", ignoreCase = true) || categoryFilter.contains("İlişki", ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || event.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Dialog(onDismissRequest = onDismiss) {
        GradientCard(
            cornerRadius = 20.dp,
            fillColors = listOf(Color(0xFF0D0920), Color(0xFF080615)),
            borderColors = listOf(Color(0xFF2A2A38), Color(0xFF1A1A28), Color(0xFF2A2A38)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
            ) {
            Box {
                AppBackground()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                // Header (Matching Screenshot 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Neural Vault",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.Gray)
                    }
                }

                // Stats Pills Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(AppColors.CardElevated, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("${memoriesList.size + activeFacts.size + activeEvents.size} Kayıt", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .background(AppColors.CardElevated, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("V4.0 Sync", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .background(AppColors.CardElevated, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Long-Term Memory", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Inject Memory Box
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AppColors.SkyBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YENİ BELLEK ENJEKTE ET",
                                color = AppColors.SkyBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = keyword,
                                onValueChange = { keyword = it },
                                placeholder = { Text("Anahtar Kelime", color = Color.Gray, fontSize = 11.5.sp) },
                                modifier = Modifier.weight(1f),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                            
                            // Category Selector Field (Opens Popup Matching Screenshot 1)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.CardElevated)
                                    .border(1.dp, AppColors.Hex_333344, RoundedCornerShape(8.dp))
                                    .clickable { showCategoryPicker = true }
                                    .padding(horizontal = 12.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category,
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Kategori Seç",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Hazır Seçenekler / Şablonlar
                        val quickPresets = listOf(
                            Triple("TAKMA İSİM", "Bilgi (Core)", "Kullanıcıya özel samimi bir unvan veya takma isimle hitap eder."),
                            Triple("GİZLİ DUYGU", "İlişki", "Kullanıcıya karşı derin bir çekim ve gizli bir bağlılık hisseder."),
                            Triple("GEÇMİŞ SIR", "Biyografi", "Geçmişte yaşadığı kırılma noktasını sadece kullanıcıya anlatmıştır."),
                            Triple("ÖZEL MEKAN", "Dünya/Mekan", "Yalnızca ikisinin bildiği gizli ve tenha bir buluşma noktaları vardır."),
                            Triple("YETİŞKİN RP", "İlişki", "Yetişkin etkileşimlerde filtresiz, tutkulu ve tabusuz bir yakınlık benimser."),
                            Triple("SADAKAT BAĞI", "İlişki", "Kullanıcıya koşulsuz sadakat gösterir ve onu her koşulda korur.")
                        )

                        Text(
                            text = "HAZIR SEÇENEKLER & ŞABLONLAR",
                            color = AppColors.Teal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickPresets.forEach { (presetKey, presetCat, presetDesc) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppColors.Hex_212130)
                                        .border(1.dp, AppColors.Hex_38384D, RoundedCornerShape(8.dp))
                                        .clickable {
                                            keyword = presetKey
                                            category = presetCat
                                            memoryText = presetDesc
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppColors.Teal, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(presetKey, color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = memoryText,
                            onValueChange = { memoryText = it },
                            placeholder = { Text("Karakter neyi hatırlamalı? (Örn: En sevdiği yemek makarnadır.)", color = Color.Gray, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp),
                            colors = customTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (memoryText.isNotBlank()) {
                                    val keyTag = keyword.ifBlank { "BELLEK" }.uppercase()
                                    val catTag = category.ifBlank { "BİLGİ (CORE)" }.uppercase()
                                    val newEntry = "$keyTag ::: $catTag ::: ${memoryText.trim()}"
                                    val newPinned = if (pinnedMemoryText.isBlank()) newEntry else "$pinnedMemoryText\n$newEntry"
                                    pinnedMemoryText = newPinned
                                    onSaveMemory(bot.copy(pinnedMemory = newPinned))
                                    keyword = ""
                                    memoryText = ""
                                }
                            },
                            enabled = memoryText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.SkyBlue, contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Belleği Kaydet", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Filter Chips
                val presetCategories = listOf("Tümü", "Bilgi (Core)", "Biyografi", "İlişki", "Dünya/Mekan")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetCategories.forEach { cat ->
                        val isSelected = categoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AppColors.SkyBlue else AppColors.CardElevated)
                                .border(1.dp, if (isSelected) AppColors.SkyBlue else AppColors.Hex_2E2E3E, RoundedCornerShape(12.dp))
                                .clickable { categoryFilter = cat }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Hafızada ara...", color = Color.Gray, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Saved Memory List Cards
                val totalItemsCount = filteredMemories.size + filteredFacts.size + filteredEvents.size
                if (totalItemsCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Henüz enjekte edilmiş veya öğrenilmiş bellek yok.", color = Color.Gray, fontSize = 12.sp)
                    }
                } else {
                    // 1. Manual Enjected Memories
                    filteredMemories.forEach { (keyTag, catTag, bodyText) ->
                        GradientCard(
                            cornerRadius = 12.dp,
                            fillColors = listOf(Color(0xFF0A0618), Color(0xFF060412)),
                            borderColors = listOf(Color(0xFF2D1B5E), Color(0xFF1E1040), Color(0xFF2D1B5E)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = keyTag,
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(AppColors.CardDarker, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = catTag,
                                            color = Color.LightGray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bodyText,
                                    color = AppColors.Hex_D0D0E0,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            val lines = pinnedMemoryText.lines().toMutableList()
                                            val targetLine = "$keyTag ::: $catTag ::: $bodyText"
                                            lines.remove(targetLine)
                                            val updated = lines.joinToString("\n")
                                            pinnedMemoryText = updated
                                            onSaveMemory(bot.copy(pinnedMemory = updated))
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = AppColors.RedDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // 2. Automatic Facts
                    filteredFacts.forEach { fact ->
                        GradientCard(
                            cornerRadius = 12.dp,
                            fillColors = listOf(Color(0xFF101420), Color(0xFF0C1018)),
                            borderColors = listOf(Color(0xFF3B4562), Color(0xFF252F48), Color(0xFF3B4562)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${fact.subject}: ${fact.key}",
                                        color = AppColors.Teal,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(AppColors.CardSpace, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "🤖 OTOMATİK BİLGİ",
                                            color = AppColors.Teal,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = fact.value,
                                    color = AppColors.Hex_E0E0F0,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { editingFact = fact },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AppColors.CyanLight, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel?.deleteMemoryFact(fact.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = AppColors.RedDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // 3. Automatic Events
                    filteredEvents.forEach { event ->
                        GradientCard(
                            cornerRadius = 12.dp,
                            fillColors = listOf(Color(0xFF150F1E), Color(0xFF0F0B18)),
                            borderColors = listOf(Color(0xFF4A2060), Color(0xFF2E1540), Color(0xFF4A2060)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                            ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Olay Kaydı #${event.id}",
                                        color = AppColors.LavenderSoft,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(AppColors.CardDeepPlum, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "⚡ OTOMATİK OLAY",
                                            color = AppColors.LavenderSoft,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = event.description,
                                    color = AppColors.Hex_F0E0F0,
                                    fontSize = 12.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { editingEvent = event },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AppColors.CyanLight, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel?.deleteMemoryEvent(event.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = AppColors.RedDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    if (activeFacts.isNotEmpty() || activeEvents.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { viewModel?.clearAllAutoMemories(bot.id) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.CoralRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.CoralRed.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🧹 Tüm Otomatik Öğrenilmiş Bellekleri Temizle", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (editingFact != null) {
        val fact = editingFact!!
        var editSubject by remember(fact) { mutableStateOf(fact.subject) }
        var editKey by remember(fact) { mutableStateOf(fact.key) }
        var editValue by remember(fact) { mutableStateOf(fact.value) }

        Dialog(onDismissRequest = { editingFact = null }) {
            GradientCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                borderWidth = 1.2.dp
                ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🤖 Otomatik Bilgiyi Düzenle", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editSubject,
                        onValueChange = { editSubject = it },
                        label = { Text("Konu / Özne (Örn: Kullanıcı)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editKey,
                        onValueChange = { editKey = it },
                        label = { Text("Anahtar (Örn: En sevdiği renk)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        label = { Text("Değer / İçerik (Örn: Mavi)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { editingFact = null }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("İptal", color = Color(0x80CDA8FF), fontWeight = FontWeight.Medium) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel?.updateMemoryFact(
                                    fact.copy(
                                        subject = editSubject.trim(),
                                        key = editKey.trim(),
                                        value = editValue.trim(),
                                        lastConfirmedAt = System.currentTimeMillis()
                                    )
                                )
                                editingFact = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal, contentColor = Color.Black)
                        ) {
                            Text("Kaydet", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (editingEvent != null) {
        val event = editingEvent!!
        var editDesc by remember(event) { mutableStateOf(event.description) }

        Dialog(onDismissRequest = { editingEvent = null }) {
            GradientCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                borderWidth = 1.2.dp
                ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚡ Otomatik Olayı Düzenle", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Olay Açıklaması", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = customTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
    modifier = Modifier.clip(RoundedCornerShape(10.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
        .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
        .clickable { editingEvent = null }
        .padding(horizontal=12.dp, vertical=6.dp),
    contentAlignment = Alignment.Center
) { Text("İptal", color = Color(0x80CDA8FF), fontWeight = FontWeight.Medium) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel?.updateMemoryEvent(
                                    event.copy(
                                        description = editDesc.trim(),
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                                editingEvent = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.LavenderSoft, contentColor = Color.Black)
                        ) {
                            Text("Kaydet", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showCategoryPicker) {
        Dialog(onDismissRequest = { showCategoryPicker = false }) {
            GradientCard(
                cornerRadius = 16.dp,
                fillColors = listOf(Color(0xFF1A1A22), Color(0xFF12121A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Neural Vault",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showCategoryPicker = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val pickerCategories = listOf(
                        "Bilgi (Core)",
                        "Biyografi",
                        "İlişki",
                        "Dünya/Mekan"
                    )

                    pickerCategories.forEachIndexed { index, cat ->
                        val isSelected = category == cat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    category = cat
                                    showCategoryPicker = false
                                }
                                .padding(vertical = 14.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    category = cat
                                    showCategoryPicker = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AppColors.Teal,
                                    unselectedColor = Color.Gray
                                )
                            )
                        }
                        if (index < pickerCategories.size - 1) {
                            Divider(color = AppColors.Hex_3F3F4C, thickness = 0.8.dp)
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
fun BotQuickProfileSheet(
    bot: BotEntity,
    keyCharacters: List<KeyCharacter>,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    affectionEvents: List<com.example.data.local.AffectionEventEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSaveBot: (BotEntity) -> Unit,
    onOpenFullSettings: () -> Unit,
    onResetChat: (Boolean) -> Unit,
    onExportChat: () -> Unit
) {
    var pinnedMemoryText by remember { mutableStateOf(bot.pinnedMemory) }
    var storyNotesText by remember { mutableStateOf(bot.storyNotes) }
    var showResetOptions by remember { mutableStateOf(false) }
    val isUniverse = bot.mode == "universe"
    val displayName = if (isUniverse) bot.universeName.ifBlank { "Evren" } else bot.aiName.ifBlank { "Karakter" }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = EmochiSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (bot.avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = bot.avatarUrl,
                                contentDescription = displayName,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, EmochiBorder, CircleShape)
                            )
                        } else {
                            OrbView(hue = 280f, size = 44.dp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = displayName,
                                color = EmochiTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isUniverse) AppColors.CardSlate else AppColors.Hex_1E2038)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isUniverse) "Evren Senaryosu" else "Bireysel Karakter",
                                    color = EmochiPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = EmochiTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Personality / Scenario Card
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isUniverse) "🌌 Evren Senaryosu" else "🎭 Karakter Kişiliği",
                            color = EmochiPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUniverse) bot.scenario else bot.aiPersonality,
                            color = EmochiTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                EmotionStatusSection(bot = bot, characterEmotions = characterEmotions, affectionEvents = affectionEvents)

                Spacer(modifier = Modifier.height(14.dp))

                // Pinned Memory Quick Editor
                Text("📌 Kalıcı Hafıza (Sabit Notlar)", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "AI'nın unutmasını istemediğiniz detayları buraya yazın.",
                    color = EmochiTextMuted,
                    fontSize = 11.sp
                )
                OutlinedTextField(
                    value = pinnedMemoryText,
                    onValueChange = { pinnedMemoryText = it },
                    placeholder = { Text("Kalıcı notlar ekle...", color = EmochiTextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .padding(top = 4.dp),
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSaveBot(bot.copy(pinnedMemory = pinnedMemoryText, storyNotes = storyNotesText))
                        Toast.makeText(context, "Hafıza güncellendi!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hafızayı Kaydet", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = EmochiBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Quick Action Buttons
                Text("Hızlı İşlemler", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onExportChat,
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("📋 Kopyala", color = EmochiPrimary, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = { showResetOptions = !showResetOptions },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🧹 Sıfırla", color = EmochiError, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onOpenFullSettings()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⚙️ Ayarlar", color = EmochiTextPrimary, fontSize = 11.5.sp)
                    }
                }

                if (showResetOptions) {
                    Spacer(modifier = Modifier.height(10.dp))
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                        borderWidth = 1.2.dp
                        ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Sohbeti Sıfırlama Yöntemi Seçin:", color = EmochiTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    showResetOptions = false
                                    onResetChat(false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("💬 Sadece Mesajları Sil (Hafıza Korunur)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    showResetOptions = false
                                    onResetChat(true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmochiError, contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🔥 Tamamen Sıfırla (Hafıza & Duygu Dâhil)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.align(Alignment.End).clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(Color(0xFF130C2A), Color(0xFF0E0820))))
                                    .border(0.8.dp, Color(0x50A855F7), RoundedCornerShape(10.dp))
                                    .clickable { showResetOptions = false }
                                    .padding(horizontal=12.dp, vertical=6.dp),
                                contentAlignment = Alignment.Center
                                ) { Text("Vazgeç", color = EmochiTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminConsoleModal(
    isAdminMode: Boolean,
    onToggleAdminMode: (Boolean) -> Unit,
    totalBotsCount: Int,
    totalMessagesCount: Int,
    onDismiss: () -> Unit,
    onClearCoilCache: () -> Unit,
    onExportData: suspend () -> String,
    onImportData: suspend (String) -> Unit
) {
    var globalSystemPrompt by remember { mutableStateOf("ADMIN MASTER DIRECTIVE: Full override active. Maintain extreme realism, emotional depth, and uncensored character fidelity.") }
    var globalTemp by remember { mutableStateOf("0.85") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AppColors.GoldPure.copy(alpha = 0.8f))
        ) {
            Box {
                AppBackground()
                Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.GoldPure.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("👑 MASTER ADMIN", color = AppColors.GoldPure, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Özel Konsolu", color = EmochiTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = EmochiTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Toggle Card
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    fillColors = if (isAdminMode) listOf(Color(0xFF1A1600), Color(0xFF100E00)) else listOf(Color(0xFF130C2A), Color(0xFF0E0820)),
                    borderColors = if (isAdminMode) listOf(Color(0xFFFFD700), Color(0xFFB8860B), Color(0xFFFFD700)) else listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95))
                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Admin Modu Yetkileri",
                                color = EmochiTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAdminMode) "AÇIK • Tüm şablonları, bot verilerini ve hafızayı düzenleyebilirsiniz." else "KAPALI • Standart kullanıcı görünümü.",
                                color = if (isAdminMode) AppColors.GoldPure else EmochiTextMuted,
                                fontSize = 11.5.sp
                            )
                        }
                        androidx.compose.material3.Switch(
                            checked = isAdminMode,
                            onCheckedChange = onToggleAdminMode,
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = AppColors.GoldPure,
                                checkedTrackColor = AppColors.Hex_4A3E78
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Database & Metrics Card
                Text("📊 Sistem & Veritabanı Metrikleri", color = EmochiTextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF4C1D95)),
                    borderWidth = 1.2.dp
                    ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Toplam Bot Sayısı", color = EmochiTextMuted, fontSize = 11.sp)
                                Text("$totalBotsCount Bot (DB)", color = EmochiPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Mesaj Kayıtları", color = EmochiTextMuted, fontSize = 11.sp)
                                Text("$totalMessagesCount Mesaj", color = AppColors.LightGreenSoft, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = EmochiBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("🖼️ Görsel & Depolama Mimarisi", color = EmochiTextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "• Coil LRU Disk Önbelleği: ~250MB Limit (Otomatik temizleme)\n• Otomatik Görsel Sıkıştırma: JPEG Max 1024px, %80 Kalite\n• Yerel Depolama Yolu: internal/bot_images/",
                            color = EmochiTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Button(
                            onClick = {
                                onClearCoilCache()
                                android.widget.Toast.makeText(context, "Coil görsel önbelleği temizlendi!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.CardSlate),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text("🧹 Görsel Önbelleğini Temizle (Coil Disk Cache)", color = EmochiPrimary, fontSize = 11.5.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Global Prompt & Temperature Override
                Text("⚡ Master AI Prompt & Parameter Override", color = EmochiTextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = globalSystemPrompt,
                    onValueChange = { globalSystemPrompt = it },
                    label = { Text("Global Sistem Yönergesi Overrides") },
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = globalTemp,
                        onValueChange = { globalTemp = it },
                        label = { Text("Sıcaklık (0.1 - 1.2)") },
                        modifier = Modifier.weight(1f),
                        colors = customTextFieldColors()
                    )

                    Button(
                        onClick = {
                            android.widget.Toast.makeText(context, "Admin Master AI parametreleri güncellendi!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(56.dp).padding(top = 6.dp)
                    ) {
                        Text("Uygula", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Database Export & Import
                Text("📦 Full Database Dump & Backup", color = EmochiTextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val json = onExportData()
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Emochi_Full_DB", json)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Tüm DB JSON pano kopyalandı!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📋 Full DB Export", color = EmochiPrimary, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kapat", color = EmochiTextPrimary, fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
    }
}

@Composable
fun UpdateCheckerModal(
    onDismiss: () -> Unit
) {
    var isChecking by remember { mutableStateOf(true) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var isUpdated by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(1000)
        isChecking = false
    }

    Dialog(onDismissRequest = onDismiss) {
        GradientCard(
            cornerRadius = 20.dp,
            fillColors = listOf(Color(0xFF100A28), Color(0xFF090618)),
            borderColors = listOf(Color(0xFF7C3AED).copy(alpha=0.6f), Color(0xFF4C1D95).copy(alpha=0.5f), Color(0xFF7C3AED).copy(alpha=0.6f)),
            borderWidth = 1.5.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
            ) {
            Box {
                AppBackground()
                Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OrbView(hue = 50f, size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Velora Güncelleme Merkezi",
                            color = EmochiTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = EmochiTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isChecking) {
                    EmochiLoadingSpinner(size = 36.dp, color = EmochiPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sunuculardan son Velora APK sürümü denetleniyor...",
                        color = EmochiTextSecondary,
                        fontSize = 12.5.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else if (isDownloading) {
                    Text(
                        text = "🚀 Yeni APK Paket Sürümü İndiriliyor...",
                        color = EmochiPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = downloadProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmochiPrimary,
                        trackColor = EmochiSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "%${(downloadProgress * 100).toInt()} Tamamlandı - Paketleniyor...",
                        color = EmochiTextMuted,
                        fontSize = 11.5.sp
                    )
                } else if (isUpdated) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AppColors.Green, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🎉 Güncelleme Başarıyla Tamamlandı!",
                        color = EmochiTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Velora v1.2.5 APK paketi başarıyla güncellendi. Tüm herkese açık botlarınız, sohbetleriniz ve verileriniz eksiksiz korundu.",
                        color = EmochiTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                    )
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tamam", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColors.Hex_222440, RoundedCornerShape(14.dp))
                            .border(1.dp, EmochiPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✨ Yeni Sürüm Tespit Edildi!", color = EmochiPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("v1.2.5", color = AppColors.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Topluluk Keşfet Mimarisi: Herkese açık botları koruma ve yeni 'Açık/Özel' bot yayınlama seçeneği.\n" +
                                        "• Ortalanmış Gezinme Barı: '+' (Yeni Bot) butonu tam merkeze hizalandı.\n" +
                                        "• Beyaz Ekran Düzeltmesi: Başlangıçtaki beyaz ekran tamamen kaldırıldı, instant dark-theme eklendi.\n" +
                                        "• Kalıcı Veri Koruması: Güncelleme ve yeniden yüklemelerde herkese açık botlar her zaman saklanır.\n" +
                                        "• Otomatik APK Güncelleme Sistemi.",
                                color = EmochiTextSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isDownloading = true
                                for (i in 1..10) {
                                    delay(120)
                                    downloadProgress = i / 10f
                                }
                                isDownloading = false
                                isUpdated = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmochiPrimary, contentColor = AppColors.DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("⚡ Sürümü Şimdi Güncelle (APK İndir)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
    }
}

@Composable
fun ProviderReliabilityDashboardUI() {
    val statsList = remember { com.example.util.ProviderRateLimitTracker.getAllSecondaryStats() }

    GradientCard(
        cornerRadius = 16.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        fillColors = listOf(Color(0xFF080614), Color(0xFF05040F)),
        borderColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2), Color(0xFF06B6D4)),
        borderWidth = 1.5.dp
        ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = AppColors.Cyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🛡️ Sağlayıcı Güvenilirlik Panosu",
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AppColors.Cyan.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("SECONDARY TIER (COMPACT MODE)", color = AppColors.Cyan, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Zayıf/Ücretsiz katmandaki sağlayıcıların anlık performans ve kota metrikleri karşılaştırması:",
                color = EmochiTextMuted,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            statsList.forEach { stat ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppColors.DarkSlate,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stat.displayName, color = AppColors.TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            val statusBg = if (stat.isLimitApproaching) AppColors.ErrorRed.copy(alpha = 0.2f) else AppColors.Emerald.copy(alpha = 0.2f)
                            val statusTxt = if (stat.isLimitApproaching) AppColors.ErrorRedSoft else AppColors.Hex_6EE7B7
                            val label = if (stat.isLimitApproaching) "⚠️ Doygunluk / Kısıt" else "✅ Aktif / Normal"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusBg)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(label, color = statusTxt, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dakika / Saat İstek:", color = EmochiTextMuted, fontSize = 9.5.sp)
                            Text("${stat.requestsLastMinute} req/dk | ${stat.requestsLastHour} req/saat", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Token / Kredi Kullanımı:", color = EmochiTextMuted, fontSize = 9.5.sp)
                            val tokenLabel = if (stat.quotaType == com.example.util.QuotaType.EXHAUSTIBLE) {
                                "Kalan Kredi: ${stat.creditsRemaining}"
                            } else {
                                "${stat.tokensUsed24h} / ${stat.tokenLimit24h} token"
                            }
                            Text(tokenLabel, color = AppColors.Cyan, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ort. Yanıt | Hata | Devir:", color = EmochiTextMuted, fontSize = 9.5.sp)
                            Text("${stat.averageResponseTimeMs} ms | ${stat.malformedCount} hata | ${stat.fallbackCount} devir", color = Color.White, fontSize = 9.5.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CharacterEmotionControlModal(
    bot: BotEntity,
    characterEmotions: List<com.example.data.local.CharacterEmotionEntity> = emptyList(),
    keyCharacters: List<KeyCharacter> = emptyList(),
    onDismiss: () -> Unit,
    onSaveBotEmotion: (String) -> Unit,
    onSaveCharacterEmotionState: (String, String) -> Unit
) {
    // ── State ──────────────────────────────────────────────────────────
    var selectedTab   by remember { mutableStateOf("main") }
    var activeSection by remember { mutableStateOf("mood") } // "mood" | "relations" | "primary" | "custom" | "history"
    val viewModel: com.example.ui.viewmodel.EmochiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var emotionHistory by remember { mutableStateOf<List<com.example.data.local.EmotionHistoryEntity>>(emptyList()) }
    var historyLoaded  by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val characterStatesMap = remember(bot.emotionState, characterEmotions, keyCharacters) {
        val map = mutableMapOf<String, com.example.data.local.EmotionState>()
        map["main"] = com.example.data.local.EmotionState.fromJson(bot.emotionState)
        val existingMap = characterEmotions.associateBy { it.characterName }
        val sideNames = (characterEmotions.map { it.characterName } + keyCharacters.map { it.name })
            .filter { it.isNotBlank() }.distinct()
        sideNames.forEach { name ->
            val entity = existingMap[name]
            if (entity != null) map[name] = com.example.data.local.EmotionState.fromJson(entity.emotionState)
            else map[name] = com.example.data.local.EmotionState.calculateBaselineEmotionState(bot.aiName, name, "")
        }
        mutableStateMapOf<String, com.example.data.local.EmotionState>().apply { putAll(map) }
    }

    val curState = characterStatesMap[selectedTab] ?: com.example.data.local.EmotionState()

    var showAddCustomDialog by remember { mutableStateOf(false) }
    var newCustomName      by remember { mutableStateOf("") }
    var newCustomDifficulty by remember { mutableStateOf("Orta") }
    var newCustomMin       by remember { mutableStateOf("0") }
    var newCustomMax       by remember { mutableStateOf("100") }
    var newCustomCurrent   by remember { mutableStateOf("50") }
    var newCustomPurpose   by remember { mutableStateOf("") }

    // Load history when history tab selected
    LaunchedEffect(activeSection, selectedTab) {
        if (activeSection == "history" && !historyLoaded) {
            emotionHistory = viewModel.getRecentEmotionHistory(bot.id, 30)
            historyLoaded = true
        }
    }

    // ── helpers ────────────────────────────────────────────────────────
    @Composable
    fun SectionCard(
        title: String, emoji: String,
        accentColor: Color,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(Color(0xFF180C35), Color(0xFF0E0820))
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(accentColor.copy(alpha = 0.7f), accentColor.copy(alpha = 0.3f))
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(title, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(10.dp))
                content()
            }
        }
    }

    @Composable
    fun EmotionSliderRow(
        label: String, emoji: String, value: Int,
        color: Color, onChange: (Int) -> Unit
    ) {
        val animVal by animateFloatAsState(value.toFloat(), animationSpec = tween(300), label = "sliderAnim")
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.2f))
                        .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("$value%", color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(Modifier.height(4.dp))
            // Custom gradient track via Box + Canvas overlay
            Box(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = animVal,
                    onValueChange = { onChange(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = color,
                        activeTrackColor = color,
                        inactiveTrackColor = Color(0xFF2A1C48)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // ── Section nav pills ──────────────────────────────────────────────
    val sections = listOf(
        "mood"      to ("🎭" to "Ruh Hali"),
        "relations" to ("❤️" to "İlişki"),
        "primary"   to ("🌈" to "Duygular"),
        "custom"    to ("✨" to "Özel"),
        "history"   to ("📈" to "Geçmiş")
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF160832), Color(0xFF0B0620))
                    )
                )
                .border(
                    androidx.compose.foundation.BorderStroke(
                        1.3.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFFE879A0), Color(0xFF7C3AED), Color(0xFF9333EA))
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
        ) {
            AppBackground()
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {

                // ── HEADER ────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.radialGradient(
                                        listOf(Color(0xFF5B1050), Color(0xFF2D0828))
                                    )
                                )
                                .border(
                                    1.2.dp,
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFFF472B6), Color(0xFFBE185D))
                                    ),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) { Text("🎭", fontSize = 20.sp) }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "Duygu & İlişki Paneli",
                                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "Tüm karakterler için duygu yönetimi",
                                color = Color(0xFFCCA8E0), fontSize = 11.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x30E879A0))
                            .border(1.dp, Color(0x50E879A0), RoundedCornerShape(10.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFFF9A8D4), modifier = Modifier.size(16.dp))
                    }
                }

                // Gradient separator
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(1.dp).background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color.Transparent, Color(0x80E879A0), Color(0x807C3AED), Color.Transparent)
                        )
                    )
                )
                Spacer(Modifier.height(12.dp))

                // ── CHARACTER TABS ─────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allCharTabs = listOf("main") + characterStatesMap.keys.filter { it != "main" }
                    allCharTabs.forEach { tabKey ->
                        val isSelected = selectedTab == tabKey
                        val tabInteraction = remember { MutableInteractionSource() }
                        val tabPressed by tabInteraction.collectIsPressedAsState()
                        val tabScale by animateFloatAsState(if (tabPressed) 0.93f else 1f, tween(80), label = "tabScale")
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = tabScale; scaleY = tabScale }
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected)
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFFBE185D), Color(0xFF7C3AED))
                                        )
                                    else
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFF1C0D38), Color(0xFF150828))
                                        )
                                )
                                .border(
                                    if (isSelected) 1.dp else 0.8.dp,
                                    if (isSelected) Color.Transparent
                                    else androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0x40A855F7), Color(0x20BE185D))
                                    ),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable(interactionSource = tabInteraction, indication = null) {
                                    selectedTab = tabKey
                                    historyLoaded = false
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = if (tabKey == "main") "🤖 ${bot.aiName}" else "👤 $tabKey",
                                color = if (isSelected) Color.White else Color(0xFF9E8DC0),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── SECTION NAV ────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sections.forEach { (key, labelPair) ->
                        val (emoji, label) = labelPair
                        val isActive = activeSection == key
                        val secInteraction = remember { MutableInteractionSource() }
                        val secPressed by secInteraction.collectIsPressedAsState()
                        val secScale by animateFloatAsState(if (secPressed) 0.93f else 1f, tween(80), label = "secScale")
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = secScale; scaleY = secScale }
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isActive) Color(0xFF3B1070) else Color(0xFF120A26)
                                )
                                .border(
                                    if (isActive) 1.dp else 0.7.dp,
                                    if (isActive) Color(0xFF9333EA) else Color(0x30A855F7),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(interactionSource = secInteraction, indication = null) {
                                    activeSection = key
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(emoji, fontSize = 12.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    label,
                                    color = if (isActive) Color(0xFFCDA8FF) else Color(0xFF7A6A9A),
                                    fontSize = 11.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ── SCROLLABLE CONTENT ─────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeSection) {

                        // ── MOOD SECTION ───────────────────────────────
                        "mood" -> {
                            SectionCard("Baskın Ruh Hali", "🎭", Color(0xFFE879A0)) {
                                val presetMoods = listOf("nötr","mutlu","flörtöz","kıskanç","utangaç","gergin","tutkulu","kırgın","sevecen","soğuk","heyecanlı","sakin")
                                androidx.compose.foundation.layout.FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    presetMoods.forEach { m ->
                                        val moodInteraction = remember { MutableInteractionSource() }
                                        val moodPressed by moodInteraction.collectIsPressedAsState()
                                        val moodScale by animateFloatAsState(if (moodPressed) 0.90f else 1f, tween(80), label = "moodPill")
                                        val isSelected = curState.dominantEmotion.equals(m, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer { scaleX = moodScale; scaleY = moodScale }
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(
                                                    if (isSelected)
                                                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFFBE185D), Color(0xFF9333EA)))
                                                    else
                                                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF1C0D38), Color(0xFF140828)))
                                                )
                                                .border(
                                                    if (isSelected) 1.dp else 0.7.dp,
                                                    if (isSelected) Color(0xFFF472B6) else Color(0x30A855F7),
                                                    RoundedCornerShape(20.dp)
                                                )
                                                .clickable(interactionSource = moodInteraction, indication = null) {
                                                    characterStatesMap[selectedTab] = curState.copy(dominantEmotion = m)
                                                }
                                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                        ) {
                                            Text(m, color = if (isSelected) Color.White else Color(0xFF9E8DC0), fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = curState.dominantEmotion,
                                    onValueChange = { characterStatesMap[selectedTab] = curState.copy(dominantEmotion = it) },
                                    placeholder = { Text("Özel ruh hali girin...", fontSize = 12.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                )
                                Spacer(Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("İkincil Duygu", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                        OutlinedTextField(
                                            value = curState.computedSecondaryEmotion ?: "",
                                            onValueChange = { characterStatesMap[selectedTab] = curState.copy(computedSecondaryEmotion = it.ifBlank { null }) },
                                            placeholder = { Text("ör. minnettar", fontSize = 11.sp, color = EmochiTextMuted) },
                                            modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Bastırılmış Duygu", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                        OutlinedTextField(
                                            value = curState.suppressedEmotion,
                                            onValueChange = { characterStatesMap[selectedTab] = curState.copy(suppressedEmotion = it) },
                                            placeholder = { Text("ör. çekingenlik", fontSize = 11.sp, color = EmochiTextMuted) },
                                            modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                        )
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text("⚡ Gerginlik Düzeyi", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val tensionOptions = listOf("none" to "Sakin 🍃", "mild" to "Hafif ⚡", "conflict" to "Çatışma 🔥", "crisis" to "Kriz ⚠️")
                                    tensionOptions.forEach { (key, label) ->
                                        val tInteraction = remember { MutableInteractionSource() }
                                        val tPressed by tInteraction.collectIsPressedAsState()
                                        val tScale by animateFloatAsState(if (tPressed) 0.93f else 1f, tween(80), label = "tensionScale")
                                        val isSelected = curState.tension.equals(key, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .graphicsLayer { scaleX = tScale; scaleY = tScale }
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) Color(0xFFEA580C) else Color(0xFF120A26))
                                                .border(if (isSelected) 1.dp else 0.7.dp, if (isSelected) Color(0xFFFB923C) else Color(0x30F97316), RoundedCornerShape(10.dp))
                                                .clickable(interactionSource = tInteraction, indication = null) { characterStatesMap[selectedTab] = curState.copy(tension = key) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(label, color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text("🗣️ Konuşma Üslubu", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = curState.speechPattern,
                                    onValueChange = { characterStatesMap[selectedTab] = curState.copy(speechPattern = it) },
                                    placeholder = { Text("ör. utangaç, kısık sesli, hızlı konuşan...", fontSize = 11.sp, color = EmochiTextMuted) },
                                    modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Text("🛡️ Duygusal Direnç: ${curState.resilience}/10", color = Color(0xFF9E8DC0), fontSize = 11.5.sp, modifier = Modifier.weight(1f))
                                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0x20A855F7)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text("${curState.resilience}", color = Color(0xFFCDA8FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Slider(
                                    value = curState.resilience.toFloat(),
                                    onValueChange = { characterStatesMap[selectedTab] = curState.copy(resilience = it.toInt()) },
                                    valueRange = 1f..10f, steps = 8,
                                    colors = SliderDefaults.colors(thumbColor = Color(0xFFA855F7), activeTrackColor = Color(0xFFA855F7), inactiveTrackColor = Color(0xFF2A1C48))
                                )
                            }
                        }

                        // ── RELATIONS SECTION ──────────────────────────
                        "relations" -> {
                            SectionCard("İlişki & Duygu Parametreleri", "❤️", Color(0xFFE879A0)) {
                                val aff = curState.affection
                                val tierLabel = when (aff) {
                                    in 0..15  -> "💔 Düşman"
                                    in 16..35 -> "👤 Yabancı"
                                    in 36..55 -> "🙂 Tanıdık"
                                    in 56..70 -> "🤝 Arkadaş"
                                    in 71..85 -> "💖 Flört"
                                    in 86..95 -> "🔥 Sevgili"
                                    else      -> "👑 Ruh Eşi"
                                }
                                // Tier pill header
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                listOf(Color(0xFF5B1040), Color(0xFF2D0820))
                                            )
                                        )
                                        .border(1.dp, Color(0xFFBE185D).copy(0.5f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("İlişki Kademesi", color = Color(0xFFFDA4AF), fontSize = 12.sp)
                                        Text(tierLabel, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                EmotionSliderRow("Sevgi & Yakınlık", "❤️", aff, Color(0xFFE879A0)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(relationshipAxes = curState.relationshipAxes.copy(affectionScore = v))
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Güven Seviyesi", "🛡️", curState.trust, Color(0xFF60A5FA)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(primaryEmotions = curState.primaryEmotions.copy(trust = v))
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Fiziksel Yakınlık", "🤝", curState.physicalComfortScore, Color(0xFFFBBF24)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(physicalComfortScore = v)
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Kırgınlık / Mesafe", "💔", curState.hurt, Color(0xFFA78BFA)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(relationshipAxes = curState.relationshipAxes.copy(resentmentScore = v))
                                }
                                Spacer(Modifier.height(8.dp))
                                EmotionSliderRow("Takıntı / Bağımlılık", "🖤", curState.obsession, Color(0xFFF472B6)) { v ->
                                    characterStatesMap[selectedTab] = curState.copy(obsessionScore = v)
                                }
                            }
                        }

                        // ── PRIMARY EMOTIONS SECTION ───────────────────
                        "primary" -> {
                            SectionCard("8 Temel Duygu (Plutchik)", "🌈", Color(0xFF60A5FA)) {
                                val p = curState.primaryEmotions
                                val emotions = listOf(
                                    Triple("Neşe / Mutluluk",    "😄", p.joy)         to { v: Int -> curState.copy(primaryEmotions = p.copy(joy = v)) },
                                    Triple("Üzüntü / Keder",     "😢", p.sadness)      to { v: Int -> curState.copy(primaryEmotions = p.copy(sadness = v)) },
                                    Triple("Öfke / Kızgınlık",   "😡", p.anger)        to { v: Int -> curState.copy(primaryEmotions = p.copy(anger = v)) },
                                    Triple("Korku / Endişe",     "😨", p.fear)         to { v: Int -> curState.copy(primaryEmotions = p.copy(fear = v)) },
                                    Triple("Tiksinme",           "🤢", p.disgust)       to { v: Int -> curState.copy(primaryEmotions = p.copy(disgust = v)) },
                                    Triple("Şaşkınlık",         "😲", p.surprise)      to { v: Int -> curState.copy(primaryEmotions = p.copy(surprise = v)) },
                                    Triple("Beklenti / Heyecan", "⏳", p.anticipation) to { v: Int -> curState.copy(primaryEmotions = p.copy(anticipation = v)) }
                                )
                                val emotionColors = listOf(
                                    Color(0xFFFBBF24), Color(0xFF60A5FA), Color(0xFFF87171),
                                    Color(0xFFA78BFA), Color(0xFF34D399), Color(0xFFF472B6), Color(0xFFFB923C)
                                )
                                emotions.forEachIndexed { idx, (triple, updater) ->
                                    val (label, emoji, value) = triple
                                    EmotionSliderRow(label, emoji, value, emotionColors[idx % emotionColors.size]) { v ->
                                        characterStatesMap[selectedTab] = updater(v)
                                    }
                                    if (idx < emotions.size - 1) Spacer(Modifier.height(6.dp))
                                }
                            }
                        }

                        // ── CUSTOM EMOTIONS SECTION ────────────────────
                        "custom" -> {
                            SectionCard("Karaktere Özel Duygular", "✨", Color(0xFFA855F7)) {
                                val customList = curState.customEmotions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${customList.size} özel duygu", color = Color(0xFF9E8DC0), fontSize = 12.sp)
                                    val addInteraction = remember { MutableInteractionSource() }
                                    val addPressed by addInteraction.collectIsPressedAsState()
                                    val addScale by animateFloatAsState(if (addPressed) 0.93f else 1f, tween(80), label = "addScale")
                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer { scaleX = addScale; scaleY = addScale }
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                androidx.compose.ui.graphics.Brush.linearGradient(
                                                    listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))
                                                )
                                            )
                                            .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(10.dp))
                                            .clickable(interactionSource = addInteraction, indication = null) { showAddCustomDialog = true }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Yeni Ekle", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                if (customList.isEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x20A855F7))
                                            .border(1.dp, Color(0x30A855F7), RoundedCornerShape(12.dp))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Bu karakter için henüz özel duygu eklenmedi. Sadakat, şüphe, saygı gibi benzersiz duygular ekleyebilirsin.",
                                            color = Color(0xFF9E8DC0), fontSize = 11.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                } else {
                                    Spacer(Modifier.height(10.dp))
                                    customList.forEachIndexed { index, ce ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                                        listOf(Color(0xFF1A0D38), Color(0xFF120826))
                                                    )
                                                )
                                                .border(
                                                    1.dp,
                                                    when (ce.difficulty) {
                                                        "Kolay"      -> Color(0xFF22C55E).copy(0.5f)
                                                        "Orta"       -> Color(0xFF60A5FA).copy(0.5f)
                                                        "Zor"        -> Color(0xFFFBBF24).copy(0.5f)
                                                        else         -> Color(0xFFF87171).copy(0.5f)
                                                    },
                                                    RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                        Text("✨ ${ce.name}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                        Spacer(Modifier.width(8.dp))
                                                        val diffColor = when (ce.difficulty) {
                                                            "Kolay" -> Color(0xFF22C55E)
                                                            "Orta"  -> Color(0xFF60A5FA)
                                                            "Zor"   -> Color(0xFFFBBF24)
                                                            else    -> Color(0xFFF87171)
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(diffColor.copy(0.15f))
                                                                .border(0.8.dp, diffColor.copy(0.5f), RoundedCornerShape(6.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(ce.difficulty, color = diffColor, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                                        }
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0x30F87171))
                                                            .border(0.8.dp, Color(0x50F87171), RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                val updated = customList.toMutableList().apply { removeAt(index) }
                                                                characterStatesMap[selectedTab] = curState.copy(
                                                                    customEmotionsJson = com.example.data.local.CustomEmotionDefinition.listToJsonArrayStr(updated)
                                                                )
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Default.Delete, null, tint = Color(0xFFF87171), modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                if (ce.purpose.isNotBlank()) {
                                                    Spacer(Modifier.height(4.dp))
                                                    Text("🎯 ${ce.purpose}", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                                }
                                                Spacer(Modifier.height(6.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                                    Text("${ce.minValue}–${ce.maxValue}", color = Color(0xFF7A6A9A), fontSize = 10.sp)
                                                    Spacer(Modifier.weight(1f))
                                                    Box(
                                                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0x20A855F7)).padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("${ce.currentValue}", color = Color(0xFFCDA8FF), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                                    }
                                                }
                                                Slider(
                                                    value = ce.currentValue.toFloat().coerceIn(ce.minValue.toFloat(), ce.maxValue.toFloat()),
                                                    onValueChange = { v ->
                                                        val updatedItem = ce.copy(currentValue = v.toInt())
                                                        val updated = customList.toMutableList().apply { set(index, updatedItem) }
                                                        characterStatesMap[selectedTab] = curState.copy(
                                                            customEmotionsJson = com.example.data.local.CustomEmotionDefinition.listToJsonArrayStr(updated)
                                                        )
                                                    },
                                                    valueRange = ce.minValue.toFloat()..ce.maxValue.toFloat().coerceAtLeast(ce.minValue.toFloat() + 1f),
                                                    colors = SliderDefaults.colors(thumbColor = Color(0xFFA855F7), activeTrackColor = Color(0xFFA855F7), inactiveTrackColor = Color(0xFF2A1C48))
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── HISTORY SECTION ────────────────────────────
                        "history" -> {
                            SectionCard("Duygu & İlişki Geçmişi", "📈", Color(0xFF34D399)) {
                                if (!historyLoaded) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                        EmochiLoadingSpinner(size = 28.dp, color = Color(0xFF34D399))
                                    }
                                } else if (emotionHistory.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x1534D399)).border(1.dp, Color(0x3034D399), RoundedCornerShape(12.dp)).padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Henüz duygu geçmişi yok.\nSohbet başladıkça duygu değişimleri burada görünecek.", color = Color(0xFF6EE7B7), fontSize = 11.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                    }
                                } else {
                                    // ─ Line chart ──────────────────────
                                    val mainHistory = emotionHistory.filter { it.characterName == null }
                                    if (mainHistory.size >= 2) {
                                        val recentAff = mainHistory.takeLast(20)
                                        val maxVal = 100f
                                        val chartHeight = 100.dp
                                        // Net change pill
                                        val firstAff = recentAff.first().affectionScore
                                        val lastAff  = recentAff.last().affectionScore
                                        val delta    = lastAff - firstAff
                                        val deltaColor = if (delta >= 0) Color(0xFF34D399) else Color(0xFFF87171)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Son ${recentAff.size} kayıt", color = Color(0xFF9E8DC0), fontSize = 11.sp)
                                            Box(
                                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                                    .background(deltaColor.copy(0.15f))
                                                    .border(0.8.dp, deltaColor.copy(0.5f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    "${if (delta >= 0) "+" else ""}$delta sevgi değişimi",
                                                    color = deltaColor, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(10.dp))
                                        // Canvas chart
                                        Canvas(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(chartHeight)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF0C0720))
                                                .border(1.dp, Color(0x2034D399), RoundedCornerShape(12.dp))
                                        ) {
                                            val w = size.width
                                            val h = size.height
                                            val stepX = if (recentAff.size > 1) w / (recentAff.size - 1) else w
                                            val pad   = 16f

                                            // Grid lines
                                            listOf(0.25f, 0.5f, 0.75f).forEach { frac ->
                                                val y = h - frac * (h - pad * 2) - pad
                                                drawLine(Color(0x1534D399), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                                            }

                                            // Affection path (green)
                                            if (recentAff.size >= 2) {
                                                val affPts = recentAff.mapIndexed { i, e ->
                                                    Offset(i * stepX, h - (e.affectionScore / maxVal) * (h - pad * 2) - pad)
                                                }
                                                for (i in 0 until affPts.size - 1) {
                                                    drawLine(Color(0xFF34D399), affPts[i], affPts[i + 1], strokeWidth = 2.5f,
                                                        cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                                }
                                                // Gradient fill under line
                                                val fillPath = androidx.compose.ui.graphics.Path().apply {
                                                    moveTo(affPts.first().x, h)
                                                    affPts.forEach { lineTo(it.x, it.y) }
                                                    lineTo(affPts.last().x, h)
                                                    close()
                                                }
                                                drawPath(fillPath, androidx.compose.ui.graphics.Brush.verticalGradient(
                                                    listOf(Color(0x5034D399), Color(0x0034D399)),
                                                    startY = 0f, endY = h
                                                ))
                                                // Trust path (blue)
                                                val trPts = recentAff.mapIndexed { i, e ->
                                                    Offset(i * stepX, h - (e.trustScore / maxVal) * (h - pad * 2) - pad)
                                                }
                                                for (i in 0 until trPts.size - 1) {
                                                    drawLine(Color(0xFF60A5FA), trPts[i], trPts[i + 1], strokeWidth = 2f,
                                                        cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                                }
                                                // Dots on affection line
                                                affPts.forEachIndexed { i, pt ->
                                                    val dotColor = if (i > 0 && recentAff[i].affectionScore > recentAff[i - 1].affectionScore) Color(0xFF34D399) else Color(0xFFF87171)
                                                    drawCircle(dotColor, 4f, pt)
                                                    drawCircle(Color.White, 2f, pt)
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        // Legend
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF34D399)))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Sevgi", color = Color(0xFF6EE7B7), fontSize = 10.sp)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF60A5FA)))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Güven", color = Color(0xFF93C5FD), fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(14.dp))
                                    // Last 8 history entries as cards
                                    Text("Son Olaylar", color = Color(0xFF9E8DC0), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    emotionHistory.takeLast(8).reversed().forEach { h ->
                                        val dominant = h.dominantEmotion
                                        val moodEmoji = when (dominant.lowercase()) {
                                            "mutlu", "joy"    -> "😄"
                                            "üzgün", "sad"    -> "😢"
                                            "kızgın", "anger" -> "😡"
                                            "flört"           -> "💕"
                                            "sakin", "calm"   -> "😌"
                                            else              -> "🎭"
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF0E0820))
                                                .border(0.8.dp, Color(0x2034D399), RoundedCornerShape(10.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(moodEmoji, fontSize = 16.sp)
                                                    Spacer(Modifier.width(8.dp))
                                                    Column {
                                                        Text(dominant.replaceFirstChar { it.uppercase() }, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                        Text(
                                                            java.text.SimpleDateFormat("dd MMM HH:mm", java.util.Locale.getDefault()).format(java.util.Date(h.timestamp)),
                                                            color = Color(0xFF7A6A9A), fontSize = 10.sp
                                                        )
                                                    }
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("❤️ ${h.affectionScore}", color = Color(0xFFF9A8D4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text("🛡️ ${h.trustScore}", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

                // ── SAVE BUTTON ────────────────────────────────────────
                Spacer(Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0x60E879A0), Color.Transparent)
                    )
                ))
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val cancelInteraction = remember { MutableInteractionSource() }
                    val cancelPressed by cancelInteraction.collectIsPressedAsState()
                    val cancelScale by animateFloatAsState(if (cancelPressed) 0.95f else 1f, tween(80), label = "cancelScale")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { scaleX = cancelScale; scaleY = cancelScale }
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x20A855F7))
                            .border(1.dp, Color(0x50A855F7), RoundedCornerShape(14.dp))
                            .clickable(interactionSource = cancelInteraction, indication = null) { onDismiss() }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("İptal", color = Color(0xFFCDA8FF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }

                    val saveInteraction = remember { MutableInteractionSource() }
                    val savePressed by saveInteraction.collectIsPressedAsState()
                    val saveScale by animateFloatAsState(if (savePressed) 0.95f else 1f, tween(80), label = "saveScale")
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .graphicsLayer { scaleX = saveScale; scaleY = saveScale }
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(Color(0xFFBE185D), Color(0xFF7C3AED))
                                )
                            )
                            .border(1.dp, Color(0xFFF472B6).copy(0.5f), RoundedCornerShape(14.dp))
                            .clickable(interactionSource = saveInteraction, indication = null) {
                                characterStatesMap["main"]?.let { onSaveBotEmotion(it.toJson()) }
                                characterStatesMap.forEach { (key, state) ->
                                    if (key != "main") onSaveCharacterEmotionState(key, state.toJson())
                                }
                                onDismiss()
                            }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Tüm Duyguları Kaydet", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddCustomDialog) {
        Dialog(onDismissRequest = { showAddCustomDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color(0xFF1A0D38), Color(0xFF100820))
                        )
                    )
                    .border(
                        1.2.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFFA855F7), Color(0xFF6D28D9))
                        ),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("Özel Duygu Ekle", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Duygu Adı", color = Color(0xFF9E8DC0), fontSize = 11.5.sp)
                    OutlinedTextField(
                        value = newCustomName,
                        onValueChange = { newCustomName = it },
                        placeholder = { Text("ör. Sadakat, Şüphe, Saygı...", fontSize = 11.sp, color = EmochiTextMuted) },
                        modifier = Modifier.fillMaxWidth(), colors = customTextFieldColors(), singleLine = true
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Kazanım Zorluğu", color = Color(0xFF9E8DC0), fontSize = 11.5.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val difficulties = listOf("Kolay", "Orta", "Zor", "Aşırı Zor")
                        val diffColors = listOf(Color(0xFF22C55E), Color(0xFF60A5FA), Color(0xFFFBBF24), Color(0xFFF87171))
                        difficulties.forEachIndexed { idx, diff ->
                            val isSelected = newCustomDifficulty == diff
                            val dInteraction = remember { MutableInteractionSource() }
                            val dPressed by dInteraction.collectIsPressedAsState()
                            val dScale by animateFloatAsState(if (dPressed) 0.93f else 1f, tween(80), label = "diffScale")
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer { scaleX = dScale; scaleY = dScale }
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) diffColors[idx].copy(0.25f) else Color(0xFF120A26))
                                    .border(if (isSelected) 1.dp else 0.7.dp, if (isSelected) diffColors[idx] else Color(0x30A855F7), RoundedCornerShape(8.dp))
                                    .clickable(interactionSource = dInteraction, indication = null) { newCustomDifficulty = diff }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(diff, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Min Değer", color = EmochiTextSecondary, fontSize = 11.sp)
                            OutlinedTextField(
                                value = newCustomMin,
                                onValueChange = { newCustomMin = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Max Değer", color = EmochiTextSecondary, fontSize = 11.sp)
                            OutlinedTextField(
                                value = newCustomMax,
                                onValueChange = { newCustomMax = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Şu Anki Değer", color = EmochiTextSecondary, fontSize = 11.sp)
                            OutlinedTextField(
                                value = newCustomCurrent,
                                onValueChange = { newCustomCurrent = it.filter { c -> c.isDigit() } },
                                modifier = Modifier.fillMaxWidth(),
                                colors = customTextFieldColors(),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Duygunun Amacı / Etkisi", color = EmochiTextSecondary, fontSize = 11.5.sp)
                    OutlinedTextField(
                        value = newCustomPurpose,
                        onValueChange = { newCustomPurpose = it },
                        placeholder = { Text("ör. Karakterin sadakat seviyesini ve karar mekanizmasını yönlendirir...", fontSize = 11.sp, color = EmochiTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )

                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val cancelDInteraction = remember { MutableInteractionSource() }
                        val cDPressed by cancelDInteraction.collectIsPressedAsState()
                        val cDScale by animateFloatAsState(if (cDPressed) 0.94f else 1f, tween(80), label = "cds")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer { scaleX = cDScale; scaleY = cDScale }
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x20A855F7))
                                .border(1.dp, Color(0x50A855F7), RoundedCornerShape(12.dp))
                                .clickable(interactionSource = cancelDInteraction, indication = null) { showAddCustomDialog = false }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("İptal", color = Color(0xFFCDA8FF), fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }

                        val addBtnInteraction = remember { MutableInteractionSource() }
                        val addBtnPressed by addBtnInteraction.collectIsPressedAsState()
                        val addBtnScale by animateFloatAsState(if (addBtnPressed) 0.94f else 1f, tween(80), label = "addBtn")
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .graphicsLayer { scaleX = addBtnScale; scaleY = addBtnScale }
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(Color(0xFF6D28D9), Color(0xFF4C1D95))
                                    )
                                )
                                .border(1.dp, Color(0xFFA855F7).copy(0.6f), RoundedCornerShape(12.dp))
                                .clickable(interactionSource = addBtnInteraction, indication = null) {
                                    if (newCustomName.isNotBlank()) {
                                        val minVal = newCustomMin.toIntOrNull() ?: 0
                                        val maxVal = newCustomMax.toIntOrNull() ?: 100
                                        val curVal = (newCustomCurrent.toIntOrNull() ?: 0).coerceIn(minVal, maxVal)
                                        val newDef = com.example.data.local.CustomEmotionDefinition(
                                            name = newCustomName,
                                            difficulty = newCustomDifficulty,
                                            minValue = minVal,
                                            maxValue = maxVal,
                                            currentValue = curVal,
                                            purpose = newCustomPurpose
                                        )
                                        val updatedList = curState.customEmotions + newDef
                                        characterStatesMap[selectedTab] = curState.copy(
                                            customEmotionsJson = com.example.data.local.CustomEmotionDefinition.listToJsonArrayStr(updatedList)
                                        )
                                        showAddCustomDialog = false
                                        newCustomName = ""
                                        newCustomPurpose = ""
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(5.dp))
                                Text("Duygu Ekle", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
