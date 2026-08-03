package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.data.local.dao.TaskDao
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import br.com.knopdev.casaemdia.data.mapper.toTask
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.testing.EspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override val tasks: Flow<List<Task>> = taskDao.observeAll().map { entities ->
        entities.map { entity -> entity.toTask() }
    }

    override suspend fun addTask(title: String, dueDate: String) {
        EspressoIdlingResource.wrap {
            taskDao.insert(
                TaskEntity(
                    title = title,
                    dueDate = dueDate
                )
            )
        }
    }

    override suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        EspressoIdlingResource.wrap {
            taskDao.updateCompletion(taskId, isCompleted)
        }
    }
}