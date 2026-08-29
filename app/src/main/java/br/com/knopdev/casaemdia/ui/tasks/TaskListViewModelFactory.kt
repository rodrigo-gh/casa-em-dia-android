package br.com.knopdev.casaemdia.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.knopdev.casaemdia.domain.usecase.TaskUseCases

class TaskListViewModelFactory(
    private val useCases: TaskUseCases
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TaskListViewModel::class.java))
        return TaskListViewModel(useCases) as T
    }
}
