package br.com.knopdev.casaemdia.testing

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    val countingIdlingResource = CountingIdlingResource(
        "TaskRepository"
    )

    suspend fun <T> wrap(block: suspend () -> T): T {
        countingIdlingResource.increment()

        return try {
            block()
        } finally {
            countingIdlingResource.decrement()
        }
    }
}