package io.agora.chatroom.viewmodel.tab

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

open class TabWithVpViewModel<T>(
    private val isDarkTheme: Boolean? = false,
    private val title:String = "",
    var contentList: List<T> = emptyList(),
): ViewModel(){

    private val _currentPage = mutableIntStateOf(0)
    val pageIndex : Int = _currentPage.intValue

    private val _currentTab = mutableIntStateOf(0)
    val tabIndex : Int = _currentTab.intValue

    private val _contentList: MutableList<T> = contentList.toMutableStateList()
    val list: List<T> = _contentList

    val getTheme: Boolean?
        get() = isDarkTheme

    fun setPageIndex(index: Int) {
        _currentPage.intValue = index
    }

    fun setTabIndex(index: Int){
        _currentTab.intValue = index
    }
}