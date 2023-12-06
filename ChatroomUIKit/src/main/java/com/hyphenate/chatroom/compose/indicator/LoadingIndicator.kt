package com.hyphenate.chatroom.compose.indicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme

/**
 * Shows the default loading UI.
 *
 * @param modifier Modifier for styling.
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 20.dp,
    indicatorColor: Color = ChatroomUIKitTheme.colors.primary,
    indicatorStrokeWidth: Dp = 2.dp,
    loadingContent: String = "",
    loadingContentColor: Color = ChatroomUIKitTheme.colors.primary,
    contentPadding: Dp = 10.dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            color = indicatorColor,
            strokeWidth = indicatorStrokeWidth,
            trackColor = ChatroomUIKitTheme.colors.background
        )
        if (!loadingContent.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(contentPadding))
            Text(text = loadingContent, color = loadingContentColor)
        }
    }
}
