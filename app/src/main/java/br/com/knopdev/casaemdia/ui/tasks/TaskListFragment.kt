package br.com.knopdev.casaemdia.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.model.Task

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasks = listOf(
            Task(1, "Trocar filtro de água", "Vence amanhã"),
            Task(2, "Pagar conta de luz", "Vence em 3 dias"),
            Task(3, "Limpar geladeira", "Agendada para sábado"),
            Task(4, "Revisar máquina de lavar", "Vence em 10 dias")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.task_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = TaskAdapter(tasks)
    }
}