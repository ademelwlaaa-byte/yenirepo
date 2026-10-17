package com.example.ui.components

import com.example.ui.theme.AppColors

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.local.AffectionEventEntity
import com.example.data.local.BotEntity
import com.example.data.local.CharacterEmotionEntity
import com.example.data.local.EmotionState
import com.example.data.local.WorldState
import com.example.ui.theme.EmochiBorder
import com.example.ui.theme.EmochiCard
import com.example.ui.theme.EmochiPrimary
import com.example.ui.theme.EmochiTextMuted
import com.example.ui.theme.EmochiTextPrimary
import com.example.ui.theme.EmochiTextSecondary

@Composable
fun EmotionStatusSection(
    bot: BotEntity,
    characterEmotions: List<CharacterEmotionEntity> = emptyList(),
    affectionEvents: List<AffectionEventEntity> = emptyList(),
    filterCount: Int = 0,
    onUpdateCharacterEmotion: ((characterName: String, mood: String, affection: Int, trust: Int, tension: Int) -> Unit)? = null,
    onDeleteCharacter: ((characterName: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val emotion = remember(bot.emotionState) { EmotionState.fromJson(bot.emotionState) }
    val isUniverse = bot.mode == "universe"
    val worldState = remember(bot.worldAtmosphere) { WorldState.fromJson(bot.worldAtmosphere) }
    var showTimeline by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF130B2E), Color(0xFF0C0720), Color(0xFF10082A))
                )
            )
            .border(
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(Color(0xFF6D28D9), Color(0xFF4C1D95), Color(0xFF7C3AED))
                    )
                ),
                RoundedCornerShape(20.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon circle
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF7C3AED), Color(0xFF3B1070))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isUniverse) "🌌" else "❤️",
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isUniverse) "Evren & Atmosfer" else "Duygu & İlişki",
                            color = Color(0xFFCDA8FF),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (isUniverse) "Dünya simülasyonu aktif" else "Gerçek zamanlı takip",
                            color = Color(0xFF7C3AED),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                // Mood badge — gradient border pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1E1040), Color(0xFF130A2A))
                            )
                        )
                        .border(
                            androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(Color(0xFFA855F7), Color(0xFF6D28D9))
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${emotion.getMoodEmoji()} ${emotion.mood.replaceFirstChar { it.uppercase() }}",
                        color = Color(0xFFE9D5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            // Thin separator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.8.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Color(0x50A855F7), Color.Transparent)
                        )
                    )
            )

            // İçerik Filtresi İstatistiği Rozeti
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1A0E38), Color(0xFF100825))
                        )
                    )
                    .border(
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(Color(0x60A855F7), Color(0x304C1D95))
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛡️", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            "Filtre İstatistiği",
                            color = Color(0xFFCDA8FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (filterCount > 0)
                                    Brush.linearGradient(listOf(Color(0xFF3D1018), Color(0xFF1F0A10)))
                                else
                                    Brush.linearGradient(listOf(Color(0xFF0D2818), Color(0xFF071510)))
                            )
                            .border(
                                1.dp,
                                if (filterCount > 0) Color(0xFFF87171) else Color(0xFF86EFAC),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (filterCount > 0) "$filterCount tetiklendi" else "Temiz",
                            color = if (filterCount > 0) Color(0xFFFCA5A5) else Color(0xFF86EFAC),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Metrics (Affection with Tier, Trust, Tension, Hurt)
            EmotionBarItem(
                label = "Yakınlık Kademesi: ${emotion.getAffectionTierLabel()}",
                value = emotion.affection,
                maxValue = 100,
                color = AppColors.CoralPink,
                icon = "❤️"
            )
            Spacer(modifier = Modifier.height(8.dp))

            EmotionBarItem(
                label = "Güven Seviyesi (Trust)",
                value = emotion.trust,
                maxValue = 100,
                color = AppColors.LightBlue,
                icon = "🛡️"
            )
            Spacer(modifier = Modifier.height(8.dp))

            EmotionBarItem(
                label = "Fiziksel Yakınlık: ${emotion.getPhysicalComfortTierLabel()}",
                value = emotion.physicalComfortScore,
                maxValue = 100,
                color = AppColors.WarningAmber,
                icon = "🤝"
            )
            Spacer(modifier = Modifier.height(8.dp))

            EmotionBarItem(
                label = "Kırgınlık / Mesafe (Hurt)",
                value = emotion.hurt,
                maxValue = 100,
                color = AppColors.PurpleDark,
                icon = "💔"
            )
            Spacer(modifier = Modifier.height(8.dp))

            EmotionBarItem(
                label = "Gerginlik / Stres (${emotion.tension})",
                value = when (emotion.tension.lowercase()) { "crisis" -> 100; "conflict" -> 50; else -> 0 },
                maxValue = 100,
                color = AppColors.OrangeBright,
                icon = "⚡"
            )

            // Obsession score - ONLY visible if >= 50
            if (emotion.obsession >= 50) {
                Spacer(modifier = Modifier.height(8.dp))
                EmotionBarItem(
                    label = "Takıntı / Bağımlılık (Obsession)",
                    value = emotion.obsession,
                    maxValue = 100,
                    color = AppColors.Rose,
                    icon = "🖤"
                )
            }

            // User Defined Custom Emotions
            if (emotion.customEmotions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "✨ Özel Tanımlı Duygular",
                    color = AppColors.PurpleSoft,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                emotion.customEmotions.forEach { ce ->
                    Spacer(modifier = Modifier.height(6.dp))
                    val labelText = if (ce.purpose.isNotBlank()) "${ce.name} (${ce.difficulty}) — ${ce.purpose}" else "${ce.name} (${ce.difficulty})"
                    EmotionBarItem(
                        label = labelText,
                        value = ce.currentValue.coerceIn(ce.minValue, ce.maxValue),
                        maxValue = ce.maxValue.coerceAtLeast(ce.minValue + 1),
                        color = AppColors.PurpleVibrant,
                        icon = "✨"
                    )
                }
            }

            // Secondary & Suppressed Emotions
            if (emotion.secondaryMood.isNotBlank() || emotion.suppressedEmotion.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.CardDark)
                        .padding(8.dp)
                ) {
                    if (emotion.secondaryMood.isNotBlank()) {
                        Text(
                            text = "🎭 İkincil Duygu: ${emotion.secondaryMood}",
                            color = EmochiTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    if (emotion.suppressedEmotion.isNotBlank()) {
                        if (emotion.secondaryMood.isNotBlank()) Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔒 Bastırılmış İç Duygu: ${emotion.suppressedEmotion}",
                            color = AppColors.Hex_FF8A8A,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Mood Intensity & Resilience & Speech Pattern
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Duygu Şiddeti: ${emotion.intensity}/10", color = EmochiTextMuted, fontSize = 11.sp)
                Text("Duygusal Direnç: ${emotion.resilience}/10", color = EmochiTextMuted, fontSize = 11.sp)
            }

            if (emotion.speechPattern.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🗣️ Konuşma Üslubu/Hızı: ${emotion.speechPattern}",
                    color = EmochiTextSecondary,
                    fontSize = 11.sp
                )
            }

            // Universe Mode Specifics
            if (isUniverse) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(EmochiBorder)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🌌 Evren & Atmosfer Simülasyonu",
                    color = AppColors.DeepPurple,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mekan: ${worldState.meso.currentLocation.ifBlank { "Bilinmiyor" }}", color = EmochiTextPrimary, fontSize = 11.5.sp)
                    Text("Gerilim: ${worldState.micro.sceneTension.uppercase()}", color = EmochiTextMuted, fontSize = 11.5.sp)
                }

                if (worldState.macro.eraRules.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🌐 Makro Kurallar: ${worldState.macro.eraRules}",
                        color = EmochiTextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (worldState.meso.locationPersistentNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🏢 Mekan Notları: ${worldState.meso.locationPersistentNotes}",
                        color = EmochiTextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (worldState.micro.sceneMood.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚡ Sahnede Hava: ${worldState.micro.sceneMood}",
                        color = EmochiPrimary,
                        fontSize = 11.sp
                    )
                }

            }

            // Character Emotions List & Manual Editor (Available in BOTH Universe and Personal modes)
            if (characterEmotions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(EmochiBorder)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "👥 Yan Karakterler Duygu Durumu (Düzenlenebilir)",
                    color = EmochiPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                var editingCharacter by remember { mutableStateOf<CharacterEmotionEntity?>(null) }

                characterEmotions.forEach { charEntity ->
                    val charEmotion = remember(charEntity.emotionState) { EmotionState.fromJson(charEntity.emotionState) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.CardDark)
                            .clickable {
                                if (onUpdateCharacterEmotion != null) {
                                    editingCharacter = charEntity
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = charEntity.characterName + if (onUpdateCharacterEmotion != null) " ✏️" else "",
                                    color = EmochiTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (onDeleteCharacter != null) {
                                    IconButton(
                                        onClick = { onDeleteCharacter(charEntity.characterName) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("🗑️", fontSize = 11.sp)
                                    }
                                }
                            }
                            Text(
                                text = "${charEmotion.getMoodEmoji()} ${charEmotion.mood}",
                                color = EmochiTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("❤️ Sevgi: ${charEmotion.affection}%", color = AppColors.CoralPink, fontSize = 10.sp)
                            Text("🛡️ Güven: ${charEmotion.trust}%", color = AppColors.LightBlue, fontSize = 10.sp)
                            Text("⚡ Gerginlik: ${charEmotion.tension}%", color = AppColors.OrangeBright, fontSize = 10.sp)
                        }
                        if (charEmotion.customEmotions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✨ Özel: " + charEmotion.customEmotions.joinToString(", ") { ce -> "${ce.name}: ${ce.currentValue}/${ce.maxValue}" },
                                color = AppColors.PurpleSoft,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                editingCharacter?.let { charEntity ->
                    val currentEmotion = remember(charEntity.emotionState) { EmotionState.fromJson(charEntity.emotionState) }
                    var editMood by remember { mutableStateOf(currentEmotion.mood) }
                    var editAffection by remember { mutableStateOf(currentEmotion.affection.toFloat()) }
                    var editTrust by remember { mutableStateOf(currentEmotion.trust.toFloat()) }
                    var editTension by remember { mutableStateOf(currentEmotion.tension.toIntOrNull() ?: 10) }

                    androidx.compose.ui.window.Dialog(onDismissRequest = { editingCharacter = null }) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppColors.Hex_1E1E2E),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmochiBorder),
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "✏️ ${charEntity.characterName} Duygu Düzenleme",
                                    color = EmochiTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Text("Ruh Hali (Mood)", color = EmochiTextSecondary, fontSize = 12.sp)
                                androidx.compose.material3.OutlinedTextField(
                                    value = editMood,
                                    onValueChange = { editMood = it },
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                    colors = customTextFieldColors(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("❤️ Sevgi: ${editAffection.toInt()}%", color = AppColors.CoralPink, fontSize = 12.sp)
                                androidx.compose.material3.Slider(
                                    value = editAffection,
                                    onValueChange = { editAffection = it },
                                    valueRange = 0f..100f
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("🛡️ Güven: ${editTrust.toInt()}%", color = AppColors.LightBlue, fontSize = 12.sp)
                                androidx.compose.material3.Slider(
                                    value = editTrust,
                                    onValueChange = { editTrust = it },
                                    valueRange = 0f..100f
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("⚡ Gerginlik: $editTension%", color = AppColors.OrangeBright, fontSize = 12.sp)
                                androidx.compose.material3.Slider(
                                    value = editTension.toFloat(),
                                    onValueChange = { editTension = it.toInt() },
                                    valueRange = 0f..100f
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                            .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(androidx.compose.ui.graphics.Color(0xFF130C2A), androidx.compose.ui.graphics.Color(0xFF0E0820))))
                                            .border(0.8.dp, androidx.compose.ui.graphics.Color(0x50A855F7), androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                            .clickable { editingCharacter = null }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("İptal", color = EmochiTextMuted, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    androidx.compose.material3.Button(
                                        onClick = {
                                            onUpdateCharacterEmotion?.invoke(
                                                charEntity.characterName,
                                                editMood,
                                                editAffection.toInt(),
                                                editTrust.toInt(),
                                                editTension
                                            )
                                            editingCharacter = null
                                        },
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = EmochiPrimary)
                                    ) {
                                        Text("Kaydet", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val formattedDate = remember(bot.updatedAt) {
                try {
                    val sdf = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("tr"))
                    sdf.format(java.util.Date(if (bot.updatedAt > 0) bot.updatedAt else System.currentTimeMillis()))
                } catch (e: Exception) {
                    ""
                }
            }
            if (formattedDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "⏱️ Son Güncelleme: $formattedDate",
                        color = EmochiTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Relationship History Timeline Section
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(EmochiBorder)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF180E38), Color(0xFF100825))
                        )
                    )
                    .border(
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(Color(0x50A855F7), Color(0x306D28D9))
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { showTimeline = !showTimeline }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📜", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "İlişki Zaman Çizelgesi",
                                color = Color(0xFFCDA8FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (affectionEvents.isNotEmpty()) {
                                Text(
                                    text = "${affectionEvents.size} kayıt",
                                    color = Color(0xFF7C3AED),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x30A855F7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (showTimeline) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Aç/Kapat",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (showTimeline) {
                Spacer(modifier = Modifier.height(8.dp))
                if (affectionEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D0820))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz ilişki kırılması veya yakınlık değişimi kaydedilmedi.",
                            color = EmochiTextMuted,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        affectionEvents.forEach { ev ->
                            val isPos = ev.scoreDelta > 0
                            val badgeColor = if (isPos) AppColors.Emerald else AppColors.ErrorRed
                            val badgeText = if (isPos) "+${ev.scoreDelta}" else "${ev.scoreDelta}"
                            val dateStr = try {
                                val sdf = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale("tr"))
                                sdf.format(java.util.Date(ev.timestamp))
                            } catch (e: Exception) { "" }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF130B28), Color(0xFF0C0720))
                                        )
                                    )
                                    .border(
                                        androidx.compose.foundation.BorderStroke(
                                            0.8.dp,
                                            if (isPos) Brush.linearGradient(listOf(Color(0x4086EFAC), Color(0x2022C55E)))
                                            else Brush.linearGradient(listOf(Color(0x40F87171), Color(0x20DC2626)))
                                        ),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(badgeColor.copy(alpha = 0.18f))
                                        .border(
                                            0.8.dp,
                                            badgeColor.copy(alpha = 0.5f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        color = badgeColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ev.shortDescription,
                                        color = EmochiTextPrimary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                    if (dateStr.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dateStr,
                                            color = EmochiTextMuted,
                                            fontSize = 9.5.sp
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
private fun EmotionBarItem(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color,
    icon: String
) {
    val targetFraction = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    var animTarget by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(targetFraction) { animTarget = targetFraction }
    val animatedFraction by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "barFill"
    )

    // Lighten color for gradient
    val colorLight = Color(
        red   = (color.red   + 0.28f).coerceAtMost(1f),
        green = (color.green + 0.22f).coerceAtMost(1f),
        blue  = (color.blue  + 0.28f).coerceAtMost(1f),
        alpha = 1f
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(label, color = EmochiTextSecondary, fontSize = 11.sp)
            }
            Text(
                "$value",
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
        ) {
            val trackH = size.height
            val trackW = size.width
            val cr = CornerRadius(trackH / 2f, trackH / 2f)

            // Track background
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1A1035),
                        Color(0xFF120826)
                    )
                ),
                size = Size(trackW, trackH),
                cornerRadius = cr
            )

            // Subtle track glow
            drawRoundRect(
                color = color.copy(alpha = 0.12f),
                size = Size(trackW, trackH),
                cornerRadius = cr
            )

            if (animatedFraction > 0f) {
                val fillW = trackW * animatedFraction

                // Filled bar gradient
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(colorLight, color),
                        startX = 0f,
                        endX = fillW
                    ),
                    size = Size(fillW, trackH),
                    cornerRadius = cr
                )

                // Glow line on top of bar
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorLight.copy(alpha = 0.55f),
                            Color.White.copy(alpha = 0.30f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = fillW
                    ),
                    size = Size(fillW, trackH * 0.45f),
                    cornerRadius = CornerRadius(trackH * 0.45f / 2f)
                )

                // Tip glow dot
                if (fillW > trackH) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.80f),
                                colorLight.copy(alpha = 0.40f),
                                Color.Transparent
                            ),
                            center = Offset(fillW - trackH / 2f, trackH / 2f),
                            radius = trackH * 1.1f
                        ),
                        radius = trackH * 1.1f,
                        center = Offset(fillW - trackH / 2f, trackH / 2f)
                    )
                }
            }
        }
    }
}
