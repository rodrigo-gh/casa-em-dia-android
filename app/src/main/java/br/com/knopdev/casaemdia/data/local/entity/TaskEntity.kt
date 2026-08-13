package br.com.knopdev.casaemdia.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String,
    val dueAtEpochMillis: Long?,
    val category: String,
    val isCompleted: Boolean,
    val reminderEnabled: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?
)
