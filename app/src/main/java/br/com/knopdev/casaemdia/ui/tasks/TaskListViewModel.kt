package br.com.knopdev.casaemdia.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import br.com.knopdev.casaemdia.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks = repository.tasks.asLiveData()

    fun addTask(title: String, dueDate: String) {
        viewModelScope.launch {
            repository.addTask(title, dueDate)
        }
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, isCompleted)
        }
    }
}