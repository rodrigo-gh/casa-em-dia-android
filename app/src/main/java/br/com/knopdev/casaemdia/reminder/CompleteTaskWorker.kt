package br.com.knopdev.casaemdia.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.knopdev.casaemdia.CasaEmDiaApplication

class CompleteTaskWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(TaskReminderWorker.KEY_TASK_ID, -1)
        if (taskId < 0) return Result.failure()

        return runCatching {
            val app = applicationContext as CasaEmDiaApplication
            app.taskUseCases().setTaskCompletion(taskId, true)
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}
