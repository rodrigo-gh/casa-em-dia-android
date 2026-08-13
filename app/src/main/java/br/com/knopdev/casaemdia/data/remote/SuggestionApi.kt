package br.com.knopdev.casaemdia.data.remote

import retrofit2.http.GET

interface SuggestionApi {

    @GET("rodrigo-gh/casa-em-dia-android/main/docs/suggestions.json")
    suspend fun getSuggestions(): List<SuggestionDto>
}
