package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milestones")
data class MilestoneEntity(
    @PrimaryKey val id: String,
    val botId: String,
    val type: String,       // "affection_25", "affection_50", "trust_75", etc.
    val title: String,
    val emoji: String,
    val affectionAtTime: Int,
    val trustAtTime: Int,
    val reachedAt: Long = System.currentTimeMillis()
)

object MilestoneDefinitions {
    data class MilestoneDef(val type: String, val threshold: Int, val field: String, val title: String, val emoji: String)

    val all = listOf(
        MilestoneDef("affection_25",  25,  "affection", "İlk Bağ",      "💫"),
        MilestoneDef("affection_50",  50,  "affection", "Yakın Dost",   "💛"),
        MilestoneDef("affection_75",  75,  "affection", "Derin Bağ",    "🧡"),
        MilestoneDef("affection_90",  90,  "affection", "Ruh Eşi",      "❤️"),
        MilestoneDef("affection_100", 100, "affection", "Sonsuz Bağ",   "💖"),
        MilestoneDef("trust_50",      50,  "trust",     "Güven Kazandı","🤝"),
        MilestoneDef("trust_90",      90,  "trust",     "Tam Güven",    "🛡️")
    )
}
