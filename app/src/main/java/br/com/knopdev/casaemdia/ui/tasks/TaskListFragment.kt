package br.com.knopdev.casaemdia.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.knopdev.casaemdia.CasaEmDiaApplication
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskFilter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private val viewModel: TaskListViewModel by activityViewModels {
        val app = requireActivity().application as CasaEmDiaApplication
        TaskListViewModelFactory(app.taskUseCases())
    }

    private val taskAdapter = TaskAdapter(
        onCompletionChanged = { task, completed ->
            viewModel.updateTaskCompletion(task.id, completed)
        },
        onEdit = ::openTask,
        onDelete = ::confirmDelete
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.task_toolbar)
        val addButton = view.findViewById<ExtendedFloatingActionButton>(R.id.add_task_button)
        val recyclerView = view.findViewById<RecyclerView>(R.id.task_recycler_view)
        val searchInput = view.findViewById<TextInputEditText>(R.id.task_search_input)
        val filterGroup = view.findViewById<ChipGroup>(R.id.task_filter_group)

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_suggestions -> findNavController().navigate(R.id.action_taskListFragment_to_suggestionsFragment)
                R.id.action_about -> findNavController().navigate(R.id.action_taskListFragment_to_aboutFragment)
                else -> return@setOnMenuItemClickListener false
            }
            true
        }
        addButton.setOnClickListener { openTask(null) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = taskAdapter
        searchInput.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }
        filterGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            viewModel.setFilter(
                when (checkedIds.firstOrNull()) {
                    R.id.filter_all_chip -> TaskFilter.ALL
                    R.id.filter_completed_chip -> TaskFilter.COMPLETED
                    else -> TaskFilter.PENDING
                }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state -> render(view, state) }
                }
                launch {
                    viewModel.events.collect { event -> handleEvent(view, event) }
                }
            }
        }
    }

    private fun render(view: View, state: TaskListUiState) {
        taskAdapter.submitList(state.tasks)
        view.findViewById<ProgressBar>(R.id.task_loading).isVisible = state.isLoading
        view.findViewById<RecyclerView>(R.id.task_recycler_view).isVisible = !state.isLoading && state.tasks.isNotEmpty()
        val emptyState = view.findViewById<View>(R.id.task_empty_state)
        emptyState.isVisible = !state.isLoading && state.tasks.isEmpty()
        view.findViewById<TextView>(R.id.task_summary).text = getString(
            R.string.task_summary,
            state.pendingCount,
            state.completedCount
        )
        if (emptyState.isVisible) {
            val isSearching = state.query.isNotBlank() || state.totalCount > 0
            view.findViewById<TextView>(R.id.task_empty_title).setText(
                if (isSearching) R.string.empty_search_title else R.string.empty_tasks_title
            )
            view.findViewById<TextView>(R.id.task_empty_message).setText(
                if (isSearching) R.string.empty_search_message else R.string.empty_tasks_message
            )
        }
    }

    private fun handleEvent(view: View, event: TaskEvent) {
        when (event) {
            TaskEvent.TaskSaved -> Unit
            is TaskEvent.Error -> Snackbar.make(view, event.message, Snackbar.LENGTH_LONG).show()
            is TaskEvent.TaskDeleted -> Snackbar.make(view, R.string.task_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) { viewModel.restoreTask(event.task) }
                .show()
        }
    }

    private fun openTask(task: Task?) {
        findNavController().navigate(
            R.id.action_taskListFragment_to_taskFormFragment,
            bundleOf(TaskFormFragment.ARG_TASK_ID to (task?.id ?: 0L))
        )
    }

    private fun confirmDelete(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_task_title)
            .setMessage(getString(R.string.delete_task_message, task.title))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteTask(task) }
            .show()
    }
}
