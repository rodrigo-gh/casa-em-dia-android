package br.com.knopdev.casaemdia.model

data class Task(
    val id: Long,
    val title: String,
    val dueDate: String,
    val completed: Boolean = false
)