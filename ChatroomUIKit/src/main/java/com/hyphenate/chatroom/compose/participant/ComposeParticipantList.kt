package com.hyphenate.chatroom.compose.participant

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
import com.hyphenate.chatroom.compose.indicator.LoadingIndicator
import com.hyphenate.chatroom.compose.gift.LazyColumnList
import com.hyphenate.chatroom.compose.gift.LazyColumnListState
import com.hyphenate.chatroom.service.UserEntity
import com.hyphenate.chatroom.uikit.R
import com.hyphenate.chatroom.viewmodel.LoadMoreState
import com.hyphenate.chatroom.viewmodel.RefreshState
import com.hyphenate.chatroom.viewmodel.RequestState
import com.hyphenate.chatroom.viewmodel.member.MemberListViewModel

@Composable
fun ComposeParticipantList(
    viewModel: MemberListViewModel,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showRole: Boolean = false,
    onScrollChange: (LazyColumnListState) -> Unit = {},
    onItemClick: ((UserEntity) -> Unit)? = null,
    onExtendClick: ((UserEntity) -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Int, UserEntity) -> Unit = { index, item ->
        DefaultMemberItem(
            viewModel = viewModel,
            user = item,
            showRole = showRole,
            onItemClick = onItemClick,
            onExtendClick = onExtendClick
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