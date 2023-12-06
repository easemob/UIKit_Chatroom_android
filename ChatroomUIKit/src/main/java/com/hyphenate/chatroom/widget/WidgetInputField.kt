package com.hyphenate.chatroom.widget

import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.uikit.R
import com.hyphenate.chatroom.viewmodel.messages.MessageChatBarViewModel
import kotlinx.coroutines.delay


/**
 * Custom input field that we use for our UI. It's fairly simple - shows a basic input with clipped
 * corners and a border stroke, with some extra padding on each side.
 *
 * Within it, we allow for custom decoration, so that the user can define what the input field looks like
 * when filled with content.
 *
 * @param value The current input value.
 * @param onValueChange Handler when the value changes as the user types.
 * @param modifier Modifier for styling.
 * @param enabled If the Composable is enabled for text input or not.
 * @param maxLines The number of lines that are allowed in the input, no limit by default.
 * @param border The [BorderStroke] that will appear around the input field.
 */
@Composable
fun WidgetInputField(
    onValueChange: (String) -> Unit,
    onKeyDown:(String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    viewModel: MessageChatBarViewModel,
    maxLines: Int = Int.MAX_VALUE,
    hint:String = stringResource(id = R.string.stream_compose_message_label),
    border: BorderStroke = BorderStroke(1.dp, ChatroomUIKitTheme.colors.neutralL95D20),
) {
    val isNeedClear = viewModel.isClear
    val emoji = viewModel.emoji
    val isInsertEmoji = viewModel.isInsertEmoji
    val textColor = ChatroomUIKitTheme.colors.onBackground
    val hintColor = ChatroomUIKitTheme.colors.inputHint
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    // Create a soft keyboard controller
    val keyboard = LocalSoftwareKeyboardController.current

    // Create a focus requester
    val focus = remember {
        FocusRequester()
    }

    val editText = remember { EditText(context) }
    AndroidView(
        factory = { context ->
            editText.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.gravity = Gravity.CENTER_VERTICAL
                this.setPadding(30,0,12,0)
                inputType = InputType.TYPE_CLASS_TEXT
                imeOptions = EditorInfo.IME_ACTION_DONE
                this.maxLines = maxLines
                this.isEnabled = enabled
                this.background = null
                this.hint = hint
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    this.textCursorDrawable = context.getDrawable(R.drawable.text_cursor_color)
                }
                this.setHintTextColor(hintColor.toArgb())
                this.textSize = 20f
                this.setTextColor(textColor.toArgb())
                this.layoutParams = layoutParams

                setOnEditorActionListener(object : TextView.OnEditorActionListener{
                    override fun onEditorAction(
                        v: TextView?,
                        actionId: Int,
                        event: KeyEvent?
                    ): Boolean {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            val text = text.toString()
                            onKeyDown(text)
                            return true
                        }
                        return false
                    }
                })

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        onValueChange(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            }
        },
        update = { editText ->
            editText.requestFocus()
            if (isNeedClear){
                editText.setText("")
            }else{
                if (emoji.isNotEmpty() && isInsertEmoji){
                    editText.append(emoji)
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focus)
            .focusModifier()
            .background(ChatroomUIKitTheme.colors.neutralL95D20, ChatroomUIKitTheme.shapes.large)
            .border(border = border, shape = ChatroomUIKitTheme.shapes.large)
    )

    LaunchedEffect(viewModel.isShowKeyboard.value) {
        if (viewModel.isShowKeyboard.value){
            delay(50)
            focus.requestFocus()
            keyboard?.show()
        }else{
            delay(50)
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }

    LaunchedEffect(viewModel.isClickDelete.value) {
        if (viewModel.isClickDelete.value){
            if (!TextUtils.isEmpty(editText.text)) {
                val event = KeyEvent(
                    0,
                    0,
                    0,
                    KeyEvent.KEYCODE_DEL,
                    0,
                    0,
                    0,
                    0,
                    KeyEvent.KEYCODE_ENDCALL
                )
                editText.dispatchKeyEvent(event)
            }
            viewModel.isClickDelete.value = false
        }
    }

}
