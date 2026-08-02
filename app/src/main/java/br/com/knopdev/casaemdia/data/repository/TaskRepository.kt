package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    val tasks: Flow<List<Task>>

    suspend fun addTask(title: String, dueDate: String)

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)
}