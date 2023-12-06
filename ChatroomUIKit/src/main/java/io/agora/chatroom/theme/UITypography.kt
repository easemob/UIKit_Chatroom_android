package io.agora.chatroom.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Contains all the typography for components.
 * @param headlineLarge Used for large headline.
 * @param headlineMedium Used for medium headline.
 * @param headlineSmall Used for small headline.
 * @param titleLarge Used for large title.
 * @param titleMedium Used for medium title.
 * @param titleSmall Used for small title.
 * @param bodyLarge Used for large body.
 * @param bodyMedium Used for medium body.
 * @param bodySmall Used for small body.
 * @param labelLarge Used for large label.
 * @param labelMedium Used for medium label.
 * @param labelSmall Used for small label.
 * @param labelExtraSmall Used for extra small label.
 */
@Immutable
data class UITypography(
    val headlineLarge: TextStyle ,
    val headlineMedium: TextStyle ,
    val headlineSmall: TextStyle ,
    val titleLarge: TextStyle ,
    val titleMedium: TextStyle ,
    val titleSmall: TextStyle ,
    val bodyLarge: TextStyle ,
    val bodyMedium: TextStyle ,
    val bodySmall: TextStyle ,
    val labelLarge: TextStyle ,
    val labelMedium: TextStyle ,
    val labelSmall: TextStyle ,
    val labelExtraSmall:TextStyle,
) {
    companion object {
        @Composable
        fun defaultTypography(): UITypography = UITypography(
            headlineLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 28.sp,
                fontSize = 20.sp,
                letterSpacing = 0.08.sp,
            ),
            headlineMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
                fontSize = 18.sp,
                letterSpacing = 0.06.sp,
            ),
            headlineSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp,
                fontSize = 16.sp,
                letterSpacing = 0.03.sp,
            ),
            titleLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 24.sp,
                fontSize = 18.sp,
                letterSpacing = 0.06.sp,
            ),
            titleMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
                fontSize = 16.sp,
                letterSpacing = 0.03.sp,
            ),
            titleSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp,
                fontSize = 14.sp,
                letterSpacing = 0.01.sp,
            ),
            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
                fontSize = 16.sp,
                letterSpacing = 0.03.sp,
            ),
            bodyMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 18.sp,
                fontSize = 14.sp,
                letterSpacing = 0.01.sp,
            ),
            bodySmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp,
                fontSize = 12.sp,
            ),
            labelLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp,
                fontSize = 16.sp,
                letterSpacing = 0.03.sp,
            ),
            labelMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 18.sp,
                fontSize = 14.sp,
                letterSpacing = 0.01.sp,
            ),
            labelSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
                fontSize = 12.sp,
                letterSpacing = 0.01.sp,
            ),
            labelExtraSmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                lineHeight = 14.sp,
                fontSize = 11.sp,
            ),
        )
    }
}