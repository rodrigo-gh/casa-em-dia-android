package br.com.knopdev.casaemdia.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.knopdev.casaemdia.data.local.dao.SuggestionDao
import br.com.knopdev.casaemdia.data.local.dao.TaskDao
import br.com.knopdev.casaemdia.data.local.entity.SuggestionEntity
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, SuggestionEntity::class],
    version = 2,
    exportSchema = true
)
abstract class CasaEmDiaDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun suggestionDao(): SuggestionDao

    companion object {
        @Volatile
        private var instance: CasaEmDiaDatabase? = null

        fun getInstance(context: Context): CasaEmDiaDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CasaEmDiaDatabase::class.java,
                    "casa_em_dia_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tasks_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `dueAtEpochMillis` INTEGER,
                        `category` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `reminderEnabled` INTEGER NOT NULL,
                        `createdAtEpochMillis` INTEGER NOT NULL,
                        `updatedAtEpochMillis` INTEGER NOT NULL,
                        `completedAtEpochMillis` INTEGER
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `tasks_new` (
                        `id`, `title`, `notes`, `dueAtEpochMillis`, `category`,
                        `isCompleted`, `reminderEnabled`, `createdAtEpochMillis`,
                        `updatedAtEpochMillis`, `completedAtEpochMillis`
                    )
                    SELECT
                        `id`, `title`,
                        CASE WHEN `dueDate` = 'Sem prazo definido' THEN '' ELSE 'Prazo anterior: ' || `dueDate` END,
                        NULL, 'OTHER', `isCompleted`, 0, 0, 0,
                        CASE WHEN `isCompleted` = 1 THEN 0 ELSE NULL END
                    FROM `tasks`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `tasks`")
                db.execSQL("ALTER TABLE `tasks_new` RENAME TO `tasks`")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `suggestions` (
                        `id` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `updatedAtEpochMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
