package br.com.knopdev.casaemdia.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class TaskCompletionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(TaskReminderWorker.KEY_TASK_ID, -1)
        if (taskId < 0) return

        val request = OneTimeWorkRequestBuilder<CompleteTaskWorker>()
            .setInputData(Data.Builder().putLong(TaskReminderWorker.KEY_TASK_ID, taskId).build())
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
