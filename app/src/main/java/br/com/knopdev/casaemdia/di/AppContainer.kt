package br.com.knopdev.casaemdia.di

import android.content.Context
import br.com.knopdev.casaemdia.data.local.CasaEmDiaDatabase
import br.com.knopdev.casaemdia.data.remote.SuggestionApi
import br.com.knopdev.casaemdia.data.repository.CachedSuggestionRepository
import br.com.knopdev.casaemdia.data.repository.NoOpTaskOperationTracker
import br.com.knopdev.casaemdia.data.repository.RoomTaskRepository
import br.com.knopdev.casaemdia.data.repository.SuggestionRepository
import br.com.knopdev.casaemdia.data.repository.TaskOperationTracker
import br.com.knopdev.casaemdia.data.repository.TaskRepository
import br.com.knopdev.casaemdia.domain.reminder.TaskReminderScheduler
import br.com.knopdev.casaemdia.domain.usecase.DeleteTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.GetTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.ObserveTasksUseCase
import br.com.knopdev.casaemdia.domain.usecase.RestoreTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.SaveTaskUseCase
import br.com.knopdev.casaemdia.domain.usecase.SetTaskCompletionUseCase
import br.com.knopdev.casaemdia.domain.usecase.TaskUseCases
import br.com.knopdev.casaemdia.reminder.WorkManagerTaskReminderScheduler
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    private val appContext = context.applicationContext
    private val database: CasaEmDiaDatabase by lazy {
        CasaEmDiaDatabase.getInstance(appContext)
    }

    var taskOperationTracker: TaskOperationTracker = NoOpTaskOperationTracker

    val reminderScheduler: TaskReminderScheduler by lazy {
        WorkManagerTaskReminderScheduler(appContext)
    }

    val taskRepository: TaskRepository
        get() = RoomTaskRepository(
            taskDao = database.taskDao(),
            operationTracker = taskOperationTracker
        )

    val suggestionRepository: SuggestionRepository by lazy {
        CachedSuggestionRepository(
            suggestionDao = database.suggestionDao(),
            suggestionApi = createSuggestionApi()
        )
    }

    fun taskUseCases(): TaskUseCases {
        val repository = taskRepository
        return TaskUseCases(
            observeTasks = ObserveTasksUseCase(repository),
            getTask = GetTaskUseCase(repository),
            saveTask = SaveTaskUseCase(repository, reminderScheduler),
            deleteTask = DeleteTaskUseCase(repository, reminderScheduler),
            restoreTask = RestoreTaskUseCase(repository, reminderScheduler),
            setTaskCompletion = SetTaskCompletionUseCase(repository, reminderScheduler)
        )
    }

    private fun createSuggestionApi(): SuggestionApi {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .header("User-Agent", "CasaEmDia-Android")
                    .build()
                chain.proceed(request)
            }
            .build()
        val moshi = Moshi.Builder().build()

        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SuggestionApi::class.java)
    }
}
