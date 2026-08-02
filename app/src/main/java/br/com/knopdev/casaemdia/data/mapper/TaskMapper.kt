package br.com.knopdev.casaemdia.data.mapper

import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import br.com.knopdev.casaemdia.model.Task

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        dueDate = dueDate,
        completed = isCompleted
    )
}