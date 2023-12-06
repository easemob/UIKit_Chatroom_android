package com.hyphenate.chatroom.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hyphenate.chatroom.R
import com.hyphenate.chatroom.bean.RoomDetailBean
import com.hyphenate.chatroom.compose.avatar.Avatar
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.utils.UserInfoGenerator

@Composable
fun ChatroomListItem(
    modifier: Modifier = Modifier,
    roomDetail: RoomDetailBean,
    onItemClick: ((RoomDetailBean) -> Unit)? = null,
) {
    ElevatedCard(
        modifier= modifier.clickable { onItemClick?.invoke(roomDetail) },
        shape = ChatroomUIKitTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = ChatroomUIKitTheme.colors.background,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Row(modifier = Modifier
            .fillMaxSize()) {

            var drawableId = UserInfoGenerator.getRoomImage(LocalContext.current, roomDetail.id)
            if (roomDetail.video_type == "agora_promotion_live") {
                drawableId = R.drawable.default_cover
            }
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = roomDetail.name,
                    style = ChatroomUIKitTheme.typography.bodyLarge,
                    color = ChatroomUIKitTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(
                        imageUrl = roomDetail.iconKey,
                        modifier = Modifier.size(16.dp),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = roomDetail.nickname,
                        style = ChatroomUIKitTheme.typography.bodySmall,
                        color = ChatroomUIKitTheme.colors.neutralL50D70
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(width = 55.dp, height = 24.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(ChatroomUIKitTheme.colors.backgroundHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = LocalContext.current.resources.getString(R.string.chatroom_enter),
                            style = ChatroomUIKitTheme.typography.labelSmall,
                            color = ChatroomUIKitTheme.colors.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

            }
        }

    }
}