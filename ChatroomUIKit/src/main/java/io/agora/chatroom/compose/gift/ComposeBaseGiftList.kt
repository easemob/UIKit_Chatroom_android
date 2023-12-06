package io.agora.chatroom.compose.gift

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.agora.chatroom.viewmodel.ComposeBaseListViewModel
import java.io.Serializable
import kotlinx.coroutines.delay


@Composable
fun <T> ComposeBaseList(
    viewModel: ComposeBaseListViewModel<T>,
    modifier: Modifier = Modifier,
    reverseLayout:Boolean = true,
    horizontal:Alignment.Horizontal = Alignment.Start,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    itemContent: @Composable (Int, T) -> Unit,
){
    val items = viewModel.items
    var parentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val density = LocalDensity.current
    var timer by remember { mutableLongStateOf(0L) }

    val listState = rememberLazyListState()

    if (viewModel.isAutoClear.value){
        LaunchedEffect(items) {
            timer = System.currentTimeMillis()
            delay(viewModel.autoClearTime.value)
            Log.e("apex","System.currentTimeMillis() - timer:" +
                    " ${System.currentTimeMillis()} - $timer  : ${System.currentTimeMillis() - timer}")
            if (System.currentTimeMillis() - timer >= 3000 && items.isNotEmpty()) {
                viewModel.clear()
            }
        }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = modifier
                .onGloballyPositioned {
                    val bottomPadding = contentPadding.calculateBottomPadding()
                    val topPadding = contentPadding.calculateTopPadding()

                    val paddingPixels = with(density) {
                        bottomPadding.roundToPx() + topPadding.roundToPx()
                    }

                    parentSize = IntSize(
                        width = it.size.width,
                        height = it.size.height - paddingPixels
                    )
                }
            ,
            horizontalAlignment = horizontal,
            reverseLayout = reverseLayout,
            contentPadding = contentPadding
        ){
            itemsIndexed(items){index, item ->
                Box(modifier = Modifier) {
                    itemContent(index,item)
                }
            }
        }
    }
}

/**
 * List state for [LazyColumnList]
 * @param isScrollInProgress
 * @param firstVisibleIndex
 * @param lastVisibleIndex
 * @param canScrollBackward
 * @param canScrollForward
 */
data class LazyColumnListState(
    val isScrollInProgress: Boolean,
    val firstVisibleIndex: Int,
    val lastVisibleIndex: Int,
    val canScrollBackward: Boolean,
    val canScrollForward: Boolean,
): Serializable

fun LazyListState.toLazyColumnListState(): LazyColumnListState {
    return LazyColumnListState(
        isScrollInProgress = isScrollInProgress,
        firstVisibleIndex = firstVisibleItemIndex,
        lastVisibleIndex = if (layoutInfo.visibleItemsInfo.size - 1 >= 0)
            layoutInfo.visibleItemsInfo[layoutInfo.visibleItemsInfo.size - 1].index else 0,
        canScrollBackward = canScrollBackward,
        canScrollForward = canScrollForward,
    )
}
@Composable
fun <T> LazyColumnList(
    viewModel: ComposeBaseListViewModel<T>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onScrollChange: (LazyColumnListState) -> Unit = { _ -> },
    bottomContent: @Composable (() -> Unit)? = null,
    itemContent: @Composable (Int, T) -> Unit
) {
    var contentPaddingSize by remember { mutableStateOf(IntSize(0, 0)) }
    val density = LocalDensity.current

    // Provide it to LazyColumn
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.onGloballyPositioned {
                val bottomPadding = contentPadding.calculateBottomPadding()
                val topPadding = contentPadding.calculateTopPadding()

                val paddingPixels = with(density) {
                    bottomPadding.roundToPx() + topPadding.roundToPx()
                }

                contentPaddingSize = IntSize(
                    width = it.size.width,
                    height = it.size.height - paddingPixels
                )
            },
            horizontalAlignment = horizontalAlignment,
            reverseLayout = reverseLayout,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement
        ) {
            itemsIndexed(viewModel.items) { index, item ->
                Box{
                    itemContent(index, item)
                }
            }

            bottomContent?.let { bottom ->
                item {
                    bottom.invoke()
                }
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.isScrollInProgress }
                .collect { isScrollInProgress ->
                    onScrollChange(listState.toLazyColumnListState())
                }
        }
    }

}