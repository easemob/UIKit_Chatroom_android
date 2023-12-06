package com.hyphenate.chatroom.compose.gift

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.hyphenate.chatroom.compose.drawer.ComposeBottomSheet
import com.hyphenate.chatroom.compose.drawer.DefaultDragHandle
import com.hyphenate.chatroom.compose.drawer.GiftTabLayoutWithViewPager
import com.hyphenate.chatroom.service.GiftEntityProtocol
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.viewmodel.gift.ComposeGiftTabViewModel
import com.hyphenate.chatroom.viewmodel.gift.ComposeGiftSheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun ComposeGiftBottomSheet(
    viewModel:ComposeGiftSheetViewModel,
    modifier: Modifier = Modifier,
    onGiftItemClick: (GiftEntityProtocol) -> Unit = {},
    drawerContent: @Composable () -> Unit = { DefaultGiftContent(viewModel, onGiftItemClick) },
    screenContent: @Composable () -> Unit = {  },
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultGiftContent(viewModel: ComposeGiftSheetViewModel, onGiftItemClick: (GiftEntityProtocol) -> Unit){
    GiftTabLayoutWithViewPager(
        viewModel =  ComposeGiftTabViewModel(giftTabInfo = viewModel.contentList),
        modifier = Modifier,
        sendGift = onGiftItemClick,
    )
}