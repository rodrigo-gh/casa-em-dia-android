package br.com.knopdev.casaemdia.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.knopdev.casaemdia.model.Task

class TaskListViewModel : ViewModel() {

    private val _tasks = MutableLiveData(
        listOf(
            Task(1, "Trocar filtro de água", "Vence amanhã"),
            Task(2, "Pagar conta de luz", "Vence em 3 dias"),
            Task(3, "Limpar geladeira", "Agendada para sábado"),
            Task(4, "Revisar máquina de lavar", "Vence em 10 dias")
        )
    )

    val tasks: LiveData<List<Task>> = _tasks

    fun addTask(title: String, dueDate: String) {
        val currentTasks = _tasks.value.orEmpty()

        val nextId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1

        val newTask = Task(
            id = nextId,
            title = title,
            dueDate = dueDate
        )

        _tasks.value = currentTasks + newTask
    }
}