package io.agora.chatroom.compose.participant

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.compose.drawer.ComposeBottomSheet
import io.agora.chatroom.compose.drawer.DefaultDragHandle
import io.agora.chatroom.compose.search.DefaultSearchBar
import io.agora.chatroom.compose.tabrow.ComposePagerWithTabs
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.UIChatroomService
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.member.MutedListViewModel
import io.agora.chatroom.viewmodel.pager.PagerViewModel
import io.agora.chatroom.viewmodel.pager.TabInfo

@ExperimentalFoundationApi
@Composable
fun ComposeParticipantWithPager(
    modifier: Modifier = Modifier,
    roomId: String,
    roomService: UIChatroomService,
    isAdmin: Boolean = false,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null
) {
    var tabList = mutableListOf<TabInfo>()
    tabList += TabInfo(stringResource(id = R.string.member_management_participant))
    if (isAdmin) {
        tabList += TabInfo(stringResource(id = R.string.member_management_mute))
    }
    val memberViewModel = viewModel(MemberListViewModel::class.java, factory = MemberViewModelFactory(roomId, roomService, isRoomAdmin = isAdmin))
    val mutedViewModel = viewModel(MutedListViewModel::class.java, factory = MemberViewModelFactory(roomId, roomService, isRoomAdmin = isAdmin))

    memberViewModel.enableLoadMore(true)
    mutedViewModel.enableLoadMore(true)

    ComposePagerWithTabs(
        viewModel = PagerViewModel(tabList = tabList),
        modifier = modifier,
        tabIndicatorHeight = 4.dp,
        tabIndicatorShape = RoundedCornerShape(4.dp)
    ) {
        page ->

        val tabInfo = tabList[page]
        when(tabInfo.title) {
            stringResource(id = R.string.member_management_participant) -> {
                MembersPage(
                    viewModel = memberViewModel,
                    tab = tabInfo.title,
                    showSearch = true,
                    autoRequest = true,
                    onItemClick = onItemClick,
                    onExtendClick = onExtendClick,
                    onSearchClick = onSearchClick)
            }
            stringResource(id = R.string.member_management_mute) -> {
                MutedListPage(
                    viewModel = mutedViewModel,
                    tab = tabInfo.title,
                    showSearch = true,
                    autoRequest = true,
                    onItemClick = onItemClick,
                    onExtendClick = onExtendClick,
                    onSearchClick = onSearchClick)
            }
        }
    }
}

@Composable
fun MembersPage(
    modifier: Modifier = Modifier,
    viewModel: MemberListViewModel,
    showSearch: Boolean = false,
    autoRequest: Boolean = false,
    tab: String,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null
) {
    var request by rememberSaveable { mutableStateOf(autoRequest) }
    if (request) {
        viewModel.fetchRoomMembers()
        request = false
    }
    Column(modifier = modifier) {
        if (showSearch) {
            DefaultSearchBar(
                onClick = {
                    onSearchClick?.invoke(tab)
                }
            )
        }
        ComposeParticipantList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize(),
            showRole = true,
            onItemClick = { user ->
                onItemClick?.invoke(tab, user)
            },
            onExtendClick = { user ->
                onExtendClick?.invoke(tab, user)
            },
            onScrollChange = { listState ->
                if (listState.isScrollInProgress) {
                    if (!listState.canScrollForward && viewModel.hasMore()) viewModel.fetchMoreRoomMembers()
                } else {
                    viewModel.fetchUsersInfo(listState.firstVisibleIndex, listState.lastVisibleIndex)
                }
            }
        )
    }
}

@Composable
fun MutedListPage(
    modifier: Modifier = Modifier,
    viewModel: MutedListViewModel,
    showSearch: Boolean = false,
    autoRequest: Boolean = false,
    tab: String,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null
) {
    var request by rememberSaveable { mutableStateOf(autoRequest) }
    if (request) {
        viewModel.fetchMuteList()
        request = false
    }
    Column(modifier = modifier) {
        if (showSearch) {
            DefaultSearchBar(
                onClick = {
                    onSearchClick?.invoke(tab)
                }
            )
        }
        ComposeParticipantList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize(),
            showRole = false,
            onItemClick = { user ->
                onItemClick?.invoke(tab, user)
            },
            onExtendClick = { user ->
                onExtendClick?.invoke(tab, user)
            },
            onScrollChange = { listState ->
                if (!listState.isScrollInProgress) {
                    viewModel.fetchUsersInfo(listState.firstVisibleIndex, listState.lastVisibleIndex)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ComposeMembersBottomSheet(
    viewModel: MembersBottomSheetViewModel,
    modifier: Modifier = Modifier,
    onItemClick: ((String, UserEntity) -> Unit)? = null,
    onExtendClick: ((String, UserEntity) -> Unit)? = null,
    onSearchClick: ((String) -> Unit)? = null,
    drawerContent: @Composable () -> Unit = {
        ComposeParticipantWithPager(
            roomId = viewModel.roomId,
            roomService = viewModel.roomService,
            isAdmin = viewModel.isAdmin,
            onItemClick = onItemClick,
            onExtendClick = onExtendClick,
            onSearchClick = onSearchClick
        )
    },
    screenContent: @Composable () -> Unit = {  },
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = ChatroomUIKitTheme.colors.background,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { DefaultDragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
){
    ComposeBottomSheet(
        modifier = modifier,
        viewModel = viewModel,
        drawerContent = drawerContent,
        screenContent = screenContent,
        onDismissRequest = onDismissRequest,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        windowInsets = windowInsets
    )
}
