package com.hyphenate.chatroom.compose.participant

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.compose.avatar.Avatar
import com.hyphenate.chatroom.compose.image.AsyncImage
import com.hyphenate.chatroom.service.ChatClient
import com.hyphenate.chatroom.service.ChatroomUIKitClient
import com.hyphenate.chatroom.service.UserEntity
import com.hyphenate.chatroom.service.model.UIChatroomInfo
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.uikit.R
import com.hyphenate.chatroom.viewmodel.member.MemberListViewModel

@Composable
fun ComposeParticipantItem(
    member: UserEntity,
    modifier: Modifier,
    labelContent: @Composable ((UserEntity) -> Unit)? = null,
    avatarContent: @Composable (UserEntity) -> Unit = {},
    nameContent: @Composable (UserEntity) -> Unit = {},
    extendContent: @Composable ((UserEntity) -> Unit)? = null,
    showDivider: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        labelContent?.let {
            labelContent(member)
        }

        avatarContent(member)

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()) {

            Row(modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    nameContent(member)
                }

                extendContent?.let {
                    extendContent(member)
                }
            }

            if (showDivider) {
                Divider(thickness = 0.5.dp,
                    color = ChatroomUIKitTheme.colors.outlineVariant,
                    modifier = Modifier
                    .fillMaxWidth())
            }
        }

    }
}

@Composable
fun DefaultMemberItem(
    viewModel: MemberListViewModel,
    user: UserEntity,
    labelContent: @Composable ((UserEntity) -> Unit)? = { user ->
        if (viewModel.isShowLabel.value){
            user.identify?.let {
                if (it.isNotBlank()) {
                    AsyncImage(
                        imageUrl = it,
                        modifier = Modifier
                            .size(width = 36.dp, height = 24.dp).padding(start = 12.dp),
                        shape = RoundedCornerShape(0.dp),
                        placeholderPainter = painterResource(id = R.drawable.icon_default_label)
                    )
                }
            }
        }
    },
    avatarContent: @Composable (UserEntity) -> Unit = { user ->
        Log.e("DefaultMemberItem", "avatarContent: $user")
        Spacer(modifier = Modifier.width(12.dp))
        Avatar(
            imageUrl = user.avatarURL ?: "",
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
    },
    showRole: Boolean = false,
    nameContent: @Composable (UserEntity) -> Unit = {user ->
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Text(
                text = user.nickname?.let {
                    it.ifBlank { user.userId }
                } ?: user.userId ,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                style = ChatroomUIKitTheme.typography.titleMedium,
                color = ChatroomUIKitTheme.colors.onBackground
            )
            if (showRole && (ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId == user.userId)) {
                Text(
                    text = stringResource(id = R.string.role_owner),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp),
                    style = ChatroomUIKitTheme.typography.bodyMedium,
                    color = ChatroomUIKitTheme.colors.onBackground
                )
            }
        }
    },
    onItemClick: ((UserEntity) -> Unit)? = null,
    onExtendClick: ((UserEntity) -> Unit)? = null,
    extendContent: @Composable ((UserEntity) -> Unit)? = {user ->
        val roomOwner = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomOwner?.userId
        if (roomOwner == ChatClient.getInstance().currentUser
            && roomOwner != user.userId) {
            IconButton(
                onClick = { onExtendClick?.invoke(user) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_more),
                    contentDescription = null,
                    tint = ChatroomUIKitTheme.colors.onBackground
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
    },
    showDivider: Boolean = true
) {
    ComposeParticipantItem(
        user,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(ChatroomUIKitTheme.colors.background)
            .clickable { onItemClick?.invoke(user) },
        labelContent,
        avatarContent,
        nameContent,
        extendContent,
        showDivider = showDivider
    )
}

@Composable
fun DefaultMuteListItem(
    viewModel: MemberListViewModel,
    user: UserEntity,
    onItemClick: ((UserEntity) -> Unit)? = null,
    onExtendClick: ((UserEntity) -> Unit)? = null,
) {
    DefaultMemberItem(
        viewModel = viewModel,
        user = user,
        onItemClick = onItemClick,
        onExtendClick = onExtendClick,
        labelContent = null,
    )
}

@Preview(showBackground = true)
@Composable
fun MemberItemPreview() {
    ChatroomUIKitTheme {
        DefaultMemberItem(
            viewModel = MemberListViewModel(
                "roomID",
                UIChatroomService(UIChatroomInfo("roomID", UserEntity("userId")))
            ),
            user = UserEntity(
                userId = "123",
                nickname = "nickname",
                avatarURL = "",
                identify = ""
            )
        )
    }
}
