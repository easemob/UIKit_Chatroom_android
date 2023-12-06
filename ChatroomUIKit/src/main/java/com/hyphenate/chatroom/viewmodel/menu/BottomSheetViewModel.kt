package com.hyphenate.chatroom.viewmodel.menu

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.hyphenate.chatroom.uikit.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The view model of the bottom sheet.
 */
open class BottomSheetViewModel<T> (
    private val isDarkTheme: Boolean? = false,
    private val isShowTitle:Boolean = false,
    private val isShowCancel:Boolean = false,
    private val title:String = "",
    private val cancel:Int = R.string.compose_bottom_drawer_cancel,
    val isExpanded: Boolean = false,
    var contentList: List<T> = emptyList(),
): ViewModel() {

    /**
     * Control whether the bottom drawer can be drawn.
     */
    private val _enable = mutableStateOf(false)
    /**
     * Check whether the bottom drawer is visible.
     */
    private val _isVisible = mutableStateOf(false)

    private val _contentList: MutableList<T> = contentList.toMutableStateList()
    val list: List<T> = _contentList

    private val _show : MutableState<Boolean> = mutableStateOf(false)
    var isBottomSheetVisible = _show

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    val getTheme: Boolean?
        get() = isDarkTheme

    val getTitle: String
        get() = title

    val getCancelText: Int
        get() = cancel

    val getIsShowTitle: Boolean
        get() = isShowTitle

    val getIsShowCancel: Boolean
        get() = isShowCancel

    fun openDrawer(){
        _show.value = true
        _enable.value = true
    }

    fun closeDrawer(){
        _show.value = false
        _enable.value = false
    }

    fun openBottomDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    /**
     * Set the bottom drawer visible or not.
     */
    fun setVisible(isVisible: Boolean) {
        _isVisible.value = isVisible
    }

    /**
     * Check whether the bottom drawer is visible.
     */
    fun isVisible(): Boolean {
        return _isVisible.value
    }

    fun setEnable(enable: Boolean) {
        _enable.value = enable
    }

    fun isEnable(): Boolean {
        return _enable.value
    }

    fun add(list: List<T>) {
        _contentList.addAll(list)
    }

    fun clear() {
        _contentList.clear()
    }

}