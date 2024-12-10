package com.hyphenate.chatroom.compose.broadcast

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.uikit.R
import com.hyphenate.chatroom.viewmodel.broadcast.GlobalBroadcastViewModel
import kotlinx.coroutines.delay

@Composable
fun ComposeGlobalBroadcast(
    modifier: Modifier = Modifier,
    viewModel: GlobalBroadcastViewModel,
    marqueeBg: Color = ChatroomUIKitTheme.colors.primary,
    leftIcon: Painter = painterResource(id = R.drawable.icon_notification),
    lIconModifier: Modifier =Modifier.size(16.dp).padding(2.dp),
    fontColor:Color = Color.White,
) {
    val content = viewModel.marqueeTextList
    val duration = viewModel.duration
    val durationMillis = viewModel.durationMillis

    if (content.isEmpty()) return

    val text = remember { mutableStateOf( if (content.isNotEmpty()) content[0] else "") }

    val offsetX = remember { mutableFloatStateOf(0f) }

    var initValue by rememberSaveable { mutableFloatStateOf(1f) }

    val animateValue = remember { Animatable(initValue) }


    Box{
        Row(
            modifier = modifier
                .background(
                    color = marqueeBg,
                    ChatroomUIKitTheme.shapes.medium
                )
                .wrapContentWidth()
                .height(20.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = lIconModifier,
                painter = leftIcon,
                contentDescription = "notification"
            )

            Canvas(modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
                .height(20.dp)
            ) {

                offsetX.floatValue = size.width * animateValue.value
                drawContext.canvas.nativeCanvas.apply {
                    clipRect(Rect(0, 0, size.width.toInt(), size.height.toInt()))
                    drawText(
                        text.value,
                        offsetX.floatValue,
                        size.height / 2  + 10,
                        Paint().apply {
                            textAlign = Paint.Align.LEFT
                            textSize = 30f
                            color = fontColor.toArgb()
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(initValue) {
        animateValue.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = durationMillis)
        ) {
            if (this.value == 0.0f) {
                initValue = this.value
            }
        }

        if (!animateValue.isRunning) {
            if (content.isNotEmpty()) {
                viewModel.removeMarqueeText(0)
                delay(duration)
                text.value = if (content.isNotEmpty()) content[0] else ""
                initValue = 1f
                animateValue.snapTo(initValue)
            } else {
                text.value = ""
                return@LaunchedEffect
            }
        }
    }
}
