package br.com.knopdev.casaemdia.domain.usecase

import br.com.knopdev.casaemdia.data.repository.TaskRepository
import br.com.knopdev.casaemdia.domain.reminder.TaskReminderScheduler
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskInput
import kotlinx.coroutines.flow.Flow

class ObserveTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.tasks
}

class GetTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: Long): Task? = repository.getTask(taskId)
}

class SaveTaskUseCase(
    private val repository: TaskRepository,
    private val reminderScheduler: TaskReminderScheduler,
    private val nowProvider: () -> Long = System::currentTimeMillis
) {
    suspend operator fun invoke(input: TaskInput, existingTask: Task? = null): Task {
        val title = input.title.trim()
        val notes = input.notes.trim()
        require(title.isNotBlank()) { "O título é obrigatório" }
        require(title.length <= 80) { "O título deve ter até 80 caracteres" }
        require(notes.length <= 500) { "As observações devem ter até 500 caracteres" }

        val now = nowProvider()
        val task = Task(
            id = existingTask?.id ?: 0,
            title = title,
            notes = notes,
            dueAtEpochMillis = input.dueAtEpochMillis,
            category = input.category,
            completed = existingTask?.completed ?: false,
            reminderEnabled = input.reminderEnabled && input.dueAtEpochMillis != null,
            createdAtEpochMillis = existingTask?.createdAtEpochMillis ?: now,
            updatedAtEpochMillis = now,
            completedAtEpochMillis = existingTask?.completedAtEpochMillis
        )

        val taskId = repository.saveTask(task)
        val savedTask = task.copy(id = if (task.id == 0L) taskId else task.id)

        if (savedTask.reminderEnabled && !savedTask.completed &&
            (savedTask.dueAtEpochMillis ?: 0) > now
        ) {
            reminderScheduler.schedule(savedTask)
        } else {
            reminderScheduler.cancel(savedTask.id)
        }

        return savedTask
    }
}

class DeleteTaskUseCase(
    private val repository: TaskRepository,
    private val reminderScheduler: TaskReminderScheduler
) {
    suspend operator fun invoke(task: Task) {
        repository.deleteTask(task.id)
        reminderScheduler.cancel(task.id)
    }
}

class RestoreTaskUseCase(
    private val repository: TaskRepository,
    private val reminderScheduler: TaskReminderScheduler,
    private val nowProvider: () -> Long = System::currentTimeMillis
) {
    suspend operator fun invoke(task: Task) {
        repository.saveTask(task)
        if (task.reminderEnabled && !task.completed && (task.dueAtEpochMillis ?: 0) > nowProvider()) {
            reminderScheduler.schedule(task)
        }
    }
}

class SetTaskCompletionUseCase(
    private val repository: TaskRepository,
    private val reminderScheduler: TaskReminderScheduler,
    private val nowProvider: () -> Long = System::currentTimeMillis
) {
    suspend operator fun invoke(taskId: Long, completed: Boolean) {
        val now = nowProvider()
        repository.updateTaskCompletion(
            taskId = taskId,
            isCompleted = completed,
            completedAtEpochMillis = if (completed) now else null,
            updatedAtEpochMillis = now
        )

        if (completed) {
            reminderScheduler.cancel(taskId)
        } else {
            repository.getTask(taskId)?.let { task ->
                if (task.reminderEnabled && (task.dueAtEpochMillis ?: 0) > now) {
                    reminderScheduler.schedule(task.copy(completed = false))
                }
            }
        }
    }
}

data class TaskUseCases(
    val observeTasks: ObserveTasksUseCase,
    val getTask: GetTaskUseCase,
    val saveTask: SaveTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val restoreTask: RestoreTaskUseCase,
    val setTaskCompletion: SetTaskCompletionUseCase
)
