package com.hyphenate.chatroom.compose.messagelist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hyphenate.chatroom.ChatroomUIKitClient
import com.hyphenate.chatroom.compose.avatar.ImageAvatar
import com.hyphenate.chatroom.compose.utils.ExpressionUtils
import com.hyphenate.chatroom.model.emoji.UIRegexEntity
import com.hyphenate.chatroom.service.ChatTextMessageBody
import com.hyphenate.chatroom.service.GiftEntityProtocol
import com.hyphenate.chatroom.service.UserEntity
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.uikit.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComposeMessageItem(
    itemIndex: Int,
    isShowDateSeparator: Boolean = true,
    isShowLabel: Boolean = true,
    isShowAvatar: Boolean = false,
    messageItem: ComposeMessageListItemState,
    itemType: ComposeItemType = ComposeItemType.NORMAL,
    onLongItemClick: (Int, ComposeMessageListItemState) -> Unit,
){

    val translationContent = remember {
        mutableStateOf("")
    }
    if (messageItem is ComposeMessageItemState){
        translationContent.value = messageItem.translateContent
    }

    val message = when (itemType) {
        ComposeItemType.NORMAL -> {
            (messageItem as ComposeMessageItemState).message
        }
        ComposeItemType.ITEM_JOIN -> {
            (messageItem as JoinedMessageState).message
        }
        ComposeItemType.ITEM_GIFT -> {
            (messageItem as GiftMessageState).message
        }
        else -> {
            null
        }
    }

    val gift  = if (itemType == ComposeItemType.ITEM_GIFT && messageItem is GiftMessageState){
        messageItem.gift
    } else {
        null
    }

    Row (
        modifier = Modifier
            .padding(start = 16.dp, top = 2.dp, bottom = 2.dp, end = 16.dp)
            .combinedClickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onLongClick = { onLongItemClick(itemIndex, messageItem) },
                onClick = {}
            )
            .wrapContentWidth()
            .wrapContentHeight()
            .background(
                ChatroomUIKitTheme.colors.barrageL20D10,
                shape = ChatroomUIKitTheme.shapes.small
            ),
    ){
        var userInfo:UserEntity? = null
        var userName = ""
        val userId = message?.from
        if (userId != null) {
            if (  userId.isNotEmpty() && userId.isNotBlank()){
                userInfo = ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId)
                userName = userInfo.nickname?.let {
                    it.ifEmpty { userInfo.userId }
                } ?: userInfo.userId
            }
        }

        val dateSeparator = message?.msgTime?.let { convertMillisTo24HourFormat(it) }

        val content =  if(message?.body is ChatTextMessageBody){
            translationContent.value.ifEmpty {
                (message.body as ChatTextMessageBody).message
            }
        }else{
            ""
        }

        val inlineMap = mutableMapOf<String,InlineTextContent>()

        val annotatedText = buildAnnotatedString {

            if (isShowDateSeparator){
                withStyle(style = SpanStyle(color = ChatroomUIKitTheme.colors.secondaryL80D70)) {
                    append(dateSeparator);append(" ")
                }
            }

            if (isShowLabel && itemType != ComposeItemType.ITEM_JOIN){
                withStyle(style = SpanStyle()) {
                    appendInlineContent("Label");append(" ")
                }
            }

            if (isShowAvatar){
                withStyle(style = SpanStyle()) {
                    appendInlineContent("Avatar");append(" ")
                }
            }

            // 设置昵称
            withStyle(style = SpanStyle(
                color = ChatroomUIKitTheme.colors.primaryL80D80,
                letterSpacing = 0.01.sp,
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
            )) {
                append(userName);append(" ")
            }

            //设置内容
            if (itemType == ComposeItemType.NORMAL){
                if (!content.isNullOrEmpty()){
                    if (ExpressionUtils.containsKey(content)){
                        var exchange = content
                        var combination = ""
                        var insertIndex = -1
                        var oldTagLength = 0

                        val insertMap = mutableMapOf<Int,UIRegexEntity>()
                        val roleList = ExpressionUtils.getRole(content)

                        for (i in 0 until roleList.size){
                            var before = ""
                            var after = ""

                            if (roleList[i].startIndex > 0){
                                before = exchange.substring(0,roleList[i].startIndex - oldTagLength)
                            }
                            after = exchange.substring(roleList[i].endIndex - oldTagLength)

                            if (before.isEmpty()){
                                withStyle(style = SpanStyle()) {
                                    appendInlineContent(roleList[i].emojiTag)
                                }
                                ExpressionUtils.addLienMap(roleList[i],inlineMap)
                                combination = before + after
                                exchange = combination
                                oldTagLength += roleList[i].emojiTag.length
                            } else{
                                combination = before + after
                                exchange = combination
                                insertIndex = roleList[i].startIndex - oldTagLength - 1
                                if (insertMap.containsKey(insertIndex)){
                                    roleList[i].count +=1
                                }
                                insertMap[insertIndex] = roleList[i]
                                oldTagLength += roleList[i].emojiTag.length
                            }
                        }

                        combination.let { cb->
                            cb.withIndex().forEach { (i, char) ->
                                append(char)
                                insertMap.forEach {
                                    if (i == it.key){
                                        for (a in 0 until it.value.count) {
                                            withStyle(style = SpanStyle()) {
                                                appendInlineContent(it.value.emojiTag)
                                            }
                                            ExpressionUtils.addLienMap(it.value,inlineMap)
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        append(content)
                    }
                }
            }else if (itemType == ComposeItemType.ITEM_JOIN){
                withStyle(style = SpanStyle(
                    color = ChatroomUIKitTheme.colors.secondaryL80D70,
                    letterSpacing = 0.01.sp,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                )) {
                    append(stringResource(id = R.string.compose_message_item_joined))
                }
            }else if (itemType == ComposeItemType.ITEM_GIFT){
                gift?.let {
                    append(stringResource(id = R.string.compose_message_gift_sent))
                    append("  ")
                    append(it.giftName)
                }
                withStyle(style = SpanStyle()) {
                    append("  ")
                    appendInlineContent("Gift")
                }
                inlineMap["Gift"] = InlineTextContent(
                    placeholder = Placeholder(20.sp,20.sp, PlaceholderVerticalAlign.Center),
                    children = { DrawGiftImage(gift) }
                )
            }
        }

        if (isShowLabel && itemType != ComposeItemType.ITEM_JOIN){
            inlineMap["Label"] = InlineTextContent(
                placeholder = Placeholder(18.sp,18.sp, PlaceholderVerticalAlign.Center),
                children = {
                    DrawLabelImage(userInfo)
                }
            )
        }

        if (isShowAvatar){
            inlineMap["Avatar"] = InlineTextContent(
                placeholder = Placeholder(18.sp,18.sp, PlaceholderVerticalAlign.Center),
                children = {
                    DrawAvatarImage(userInfo)
                }
            )
        }

        Text(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
            text = annotatedText,
            inlineContent = inlineMap,
            style = ChatroomUIKitTheme.typography.bodyMedium,
            color = ChatroomUIKitTheme.colors.neutralL98D98
        )

    }
}

@Composable
fun DrawLabelImage(userInfo:UserEntity?) {
    var labelUrl = ""
    userInfo?.identify?.let {
        labelUrl = it
    }
    val painter = rememberAsyncImagePainter(
        model = labelUrl
    )
    ImageAvatar(
        painter = if (labelUrl.isEmpty()) painterResource(id = R.drawable.icon_default_label) else painter,
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
    )
}
@Composable
fun DrawAvatarImage(userInfo:UserEntity?){
    var avatarUrl:String? = ""
    userInfo?.let {
        avatarUrl = it.avatarURL
    }
    val painter = rememberAsyncImagePainter(
        model = avatarUrl
    )

    Box(
        modifier = Modifier
            .fillMaxSize() // 设置图片的大小
            .clip(shape = CircleShape) // 将图片裁剪为圆形
    ) {
        ImageAvatar(
            modifier = Modifier.padding(2.dp),
            painter = if (avatarUrl?.isEmpty() == true)painterResource(id = R.drawable.icon_default_avatar) else painter,
        )
    }
}
@Composable
fun DrawGiftImage(gift:GiftEntityProtocol?){
    var giftUrl = ""
    gift?.let {
        giftUrl =  it.giftIcon
    }
    val painter = rememberAsyncImagePainter(
        model = giftUrl
    )
    ImageAvatar(
        modifier = Modifier.size(20.dp,20.dp),
        painter = if (giftUrl.isEmpty())painterResource(id = R.drawable.icon_bottom_bar_gift) else painter,
        shape = RoundedCornerShape(0.dp)
    )
}

fun convertMillisTo24HourFormat(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}