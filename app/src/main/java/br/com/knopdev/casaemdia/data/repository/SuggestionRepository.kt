package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.model.Suggestion
import kotlinx.coroutines.flow.Flow

interface SuggestionRepository {
    val suggestions: Flow<List<Suggestion>>

    suspend fun refresh(): Result<Unit>
}
