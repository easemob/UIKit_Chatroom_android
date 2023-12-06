package io.agora.chatroom.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


/**
 * Contains all the shapes for components.
 * @param extraSmall Used for extra small shape.
 * @param small Used for small shape.
 * @param medium Used for medium shape.
 * @param large Used for large shape.
 * @param extraLarge Used for extra large shape.
 * @param avatar Used for avatar shape.
 * @param picture Used for picture shape.
 * @param messageBubble Used for message bubble shape.
 * @param inputField Used for input field shape.
 * @param attachment Used for attachment shape.
 * @param imageThumbnail Used for image thumbnail shape.
 * @param bottomSheet Used for bottom sheet shape.
 * @param header Used for header shape.
 * @param sendGift Used for send gift shape.
 * @param giftItemBg Used for gift item background shape.
 */
@Immutable
data class UIShapes(
    val extraSmall: Shape,
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val extraLarge: Shape = ShapeDefaults.ExtraLarge,
    val avatar: Shape,
    val picture: Shape,
    val messageBubble: Shape,
    val inputField: Shape,
    val attachment: Shape,
    val imageThumbnail: Shape,
    val bottomSheet: Shape,
    val header: Shape,
    val sendGift:Shape,
    val giftItemBg:Shape,
) {
    companion object {
        @Composable
        fun defaultShapes(): UIShapes = UIShapes(
            extraSmall = RoundedCornerShape(4.dp),
            small  = RoundedCornerShape(8.dp),
            medium  = RoundedCornerShape(12.dp),
            large  = RoundedCornerShape(16.dp),
            extraLarge  = ShapeDefaults.ExtraLarge,
            avatar = CircleShape,
            picture = RoundedCornerShape(8.dp),
            messageBubble = RoundedCornerShape(16.dp),
            inputField = RoundedCornerShape(24.dp),
            attachment = RoundedCornerShape(16.dp),
            imageThumbnail = RoundedCornerShape(8.dp),
            bottomSheet = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            header = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sendGift = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
            giftItemBg = RoundedCornerShape(22.dp),
        )
    }

}