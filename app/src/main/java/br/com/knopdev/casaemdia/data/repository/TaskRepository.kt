package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    val tasks: Flow<List<Task>>

    suspend fun getTask(taskId: Long): Task?

    suspend fun saveTask(task: Task): Long

    suspend fun deleteTask(taskId: Long)

    suspend fun updateTaskCompletion(
        taskId: Long,
        isCompleted: Boolean,
        completedAtEpochMillis: Long?,
        updatedAtEpochMillis: Long
    )
}
