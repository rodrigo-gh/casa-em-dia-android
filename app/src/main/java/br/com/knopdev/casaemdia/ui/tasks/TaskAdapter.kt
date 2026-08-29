package br.com.knopdev.casaemdia.ui.tasks

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.model.Task
import br.com.knopdev.casaemdia.ui.common.displayName
import br.com.knopdev.casaemdia.ui.common.formatDueAt
import br.com.knopdev.casaemdia.ui.common.isOverdue

class TaskAdapter(
    private val onCompletionChanged: (Task, Boolean) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, onCompletionChanged, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(
        itemView: View,
        private val onCompletionChanged: (Task, Boolean) -> Unit,
        private val onEdit: (Task) -> Unit,
        private val onDelete: (Task) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val completedCheckBox = itemView.findViewById<CheckBox>(R.id.task_completed_checkbox)
        private val titleTextView = itemView.findViewById<TextView>(R.id.task_title)
        private val dueDateTextView = itemView.findViewById<TextView>(R.id.task_due_date)
        private val categoryTextView = itemView.findViewById<TextView>(R.id.task_category)
        private val editButton = itemView.findViewById<ImageButton>(R.id.edit_task_button)
        private val deleteButton = itemView.findViewById<ImageButton>(R.id.delete_task_button)

        fun bind(task: Task) {
            val context = itemView.context
            completedCheckBox.setOnCheckedChangeListener(null)
            completedCheckBox.isChecked = task.completed
            completedCheckBox.contentDescription = context.getString(
                R.string.task_completion_description,
                task.title
            )
            titleTextView.text = task.title
            titleTextView.paintFlags = if (task.completed) {
                titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            dueDateTextView.text = formatDueAt(context, task.dueAtEpochMillis)
            dueDateTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isOverdue(task.dueAtEpochMillis, task.completed)) R.color.brand_error
                    else R.color.brand_outline
                )
            )
            categoryTextView.text = buildString {
                append(task.category.displayName(context))
                append(" · ")
                append(context.getString(if (task.completed) R.string.task_completed_state else R.string.task_pending_state))
            }
            editButton.contentDescription = context.getString(R.string.edit_task_description, task.title)
            deleteButton.contentDescription = context.getString(R.string.delete_task_description, task.title)
            itemView.contentDescription = "${task.title}. ${dueDateTextView.text}. ${categoryTextView.text}"

            completedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != task.completed) onCompletionChanged(task, isChecked)
            }
            editButton.setOnClickListener { onEdit(task) }
            itemView.setOnClickListener { onEdit(task) }
            deleteButton.setOnClickListener { onDelete(task) }
        }
    }

    private object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
