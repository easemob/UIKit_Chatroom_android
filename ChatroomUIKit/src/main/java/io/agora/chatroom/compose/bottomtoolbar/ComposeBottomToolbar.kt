package io.agora.chatroom.compose.bottomtoolbar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.agora.chatroom.commons.ComposerInputMessageState
import io.agora.chatroom.commons.UIValidationError
import io.agora.chatroom.compose.utils.DisplayUtils
import io.agora.chatroom.compose.utils.mirrorRtl
import io.agora.chatroom.data.emojiList
import io.agora.chatroom.model.UICapabilities
import io.agora.chatroom.model.UIChatBarMenuItem
import io.agora.chatroom.model.emoji.UIExpressionEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.uikit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Default ComposeChatBottomBar component that relies on [MessageChatBarViewModel] to handle data and
 * communicate various events.
 *
 * @param viewModel The ViewModel that provides pieces of data to show in the composer, like the
 * currently selected integration data or the user input. It also handles sending messages.
 * @param modifier Modifier for styling.
 * @param onSendMessage Handler when the user sends a message. By default it delegates this to the
 * ViewModel, but the user can override if they want more custom behavior.
 * @param onValueChange Handler when the input field value changes.
 * their own integrations, which they need to hook up to their own data providers and UI.
 * @param input Customizable composable that represents the input field for the composer, [ComposeMessageInput] by default.
 * by default.
 */
