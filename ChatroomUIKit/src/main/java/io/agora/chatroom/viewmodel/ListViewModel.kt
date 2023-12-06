package io.agora.chatroom.viewmodel

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class RequestState {
    object Idle : RequestState()
    object Loading : RequestState()
    object LoadingMore : RequestState()
    data class Success<T>(val data: List<T> = emptyList()) : RequestState()

    data class SuccessMore<T>(val data: List<T>) : RequestState()

    object Refresh: RequestState()
    data class Error(val errorCode: Int, val message: String?) : RequestState()
}


open class ListViewModel<T>(
   private val data: List<T> = emptyList(),
): ViewModel() {

    private val _data = data.toMutableStateList()

    /**
     * Add data to the list.
     */
    fun addData(data: T): List<T>{
        _data.add(data)
        return _data
    }

    /**
     * Add data to the list.
     */
    fun addData(data: List<T>): List<T>{
        _data.addAll(data)
        return _data
    }

    /**
     * Remove data from the list.
     */
    fun removeData(data: T): List<T> {
        if (_data.contains(data)){
            _data.remove(data)
        }
        return _data
    }

    /**
     * Clear all data from the list.
     */
    fun clearData(){
        _data.clear()
    }

    /**
     * Set data to the list.
     */
    fun setData(data: List<T>): List<T>{
        _data.clear()
        _data.addAll(data)
        return _data
    }

    val getData: List<T>
        get() = _data

}

open class RequestListViewModel<T>(
   private val state: RequestState = RequestState.Idle,
    private val atLeastShowingTime: Long = 1000L
): ComposeBaseListViewModel<T>() {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    init {
        if (state is RequestState.Success<*>){
            addDataList(state.data as List<T>)
        }
    }
    private val _state = mutableStateOf(state)
    private val _startLoadMoreTime = mutableLongStateOf(0L)

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
    val getState: RequestState
        get() = _state.value

    fun add(data: List<T>){
        addDataList(data)
        _state.value = RequestState.Success(items)
    }

    fun addMore(list: List<T>){
        if (getCurrentTime() - _startLoadMoreTime.longValue < atLeastShowingTime){
            scope.launch {
                val duration = _startLoadMoreTime.longValue + atLeastShowingTime - getCurrentTime()
                delay(if (duration > 0) duration else 0)
                addDataList(list)
                _state.value = RequestState.SuccessMore(items)
            }
        } else {
            addDataList(list)
            _state.value = RequestState.SuccessMore(items)
        }
    }

    fun error(code: Int, message: String?) {
        _state.value = RequestState.Error(code, message)
    }

    fun loading(){
        _state.value = RequestState.Loading
    }

    fun loadMore() {
        _state.value = RequestState.LoadingMore
        _startLoadMoreTime.longValue = System.currentTimeMillis()
    }

    fun refresh(){
        _state.value = RequestState.Refresh
    }

    private fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }
}


