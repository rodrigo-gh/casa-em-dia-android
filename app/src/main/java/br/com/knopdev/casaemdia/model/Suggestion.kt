package br.com.knopdev.casaemdia.model

data class Suggestion(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory
)
