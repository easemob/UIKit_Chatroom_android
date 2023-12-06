package com.hyphenate.chatroom.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

enum class LinearGradientDirection{
    HorizontalLeftToRight,
    HorizontalRightToLeft,
    VerticalTopToBottom,
    VerticalBottomToTop,
    DiagonalTopLeftToBottomRight,
    DiagonalTopRightToBottomLeft,
    DiagonalBottomLeftToTopRight,
    DiagonalBottomRightToTopLeft
}

@Suppress("IMPLICIT_CAST_TO_ANY")
fun getLinearGradientBrush(
    colors: List<Color>,
    direction:LinearGradientDirection): Brush {

    val brush = when (direction){
        LinearGradientDirection.HorizontalLeftToRight -> Brush.linearGradient(
            colors = colors,
            start = Offset(0.0f, 0.0f),
            end = Offset(Float.POSITIVE_INFINITY, 0.0f),
            tileMode = TileMode.Clamp
        )
        LinearGradientDirection.HorizontalRightToLeft -> Brush.linearGradient(
            colors = colors,
            start = Offset(Float.POSITIVE_INFINITY, 0.0f),
            end = Offset(0.0f, 0.0f),
            tileMode = TileMode.Clamp
        )
        LinearGradientDirection.VerticalTopToBottom -> Brush.linearGradient(
            colors = colors,
            start = Offset(0.0f, 0.0f),
            end = Offset(0.0f, Float.POSITIVE_INFINITY),
            tileMode = TileMode.Clamp
        )
        LinearGradientDirection.VerticalBottomToTop -> Brush.linearGradient(
            colors = colors,
            start = Offset(0.0f, Float.POSITIVE_INFINITY),
            end = Offset(0.0f, 0.0f),
            tileMode = TileMode.Clamp
        )
        LinearGradientDirection.DiagonalTopLeftToBottomRight -> Brush.linearGradient(
            colors = colors,
            start = Offset(0.0f, 0.0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            tileMode = TileMode.Clamp
        )
        LinearGradientDirection.DiagonalTopRightToBottomLeft -> Brush.linearGradient(
            colors = colors,
            start = Offset(Float.POSITIVE_INFINITY, 0.0f),
            end = Offset(0.0f, Float.POSITIVE_INFINITY),
            tileMode = TileMode.Clamp
        )
        LinearGradientDirection.DiagonalBottomLeftToTopRight -> Brush.linearGradient(
            colors = colors,
            start = Offset(0.0f, Float.POSITIVE_INFINITY),
            end = Offset(Float.POSITIVE_INFINITY, 0.0f),
            tileMode = TileMode.Clamp
        )
        LinearGradientDirection.DiagonalBottomRightToTopLeft -> Brush.linearGradient(
            colors = colors,
            start = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            end = Offset(0.0f, 0.0f),
            tileMode = TileMode.Clamp
        )
        else -> {}
    }
    return brush as Brush
}