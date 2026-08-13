package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.data.local.dao.TaskDao
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskCategory
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RoomTaskRepositoryTest {

    private val taskDao: TaskDao = mock()
    private lateinit var repository: RoomTaskRepository

    @Before
    fun setup() {
        whenever(taskDao.observeAll()).thenReturn(emptyFlow())
        repository = RoomTaskRepository(taskDao)
    }

    @Test
    fun saveTask_enviaEntidadeCorretaAoDao() = runTest {
        whenever(taskDao.upsert(taskEntity)).thenReturn(8)

        repository.saveTask(task)

        verify(taskDao).upsert(taskEntity)
    }

    @Test
    fun updateTaskCompletion_atualizaStatusETimestampsNoDao() = runTest {
        repository.updateTaskCompletion(
            taskId = 8,
            isCompleted = true,
            completedAtEpochMillis = 2_000,
            updatedAtEpochMillis = 2_000
        )

        verify(taskDao).updateCompletion(
            taskId = 8,
            isCompleted = true,
            completedAtEpochMillis = 2_000,
            updatedAtEpochMillis = 2_000
        )
    }

    companion object {
        private val task = Task(
            id = 8,
            title = "Lavar roupas",
            notes = "Separar roupas claras",
            dueAtEpochMillis = 3_000,
            category = TaskCategory.CLEANING,
            createdAtEpochMillis = 1_000,
            updatedAtEpochMillis = 1_000
        )
        private val taskEntity = TaskEntity(
            id = 8,
            title = "Lavar roupas",
            notes = "Separar roupas claras",
            dueAtEpochMillis = 3_000,
            category = "CLEANING",
            isCompleted = false,
            reminderEnabled = false,
            createdAtEpochMillis = 1_000,
            updatedAtEpochMillis = 1_000,
            completedAtEpochMillis = null
        )
    }
}
