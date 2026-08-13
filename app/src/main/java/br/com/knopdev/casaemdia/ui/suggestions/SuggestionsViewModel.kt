package br.com.knopdev.casaemdia.ui.suggestions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.knopdev.casaemdia.data.repository.SuggestionRepository
import br.com.knopdev.casaemdia.model.Suggestion
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SuggestionsUiState(
    val isLoading: Boolean = true,
    val suggestions: List<Suggestion> = emptyList()
)

class SuggestionsViewModel(
    private val repository: SuggestionRepository
) : ViewModel() {

    private val loading = MutableStateFlow(true)
    private val messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events = messages.asSharedFlow()

    val uiState: StateFlow<SuggestionsUiState> = combine(repository.suggestions, loading) { items, isLoading ->
        SuggestionsUiState(isLoading, items)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SuggestionsUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            loading.value = true
            repository.refresh().onFailure { messages.emit("Sem conexão. Mostrando sugestões salvas.") }
            loading.value = false
        }
    }
}

class SuggestionsViewModelFactory(
    private val repository: SuggestionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SuggestionsViewModel::class.java))
        return SuggestionsViewModel(repository) as T
    }
}
