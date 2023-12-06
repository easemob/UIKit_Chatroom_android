package io.agora.chatroom.compose.utils

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.agora.chatroom.theme.UIColors

@Composable
fun WindowConfigUtils(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colors: UIColors = if (!isDarkTheme) UIColors.defaultColors() else UIColors.defaultDarkColors(),
    statusBarColor: Color = Color.Transparent,
    nativeBarColor: Color = colors.background ,
){
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = statusBarColor.toArgb()
            window.navigationBarColor = nativeBarColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDarkTheme
        }
    }
}