package io.helikon.subvt.data

sealed class DataRequestState<out T : Any> {
    data object Idle : DataRequestState<Nothing>()

    data object Loading : DataRequestState<Nothing>()

    data class Success<out T : Any>(val result: T) : DataRequestState<T>()

    data class Error(val error: Throwable?) : DataRequestState<Nothing>()
}
