package br.com.knopdev.casaemdia.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CasaEmDiaMigrationTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @After
    fun deleteDatabase() {
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migrate1To2_preservaTituloConclusaoEPrazoLegado() = runBlocking {
        context.openOrCreateDatabase(TEST_DB, 0, null).apply {
            execSQL(
                "CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, dueDate TEXT NOT NULL, isCompleted INTEGER NOT NULL)"
            )
            execSQL(
                "INSERT INTO tasks (id, title, dueDate, isCompleted) VALUES (1, 'Trocar filtro', 'Sábado', 1)"
            )
            version = 1
            close()
        }

        val database = Room.databaseBuilder(context, CasaEmDiaDatabase::class.java, TEST_DB)
            .addMigrations(CasaEmDiaDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()

        val task = database.taskDao().getById(1)
        database.close()

        requireNotNull(task)
        assertEquals("Trocar filtro", task.title)
        assertEquals("Prazo anterior: Sábado", task.notes)
        assertEquals(true, task.isCompleted)
        assertEquals("OTHER", task.category)
    }

    companion object {
        private const val TEST_DB = "migration-test"
    }
}
