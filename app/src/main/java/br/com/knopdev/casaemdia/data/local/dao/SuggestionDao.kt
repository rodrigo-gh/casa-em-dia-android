package br.com.knopdev.casaemdia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.knopdev.casaemdia.data.local.entity.SuggestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionDao {

    @Query("SELECT * FROM suggestions ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<SuggestionEntity>>

    @Query("SELECT COUNT(*) FROM suggestions")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suggestions: List<SuggestionEntity>)

    @Query("DELETE FROM suggestions")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(suggestions: List<SuggestionEntity>) {
        deleteAll()
        insertAll(suggestions)
    }
}
