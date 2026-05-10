package com.jsonquizzz.core.common

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable? = null, val message: String? = null) : Result<Nothing>
    data object Loading : Result<Nothing>
}

fun <T> Result<T>.successOr(fallback: T): T = when (this) {
    is Result.Success -> data
    else -> fallback
}
