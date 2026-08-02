package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.data.local.dao.TaskDao
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import br.com.knopdev.casaemdia.data.mapper.toTask
import br.com.knopdev.casaemdia.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao
) {

    val tasks: Flow<List<Task>> = taskDao.observeAll().map { entities ->
        entities.map { entity -> entity.toTask() }
    }

    suspend fun addTask(title: String, dueDate: String) {
        taskDao.insert(
            TaskEntity(
                title = title,
                dueDate = dueDate
            )
        )
    }

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        taskDao.updateCompletion(
            taskId = taskId,
            isCompleted = isCompleted
        )
    }
}