package br.com.knopdev.casaemdia.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestions")
data class SuggestionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val updatedAtEpochMillis: Long
)
