package com.example.ethwallet.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

abstract class NoResultUseCase<in P>(private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(params: P) {
        return withContext(dispatcher) {
            doWork(params)
        }
    }

    suspend fun executeSync(params: P) = doWork(params)

    protected abstract suspend fun doWork(params: P)
}

abstract class ResultUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(params: P): R = withContext(dispatcher) { doWork(params) }

    suspend fun executeSync(params: P): R = doWork(params)

    protected abstract suspend fun doWork(params: P): R
}

abstract class SubjectUseCase<in P, out T>(private val dispatcher: CoroutineDispatcher) {
    // Ideally this would be buffer = 0, since we use flatMapLatest below, BUT invoke is not
    // suspending. This means that we can't suspend while flatMapLatest cancels any
    // existing flows. The buffer of 1 means that we can use tryEmit() and buffer the value
    // instead, resulting in mostly the same result.
    private val paramState = MutableSharedFlow<P>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val flow: Flow<T> = paramState
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it).flowOn(dispatcher) }
        .distinctUntilChanged()

    operator fun invoke(params: P) {
        paramState.tryEmit(params)
    }

    protected abstract suspend fun createObservable(params: P): Flow<T>
}
