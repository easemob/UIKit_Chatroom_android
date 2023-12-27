package io.agora.chatroom.compose.input

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import kotlinx.coroutines.delay

/**
 * Custom input field that we use for our UI.
 */
@Composable
fun InputField(
    value: String,
    modifier: Modifier = Modifier,
    isRequestFocus:Boolean = false,
    onValueChange: (String) -> Unit = {},
    textStyle: TextStyle = ChatroomUIKitTheme.typography.bodyLarge,
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    border: BorderStroke = BorderStroke(1.dp, ChatroomUIKitTheme.colors.inputSurface),
    innerPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    decorationBox: @Composable ((innerTextField: @Composable () -> Unit) -> Unit)? = null,
) {
    val requestFocus by rememberSaveable { mutableStateOf(isRequestFocus) }
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

    val focusManager = LocalFocusManager.current

    val keyboard = LocalSoftwareKeyboardController.current

    val focus = remember {
        FocusRequester()
    }

    // Workaround to move cursor to the end after selecting a suggestion
    val selection = if (textFieldValueState.isCursorAtTheEnd()) {
        TextRange(value.length)
    } else {
        textFieldValueState.selection
    }

    var textFieldValue = textFieldValueState.copy(
        text = value,
        selection = selection
    )

    val description = stringResource(id = R.string.compose_description_input_field)

    BasicTextField(
        modifier = modifier
            .focusRequester(focus)
            .border(border = border, shape = ChatroomUIKitTheme.shapes.large)
            .clip(ChatroomUIKitTheme.shapes.large)
            .background(ChatroomUIKitTheme.colors.inputSurface)
            .padding(innerPadding)
            .semantics { contentDescription = description },
        value = textFieldValue,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        textStyle = textStyle,
        cursorBrush = SolidColor(ChatroomUIKitTheme.colors.primary),
        decorationBox = { innerTextField ->
            if (decorationBox == null) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {

                    leadingIcon?.let { it.invoke() }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                        placeholder?.let { it.invoke() }
                    }

                    trailingIcon?.let { it.invoke() }

                }
            } else {
                decorationBox.invoke(innerTextField)
            }
                        },
        maxLines = maxLines,
        singleLine = maxLines == 1,
        enabled = enabled,
        visualTransformation= visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
    )

    LaunchedEffect(requestFocus) {
        if (requestFocus){
            delay(100)
            focus.requestFocus()
            keyboard?.show()
        }else{
            delay(100)
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }
}

/**
 * Custom search input field.
 */
@Composable
fun SearchInputFiled(
    value: String,
    modifier: Modifier = Modifier,
    isRequestFocus:Boolean = false,
    onValueChange: (String) -> Unit = {},
    textStyle: TextStyle = ChatroomUIKitTheme.typography.bodyLarge.copy(
        color = ChatroomUIKitTheme.colors.onBackground
    ),
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    border: BorderStroke = BorderStroke(1.dp, ChatroomUIKitTheme.colors.inputSurface),
    innerPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    onClearClick: () -> Unit = {},
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var textValue by rememberSaveable { mutableStateOf(value) }
    InputField(
        value = textValue,
        modifier = modifier,
        isRequestFocus = isRequestFocus,
        onValueChange = {newValue ->
            textValue = newValue
            onValueChange.invoke(newValue)
        },
        enabled = enabled,
        maxLines = maxLines,
        textStyle = textStyle,
        border = border,
        innerPadding = innerPadding,
        keyboardOptions = keyboardOptions,
        placeholder = {
            if (placeholder == null) {
                if (textValue.isBlank()) {
                    Text(text = stringResource(id = R.string.search),
                        color = ChatroomUIKitTheme.colors.inputHint,
                        style = ChatroomUIKitTheme.typography.bodyLarge)
                }
            } else {
                placeholder.invoke()
            }
        },
        leadingIcon = {
            if (leadingIcon == null) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_magnifier),
                    contentDescription = "Search magnifier",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            } else {
                leadingIcon.invoke()
            }
        },
        trailingIcon = {
            if (trailingIcon == null) {
                if (textValue.isNotBlank()) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_clear),
                        contentDescription = "Search magnifier",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ){
                                textValue = ""
                                onClearClick.invoke()
                            }
                    )
                }
            } else {
                trailingIcon.invoke()
            }
        },
    )
}

/**
 * Check if the [TextFieldValue] state represents a UI with the cursor at the end of the input.
 *
 * @return True if the cursor is at the end of the input.
 */
private fun TextFieldValue.isCursorAtTheEnd(): Boolean {
    val textLength = text.length
    val selectionStart = selection.start
    val selectionEnd = selection.end

    return textLength == selectionStart && textLength == selectionEnd
}
