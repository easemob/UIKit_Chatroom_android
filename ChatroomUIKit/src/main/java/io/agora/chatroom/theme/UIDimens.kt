package io.agora.chatroom.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Contains all the dimensions for components.
 * @param messageItemMaxWidth Used for message item max width.
 * @param titleBarHeight Used for title bar height.
 */
@Immutable
class UIDimens(
    val messageItemMaxWidth: Dp,
    val titleBarHeight: Dp
) {
    companion object {
        @Composable
        fun defaultDimens(): UIDimens = UIDimens(
            messageItemMaxWidth = 250.dp,
            titleBarHeight = 44.dp
        )
    }
}