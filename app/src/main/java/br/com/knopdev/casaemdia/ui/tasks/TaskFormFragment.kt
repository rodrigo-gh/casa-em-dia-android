package br.com.knopdev.casaemdia.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import br.com.knopdev.casaemdia.CasaEmDiaApplication
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.knopdev.casaemdia.R

class TaskFormFragment : Fragment(R.layout.fragment_task_form) {

    private val viewModel: TaskListViewModel by activityViewModels {
        val app = requireActivity().application as CasaEmDiaApplication
        TaskListViewModelFactory(app.taskRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleLayout = view.findViewById<TextInputLayout>(R.id.task_title_layout)
        val titleInput = view.findViewById<TextInputEditText>(R.id.task_title_input)
        val dueDateInput = view.findViewById<TextInputEditText>(R.id.task_due_date_input)
        val saveButton = view.findViewById<Button>(R.id.save_task_button)

        saveButton.setOnClickListener {
            val title = titleInput.text?.toString()?.trim().orEmpty()
            val dueDate = dueDateInput.text?.toString()?.trim()
                .orEmpty()
                .ifBlank { getString(R.string.no_due_date) }

            if (title.isBlank()) {
                titleLayout.error = getString(R.string.task_title_required)
                return@setOnClickListener
            }

            titleLayout.error = null
            viewModel.addTask(title, dueDate)

            findNavController().navigateUp()
        }
    }
}