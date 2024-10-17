package com.hyphenate.chatroom.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.hyphenate.chatroom.service.ChatroomUIKitClient

private val LocalDimens = compositionLocalOf<UIDimens> {
    error("No dimens provided! Make sure to wrap all usages of Chatroom components in a ChatroomUIKitTheme.")
}

private val LocalColors = compositionLocalOf<UIColors> {
    error("No colors provided! Make sure to wrap all usages of Chatroom components in a ChatroomUIKitTheme.")
}

private val LocalShapes = compositionLocalOf<UIShapes> {
    error("No shapes provided! Make sure to wrap all usages of Chatroom components in a ChatroomUIKitTheme.")
}

private val LocalTypography = compositionLocalOf<UITypography> {
    error("No typography provided! Make sure to wrap all usages of Chatroom components in a ChatroomUIKitTheme.")
}

/**
 * Chatroom theme that provides [UIColors], [UIDimens], [UIShapes], and [UITypography]
 *
 * @param isDarkTheme Whether the theme is dark or not.
 * @param colors The [UIColors] to use for this theme.
 * @param shapes The [UIShapes] to use for this theme.
 * @param dimens The [UIDimens] to use for this theme.
 * @param typography The [UITypography] to use for this theme.
 * @param content The content of the theme.
 */
@Composable
fun ChatroomUIKitTheme(
    isDarkTheme: Boolean = ChatroomUIKitClient.getInstance().getCurrentTheme(),
    colors: UIColors = if (!isDarkTheme) UIColors.defaultColors() else UIColors.defaultDarkColors(),
    shapes: UIShapes = UIShapes.defaultShapes(),
    dimens: UIDimens = UIDimens.defaultDimens(),
    typography: UITypography = UITypography.defaultTypography(),
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalDimens provides dimens,
        LocalShapes provides shapes,
        LocalTypography provides typography) {
        content()
    }
}

object ChatroomUIKitTheme {

    /**
     * Retrieves the current [UIColors] at the call site's position in the hierarchy.
     */
    val colors: UIColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    /**
     * Retrieves the current [UIDimens] at the call site's position in the hierarchy.
     */
    val dimens: UIDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimens.current

    /**
     * Retrieves the current [UIShapes] at the call site's position in the hierarchy.
     */
    val shapes: UIShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current

    /**
     * Retrieves the current [UITypography] at the call site's position in the hierarchy.
     */
    val typography: UITypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}