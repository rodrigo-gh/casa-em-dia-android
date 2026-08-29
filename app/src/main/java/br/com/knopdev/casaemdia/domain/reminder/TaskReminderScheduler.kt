package br.com.knopdev.casaemdia.domain.reminder

import br.com.knopdev.casaemdia.model.Task

interface TaskReminderScheduler {
    fun schedule(task: Task)

    fun cancel(taskId: Long)
}

object NoOpTaskReminderScheduler : TaskReminderScheduler {
    override fun schedule(task: Task) = Unit

    override fun cancel(taskId: Long) = Unit
}
