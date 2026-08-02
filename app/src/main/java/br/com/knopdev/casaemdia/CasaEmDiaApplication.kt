package br.com.knopdev.casaemdia

import android.app.Application
import br.com.knopdev.casaemdia.data.local.CasaEmDiaDatabase
import br.com.knopdev.casaemdia.data.repository.TaskRepository

class CasaEmDiaApplication : Application() {

    private val database by lazy {
        CasaEmDiaDatabase.getInstance(this)
    }

    val taskRepository by lazy {
        TaskRepository(database.taskDao())
    }
}