@Composable
fun ComposeBottomToolbar(
    viewModel: MessageChatBarViewModel,
    modifier: Modifier = Modifier,
    showInput: Boolean = false,
    onInputClick: () -> Unit = {},
    onMenuClick: (Int) -> Unit = {},
    menuItemResource: List<UIChatBarMenuItem> = viewModel.getMenuItem,
    onSendMessage: (String) -> Unit = { },
    onValueChange: (String) -> Unit = {
        viewModel.setMessageInput(it)
    },
    onKeyDown:(String) -> Unit = {
        onSendMessage(it)
    },
    input: @Composable RowScope.(ComposerInputMessageState) -> Unit = { it ->
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            composerMessageState = it,
            onValueChange = onValueChange,
            viewModel = viewModel,
            onKeyDown = {
                onSendMessage(it)
                viewModel.clearData()
            }
        )
    },
    trailingContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            value = it.inputValue,
            validationErrors = it.validationErrors,
            ownCapabilities = it.ownCapabilities,
            onSendMessage = { input ->
                onSendMessage(input)
                viewModel.clearData()
            }
        )
    },
    voiceContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (ComposerInputMessageState,onEmojiClick: (isShowFace:Boolean) -> Unit) -> Unit = { it,status->
        DefaultMessageComposerEmojiContent(
            viewModel = viewModel,
            onEmojiClick = {
                status(it)
            }
        )
    },
    defaultChatBar: @Composable () -> Unit = {
        DefaultChatBarComposerContent()
    },
    defaultChatBarMenu: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultChatBarMenuComposerContent(
            isDarkTheme = viewModel.getTheme,
            onMenuClick = onMenuClick,
            menuItemResource = menuItemResource,
            ownCapabilities = it.ownCapabilities,
        )
    }
) {
    val messageComposerState by viewModel.composerMessageState.collectAsState()

    ComposeBottomToolbar(
        viewModel = viewModel,
        isDarkTheme = viewModel.getTheme,
        modifier = modifier,
        onSendMessage = { text ->
            onSendMessage(text)
        },
        onKeyDown = {
            onSendMessage(it)
        },
        showInput = showInput,
        input = input,
        onMenuClick = onMenuClick,
        onInputClick = onInputClick,
        menuItemResource = menuItemResource,
        voiceContent = voiceContent,
        emojiContent = emojiContent,
        trailingContent = trailingContent,
        defaultChatBar = defaultChatBar,
        defaultChatBarMenu = defaultChatBarMenu,
        composerMessageState = messageComposerState,
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ComposeBottomToolbar(
    isDarkTheme: Boolean?,
    viewModel: MessageChatBarViewModel,
    composerMessageState: ComposerInputMessageState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    showInput: Boolean,
    onInputClick: () -> Unit,
    onMenuClick: (Int) -> Unit = {},
    menuItemResource: List<UIChatBarMenuItem>,
    onValueChange: (String) -> Unit = {},
    onKeyDown:(String) -> Unit = {},
    input: @Composable RowScope.(ComposerInputMessageState) -> Unit = { it ->
        @Suppress("DEPRECATION_ERROR")
        DefaultComposerInputContent(
            composerMessageState = composerMessageState,
            onValueChange = onValueChange,
            viewModel = viewModel,
            onKeyDown = onKeyDown
        )
    },
    trailingContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            value = it.inputValue,
            validationErrors = it.validationErrors,
            onSendMessage = onSendMessage,
            ownCapabilities = composerMessageState.ownCapabilities,
        )
    },
    voiceContent: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultMessageComposerVoiceContent(
            ownCapabilities = it.ownCapabilities,
            onVoiceClick = {}
        )
    },
    emojiContent: @Composable (ComposerInputMessageState, onEmojiClick: (isShowFace:Boolean) -> Unit) -> Unit = { it,status->
        DefaultMessageComposerEmojiContent(
            viewModel = viewModel,
            onEmojiClick = {
                status(it)
            }
        )
    },
    defaultChatBar: @Composable ( ) -> Unit = {
        DefaultChatBarComposerContent()
    },
    defaultChatBarMenu: @Composable (ComposerInputMessageState) -> Unit = {
        DefaultChatBarMenuComposerContent(
            isDarkTheme = isDarkTheme,
            onMenuClick = onMenuClick,
            menuItemResource = menuItemResource,
            ownCapabilities = it.ownCapabilities,
        )
    }
) {
    val (_,_,validationErrors) = composerMessageState
    val snackbarHostState = remember { SnackbarHostState() }

    MessageInputValidationError(
        validationErrors = validationErrors,
        snackbarHostState = snackbarHostState
    )

    val scope = CoroutineScope(Dispatchers.Default)
    val kh =  remember { mutableIntStateOf(-1) }
    val exKh by kh

    val exH =  remember { mutableIntStateOf(0) }
    val exHeight by exH

    val kbHeight =  remember { mutableIntStateOf(0) }
    val keyboardHeight by kbHeight

    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(Density(LocalContext.current))

    AndroidView(factory = { context ->
        LinearLayout(context).apply {
            viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom
                if (keypadHeight > screenHeight * 0.15) { // A threshold to filter the visibility of the keypad
                    kbHeight.intValue = keypadHeight
                    if (kh.intValue == -1){
                        kh.intValue = DisplayUtils.pxToDp(keyboardHeight - navigationBarsHeight).toInt()
                    }
                }else{
                    kbHeight.intValue = 0
                    viewModel.hideKeyBoard()
                    exH.intValue = 0
                }
            }
        }
    })

    exH.intValue = DisplayUtils.pxToDp(keyboardHeight - navigationBarsHeight).toInt()

    Box(modifier = modifier) {
        if (showInput){
            Column(
                Modifier.wrapContentHeight()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(ChatroomUIKitTheme.colors.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    voiceContent(composerMessageState)

                    Row (
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                    ){
                        input(this,composerMessageState)
                    }

                    emojiContent(composerMessageState,object : (Boolean) -> Unit{
                        override fun invoke(isShowEmoji: Boolean) {
                            if (isShowEmoji){
                                viewModel.hideKeyBoard()
                                scope.launch {
                                    delay(350)
                                    viewModel.showEmoji()
                                }
                            }else{
                                viewModel.showKeyBoard()
                                viewModel.hideEmoji()
                            }
                        }
                    })

                    trailingContent(composerMessageState)
                }

                if (viewModel.isShowEmoji.value){
                    DefaultComposerEmoji(
                        maxH = exKh,
                        emojis = emojiList,
                        viewModel = viewModel,
                    )
                }else{
                    Row(
                        Modifier
                            .height(exHeight.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(ChatroomUIKitTheme.colors.primary),
                        verticalAlignment = Bottom
                    ) {}
                }
            }
        }else{

            Column(
                Modifier
                    .wrapContentHeight()
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(Color.Transparent),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                            .background(
                                shape = RoundedCornerShape(size = 20.dp),
                                color = ChatroomUIKitTheme.colors.barrageL20D10
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) {
                                viewModel.showKeyBoard()
                                viewModel.hideEmoji()
                                onInputClick()
                            }

                    ){
                        defaultChatBar()
                    }

                    defaultChatBarMenu(composerMessageState)
                }
            }
        }

    }
}

