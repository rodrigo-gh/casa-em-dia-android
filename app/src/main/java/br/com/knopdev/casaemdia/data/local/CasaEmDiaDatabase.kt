package br.com.knopdev.casaemdia.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.knopdev.casaemdia.data.local.dao.TaskDao
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CasaEmDiaDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: CasaEmDiaDatabase? = null

        fun getInstance(context: Context): CasaEmDiaDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CasaEmDiaDatabase::class.java,
                    "casa_em_dia_database"
                ).build().also { instance = it }
            }
        }
    }
}