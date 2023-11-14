package io.helikon.subvt.data

sealed class DataRequestState
object Idle: DataRequestState()
object Loading: DataRequestState()
data class Success<T>(val result: T): DataRequestState()
data class Error(val error: Throwable?): DataRequestState()
