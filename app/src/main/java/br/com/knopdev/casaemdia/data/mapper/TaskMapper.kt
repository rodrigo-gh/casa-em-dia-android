package br.com.knopdev.casaemdia.data.mapper

import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskCategory

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        notes = notes,
        dueAtEpochMillis = dueAtEpochMillis,
        category = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.OTHER),
        completed = isCompleted,
        reminderEnabled = reminderEnabled,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
        completedAtEpochMillis = completedAtEpochMillis
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        notes = notes,
        dueAtEpochMillis = dueAtEpochMillis,
        category = category.name,
        isCompleted = completed,
        reminderEnabled = reminderEnabled,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
        completedAtEpochMillis = completedAtEpochMillis
    )
}
