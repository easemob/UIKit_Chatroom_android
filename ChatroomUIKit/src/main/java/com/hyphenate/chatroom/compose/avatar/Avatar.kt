package com.hyphenate.chatroom.compose.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.hyphenate.chatroom.compose.utils.rememberStreamImagePainter
import com.hyphenate.chatroom.service.model.UserInfoProtocol
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.uikit.R


@Composable
fun UserAvatar(
    user: UserInfoProtocol,
    modifier: Modifier = Modifier,
    shape: Shape = ChatroomUIKitTheme.shapes.avatar,
    contentDescription: String? = null,
    extensionContent: @Composable ()->Unit = {},
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier) {
        Avatar(
            modifier = Modifier.fillMaxSize(),
            imageUrl = user.avatarURL ?: "",
            shape = shape,
            contentDescription = contentDescription,
            onClick = onClick,
        )
        extensionContent()
    }
}

@Composable
fun ImageAvatar(
    painter: Painter,
    modifier: Modifier = Modifier,
    shape: Shape = ChatroomUIKitTheme.shapes.avatar,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier: Modifier = if (onClick != null) {
        modifier.clickable(
            onClick = onClick,
            indication = rememberRipple(bounded = false),
            interactionSource = remember { MutableInteractionSource() },
        )
    } else {
        modifier
    }

    Image(
        modifier = clickableModifier.clip(shape),
        contentScale = ContentScale.Crop,
        painter = painter,
        contentDescription = contentDescription,
    )
}

@Composable
fun Avatar(
    imageUrl: String,
    modifier: Modifier = Modifier,
    hideWhenLoadError: Boolean = false,
    shape: Shape = ChatroomUIKitTheme.shapes.avatar,
    placeholderPainter: Painter? = null,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val defaultPainter = placeholderPainter ?: painterResource(id = R.drawable.icon_default_avatar)
    if (imageUrl.isBlank()) {
        ImageAvatar(
            modifier = modifier,
            shape = shape,
            painter = defaultPainter,
            contentDescription = contentDescription,
            onClick = onClick,
        )
        return
    }

    val painter = rememberStreamImagePainter(
        data = imageUrl,
        placeholderPainter = defaultPainter,
    )

    when (painter.state) {
        is AsyncImagePainter.State.Error -> {
            if (!hideWhenLoadError) {
                ImageAvatar(
                    modifier = modifier,
                    shape = shape,
                    painter = defaultPainter,
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

@Preview(showBackground = true, name = "With image URL")
@Composable
private fun AvatarWithImageUrlPreview() {
    AvatarPreview(
        imageUrl = "https://www.testimage.com/shape.png",
    )
}

@Preview(showBackground = true, name = "Without image URL")
@Composable
private fun AvatarWithoutImageUrlPreview() {
    AvatarPreview(
        imageUrl = "",
        placeholderPainter = painterResource(id = R.drawable.icon_face),
    )
}

@Composable
fun AvatarPreview(
    imageUrl: String,
    placeholderPainter: Painter? = null,
) {
    ChatroomUIKitTheme {
        Avatar(
            modifier = Modifier.size(36.dp),
            imageUrl = imageUrl,
            placeholderPainter = placeholderPainter,
        )
    }
}