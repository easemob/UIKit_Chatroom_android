package com.hyphenate.chatroom.compose.utils

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

object DisplayUtils {
    val metrics = Resources.getSystem().displayMetrics
    val density = metrics.density

    fun pxToDp(px: Int): Float {
        val density = Resources.getSystem().displayMetrics.density
        return px / density
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics = Resources.getSystem().displayMetrics
        return (dp * displayMetrics.density).toInt()
    }
}

// 扩展函数将 TextUnit 转换为 Dp
@Composable
fun TextUnit.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) {
        this@toDp.toDp()
    }

}