package com.hyphenate.chatroom.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import coil.Coil
import coil.ImageLoader
import coil.imageLoader

/**
 * A [CompositionLocal] that returns the current [ImageLoader] for the composition.
 * If a local [ImageLoader] has not been provided, it returns the singleton instance of [ImageLoader] in [Coil].
 */
val LocalStreamImageLoader: StreamImageLoaderProvidableCompositionLocal =
    StreamImageLoaderProvidableCompositionLocal()

/**
 * A provider of [CompositionLocal] that returns the current [ImageLoader] for the composition.
 */
@JvmInline
value class StreamImageLoaderProvidableCompositionLocal internal constructor(
    private val delegate: ProvidableCompositionLocal<ImageLoader?> = staticCompositionLocalOf { null },
) {

    val current: ImageLoader
        @Composable
        @ReadOnlyComposable
        get() = delegate.current ?: LocalContext.current.imageLoader

    infix fun provides(value: ImageLoader): ProvidedValue<ImageLoader?> = delegate provides value
}
