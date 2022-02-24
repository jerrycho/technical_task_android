package com.sliide.task.base


sealed class ViewState<out T> where T : Any? {
    object Clean : ViewState<Nothing>()
    object Normal : ViewState<Nothing>()
    object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    data class Failure(val errorAny: Any) : ViewState<Nothing>()
}