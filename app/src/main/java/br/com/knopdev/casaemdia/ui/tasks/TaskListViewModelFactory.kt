package br.com.knopdev.casaemdia.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.knopdev.casaemdia.data.repository.TaskRepository

class TaskListViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            return TaskListViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}