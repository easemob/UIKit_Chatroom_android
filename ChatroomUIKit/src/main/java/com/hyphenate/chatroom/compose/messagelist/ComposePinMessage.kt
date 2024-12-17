package com.hyphenate.chatroom.compose.messagelist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyphenate.chatroom.ChatroomUIKitClient
import com.hyphenate.chatroom.compose.utils.toDp
import com.hyphenate.chatroom.service.ChatLog
import com.hyphenate.chatroom.service.ChatMessage
import com.hyphenate.chatroom.service.TextMessageBody
import com.hyphenate.chatroom.service.UserEntity
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.uikit.R
import com.hyphenate.chatroom.viewmodel.messages.MessageListViewModel
import kotlinx.coroutines.launch

@Composable
fun ComposePinMessage(modifier: Modifier, viewModel: MessageListViewModel) {
    val pinMessage by viewModel.pinMessage.observeAsState(null)
    if (pinMessage != null) {
        MultiColorTextWithIcon(modifier,pinMessage!!)
    }
}

@Composable
fun MultiColorTextWithIcon(modifier: Modifier, pinMessage: ChatMessage) {
    val density = LocalDensity.current
    var canExpand by remember { mutableStateOf(false) }
    var measureMaxLines by remember { mutableStateOf(0) }
    val textMeasurer = rememberTextMeasurer()

    var content = ""
    if (pinMessage.body is TextMessageBody) {
         content = (pinMessage.body as TextMessageBody).message
    }

    var textWidth = 280.dp
    LaunchedEffect(content, textWidth) {

        val widthInPx = with(density) { textWidth.toPx() }

        val layoutResult = textMeasurer.measure(
            text = content,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default
            ),
            constraints = Constraints(maxWidth = widthInPx.toInt())
        )
        measureMaxLines = layoutResult.lineCount
        if (measureMaxLines>=2){
            canExpand=true
        }else{
            canExpand=false
        }
    }
    //打印日志
    ChatLog.d("MultiColorTextWithIcon", "measureMaxLines:$measureMaxLines")

    var userInfo: UserEntity? = null
    var userName = ""
    val userId = pinMessage?.from
    if (userId != null) {
        if (  userId.isNotEmpty() && userId.isNotBlank()){
            userInfo = ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId)
            userName = userInfo.nickname?.let {
                it.ifEmpty { userInfo.userId }
            } ?: userInfo.userId
        }
    }

    val text = buildAnnotatedString {
        appendInlineContent("pin_icon", "[pin_icon]")
        appendInlineContent("user_icon", "[user_icon]")
        withStyle(style = SpanStyle(
            color = ChatroomUIKitTheme.colors.primaryL80D80,
            letterSpacing = 0.01.sp,
            fontSize = 14.sp,
            fontFamily = FontFamily.Default,)) {
            append("  "+userName.trimIndent())
        }
        withStyle(style = SpanStyle(
            color = ChatroomUIKitTheme.colors.neutralL98D98,
            letterSpacing = 0.01.sp,
            fontSize = 14.sp,
            fontFamily = FontFamily.Default,)) {
            append("  "+content.trimIndent())
        }
    }

    val inlineContent = mapOf(
        "pin_icon" to InlineTextContent(
            placeholder = Placeholder(
                width = 24.sp,
                height = 24.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            ),
            children = {
                Image(
                    painter = painterResource(R.drawable.icon_message_pin),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)

                )
            }
        ),
        "user_icon" to InlineTextContent(
            placeholder = Placeholder(
                width = 24.sp,
                height = 24.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            ),
            children = {
                DrawAvatarImage(userInfo)
            }
        )
    )

    val lineHeight = 16.sp // 每行的高度
    var maxHeight =0.dp
    var maxLineCount = 0
    // 默认最多显示两行
    if (measureMaxLines <= 2) {
        maxLineCount = measureMaxLines
    } else if(measureMaxLines ==3){
        if (canExpand) {
            maxLineCount = 2
        } else {
            maxLineCount = 3
        }
    }

    with(density) {
        maxHeight = (lineHeight * maxLineCount).toDp()
    }
    if (measureMaxLines >= 4) {
        if (canExpand) {
            maxHeight = (lineHeight * 2).toDp()
        } else {
            maxHeight = (lineHeight * 4).toDp()
        }
    }
    val scrollState = rememberScrollState()

    ChatLog.d("MultiColorTextWithIcon", "maxHeight:$maxHeight ,canExpand:$canExpand")
    if (measureMaxLines != 0) {
        Box(modifier = Modifier.fillMaxHeight()){
            Row(modifier = Modifier
                .background(
                    ChatroomUIKitTheme.colors.barrageL20D10,
                    shape = ChatroomUIKitTheme.shapes.small
                )
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .padding(4.dp,4.dp,4.dp,4.dp)
                        .weight(1f)
                        .heightIn(max=maxHeight+8.dp)
                        .then(if (!canExpand && measureMaxLines>2) Modifier.verticalScroll(scrollState) else Modifier) // 动态应用 verticalScroll
                ) {
                    Text(
                        text = text,
                        inlineContent = inlineContent,
                        modifier = Modifier
                            .fillMaxHeight(),
                        style = ChatroomUIKitTheme.typography.bodyMedium,
                        color = ChatroomUIKitTheme.colors.neutralL98D98
                    )
                }

                if (!canExpand && measureMaxLines>2) {
                    VerticalScrollbar(
                        modifier = Modifier
                            .height(maxHeight+18.dp),
                        scrollState = scrollState,
                        backgroundColor = ChatroomUIKitTheme.colors.alphaBlack40, // 滚动条背景颜色
                        indicatorColor = Color.White // 滚动指示器颜色
                    )
                }

                if (measureMaxLines >2) {
                    Image(
                        painter = painterResource(if (canExpand) R.drawable.icon_arrow_down else R.drawable.icon_arrow_up),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Bottom)
                            .clickable { canExpand = !canExpand }
                    )
                }
            }

        }
    }


}

@Composable
fun VerticalScrollbar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    width: Dp = 2.dp,
    backgroundColor: Color = Color.Gray,
    indicatorColor: Color = Color.Blue
) {
    var parentHeight by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val scrollFraction = scrollState.value / scrollState.maxValue.toFloat()
    ChatLog.e("MultiColorTextWithIcon", "scrollFraction:$scrollFraction")
    val indicatorHeightFraction = scrollState.maxValue / (scrollState.maxValue + scrollState.maxValue).toFloat()

    Box(
        modifier = modifier
            .width(width)
            .padding(top = 4.dp)
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        scrollState.scrollBy(dragAmount.y)
                    }
                }
            }
            .onGloballyPositioned { coordinates ->
                // Get the height of the parent container
                parentHeight = coordinates.size.height
            }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(indicatorHeightFraction)
                .align(Alignment.TopStart)
                .offset(y = with(LocalDensity.current) { (scrollFraction * parentHeight/2).toDp() })
                .background(indicatorColor, shape = RoundedCornerShape(4.dp))
        )
    }
}