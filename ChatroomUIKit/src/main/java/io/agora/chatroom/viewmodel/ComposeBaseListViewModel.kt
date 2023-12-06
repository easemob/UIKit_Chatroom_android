package io.agora.chatroom.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class RefreshState {
    object Idle : RefreshState()
    object Loading : RefreshState()
    object Success : RefreshState()
    object Fail : RefreshState()
}
sealed class LoadMoreState {
    object Idle : LoadMoreState()
    data class Loading(var lastIndex: Int) : LoadMoreState()
    object Success : LoadMoreState()
    object Fail : LoadMoreState()
}

open class ComposeBaseListViewModel<T>(
    private val contentList: List<T> = emptyList(),
    private val loadMoreState: LoadMoreState = LoadMoreState.Idle,
    private val refreshState: RefreshState = RefreshState.Idle,
): ViewModel(){
    private val _items: MutableList<T> = contentList.toMutableStateList()
    val items: List<T> = _items

    // Observe if enable refresh
    private var _enableRefresh = mutableStateOf(false)
    // Observe if enable load more
    private var _enableLoadMore = mutableStateOf(false)

    // Observe the loading more state
    private val _loadingMoreState = mutableStateOf(loadMoreState)
    // Observe the refresh state
    private val _refreshState = mutableStateOf(refreshState)

    private val _isAutoClear = mutableStateOf(false)
    val isAutoClear: State<Boolean> get() = _isAutoClear

    private val _autoClearTime = mutableLongStateOf(3000)
    val autoClearTime: State<Long> get() = _autoClearTime

    /**
     * Enable or disable refresh.
     */
    fun enableRefresh(enable: Boolean){
        _enableRefresh.value = enable
    }

    /**
     * Get whether refresh is enabled.
     */
    val isEnableRefresh: Boolean
        get() = _enableRefresh.value

    /**
     * Enable or disable load more.
     */
    fun enableLoadMore(enable: Boolean){
        _enableLoadMore.value = enable
    }

    /**
     * Get whether load more is enabled.
     */
    val isEnableLoadMore: Boolean
        get() = _enableLoadMore.value

    /**
     * Change the load more state.
     */
    fun changeLoadMoreState(state: LoadMoreState){
        _loadingMoreState.value = state
        if (state is LoadMoreState.Loading) {
            _refreshState.value = RefreshState.Idle
        }
    }

    /**
     * Change the refresh state.
     */
    fun changeRefreshState(state: RefreshState) {
        _refreshState.value = state
        if (state is RefreshState.Loading) {
            _loadingMoreState.value = LoadMoreState.Idle
        }
    }

    /**
     * Get the load more state.
     */
    val getLoadMoreState: LoadMoreState
        get() = _loadingMoreState.value

    /**
     * Get the refresh state.
     */
    val getRefreshState: RefreshState
        get() = _refreshState.value

    open fun addData(data: T) {
        _items.add(data)
    }

    open fun addDateToIndex(index:Int = 0,data: T){
        _items.add(index,data)
    }

    open fun addDataList(msgList:List<T>){
        _items.addAll(msgList)
    }

    open fun addDataListToIndex(index:Int = 0,msgList:List<T>){
        _items.addAll(index,msgList)
    }

    open fun removeData(data: T){
        if (_items.contains(data)  ){
            _items.remove(data)
        }
    }

    open fun clear(){
        _items.clear()
    }

    fun setAutoClearTime(time:Long){
        _autoClearTime.longValue = time
    }

    fun openAutoClear(){
        _isAutoClear.value = true
    }

    fun closeAutoClear(){
        _isAutoClear.value = false
    }

    /**
     * Notifies the UI of the calculated message offset to center it on the screen.
     */
    private val _focusedMessageOffset: MutableStateFlow<Int?> = MutableStateFlow(null)
    val focusedMessageOffset: StateFlow<Int?> = _focusedMessageOffset

    /**
     * Calculates the message offset needed for the message to center inside the list on scroll.
     *
     * @param parentSize The size of the list which contains the message.
     * @param focusedMessageSize The size of the message item we wish to bring to the center and focus.
     */
    public fun calculateMessageOffset(parentSize: IntSize, focusedMessageSize: IntSize) {
        val sizeDiff = parentSize.height - focusedMessageSize.height
        if (sizeDiff > 0) {
            _focusedMessageOffset.value = -sizeDiff / 2
        } else {
            _focusedMessageOffset.value = -sizeDiff
        }
    }
}