package com.hyphenate.chatroom.viewmodel.pager

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

open class PagerViewModel(
    val tabList: List<TabInfo> = emptyList(),
    var contentList: List<Any> = emptyList(),
): ViewModel(){

    private var pageIndex : Int = 0

    private val _contentList: MutableList<Any> = contentList.toMutableStateList()
    val list: List<Any> = _contentList

    fun setPageIndex(index: Int) {
        pageIndex = index
    }

    fun currentPageIndex(): Int{
        return pageIndex
    }
}

data class TabInfo(
    val title: String,
    val icon: Any? = null,
)