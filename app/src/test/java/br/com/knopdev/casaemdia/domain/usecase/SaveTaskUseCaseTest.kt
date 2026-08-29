package br.com.knopdev.casaemdia.domain.usecase

import br.com.knopdev.casaemdia.data.repository.TaskRepository
import br.com.knopdev.casaemdia.domain.reminder.TaskReminderScheduler
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskCategory
import br.com.knopdev.casaemdia.model.TaskInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveTaskUseCaseTest {

    @Test
    fun `salva tarefa normalizada e agenda lembrete futuro`() = runTest {
        val repository = RecordingTaskRepository()
        val scheduler = RecordingReminderScheduler()
        val useCase = SaveTaskUseCase(repository, scheduler) { 1_000 }

        val saved = useCase(
            TaskInput(
                title = "  Limpar quintal  ",
                notes = "  Recolher folhas  ",
                dueAtEpochMillis = 2_000,
                category = TaskCategory.CLEANING,
                reminderEnabled = true
            )
        )

        assertEquals("Limpar quintal", saved.title)
        assertEquals("Recolher folhas", saved.notes)
        assertEquals(1L, saved.id)
        assertEquals(saved, scheduler.scheduledTask)
    }

    @Test
    fun `rejeita titulo vazio`() = runTest {
        val useCase = SaveTaskUseCase(RecordingTaskRepository(), RecordingReminderScheduler()) { 1_000 }

        val result = runCatching {
            useCase(TaskInput("  ", "", null, TaskCategory.OTHER, false))
        }
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `desativa lembrete quando tarefa nao tem prazo`() = runTest {
        val scheduler = RecordingReminderScheduler()
        val saved = SaveTaskUseCase(RecordingTaskRepository(), scheduler) { 1_000 }(
            TaskInput("Organizar armário", "", null, TaskCategory.OTHER, true)
        )

        assertTrue(!saved.reminderEnabled)
        assertEquals(saved.id, scheduler.cancelledTaskId)
    }
}

private class RecordingTaskRepository : TaskRepository {
    private val data = MutableStateFlow<List<Task>>(emptyList())
    override val tasks: Flow<List<Task>> = data

    override suspend fun getTask(taskId: Long) = data.value.firstOrNull { it.id == taskId }

    override suspend fun saveTask(task: Task): Long {
        val id = if (task.id == 0L) 1L else task.id
        data.value = data.value.filterNot { it.id == id } + task.copy(id = id)
        return id
    }

    override suspend fun deleteTask(taskId: Long) {
        data.value = data.value.filterNot { it.id == taskId }
    }

    override suspend fun updateTaskCompletion(
        taskId: Long,
        isCompleted: Boolean,
        completedAtEpochMillis: Long?,
        updatedAtEpochMillis: Long
    ) = Unit
}

private class RecordingReminderScheduler : TaskReminderScheduler {
    var scheduledTask: Task? = null
    var cancelledTaskId: Long? = null

    override fun schedule(task: Task) {
        scheduledTask = task
    }

    override fun cancel(taskId: Long) {
        cancelledTaskId = taskId
    }
}
