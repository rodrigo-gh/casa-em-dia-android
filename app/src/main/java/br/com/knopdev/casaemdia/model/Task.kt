package br.com.knopdev.casaemdia.model

data class Task(
    val id: Long,
    val title: String,
    val notes: String = "",
    val dueAtEpochMillis: Long? = null,
    val category: TaskCategory = TaskCategory.OTHER,
    val completed: Boolean = false,
    val reminderEnabled: Boolean = false,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val completedAtEpochMillis: Long? = null
)

enum class TaskCategory {
    CLEANING,
    MAINTENANCE,
    BILLS,
    SHOPPING,
    OTHER
}

enum class TaskFilter {
    ALL,
    PENDING,
    COMPLETED
}

data class TaskInput(
    val title: String,
    val notes: String,
    val dueAtEpochMillis: Long?,
    val category: TaskCategory,
    val reminderEnabled: Boolean
)
