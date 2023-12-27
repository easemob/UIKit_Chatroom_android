package io.agora.chatroom.compose.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import io.agora.chatroom.compose.drawer.ComposeBottomSheet
import io.agora.chatroom.compose.tabrow.ComposePagerWithTabs
import io.agora.chatroom.model.report.UIReportEntity
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.pager.PagerViewModel
import io.agora.chatroom.viewmodel.pager.TabInfo
import io.agora.chatroom.viewmodel.report.ComposeReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeMessageReport(
    modifier: Modifier = Modifier,
    viewModel: ComposeReportViewModel,
    onConfirmClick: (UIReportEntity) -> Unit = {},
    onCancelClick: () -> Unit = {},
    drawerContent: @Composable () -> Unit = {
        DefaultReportContent(
            viewModel=viewModel,
            onConfirmClick = onConfirmClick,
            onCancelClick = onCancelClick
        )
    },
    screenContent: @Composable () -> Unit = {  },
    onDismissRequest: () -> Unit,
    shape: Shape = ChatroomUIKitTheme.shapes.bottomSheet,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
) {
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

@Composable
fun DefaultReportContent(
    modifier:Modifier = Modifier,
    viewModel: ComposeReportViewModel,
    onConfirmClick: (UIReportEntity) -> Unit,
    onCancelClick: () -> Unit)
{
    val context = LocalContext.current
    val tabList = mutableListOf<TabInfo>()
    tabList += TabInfo(context.resources.getString(R.string.report_button_click_menu_title))
    ComposePagerWithTabs(
        viewModel = PagerViewModel(tabList = tabList),
        modifier = modifier,
        tabIndicatorHeight = 4.dp,
        tabIndicatorShape = RoundedCornerShape(4.dp),
    ) { page ->
        val tagList = context.resources.getStringArray(R.array.report_tag)
        val reasonList = viewModel.contentList

        var selectedOption by remember { mutableIntStateOf(0) }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            val (title,list,bottomLayout) = createRefs()

            Text(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                ,
                text = context.resources.getString(R.string.report_button_click_menu_subtitle_violation),
                style = ChatroomUIKitTheme.typography.titleSmall.copy(
                    color = ChatroomUIKitTheme.colors.neutralL50D50
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .constrainAs(list) {
                        top.linkTo(title.bottom)
                        bottom.linkTo(bottomLayout.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(reasonList){ index, item ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectedOption = index },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                style = ChatroomUIKitTheme.typography.headlineSmall.copy(
                                    color = ChatroomUIKitTheme.colors.onBackground
                                ) ,
                                modifier = Modifier
                                    .weight(1.0f)
                            )
                            RadioButton(
                                selected = selectedOption == index,
                                colors = RadioButtonDefaults.colors(
                                    unselectedColor = ChatroomUIKitTheme.colors.neutralL70D40,
                                    selectedColor =ChatroomUIKitTheme.colors.primary)
                                ,
                                modifier = Modifier.size(30.dp),
                                onClick = {
                                    selectedOption = index
                                }
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(56.dp)
                    .constrainAs(bottomLayout) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ){
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1.0f)
                        .height(40.dp)
                        .padding(end = 12.dp)
                        .border(
                            1.dp,
                            ChatroomUIKitTheme.colors.neutralL70D40,
                            ChatroomUIKitTheme.shapes.inputField
                        )
                        .background(
                            ChatroomUIKitTheme.colors.background,
                            ChatroomUIKitTheme.shapes.inputField
                        )
                    ,
                    colors = ButtonColors(
                        contentColor = Color.Transparent,
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent,
                    ),
                    onClick = {
                    onCancelClick()
                }) {
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize(),
                        text = stringResource(id = R.string.report_button_click_menu_button_cancel),
                        style = ChatroomUIKitTheme.typography.titleMedium.copy(
                            color = ChatroomUIKitTheme.colors.onBackground
                        )
                    )
                }
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1.0f)
                        .height(40.dp)
                        .border(
                            1.dp,
                            ChatroomUIKitTheme.colors.primary,
                            ChatroomUIKitTheme.shapes.inputField
                        )
                        .background(
                            ChatroomUIKitTheme.colors.primary, ChatroomUIKitTheme.shapes.inputField
                        )
                    ,
                    colors = ButtonColors(
                        contentColor = Color.Transparent,
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent,
                    ),
                    onClick = {
                        onConfirmClick(UIReportEntity(
                            msgId =viewModel.reportMsgId.value ,
                            tag = tagList[selectedOption],
                            reason = reasonList[selectedOption]
                        ))
                }) {
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize(),
                        text = stringResource(id = R.string.report_button_click_menu_button_report),
                        style = ChatroomUIKitTheme.typography.titleMedium.copy(
                            color = ChatroomUIKitTheme.colors.neutralL98D98
                        )
                    )
                }

            }
        }

    }

}