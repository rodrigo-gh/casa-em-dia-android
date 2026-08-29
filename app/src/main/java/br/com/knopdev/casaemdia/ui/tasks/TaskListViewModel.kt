package br.com.knopdev.casaemdia.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.knopdev.casaemdia.domain.usecase.TaskUseCases
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskFilter
import br.com.knopdev.casaemdia.model.TaskInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskListUiState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val query: String = "",
    val filter: TaskFilter = TaskFilter.PENDING
)

sealed interface TaskEvent {
    data object TaskSaved : TaskEvent
    data class TaskDeleted(val task: Task) : TaskEvent
    data class Error(val message: String) : TaskEvent
}

class TaskListViewModel(
    private val useCases: TaskUseCases
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(TaskFilter.PENDING)
    private val mutableIsSaving = MutableStateFlow(false)
    private val mutableEditingTask = MutableStateFlow<Task?>(null)
    private val mutableEvents = MutableSharedFlow<TaskEvent>(extraBufferCapacity = 4)

    val isSaving: StateFlow<Boolean> = mutableIsSaving.asStateFlow()
    val editingTask: StateFlow<Task?> = mutableEditingTask.asStateFlow()
    val events = mutableEvents.asSharedFlow()

    val uiState: StateFlow<TaskListUiState> = combine(
        useCases.observeTasks(),
        query,
        filter
    ) { allTasks, currentQuery, currentFilter ->
        val filteredTasks = allTasks.filter { task ->
            val matchesFilter = when (currentFilter) {
                TaskFilter.ALL -> true
                TaskFilter.PENDING -> !task.completed
                TaskFilter.COMPLETED -> task.completed
            }
            val matchesQuery = currentQuery.isBlank() ||
                task.title.contains(currentQuery, ignoreCase = true) ||
                task.notes.contains(currentQuery, ignoreCase = true)
            matchesFilter && matchesQuery
        }

        TaskListUiState(
            isLoading = false,
            tasks = filteredTasks,
            totalCount = allTasks.size,
            pendingCount = allTasks.count { !it.completed },
            completedCount = allTasks.count(Task::completed),
            query = currentQuery,
            filter = currentFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskListUiState()
    )

    fun setQuery(value: String) {
        query.value = value
    }

    fun setFilter(value: TaskFilter) {
        filter.value = value
    }

    fun loadTask(taskId: Long) {
        if (taskId <= 0) {
            mutableEditingTask.value = null
            return
        }
        viewModelScope.launch {
            mutableEditingTask.value = useCases.getTask(taskId)
        }
    }

    fun saveTask(input: TaskInput, taskId: Long = 0) {
        if (mutableIsSaving.value) return
        viewModelScope.launch {
            mutableIsSaving.value = true
            runCatching {
                val existingTask = if (taskId > 0) useCases.getTask(taskId) else null
                useCases.saveTask(input, existingTask)
            }.onSuccess {
                mutableEditingTask.value = null
                mutableEvents.emit(TaskEvent.TaskSaved)
            }.onFailure { error ->
                mutableEvents.emit(TaskEvent.Error(error.message ?: "Não foi possível salvar a tarefa"))
            }
            mutableIsSaving.value = false
        }
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            runCatching { useCases.setTaskCompletion(taskId, isCompleted) }
                .onFailure { error ->
                    mutableEvents.emit(TaskEvent.Error(error.message ?: "Não foi possível atualizar a tarefa"))
                }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            runCatching { useCases.deleteTask(task) }
                .onSuccess { mutableEvents.emit(TaskEvent.TaskDeleted(task)) }
                .onFailure { error ->
                    mutableEvents.emit(TaskEvent.Error(error.message ?: "Não foi possível excluir a tarefa"))
                }
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            runCatching { useCases.restoreTask(task) }
                .onFailure { error ->
                    mutableEvents.emit(TaskEvent.Error(error.message ?: "Não foi possível restaurar a tarefa"))
                }
        }
    }
}
