package io.agora.chatroom.viewmodel.messages

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.agora.chatroom.commons.ComposerChatBarController
import io.agora.chatroom.commons.ComposerInputMessageState
import io.agora.chatroom.model.UIChatBarMenuItem
import kotlinx.coroutines.flow.StateFlow

class MessageChatBarViewModel(
    private val isDarkTheme:Boolean?,
    private val emojiColumns:Int,
    private val composerChatBarController: ComposerChatBarController,
    private val menuItemResource: List<UIChatBarMenuItem>,
) : ViewModel(){

    private val _columns : MutableState<Int> = mutableIntStateOf(emojiColumns)
    var eColumns = _columns

    private val _showEmoji : MutableState<Boolean> = mutableStateOf(false)
    var isShowEmoji = _showEmoji

    private val _showKeyboard : MutableState<Boolean> = mutableStateOf(false)
    var isShowKeyboard = _showKeyboard

    fun showEmoji(){
        _showEmoji.value = true
    }

    fun hideEmoji(){
        _showEmoji.value = false
    }

    fun showKeyBoard(){
        _showKeyboard.value = true
    }

    fun hideKeyBoard(){
        _showKeyboard.value = false
    }

    /**
     * The full UI state that has all the required data.
     */
    val composerMessageState: StateFlow<ComposerInputMessageState> = composerChatBarController.state


    val getTheme: Boolean?
        get() = isDarkTheme

    val getMenuItem:List<UIChatBarMenuItem>
        get() = menuItemResource

    val isClear:Boolean
        get() = composerChatBarController.isNeedClear.value


    val emoji:CharSequence
        get() = composerChatBarController.emoji.value

    val isInsertEmoji:Boolean
        get() = composerChatBarController.isInsertEmoji.value

    /**
     * Called when the input changes and the internal state needs to be updated.
     *
     * @param value Current state value.
     */
    fun setMessageInput(value: String): Unit = composerChatBarController.setMessageInput(value)

    fun setEmojiInput(value: String): Unit = composerChatBarController.setEmojiInput(value)

    /**
     * Clears the input and the current state of the composer.
     */
    fun clearData(): Unit = composerChatBarController.clearData()


    /**
     * Disposes the inner [ComposerChatBarController].
     */
    override fun onCleared() {
        super.onCleared()
    }




}