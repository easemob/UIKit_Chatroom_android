package com.hyphenate.chatroom.compose.messagelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.hyphenate.chatroom.commons.ComposeMessageListState

@Composable
fun ComposeMessages(
    modifier: Modifier = Modifier,
    messagesState: ComposeMessageListState,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    itemContent: @Composable (Int, ComposeMessageListItemState) -> Unit,
) {
    val messages = messagesState.messages

    var parentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val density = LocalDensity.current

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    val bottomPadding = contentPadding.calculateBottomPadding()
                    val topPadding = contentPadding.calculateTopPadding()

                    val paddingPixels = with(density) {
                        bottomPadding.roundToPx() + topPadding.roundToPx()
                    }

                    parentSize = IntSize(
                        width = it.size.width,
                        height = it.size.height - paddingPixels
                    )
                },
            horizontalAlignment = Alignment.Start,
            reverseLayout = true,
            contentPadding = contentPadding
        ){
            itemsIndexed(messages){index, item ->
                val messageItemModifier = if (item is ComposeMessageItemState && item.focusState == MessageFocused) {
                    Modifier.onGloballyPositioned {
                        if (messagesState.focusedMessageOffset.value == null) {
                            messagesState.calculateMessageOffset(parentSize, it.size)
                        }
                    }
                } else {
                    Modifier
                }

                Box(modifier = messageItemModifier) {
                    itemContent(index,item)
                }
            }
        }
    }
}
