package br.com.knopdev.casaemdia.data.repository

interface TaskOperationTracker {
    suspend fun <T> track(block: suspend () -> T): T
}

object NoOpTaskOperationTracker : TaskOperationTracker {
    override suspend fun <T> track(block: suspend () -> T): T = block()
}
