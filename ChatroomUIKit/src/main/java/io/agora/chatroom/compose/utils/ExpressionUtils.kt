package io.agora.chatroom.compose.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.agora.chatroom.data.RegexList
import io.agora.chatroom.data.emojiList
import io.agora.chatroom.data.emojiMap
import io.agora.chatroom.model.emoji.UIRegexEntity
import java.util.regex.Matcher
import java.util.regex.Pattern


object ExpressionUtils {
        const val ee_1 :String = "U+1F600"
        const val ee_2 :String = "U+1F604"
        const val ee_3 :String = "U+1F609"
        const val ee_4 :String = "U+1F62E"
        const val ee_5 :String = "U+1F92A"
        const val ee_6 :String = "U+1F60E"
        const val ee_7 :String = "U+1F971"
        const val ee_8 :String = "U+1F974"
        const val ee_9 :String = "U+263A"
        const val ee_10 :String = "U+1F641"
        const val ee_11 :String = "U+1F62D"
        const val ee_12 :String = "U+1F610"
        const val ee_13 :String = "U+1F607"
        const val ee_14 :String = "U+1F62C"
        const val ee_15 :String = "U+1F913"
        const val ee_16 :String = "U+1F633"
        const val ee_17 :String = "U+1F973"
        const val ee_18 :String = "U+1F620"
        const val ee_19 :String = "U+1F644"
        const val ee_20 :String = "U+1F910"
        const val ee_21 :String = "U+1F97A"
        const val ee_22 :String = "U+1F928"
        const val ee_23 :String = "U+1F62B"
        const val ee_24 :String = "U+1F637"
        const val ee_25 :String = "U+1F912"
        const val ee_26 :String = "U+1F631"
        const val ee_27 :String = "U+1F618"
        const val ee_28 :String = "U+1F60D"
        const val ee_29 :String = "U+1F922"
        const val ee_30 :String = "U+1F47F"
        const val ee_31 :String = "U+1F92C"
        const val ee_32 :String = "U+1F621"
        const val ee_33 :String = "U+1F44D"
        const val ee_34 :String = "U+1F44E"
        const val ee_35 :String = "U+1F44F"
        const val ee_36 :String = "U+1F64C"
        const val ee_37 :String = "U+1F91D"
        const val ee_38 :String = "U+1F64F"
        const val ee_39 :String = "U+2764"
        const val ee_40 :String = "U+1F494"
        const val ee_41 :String = "U+1F495"
        const val ee_42 :String = "U+1F4A9"
        const val ee_43 :String = "U+1F48B"
        const val ee_44 :String = "U+2600"
        const val ee_45 :String = "U+1F31C"
        const val ee_46 :String = "U+1F308"
        const val ee_47 :String = "U+2B50"
        const val ee_48 :String = "U+1F31F"
        const val ee_49 :String = "U+1F389"
        const val ee_50 :String = "U+1F490"
        const val ee_51 :String = "U+1F382"
        const val ee_52 :String = "U+1F381"

    fun containsKey(key:String):Boolean{
        var isContains = false
        emojiList.forEach {
            val startIndex = key.indexOf(it.emojiText)
            if (startIndex != -1){
                isContains = true
            }
        }
        return isContains
    }

    fun getSmiledText(content:String,inlineMap:MutableMap<String,InlineTextContent>): AnnotatedString {
        if (!containsKey(content)){
            return buildAnnotatedString {
                append(content)
            }
        }
        val annotatedText = buildAnnotatedString {
            var exchange = content
            var combination = ""
            var insertIndex = -1
            var oldTagLength = 0

            val insertMap = mutableMapOf<Int,UIRegexEntity>()
            val roleList = getRole(content)

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
                    addLienMap(roleList[i],inlineMap)
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
                            for (i in 0 until it.value.count) {
                                withStyle(style = SpanStyle()) {
                                    appendInlineContent(it.value.emojiTag)
                                }
                                addLienMap(it.value,inlineMap)
                            }
                        }
                    }
                }
            }

        }
        return annotatedText
    }

    fun addLienMap(regex:UIRegexEntity, inLienMap:MutableMap<String,InlineTextContent>){
        inLienMap[regex.emojiTag] = InlineTextContent(
            placeholder = Placeholder(18.sp,18.sp, PlaceholderVerticalAlign.Center),
            children = {
                Image(
                    modifier = Modifier
                        .size(18.dp, 18.dp)
                        .padding(start = 4.dp),
                    painter = painterResource(id = regex.emojiIcon),
                    contentDescription = "emoji"
                )
            }
        )
    }

    fun getEmojiRegex():String{
        // 将list元素拼接成正则表达式
        val regexBuilder = StringBuilder()
        regexBuilder.append("(")
        for (keyword in RegexList) {
            regexBuilder.append(keyword).append("|")
        }
        regexBuilder.deleteCharAt(regexBuilder.length - 1) // 移除最后一个多余的 |
        regexBuilder.append(")")
        return regexBuilder.toString()
    }

    fun getRole(content:String):MutableList<UIRegexEntity>{
        val list = mutableListOf<UIRegexEntity>()

        // 创建 Pattern 对象，用于匹配正则表达式
        val pattern: Pattern = Pattern.compile(getEmojiRegex())
        val matcher: Matcher = pattern.matcher(content)

        // 筛选匹配到的字符
        while (matcher.find()) {
            val matchedString = matcher.group()
            val icon = emojiMap[matchedString]
            val startIndex = matcher.start()
            val endIndex = matcher.end()
            icon?.let {
                val regexEntity = UIRegexEntity(
                    startIndex = startIndex,
                    endIndex = endIndex,
                    emojiTag = matchedString,
                    emojiIcon = it
                )
                list.add(regexEntity)
            }
        }
        return list
    }

}