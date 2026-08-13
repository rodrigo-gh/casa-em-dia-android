package br.com.knopdev.casaemdia.data.repository

import br.com.knopdev.casaemdia.data.local.dao.SuggestionDao
import br.com.knopdev.casaemdia.data.local.entity.SuggestionEntity
import br.com.knopdev.casaemdia.data.remote.SuggestionApi
import br.com.knopdev.casaemdia.model.Suggestion
import br.com.knopdev.casaemdia.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CachedSuggestionRepository(
    private val suggestionDao: SuggestionDao,
    private val suggestionApi: SuggestionApi,
    private val nowProvider: () -> Long = System::currentTimeMillis
) : SuggestionRepository {

    override val suggestions: Flow<List<Suggestion>> = suggestionDao.observeAll().map { entities ->
        entities.map { entity -> entity.toSuggestion() }
    }

    override suspend fun refresh(): Result<Unit> {
        return runCatching {
            val remoteSuggestions = suggestionApi.getSuggestions()
            require(remoteSuggestions.isNotEmpty()) { "A lista remota de sugestões está vazia" }

            suggestionDao.replaceAll(
                remoteSuggestions.map { dto ->
                    SuggestionEntity(
                        id = dto.id,
                        title = dto.title,
                        description = dto.description,
                        category = dto.category.toCategory().name,
                        updatedAtEpochMillis = nowProvider()
                    )
                }
            )
        }.onFailure {
            if (suggestionDao.count() == 0) {
                suggestionDao.insertAll(defaultSuggestions(nowProvider()))
            }
        }
    }

    private fun SuggestionEntity.toSuggestion() = Suggestion(
        id = id,
        title = title,
        description = description,
        category = category.toCategory()
    )

    private fun String.toCategory(): TaskCategory =
        runCatching { TaskCategory.valueOf(uppercase()) }.getOrDefault(TaskCategory.OTHER)

    private fun defaultSuggestions(updatedAt: Long) = listOf(
        SuggestionEntity("clean-fridge", "Limpar a geladeira", "Revise alimentos vencidos e higienize as prateleiras.", "CLEANING", updatedAt),
        SuggestionEntity("water-filter", "Trocar o filtro de água", "Confira a recomendação do fabricante e registre a próxima troca.", "MAINTENANCE", updatedAt),
        SuggestionEntity("energy-bill", "Conferir a conta de energia", "Compare o consumo com o mês anterior antes do pagamento.", "BILLS", updatedAt),
        SuggestionEntity("pantry", "Revisar a despensa", "Faça uma lista do que está acabando e priorize itens próximos do vencimento.", "SHOPPING", updatedAt),
        SuggestionEntity("washing-machine", "Limpar a máquina de lavar", "Execute o ciclo de limpeza recomendado pelo fabricante.", "MAINTENANCE", updatedAt),
        SuggestionEntity("bathroom", "Higienizar o banheiro", "Organize os produtos e faça uma limpeza completa.", "CLEANING", updatedAt)
    )
}
