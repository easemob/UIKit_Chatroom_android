package io.agora.chatroom.compose.tabrow

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.pager.PagerViewModel
import io.agora.chatroom.viewmodel.pager.TabInfo
import kotlinx.coroutines.launch

/**
 * Pager with PrivateTabRow
 * @param viewModel: PagerViewModel
 * @param modifier: Modifier
 * @param setupWithTabs: Boolean    Whether to setup with tabs
 * @param isHorizontalPager: Boolean    Whether to use HorizontalPager or VerticalPager
 * @param tabContainerColor: Color    TabRow container color
 * @param tabContentColor: Color    TabRow content color
 * @param tabDivider: @Composable () -> Unit    TabRow divider
 * @param tabIndicatorHeight: Dp    TabRow indicator height
 * @param tabIndicatorColor: Color    TabRow indicator color
 * @param tabIndicatorShape: Shape    TabRow indicator shape
 * @param tabIndicator: @Composable ((selectedTabIndex: Int, tabPositions: List<TabPosition>) -> Unit)?    TabRow indicator
 * @param tabContent: @Composable (index: Int, tabContent: TabInfo) -> Unit    TabRow content
 * @param pagerContent: @Composable (index: Int) -> Unit    Pager content
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposePagerWithTabs(
    viewModel: PagerViewModel,
    modifier: Modifier = Modifier,
    setupWithTabs: Boolean = true,
    isHorizontalPager: Boolean = true,
    tabContainerColor: Color = TabRowDefaults.primaryContainerColor,
    tabContentColor: Color = TabRowDefaults.primaryContentColor,
    tabDivider: @Composable () -> Unit = {},
    tabIndicatorHeight: Dp = 3.dp,
    tabIndicatorColor: Color = ChatroomUIKitTheme.colors.primary,
    tabIndicatorShape: Shape = RoundedCornerShape(3.0.dp),
    tabIndicator: @Composable ((selectedTabIndex: Int, tabPositions: List<TabPosition>) -> Unit)? = null,
    tabContent: @Composable (index: Int, selectedTabIndex: Int, tabContent: TabInfo) -> Unit = { index, selectedTabIndex, tabContent -> DefaultTabContent(index, selectedTabIndex, tabContent) },
    pagerContent: @Composable (index: Int) -> Unit
) {
    val tabs = viewModel.tabList
    var selectedIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState{ tabs.size }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {

        if (setupWithTabs) {
            PrimaryTabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = tabContainerColor,
                contentColor = tabContentColor,
                indicator = { tabPositions ->
                    if (tabIndicator != null) {
                        tabIndicator.invoke(selectedIndex, tabPositions)
                    }else {
                        if (selectedIndex < tabPositions.size) {
                            val width by animateDpAsState(targetValue = tabPositions[selectedIndex].contentWidth)
                            TabRowDefaults.PrimaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                width = width,
                                height = tabIndicatorHeight,
                                color = tabIndicatorColor,
                                shape = tabIndicatorShape
                            )
                        }
                    }
                },
                divider = tabDivider
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                        }
                    ) {
                        tabContent(index, selectedIndex, title)
                    }
                }
            }
        }

        if (isHorizontalPager) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                pagerContent(page)
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                pagerContent(page)
            }
        }
    }

    if (setupWithTabs) {
        LaunchedEffect(selectedIndex) {
            scope.launch {
                pagerState.animateScrollToPage(selectedIndex)
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            selectedIndex = pagerState.currentPage
        }
    }
}

@Composable
fun DefaultTabContent(index: Int, selectedTabIndex: Int, tabContent: TabInfo) {
    Box(
        modifier = Modifier.fillMaxSize()
            .height(44.dp)
            .background(ChatroomUIKitTheme.colors.background),
        contentAlignment = Alignment.Center) {

        Text(
            text = tabContent.title,
            color = if (index == selectedTabIndex) {
                ChatroomUIKitTheme.colors.onBackground
            } else {
                ChatroomUIKitTheme.colors.tabUnSelected
            },
            style = if (index == selectedTabIndex) {
                ChatroomUIKitTheme.typography.titleMedium
            } else {
                ChatroomUIKitTheme.typography.titleSmall
            }
        )
    }
}

@Preview
@Composable
fun PreviewHorizontalPagerWithTabs() {
    ChatroomUIKitTheme {
        val viewModel = PagerViewModel(tabList = mutableListOf(TabInfo("Tab1"), TabInfo("tab2"), TabInfo("tab3")))
        ComposePagerWithTabs(
            viewModel = viewModel,
        ) { page ->
            val currentTab = viewModel.tabList[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = currentTab.title)
            }
        }
    }
}

