package br.com.knopdev.casaemdia

import android.app.Application
import br.com.knopdev.casaemdia.data.repository.NoOpTaskOperationTracker
import br.com.knopdev.casaemdia.data.repository.TaskOperationTracker
import br.com.knopdev.casaemdia.data.repository.TaskRepository
import br.com.knopdev.casaemdia.di.AppContainer
import br.com.knopdev.casaemdia.domain.usecase.TaskUseCases

class CasaEmDiaApplication : Application() {

    val container: AppContainer by lazy {
        AppContainer(this)
    }

    var taskOperationTracker: TaskOperationTracker
        get() = container.taskOperationTracker
        set(value) {
            container.taskOperationTracker = value
        }

    val taskRepository: TaskRepository
        get() = container.taskRepository

    fun taskUseCases(): TaskUseCases = container.taskUseCases()
}
