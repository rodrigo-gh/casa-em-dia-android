package br.com.knopdev.casaemdia.ui.tasks

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import br.com.knopdev.casaemdia.CasaEmDiaApplication
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.model.TaskCategory
import br.com.knopdev.casaemdia.model.TaskInput
import br.com.knopdev.casaemdia.ui.common.displayName
import br.com.knopdev.casaemdia.ui.common.formatDueAt
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TaskFormFragment : Fragment(R.layout.fragment_task_form) {

    private val viewModel: TaskListViewModel by activityViewModels {
        val app = requireActivity().application as CasaEmDiaApplication
        TaskListViewModelFactory(app.taskUseCases())
    }

    private val taskId: Long by lazy { arguments?.getLong(ARG_TASK_ID) ?: 0L }
    private var dueAtEpochMillis: Long? = null
    private var didPopulate = false
    private lateinit var reminderSwitch: MaterialSwitch

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted && view != null) {
            Snackbar.make(requireView(), R.string.notification_permission_denied, Snackbar.LENGTH_LONG).show()
        }
        submitForm()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.task_form_toolbar)
        val dueDateButton = view.findViewById<MaterialButton>(R.id.task_due_date_button)
        val clearDueDateButton = view.findViewById<MaterialButton>(R.id.task_clear_due_date_button)
        val categoryInput = view.findViewById<MaterialAutoCompleteTextView>(R.id.task_category_input)
        val saveButton = view.findViewById<MaterialButton>(R.id.save_task_button)
        reminderSwitch = view.findViewById(R.id.task_reminder_switch)

        toolbar.setTitle(if (taskId > 0) R.string.edit_task else R.string.new_task)
        toolbar.setNavigationContentDescription(R.string.back)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        val categories = TaskCategory.entries.map { it.displayName(requireContext()) }
        categoryInput.setSimpleItems(categories.toTypedArray())
        val suggestedCategory = arguments?.getString(ARG_SUGGESTION_CATEGORY)
            ?.let { runCatching { TaskCategory.valueOf(it) }.getOrNull() }
            ?: TaskCategory.OTHER
        categoryInput.setText(suggestedCategory.displayName(requireContext()), false)
        if (taskId == 0L) {
            view.findViewById<TextInputEditText>(R.id.task_title_input)
                .setText(arguments?.getString(ARG_SUGGESTION_TITLE).orEmpty())
            view.findViewById<TextInputEditText>(R.id.task_notes_input)
                .setText(arguments?.getString(ARG_SUGGESTION_NOTES).orEmpty())
        }

        dueDateButton.setOnClickListener { showDateTimePicker() }
        clearDueDateButton.setOnClickListener {
            dueAtEpochMillis = null
            updateDueDateControls()
        }
        reminderSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked && dueAtEpochMillis == null) showDateTimePicker()
        }
        saveButton.setOnClickListener { validateAndRequestPermission() }

        viewModel.loadTask(taskId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.editingTask.collect { task ->
                        if (taskId > 0 && task != null && !didPopulate) populateForm(task)
                    }
                }
                launch {
                    viewModel.isSaving.collect { saving ->
                        saveButton.isEnabled = !saving
                        saveButton.setText(if (saving) R.string.saving_task else R.string.save_task)
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            TaskEvent.TaskSaved -> findNavController().navigateUp()
                            is TaskEvent.Error -> Snackbar.make(view, event.message, Snackbar.LENGTH_LONG).show()
                            is TaskEvent.TaskDeleted -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun populateForm(task: Task) {
        didPopulate = true
        dueAtEpochMillis = task.dueAtEpochMillis
        requireView().findViewById<TextInputEditText>(R.id.task_title_input).setText(task.title)
        requireView().findViewById<TextInputEditText>(R.id.task_notes_input).setText(task.notes)
        requireView().findViewById<MaterialAutoCompleteTextView>(R.id.task_category_input)
            .setText(task.category.displayName(requireContext()), false)
        reminderSwitch.isChecked = task.reminderEnabled
        updateDueDateControls()
    }

    private fun validateAndRequestPermission() {
        val title = requireView().findViewById<TextInputEditText>(R.id.task_title_input)
            .text?.toString()?.trim().orEmpty()
        val titleLayout = requireView().findViewById<TextInputLayout>(R.id.task_title_layout)
        if (title.isBlank()) {
            titleLayout.error = getString(R.string.task_title_required)
            titleLayout.requestFocus()
            titleLayout.announceForAccessibility(titleLayout.error)
            return
        }
        titleLayout.error = null

        if (reminderSwitch.isChecked && Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            submitForm()
        }
    }

    private fun submitForm() {
        val root = requireView()
        val categoryText = root.findViewById<MaterialAutoCompleteTextView>(R.id.task_category_input).text.toString()
        val selectedCategory = TaskCategory.entries.firstOrNull {
            it.displayName(requireContext()) == categoryText
        } ?: TaskCategory.OTHER
        viewModel.saveTask(
            input = TaskInput(
                title = root.findViewById<TextInputEditText>(R.id.task_title_input).text?.toString().orEmpty(),
                notes = root.findViewById<TextInputEditText>(R.id.task_notes_input).text?.toString().orEmpty(),
                dueAtEpochMillis = dueAtEpochMillis,
                category = selectedCategory,
                reminderEnabled = reminderSwitch.isChecked
            ),
            taskId = taskId
        )
    }

    private fun showDateTimePicker() {
        val zone = ZoneId.systemDefault()
        val initial = dueAtEpochMillis?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDateTime() }
            ?: LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0)
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        dueAtEpochMillis = LocalDateTime.of(year, month + 1, day, hour, minute)
                            .atZone(zone)
                            .toInstant()
                            .toEpochMilli()
                        updateDueDateControls()
                    },
                    initial.hour,
                    initial.minute,
                    true
                ).show()
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1_000
        }.show()
    }

    private fun updateDueDateControls() {
        val root = view ?: return
        root.findViewById<MaterialButton>(R.id.task_due_date_button).text =
            if (dueAtEpochMillis == null) getString(R.string.choose_due_date)
            else formatDueAt(requireContext(), dueAtEpochMillis)
        root.findViewById<MaterialButton>(R.id.task_clear_due_date_button).isVisible = dueAtEpochMillis != null
        reminderSwitch.isEnabled = dueAtEpochMillis != null
        if (dueAtEpochMillis == null) reminderSwitch.isChecked = false
    }

    companion object {
        const val ARG_TASK_ID = "taskId"
        const val ARG_SUGGESTION_TITLE = "suggestionTitle"
        const val ARG_SUGGESTION_NOTES = "suggestionNotes"
        const val ARG_SUGGESTION_CATEGORY = "suggestionCategory"
    }
}