@Composable
fun DefaultComposerEmoji(
    modifier: Modifier = Modifier,
    emojis:List<UIExpressionEntity>,
    maxH:Int,
    viewModel: MessageChatBarViewModel,
){
    Box(modifier = modifier
        .fillMaxWidth()
        .height(maxH.dp)
        .background(ChatroomUIKitTheme.colors.background)
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(viewModel.eColumns.value)) {
            items(emojis) { emoji ->
                Image(
                    painter = painterResource(emoji.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(2.dp)
                        .clickable {
                            viewModel.setEmojiInput(emoji.emojiText)
                        }
                )
            }
        }

        Box(
            contentAlignment = Center,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    viewModel.isClickDelete.value = true
                }
                .padding(end = 24.dp)
                .size(36.dp)
                .clip(CircleShape)
                .align(Alignment.BottomEnd)
                .background(ChatroomUIKitTheme.colors.neutralL98D30)
                .border(color = Color(0x26464E53), width = 0.5.dp, shape = CircleShape)
        ){
            Icon(
                modifier = Modifier
                    .size(20.dp, 20.dp),
                painter = painterResource(id = R.drawable.icon_emoji_pick),
                contentDescription = "",
                tint = ChatroomUIKitTheme.colors.neutralL30D98
            )
        }

    }
}

@Composable
fun RowScope.DefaultComposerInputContent(
    viewModel: MessageChatBarViewModel,
    composerMessageState: ComposerInputMessageState,
    onValueChange: (String) -> Unit,
    onKeyDown:(String) -> Unit,
) {
    ComposeMessageInput(
        modifier = Modifier.weight(1f),
        viewModel = viewModel,
        composerMessageState = composerMessageState,
        onValueChange = onValueChange,
        onKeyDown = onKeyDown
    )
}

/**
 * Represents the default trailing content for the Composer, which represent a send button.
 *
 * @param value The input value.
 * @param validationErrors List of errors for message validation.
 * @param onSendMessage Handler when the user wants to send a message.
 * @param ownCapabilities Set of capabilities the user is given for the Conversation.
 */
@Composable
internal fun DefaultMessageComposerTrailingContent(
    value: String,
    validationErrors: List<UIValidationError>,
    ownCapabilities: Set<String>,
    onSendMessage: (String) -> Unit,
) {
    val isInputValid by lazy { (value.isNotBlank()) && validationErrors.isEmpty() }

    Icon(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, end = 12.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                if (isInputValid) {
                    onSendMessage(value)
                }
            },
        painter = painterResource(id = R.drawable.icon_send),
        contentDescription = stringResource(id = R.string.stream_compose_cd_send_button),
        tint = ChatroomUIKitTheme.colors.primary
    )
}

@Composable
internal fun DefaultMessageComposerVoiceContent(
    ownCapabilities: Set<String>,
    onVoiceClick: () -> Unit,
) {
    val isVoiceButtonEnabled = ownCapabilities.contains(UICapabilities.SHOW_VOICE)
    val description = stringResource(id = R.string.stream_compose_cd_voice_button)

    if (isVoiceButtonEnabled){
        IconButton(
            modifier = Modifier.semantics { contentDescription = description },
            content = {
                val layoutDirection = LocalLayoutDirection.current
                Icon(
                    modifier = Modifier
                        .mirrorRtl(layoutDirection = layoutDirection)
                        .size(30.dp, 30.dp),
                    painter = painterResource(id = R.drawable.icon_wave_in_circle),
                    contentDescription = stringResource(id = R.string.stream_compose_cd_voice_button),
                    tint = ChatroomUIKitTheme.colors.onBackground
                )
            },
            onClick = {
                onVoiceClick()
            }
        )
    }
}

