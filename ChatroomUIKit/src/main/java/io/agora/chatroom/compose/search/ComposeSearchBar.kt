package io.agora.chatroom.compose.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R

@Composable
fun ComposeSearchBar(
    modifier: Modifier = Modifier,
    iconResource: Int = R.drawable.icon_face,
    hint: String = "",
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier.clickable { onClick() }, verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconResource),
                modifier = Modifier.size(22.dp),
                contentDescription = "Search bar")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = hint, color = ChatroomUIKitTheme.colors.inputOnSurface)
        }
    }
}

@Composable
fun DefaultSearchBar(
    iconResource: Int = R.drawable.icon_magnifier,
    hint: String = stringResource(id = R.string.search),
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = ChatroomUIKitTheme.colors.inputSurface,
                shape = RoundedCornerShape(size = 22.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconResource),
                modifier = Modifier.size(22.dp),
                tint = ChatroomUIKitTheme.colors.onBackground,
                contentDescription = "Search bar")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = hint,
                color = ChatroomUIKitTheme.colors.inputHint,
                style = ChatroomUIKitTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    ChatroomUIKitTheme {
        ComposeSearchBar(modifier = Modifier
            .fillMaxWidth()
            .clip(ChatroomUIKitTheme.shapes.medium)
            .border(BorderStroke(1.dp, ChatroomUIKitTheme.colors.inputSurface))
            .height(50.dp), hint = "Search")
    }
}