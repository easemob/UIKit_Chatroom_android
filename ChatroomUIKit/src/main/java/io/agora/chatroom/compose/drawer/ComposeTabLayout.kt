package io.agora.chatroom.compose.drawer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.agora.chatroom.compose.tabrow.ComposePagerWithTabs
import io.agora.chatroom.compose.utils.parsingGift
import io.agora.chatroom.compose.utils.rememberStreamImagePainter
import io.agora.chatroom.model.gift.selected
import io.agora.chatroom.service.GiftEntityProtocol
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.gift.ComposeGiftTabViewModel
import io.agora.chatroom.viewmodel.pager.PagerViewModel
import io.agora.chatroom.viewmodel.pager.TabInfo
import io.agora.chatroom.viewmodel.tab.TabWithVpViewModel

@Composable
fun <T> ComposeTabLayoutWithViewPager(
    modifier: Modifier = Modifier,
    viewModel:TabWithVpViewModel<T>,
    tabList:MutableList<TabInfo>,
    vpContent: @Composable (Int) -> Unit = { DefaultVpContent(it,viewModel) },
){
    Column(modifier = modifier
        .padding(top = 5.dp)
        .background(ChatroomUIKitTheme.colors.background)
    ) {
        ComposePagerWithTabs(
            viewModel = PagerViewModel(tabList = tabList),
            modifier = modifier,
            tabIndicatorHeight = 4.dp,
            tabIndicatorShape = RoundedCornerShape(4.dp),
        ) { page ->
            vpContent(page)
        }
    }
}

@Composable
fun <T> DefaultVpContent(index: Int,viewModel: TabWithVpViewModel<T>){
    LazyColumn {
        item {
            when (index) {
                0 -> Text(text = "Content for Tab 0 ${viewModel.contentList[index]}")
                else -> { Text(text = "Content for Tab $index ${viewModel.contentList[index]}")}
            }
        }
    }
}


@ExperimentalFoundationApi
@Composable
fun GiftTabLayoutWithViewPager(
    viewModel:ComposeGiftTabViewModel,
    modifier: Modifier = Modifier,
    sendGift: (GiftEntityProtocol) -> Unit = { },
) {
    val tabList = mutableListOf<TabInfo>()
    val contentList = viewModel.contentList
    contentList.forEach {
        tabList += TabInfo(it.tabName)
    }
    ComposeTabLayoutWithViewPager(
        modifier = modifier,
        viewModel = viewModel,
        tabList = tabList,
        vpContent = {
            DefaultGiftVpContent(pageIndex = it, viewModel = viewModel, sendGift = sendGift)
        }
    )

}

@Composable
fun DefaultGiftVpContent(
    pageIndex: Int,
    viewModel:ComposeGiftTabViewModel,
    sendGift: (GiftEntityProtocol) -> Unit,
){
    val contentList = viewModel.contentList
    val page = remember { mutableIntStateOf(pageIndex) }

    val selectedItemIndex = remember { mutableIntStateOf(-1) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(ChatroomUIKitTheme.colors.background)
    ){
        LazyVerticalGrid(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            columns = GridCells.Fixed(4)) {
            itemsIndexed(contentList[page.intValue].gifts){ index, emoji ->
                ConstraintLayout(
                    modifier = Modifier
                        .size(80.dp, 98.dp)
                        .background(
                            (if (index == selectedItemIndex.intValue)
                                ChatroomUIKitTheme.colors.primaryL95D20
                            else ChatroomUIKitTheme.colors.background),
                            ChatroomUIKitTheme.shapes.imageThumbnail
                        )
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                color = if (index == selectedItemIndex.intValue)
                                    ChatroomUIKitTheme.colors.primary
                                else ChatroomUIKitTheme.colors.background
                            ), ChatroomUIKitTheme.shapes.imageThumbnail
                        )
                        .clickable {
                            if (selectedItemIndex.intValue == index) {
                                emoji.selected = false
                                selectedItemIndex.intValue = -1
                            } else {
                                emoji.selected = true
                                selectedItemIndex.intValue = index
                            }
                        }
                ) {
                    val (giftIcon,tagLayout,sendBtn) = createRefs()

                    val painter = rememberStreamImagePainter(
                        data = emoji.giftIcon,
                        placeholderPainter = painterResource(id = R.drawable.icon_default_sweet_heart)
                    )

                    Image(
                        modifier = Modifier
                            .size(48.dp, 48.dp)
                            .constrainAs(giftIcon) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .padding(top = 6.dp),
                        painter = painter,
                        alignment = Alignment.Center,
                        contentDescription = "giftIcon"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .constrainAs(tagLayout) {
                                top.linkTo(giftIcon.bottom)
                                bottom.linkTo(sendBtn.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (index != selectedItemIndex.intValue){
                            Text(
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                text = emoji.giftName,
                                style = ChatroomUIKitTheme.typography.titleSmall.copy(
                                    color = ChatroomUIKitTheme.colors.onBackground
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(12.dp, 12.dp),
                                painter = painterResource(id = R.drawable.icon_dollagora),
                                alignment = Alignment.Center,
                                contentDescription = "tagIcon"
                            )
                            Text(
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(start = 2.dp)
                                    .wrapContentWidth()
                                    .wrapContentHeight(),
                                text = emoji.giftPrice,
                                style = ChatroomUIKitTheme.typography.labelExtraSmall.copy(
                                    color = ChatroomUIKitTheme.colors.onBackground
                                )
                            )
                        }

                    }
                    if (index == selectedItemIndex.intValue){
                        Box(modifier = Modifier
                            .background(
                                ChatroomUIKitTheme.colors.primary,
                                ChatroomUIKitTheme.shapes.sendGift
                            )
                            .fillMaxWidth()
                            .height(28.dp)
                            .constrainAs(sendBtn) {
                                bottom.linkTo(parent.bottom)
                            }
                            .clickable {
                                sendGift(emoji)
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .background(
                                        ChatroomUIKitTheme.colors.primary,
                                        ChatroomUIKitTheme.shapes.sendGift
                                    )
                                    .wrapContentWidth()
                                    .wrapContentHeight()
                                    .clickable {
                                        sendGift(emoji)
                                    },
                                style = ChatroomUIKitTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ChatroomUIKitTheme.colors.neutralL98D98
                                ),
                                text = LocalContext.current.resources.getString(R.string.compose_message_gift_sent)
                            )
                        }

                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun PreviewTabLayoutWithViewPager() {
    val tabTitles = parsingGift(context = LocalContext.current)
    GiftTabLayoutWithViewPager(viewModel = ComposeGiftTabViewModel(tabTitles))
}