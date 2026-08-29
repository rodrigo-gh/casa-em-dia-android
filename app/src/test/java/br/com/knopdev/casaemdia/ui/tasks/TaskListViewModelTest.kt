package br.com.knopdev.casaemdia.ui.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.knopdev.casaemdia.data.repository.TaskRepository
import br.com.knopdev.casaemdia.domain.reminder.NoOpTaskReminderScheduler
import br.com.knopdev.casaemdia.domain.usecase.DeleteTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.GetTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.ObserveTasksUseCase
import br.com.knopdev.casaemdia.domain.usecase.RestoreTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.SaveTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.SetTaskCompletionUseCase
import br.com.knopdev.casaemdia.domain.usecase.TaskUseCases
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskCategory
import br.com.knopdev.casaemdia.model.TaskInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `saveTask adiciona uma nova tarefa ao repositorio`() = runTest {
        val repository = FakeTaskRepository()
        val viewModel = TaskListViewModel(repository.toUseCases(now = 1_000))

        viewModel.saveTask(
            TaskInput(
                title = "Limpar varanda",
                notes = "",
                dueAtEpochMillis = 2_000,
                category = TaskCategory.CLEANING,
                reminderEnabled = false
            )
        )
        advanceUntilIdle()

        val task = repository.tasksState.value.single()
        assertEquals("Limpar varanda", task.title)
        assertEquals(2_000L, task.dueAtEpochMillis)
        assertFalse(task.completed)
    }

    @Test
    fun `updateTaskCompletion atualiza tarefa no repositorio`() = runTest {
        val repository = FakeTaskRepository(listOf(sampleTask))
        val viewModel = TaskListViewModel(repository.toUseCases(now = 5_000))

        viewModel.updateTaskCompletion(taskId = 1, isCompleted = true)
        advanceUntilIdle()

        val task = repository.tasksState.value.single()
        assertTrue(task.completed)
        assertEquals(5_000L, task.completedAtEpochMillis)
    }

    private fun FakeTaskRepository.toUseCases(now: Long): TaskUseCases {
        val clock = { now }
        return TaskUseCases(
            observeTasks = ObserveTasksUseCase(this),
            getTask = GetTaskUseCase(this),
            saveTask = SaveTaskUseCase(this, NoOpTaskReminderScheduler, clock),
            deleteTask = DeleteTaskUseCase(this, NoOpTaskReminderScheduler),
            restoreTask = RestoreTaskUseCase(this, NoOpTaskReminderScheduler, clock),
            setTaskCompletion = SetTaskCompletionUseCase(this, NoOpTaskReminderScheduler, clock)
        )
    }

    companion object {
        private val sampleTask = Task(
            id = 1,
            title = "Pagar conta",
            createdAtEpochMillis = 1_000,
            updatedAtEpochMillis = 1_000
        )
    }
}

private class FakeTaskRepository(initialTasks: List<Task> = emptyList()) : TaskRepository {
    val tasksState = MutableStateFlow(initialTasks)
    override val tasks: Flow<List<Task>> = tasksState

    override suspend fun getTask(taskId: Long): Task? = tasksState.value.firstOrNull { it.id == taskId }

    override suspend fun saveTask(task: Task): Long {
        val id = if (task.id == 0L) (tasksState.value.maxOfOrNull(Task::id) ?: 0) + 1 else task.id
        val savedTask = task.copy(id = id)
        tasksState.value = tasksState.value.filterNot { it.id == id } + savedTask
        return id
    }

    override suspend fun deleteTask(taskId: Long) {
        tasksState.value = tasksState.value.filterNot { it.id == taskId }
    }

    override suspend fun updateTaskCompletion(
        taskId: Long,
        isCompleted: Boolean,
        completedAtEpochMillis: Long?,
        updatedAtEpochMillis: Long
    ) {
        tasksState.value = tasksState.value.map { task ->
            if (task.id == taskId) task.copy(
                completed = isCompleted,
                completedAtEpochMillis = completedAtEpochMillis,
                updatedAtEpochMillis = updatedAtEpochMillis
            ) else task
        }
    }
}
