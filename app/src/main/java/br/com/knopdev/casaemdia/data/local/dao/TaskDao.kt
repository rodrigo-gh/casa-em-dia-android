package br.com.knopdev.casaemdia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.knopdev.casaemdia.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, CASE WHEN dueAtEpochMillis IS NULL THEN 1 ELSE 0 END, dueAtEpochMillis ASC, createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity): Long

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: Long)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, completedAtEpochMillis = :completedAtEpochMillis, updatedAtEpochMillis = :updatedAtEpochMillis WHERE id = :taskId")
    suspend fun updateCompletion(
        taskId: Long,
        isCompleted: Boolean,
        completedAtEpochMillis: Long?,
        updatedAtEpochMillis: Long
    )
}
