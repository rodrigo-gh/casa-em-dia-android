package br.com.knopdev.casaemdia

import android.app.Application
import br.com.knopdev.casaemdia.data.local.CasaEmDiaDatabase
import br.com.knopdev.casaemdia.data.repository.RoomTaskRepository
import br.com.knopdev.casaemdia.data.repository.TaskRepository

class CasaEmDiaApplication : Application() {

    private val database by lazy {
        CasaEmDiaDatabase.getInstance(this)
    }

    val taskRepository: TaskRepository by lazy {
        RoomTaskRepository(database.taskDao())
    }
}