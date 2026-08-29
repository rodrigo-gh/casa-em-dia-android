package br.com.knopdev.casaemdia.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SuggestionDto(
    val id: String,
    val title: String,
    val description: String,
    val category: String
)
