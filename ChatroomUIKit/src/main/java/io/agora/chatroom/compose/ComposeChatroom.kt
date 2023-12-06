package io.agora.chatroom.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.compose.indicator.LoadingIndicator
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.model.toUser
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.UIChatroomService
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.UIRoomViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftSheetViewModel
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.menu.MessageMenuViewModel
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel
import io.agora.chatroom.viewmodel.messages.MessageChatBarViewModel
import io.agora.chatroom.viewmodel.messages.MessageListViewModel
import io.agora.chatroom.viewmodel.report.ComposeReportViewModel

/**
 * This is a compose screen that shows the chat screen.
 * @param roomId The room id of the chat room.
 * @param roomOwner The owner of the chat room.
 * @param service The chat room service.
 * @param messageListViewModel The view model for the message list.
 * @param chatBottomBarViewModel The view model for the chat bottom bar.
 * @param messageItemMenuViewModel The view model for the message item menu.
 * @param giftBottomSheetViewModel The view model for the gift bottom sheet.
 * @param giftListViewModel The view model for the gift list.
 * @param reportViewModel The view model for the report.
 * @param membersBottomSheetViewModel The view model for the members bottom sheet.
 * @param memberListViewModel The view model for the member list.
 * @param memberMenuViewModel The view model for the member menu.
 * @param onMemberSheetSearchClick The callback for the member sheet search click.
 * @param onMessageMenuClick The callback for the message menu click.
 * @param onMemberMenuClick The callback for the member menu click.
 * @param onGiftBottomSheetItemClick The callback for the gift bottom sheet item click.
 * @param chatBackground The background of the chat room.
 */
@Composable
fun ComposeChatroom(
    roomId:String,
    roomOwner: UserInfoProtocol,
    service: UIChatroomService = UIChatroomService(UIChatroomInfo(roomId, roomOwner.toUser())),
    roomViewModel:UIRoomViewModel = viewModel(UIRoomViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service = service)),
    messageListViewModel: MessageListViewModel = viewModel(
        MessageListViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    chatBottomBarViewModel: MessageChatBarViewModel = viewModel(
        MessageChatBarViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    messageItemMenuViewModel: MessageMenuViewModel = viewModel(
        MessageMenuViewModel::class.java,
        factory = defaultMenuViewModelFactory()),
    giftBottomSheetViewModel: ComposeGiftSheetViewModel = viewModel(
        ComposeGiftSheetViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    giftListViewModel: ComposeGiftListViewModel = viewModel(ComposeGiftListViewModel::class.java,
        factory = defaultMessageListViewModelFactory(LocalContext.current, service.getRoomInfo().roomId, service)),
    reportViewModel: ComposeReportViewModel = viewModel(
        ComposeReportViewModel::class.java,
        factory = defaultReportViewModelFactory(LocalContext.current,service)),
    membersBottomSheetViewModel: MembersBottomSheetViewModel = viewModel(MembersBottomSheetViewModel::class.java,
        factory = defaultMembersViewModelFactory(service.getRoomInfo().roomId, service,
            ChatroomUIKitClient.getInstance().isCurrentRoomOwner(service.getRoomInfo().roomOwner?.userId))),
    memberListViewModel: MemberListViewModel = viewModel(
        MemberListViewModel::class.java,
        factory = defaultMembersViewModelFactory(service.getRoomInfo().roomId, service,
            ChatroomUIKitClient.getInstance().isCurrentRoomOwner(service.getRoomInfo().roomOwner?.userId))),
    memberMenuViewModel: RoomMemberMenuViewModel = viewModel(
        RoomMemberMenuViewModel::class.java,
        factory = defaultMenuViewModelFactory()),
    onMemberSheetSearchClick: ((String) -> Unit)? = null,
    onMessageMenuClick: ((Int, UIComposeSheetItem) -> Unit)? = null,
    onGiftBottomSheetItemClick: ((GiftEntityProtocol) -> Unit) = {},
    onMemberMenuClick: ((UIComposeSheetItem) -> Unit)? = null,
    chatBackground:Painter = if (roomViewModel.getTheme)
        painterResource(R.drawable.icon_chatroom_bg_dark)
        else painterResource(R.drawable.icon_chatroom_bg_light)
) {

    if (roomViewModel.isShowLoading.value) {
        loginToRoom()
        service.joinRoom(
            onSuccess = {
                roomViewModel.isShowLoading.value = false
                messageListViewModel.addJoinedMessageByIndex(
                    message = ChatroomUIKitClient.getInstance().insertJoinedMessage(
                        roomId, ChatroomUIKitClient.getInstance().getCurrentUser().userId
                    )
                )
            }
        )
    } else {

        if (roomViewModel.isShowBg.value){
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                painter = chatBackground,
                contentDescription = "bg",
            )
        }

        ComposeChatScreen(
            roomId = roomId,
            roomOwner = roomOwner,
            service = roomViewModel.getRoomService,
            messageListViewModel= messageListViewModel,
            chatBottomBarViewModel = chatBottomBarViewModel,
            messageItemMenuViewModel = messageItemMenuViewModel,
            giftBottomSheetViewModel = giftBottomSheetViewModel,
            giftListViewModel = giftListViewModel,
            reportViewModel = reportViewModel,
            memberListViewModel = memberListViewModel,
            memberMenuViewModel = memberMenuViewModel,
            membersBottomSheetViewModel = membersBottomSheetViewModel,
            onMessageMenuClick = onMessageMenuClick,
            onMemberSheetSearchClick = onMemberSheetSearchClick,
            onGiftBottomSheetItemClick = onGiftBottomSheetItemClick,
            onMemberMenuClick = onMemberMenuClick,
        )

        LaunchedEffect(roomViewModel.closeMemberSheet.value) {
            if (roomViewModel.closeMemberSheet.value){
                membersBottomSheetViewModel.closeDrawer()
            }
        }
    }
}

@Composable
private fun loginToRoom() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
    }
}


