package io.agora.chatroom.compose.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.menu.MenuViewModel
import io.agora.chatroom.viewmodel.menu.BottomSheetViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ComposeBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: BottomSheetViewModel<T>,
    drawerContent: @Composable () -> Unit = {},
    screenContent: @Composable () -> Unit = {},
    onCancelListener:() -> Unit = {},
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { DefaultDragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
) {
    if (viewModel.isEnable()) {
        val isBottomSheetVisible = viewModel.isBottomSheetVisible.value
        val scope = rememberCoroutineScope()
        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = viewModel.isExpanded
        )
        val isShowTitle = viewModel.getIsShowTitle
        val isShowCancel = viewModel.getIsShowCancel

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            shape = shape,
            containerColor = ChatroomUIKitTheme.colors.background,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            scrimColor = scrimColor,
            dragHandle = dragHandle,
            windowInsets = windowInsets,
            modifier = modifier
        ) {

            Column(
                modifier = modifier
            ) {

                if (isShowTitle && viewModel.getTitle.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp),
                        textAlign = TextAlign.Center,
                        text = viewModel.getTitle,
                        color = ChatroomUIKitTheme.colors.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                drawerContent()

                if (isShowCancel) {
                    HorizontalDivider(
                        thickness = 8.dp,
                        color = ChatroomUIKitTheme.colors.neutralL95D00,
                        modifier = Modifier
                    )
                    Box(modifier = Modifier
                        .background(ChatroomUIKitTheme.colors.background)
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable {
                            onCancelListener()
                            viewModel.closeDrawer()
                        },
                        contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(id = viewModel.getCancelText),
                                color = ChatroomUIKitTheme.colors.primary,
                                style = ChatroomUIKitTheme.typography.bodyLarge
                            )
                        }
                }

                Box {
                    screenContent()
                }
            }
        }

        LaunchedEffect(isBottomSheetVisible){
            if (isBottomSheetVisible){
                viewModel.setVisible(true)
            }else{
                scope.launch {
                    bottomSheetState.hide()
                }.invokeOnCompletion {
                    viewModel.closeDrawer()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeMenuBottomSheet(
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier,
    onListItemClick: (Int, UIComposeSheetItem) -> Unit,
    drawerContent: @Composable () -> Unit = { DefaultDrawerContent(viewModel, onListItemClick)},
    screenContent: @Composable () -> Unit = { },
    onCancelListener:() -> Unit = {},
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { DefaultDragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
) {
    ComposeBottomSheet(
        modifier = modifier,
        viewModel = viewModel,
        drawerContent = drawerContent,
        screenContent = screenContent,
        onCancelListener = onCancelListener,
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

@Composable
fun DefaultDragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 36.dp,
    height: Dp = 5.dp,
    shape: Shape = ChatroomUIKitTheme.shapes.extraLarge,
    color: Color = ChatroomUIKitTheme.colors.onBackgroundHighest,
) {
    val dragHandleDescription = stringResource(id = R.string.compose_description_bottom_sheet_drag_handle)
    Surface(
        modifier = modifier
            .padding(top = 6.dp, bottom = 5.dp)
            .semantics { contentDescription = dragHandleDescription },
        color = color,
        shape = shape
    ) {
        Box(
            Modifier
                .size(
                    width = width,
                    height = height
                )
        )
    }
}

@Composable
fun DefaultDrawerContent(viewModel: MenuViewModel, onListItemClick: (Int, UIComposeSheetItem) -> Unit){
    val items = remember { mutableStateListOf<UIComposeSheetItem>() }
    val sortedList = viewModel.list.sortedBy { it.index }
    items.addAll(sortedList)
    LazyColumn(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .background(ChatroomUIKitTheme.colors.background)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(viewModel.list){ index, item ->
            if (index > 0){
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = ChatroomUIKitTheme.colors.outlineVariant,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = ChatroomUIKitTheme.colors.background
                ),
                headlineContent = {
                    Text(
                        modifier = Modifier
                            .background(ChatroomUIKitTheme.colors.background)
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center,
                        style = ChatroomUIKitTheme.typography.bodyLarge,
                        color = if (item.isError) ChatroomUIKitTheme.colors.error else ChatroomUIKitTheme.colors.primary,
                        text = item.title
                    )
                },
                modifier = Modifier
                    .clickable {
                        onListItemClick(index, item)
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JetpackComposeBottomSheet() {
    ChatroomUIKitTheme {
        ComposeBottomSheet(
            viewModel = MenuViewModel(),
            drawerContent = {
                Text("Drawer Content")
            },
            screenContent = {
                Text("Screen Content")
            },
            onDismissRequest = {

            }
        )
    }
}