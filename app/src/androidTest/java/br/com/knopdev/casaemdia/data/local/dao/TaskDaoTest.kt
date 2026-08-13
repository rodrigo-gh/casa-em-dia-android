package br.com.knopdev.casaemdia.data.local.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.knopdev.casaemdia.data.local.CasaEmDiaDatabase
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: CasaEmDiaDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            CasaEmDiaDatabase::class.java
        ).allowMainThreadQueries().build()

        taskDao = database.taskDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insert_salvaTarefaNoBanco() = runBlocking {
        taskDao.upsert(
            TaskEntity(
                title = "Limpar varanda",
                notes = "",
                dueAtEpochMillis = 3_000,
                category = "CLEANING",
                isCompleted = false,
                reminderEnabled = false,
                createdAtEpochMillis = 1_000,
                updatedAtEpochMillis = 1_000,
                completedAtEpochMillis = null
            )
        )

        val tasks = taskDao.observeAll().first()

        assertEquals(1, tasks.size)
        assertEquals("Limpar varanda", tasks.first().title)
    }

    @Test
    fun updateCompletion_atualizaStatusDaTarefa() = runBlocking {
        taskDao.upsert(
            TaskEntity(
                title = "Pagar internet",
                notes = "",
                dueAtEpochMillis = 3_000,
                category = "BILLS",
                isCompleted = false,
                reminderEnabled = false,
                createdAtEpochMillis = 1_000,
                updatedAtEpochMillis = 1_000,
                completedAtEpochMillis = null
            )
        )

        val createdTask = taskDao.observeAll().first().single()

        taskDao.updateCompletion(
            taskId = createdTask.id,
            isCompleted = true,
            completedAtEpochMillis = 2_000,
            updatedAtEpochMillis = 2_000
        )

        val updatedTask = taskDao.observeAll().first().single()

        assertTrue(updatedTask.isCompleted)
    }
}
