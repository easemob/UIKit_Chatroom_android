package com.hyphenate.chatroom.compose.gift

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyphenate.chatroom.compose.avatar.ImageAvatar
import com.hyphenate.chatroom.compose.utils.rememberStreamImagePainter
import com.hyphenate.chatroom.service.ChatroomUIKitClient
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.uikit.R
import com.hyphenate.chatroom.viewmodel.gift.ComposeGiftListViewModel

@Composable
fun ComposeGiftMessageList(
    viewModel:ComposeGiftListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    onItemClick: (Int, ComposeGiftListItemState) -> Unit = { index, message-> },
    itemContent: @Composable (Int, ComposeGiftListItemState) -> Unit = { index, item ->
        DefaultGiftItemContent(
            itemIndex = index,
            giftListItem = item,
            viewModel = viewModel,
            onItemClick = onItemClick,
        )
    },
    emptyContent: @Composable () -> Unit = { },
) {
    when{
        (viewModel.items.isNotEmpty())->{
            ComposeBaseList(
                viewModel = viewModel,
                modifier = modifier,
                itemContent = itemContent,
                contentPadding = contentPadding,
            )
        }
        else -> {
            emptyContent()
        }
    }
}

@Composable
fun DefaultGiftItemContent(
    itemIndex:Int,
    giftListItem:ComposeGiftListItemState,
    viewModel:ComposeGiftListViewModel,
    onItemClick: (Int,ComposeGiftListItemState) -> Unit,
){
    when (giftListItem) {
        else -> ComposeGiftItem(itemIndex,giftListItem,onItemClick)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeGiftItem(
    itemIndex:Int,
    item:ComposeGiftListItemState,
    onItemClick: (Int,ComposeGiftListItemState) -> Unit,
    onItemLongClick:(Int,ComposeGiftListItemState) -> Unit = {index,state ->}
){
    val gift = (item as ComposeGiftItemState).gift

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 16.dp, top = 3.dp, bottom = 3.dp, end = 16.dp)
            .combinedClickable(
                onLongClick = { onItemLongClick(itemIndex, item)}
            ) {}
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ){
                onItemClick(itemIndex, item)
            }
            .wrapContentWidth()
            .height(44.dp)
            .background(
                color = ChatroomUIKitTheme.colors.barrageL20D10,
                shape = ChatroomUIKitTheme.shapes.giftItemBg
            ),
    ){

        val userInfo = ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(gift.sendUser.userId)

        val userName = userInfo.nickname?.let {
            it.ifEmpty { userInfo.userId }
        } ?: userInfo.userId

        val avatarUrl = userInfo.avatarURL?.let {
            it.ifEmpty { "" }
        } ?: ""

        val userPainter = rememberStreamImagePainter(avatarUrl, placeholderPainter = painterResource(id = R.drawable.icon_default_avatar))
        val giftPainter = rememberStreamImagePainter(gift.giftIcon, placeholderPainter = painterResource(id = R.drawable.icon_default_sweet_heart))


        if (avatarUrl.isBlank()){
            ImageAvatar(
                modifier = Modifier.wrapContentWidth().wrapContentHeight().padding(start = 4.dp),
                painter = painterResource(id = R.drawable.icon_default_avatar),
                contentDescription = "userAvatar"
            )
        }else{
            ImageAvatar(
                modifier = Modifier.wrapContentWidth().wrapContentHeight().padding(start = 4.dp),
                painter = userPainter,
                contentDescription = "userAvatar"
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 6.dp, end = 6.dp)
                .wrapContentWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center

        ) {

            Text(
                text = userName,
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                style = ChatroomUIKitTheme.typography.bodySmall.copy(
                    color = Color.White
                )
            )

            Text(
                text = stringResource(id = R.string.compose_gift_item_subtitle,gift.giftName),
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                style = ChatroomUIKitTheme.typography.bodySmall.copy(
                    color = Color.White
                )
            )

        }

        ImageAvatar(
            modifier = Modifier.size(40.dp, 40.dp),
            painter = giftPainter,
            shape = RoundedCornerShape(0.dp),
            contentDescription = "gifts"
        )

        val titleSmall = TextStyle(
            fontFamily = FontFamily(Font("RobotoNumbersVF.ttf", assetManager = LocalContext.current.assets)),
            fontWeight = FontWeight.Normal,
            lineHeight = 14.sp,
            fontSize = 10.sp,
            color = Color.White,
            letterSpacing = 0.01.sp,
        )

        Text(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(start = 6.dp, end = 6.dp),
            text = "x" + gift.giftCount,
            style = titleSmall
        )

    }
}