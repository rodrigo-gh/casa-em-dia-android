package br.com.knopdev.casaemdia.ui.tasks

import br.com.knopdev.casaemdia.data.repository.TaskRepository
import br.com.knopdev.casaemdia.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.Test

class TaskListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun `addTask adiciona uma nova tarefa ao repositorio`() = runTest {
        val repository = FakeTaskRepository()
        val viewModel = TaskListViewModel(repository)

        viewModel.addTask(
            title = "Limpar varanda",
            dueDate = "Domingo"
        )

        advanceUntilIdle()

        val task = repository.tasks.first().single()

        assertEquals("Limpar varanda", task.title)
        assertEquals("Domingo", task.dueDate)
        assertTrue(!task.completed)
    }

    @Test
    fun `updateTaskCompletion atualiza tarefa no repositorio`() = runTest {
        val repository = FakeTaskRepository(
            initialTasks = listOf(
                Task(
                    id = 1,
                    title = "Pagar conta",
                    dueDate = "Amanhã"
                )
            )
        )

        val viewModel = TaskListViewModel(repository)

        viewModel.updateTaskCompletion(
            taskId = 1,
            isCompleted = true
        )

        advanceUntilIdle()

        val task = repository.tasks.first().single()

        assertTrue(task.completed)
    }
}

private class FakeTaskRepository(
    initialTasks: List<Task> = emptyList()
) : TaskRepository {

    private val mutableTasks = MutableStateFlow(initialTasks)

    override val tasks: Flow<List<Task>> = mutableTasks

    override suspend fun addTask(title: String, dueDate: String) {
        val nextId = (mutableTasks.value.maxOfOrNull { it.id } ?: 0) + 1

        mutableTasks.value = mutableTasks.value + Task(
            id = nextId,
            title = title,
            dueDate = dueDate
        )
    }

    override suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        mutableTasks.value = mutableTasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(completed = isCompleted)
            } else {
                task
            }
        }
    }
}