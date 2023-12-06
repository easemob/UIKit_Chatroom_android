package io.agora.chatroom.compose.utils

import android.graphics.Color
import kotlin.math.roundToInt

/**
 * Used for gradient color adjustment when the user doesn't have an image.
 *
 * @param color The color to adjust.
 * @param factor The factor by which we adjust the color.
 * @return [Int] ARGB value of the color after adjustment.
 */
public fun adjustColorBrightness(color: Int, factor: Float): Int {
    val a = Color.alpha(color)
    val r = (Color.red(color) * factor).roundToInt()
    val g = (Color.green(color) * factor).roundToInt()
    val b = (Color.blue(color) * factor).roundToInt()
    return Color.argb(
        a,
        r.coerceAtMost(MAX_COLOR_COMPONENT_VALUE),
        g.coerceAtMost(MAX_COLOR_COMPONENT_VALUE),
        b.coerceAtMost(MAX_COLOR_COMPONENT_VALUE)
    )
}

/**
 * Maximum value a color component can have.
 */
private const val MAX_COLOR_COMPONENT_VALUE = 255
