package br.com.knopdev.casaemdia.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.model.Task

class TaskAdapter(
    private val onCompletionChanged: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view, onCompletionChanged)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(
        itemView: View,
        private val onCompletionChanged: (Task, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val completedCheckBox: CheckBox =
            itemView.findViewById(R.id.task_completed_checkbox)

        private val titleTextView: TextView =
            itemView.findViewById(R.id.task_title)

        private val dueDateTextView: TextView =
            itemView.findViewById(R.id.task_due_date)

        fun bind(task: Task) {
            completedCheckBox.setOnCheckedChangeListener(null)
            completedCheckBox.isChecked = task.completed

            titleTextView.text = task.title
            dueDateTextView.text = task.dueDate
            itemView.alpha = if (task.completed) 0.5f else 1f

            completedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != task.completed) {
                    onCompletionChanged(task, isChecked)
                }
            }
        }
    }

    private object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}