package io.agora.chatroom.compose.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.AsyncImagePainter
import io.agora.chatroom.compose.avatar.ImageAvatar
import io.agora.chatroom.compose.utils.rememberStreamImagePainter
import io.agora.chatroom.theme.ChatroomUIKitTheme

@Composable
fun AsyncImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    shape: Shape = ChatroomUIKitTheme.shapes.picture,
    placeholderPainter: Painter? = null,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {

    if (imageUrl.isBlank() && placeholderPainter != null) {
        ImageAvatar(
            modifier = modifier,
            shape = shape,
            painter = placeholderPainter,
            contentDescription = contentDescription,
            onClick = onClick,
        )
        return
    }

    val painter = rememberStreamImagePainter(
        data = imageUrl,
        placeholderPainter = placeholderPainter,
    )

    when (painter.state) {
        is AsyncImagePainter.State.Error -> {
            if (placeholderPainter != null) {
                ImageAvatar(
                    modifier = modifier,
                    shape = shape,
                    painter = placeholderPainter,
                    contentDescription = contentDescription,
                    onClick = onClick,
                )
            }
        }

        else -> {
            ImageAvatar(
                modifier = modifier,
                shape = shape,
                painter = painter,
                contentDescription = contentDescription,
                onClick = onClick,
            )
        }
    }

}