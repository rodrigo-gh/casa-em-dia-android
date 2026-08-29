package br.com.knopdev.casaemdia.testing

import androidx.test.espresso.idling.CountingIdlingResource
import br.com.knopdev.casaemdia.data.repository.TaskOperationTracker

object EspressoTaskOperationTracker : TaskOperationTracker {

    val idlingResource = CountingIdlingResource("TaskRepository")

    override suspend fun <T> track(block: suspend () -> T): T {
        idlingResource.increment()

        return try {
            block()
        } finally {
            idlingResource.decrement()
        }
    }
}