@Composable
internal fun DefaultMessageComposerEmojiContent(
    viewModel: MessageChatBarViewModel,
    onEmojiClick: (isShowFace:Boolean) -> Unit,
) {
    val resourceId = remember { mutableIntStateOf(R.drawable.icon_face) }
    val resource by resourceId

    var isShowEmoji = viewModel.isShowEmoji.value
    val description = stringResource(id = R.string.stream_compose_cd_emoji_button)

    val context = LocalContext.current

    IconButton(
        modifier = Modifier.semantics { contentDescription = description },
        content = {
            val layoutDirection = LocalLayoutDirection.current
            Icon(
                modifier = Modifier
                    .mirrorRtl(layoutDirection = layoutDirection)
                    .size(30.dp, 30.dp),
                painter = painterResource(id = resource),
                contentDescription = stringResource(id = R.string.stream_compose_cd_emoji_button),
                tint = ChatroomUIKitTheme.colors.neutralL30D50
            )
        },
        onClick = {
            isShowEmoji = !isShowEmoji
            resourceId.intValue = if (isShowEmoji){
                R.drawable.icon_keyboard
            }else{
                R.drawable.icon_face
            }
            onEmojiClick(isShowEmoji)
            if (isShowEmoji){
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow((context as Activity).currentFocus?.windowToken, 0)
            }
        }

    )
}

@Composable
internal fun DefaultChatBarMenuComposerContent(
    isDarkTheme: Boolean? = false,
    ownCapabilities: Set<String>,
    onMenuClick: (drawableTag:Int) -> Unit,
    menuItemResource: List<UIChatBarMenuItem>,
){
    menuItemResource.forEach {
        IconButton(
            content = {
                val layoutDirection = LocalLayoutDirection.current
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp, 38.dp)
                        .background(
                            color = ChatroomUIKitTheme.colors.barrageL20D10,
                            shape = RoundedCornerShape(20.dp)
                        )
                ){
                    if (it.drawableTag == 0){
                        Image(
                            modifier = Modifier
                                .mirrorRtl(layoutDirection = layoutDirection)
                                .size(30.dp, 30.dp),
                            painter = painterResource(id = it.drawableResource),
                            contentDescription = "",
                        )
                    }else{
                        Icon(
                            modifier = Modifier
                                .mirrorRtl(layoutDirection = layoutDirection)
                                .size(30.dp, 30.dp),
                            painter = painterResource(id = it.drawableResource),
                            contentDescription = "",
                            tint = ChatroomUIKitTheme.colors.neutralL98D98
                        )
                    }
                }
            },
            onClick = {
                onMenuClick(it.drawableTag)
            },
        )
    }

}

@Composable
internal fun DefaultChatBarComposerContent(){
    Row(
        modifier = Modifier.height(38.dp),
        verticalAlignment =Alignment.CenterVertically
    ){
        Icon(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(20.dp, 20.dp),
            painter = painterResource(id = R.drawable.icon_bubble_fill),
            contentDescription = "",
            tint = ChatroomUIKitTheme.colors.neutralL98D98,
        )

        Text(
            text = LocalContext.current.resources.getString(R.string.compose_bottom_toolbar_tag),
            style = ChatroomUIKitTheme.typography.titleMedium.copy(
                color = ChatroomUIKitTheme.colors.neutralL98D98
            ),
            modifier = Modifier.padding(start = 4.dp)
        )
    }

}

@Composable
private fun MessageInputValidationError(validationErrors: List<UIValidationError>, snackbarHostState: SnackbarHostState) {
    if (validationErrors.isNotEmpty()) {
        val firstValidationError = validationErrors.first()

        val errorMessage = when (firstValidationError) {
            is UIValidationError.MessageLengthExceeded -> {
                stringResource(
                    R.string.stream_compose_message_composer_error_message_length,
                    firstValidationError.maxMessageLength
                )
            }
            else -> {""}
        }

        val context = LocalContext.current
        LaunchedEffect(validationErrors.size) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
