package br.com.knopdev.casaemdia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insert(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateCompletion(
        taskId: Long,
        isCompleted: Boolean
    )
}