package br.com.knopdev.casaemdia.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.knopdev.casaemdia.CasaEmDiaApplication
import br.com.knopdev.casaemdia.R

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private val viewModel: TaskListViewModel by activityViewModels {
        val app = requireActivity().application as CasaEmDiaApplication
        TaskListViewModelFactory(app.taskRepository)
    }

    private val taskAdapter = TaskAdapter { task, isCompleted ->
        viewModel.updateTaskCompletion(
            taskId = task.id,
            isCompleted = isCompleted
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.add_task_button).setOnClickListener {
            findNavController().navigate(
                R.id.action_taskListFragment_to_taskFormFragment
            )
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.task_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = taskAdapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }
}
