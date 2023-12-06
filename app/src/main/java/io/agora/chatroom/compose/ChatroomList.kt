package io.agora.chatroom.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.agora.chatroom.bean.RoomDetailBean
import io.agora.chatroom.compose.indicator.LoadingIndicator
import io.agora.chatroom.compose.gift.LazyColumnList
import io.agora.chatroom.compose.gift.LazyColumnListState
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.ChatroomListViewModel
import io.agora.chatroom.viewmodel.LoadMoreState
import io.agora.chatroom.viewmodel.RefreshState
import io.agora.chatroom.viewmodel.RequestState

@Composable
fun ChatroomList(
    viewModel: ChatroomListViewModel,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    onScrollChange: (LazyColumnListState) -> Unit = {},
    onItemClick: ((RoomDetailBean) -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Int, RoomDetailBean) -> Unit = { index, item ->
        ChatroomListItem(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            roomDetail = item,
            onItemClick = onItemClick
        )
    }
) {
    val state = viewModel.getState
    if (viewModel.isEnableRefresh) {
        headerContent?.let { header ->
            header.invoke()
        }
    }
    LazyColumnList(
        viewModel = viewModel,
        modifier = modifier,
        listState = listState,
        contentPadding = contentPadding,
        onScrollChange = onScrollChange,
        bottomContent = bottomContent?.let { bottom ->
            {
                bottom.invoke()
            }
        } ?:{
            if (viewModel.isEnableLoadMore && viewModel.getLoadMoreState is LoadMoreState.Loading) {
                DefaultBottomLoadingAnimation()
            }
        },
        verticalArrangement = verticalArrangement,
        itemContent = itemContent)

    LaunchedEffect(state) {
        when (state) {
            is RequestState.Loading -> {
                if (viewModel.isEnableRefresh) {
                    viewModel.changeRefreshState(RefreshState.Loading)
                }
            }

            is RequestState.LoadingMore -> {
                if (viewModel.isEnableLoadMore) {
                    viewModel.changeLoadMoreState(LoadMoreState.Loading(viewModel.items.size - 1))
                }
            }

            is RequestState.Success<*> -> {
                if (viewModel.isEnableRefresh && viewModel.getRefreshState is RefreshState.Loading) {
                    viewModel.changeRefreshState(RefreshState.Success)
                }
            }

            is RequestState.SuccessMore<*> -> {
                if (viewModel.isEnableLoadMore && viewModel.getLoadMoreState is LoadMoreState.Loading) {
                    viewModel.changeLoadMoreState(LoadMoreState.Success)
                }
            }

            is RequestState.Error -> {
                if (viewModel.isEnableRefresh && viewModel.getRefreshState is RefreshState.Loading) {
                    viewModel.changeRefreshState(RefreshState.Fail)
                }
                if (viewModel.isEnableLoadMore && viewModel.getLoadMoreState is LoadMoreState.Loading) {
                    viewModel.changeLoadMoreState(LoadMoreState.Fail)
                }
            }

            else -> {
                // do noting
            }
        }
    }
}

@Composable
fun DefaultBottomLoadingAnimation() {
    LoadingIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        loadingContent = stringResource(id = R.string.loading_more)
    )
}