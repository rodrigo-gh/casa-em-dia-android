package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.data.local.dao.TaskDao
import br.com.knopdev.casaemdia.data.mapper.toEntity
import br.com.knopdev.casaemdia.data.mapper.toTask
import br.com.knopdev.casaemdia.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskRepository(
    private val taskDao: TaskDao,
    private val operationTracker: TaskOperationTracker = NoOpTaskOperationTracker
) : TaskRepository {

    override val tasks: Flow<List<Task>> = taskDao.observeAll().map { entities ->
        entities.map { entity -> entity.toTask() }
    }

    override suspend fun getTask(taskId: Long): Task? {
        return taskDao.getById(taskId)?.toTask()
    }

    override suspend fun saveTask(task: Task): Long {
        return operationTracker.track {
            taskDao.upsert(task.toEntity())
        }
    }

    override suspend fun deleteTask(taskId: Long) {
        operationTracker.track {
            taskDao.deleteById(taskId)
        }
    }

    override suspend fun updateTaskCompletion(
        taskId: Long,
        isCompleted: Boolean,
        completedAtEpochMillis: Long?,
        updatedAtEpochMillis: Long
    ) {
        operationTracker.track {
            taskDao.updateCompletion(
                taskId = taskId,
                isCompleted = isCompleted,
                completedAtEpochMillis = completedAtEpochMillis,
                updatedAtEpochMillis = updatedAtEpochMillis
            )
        }
    }
}
