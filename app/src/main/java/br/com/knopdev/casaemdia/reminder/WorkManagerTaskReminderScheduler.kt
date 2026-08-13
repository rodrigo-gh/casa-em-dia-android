package br.com.knopdev.casaemdia.reminder

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.com.knopdev.casaemdia.domain.reminder.TaskReminderScheduler
import br.com.knopdev.casaemdia.model.Task
import java.util.concurrent.TimeUnit

class WorkManagerTaskReminderScheduler(context: Context) : TaskReminderScheduler {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    override fun schedule(task: Task) {
        val dueAt = task.dueAtEpochMillis ?: return
        val delay = (dueAt - System.currentTimeMillis()).coerceAtLeast(0)
        val data = Data.Builder()
            .putLong(TaskReminderWorker.KEY_TASK_ID, task.id)
            .putString(TaskReminderWorker.KEY_TASK_TITLE, task.title)
            .build()
        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            workName(task.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    override fun cancel(taskId: Long) {
        workManager.cancelUniqueWork(workName(taskId))
    }

    private fun workName(taskId: Long) = "task-reminder-$taskId"
}
