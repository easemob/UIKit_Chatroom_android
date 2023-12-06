package io.agora.chatroom.model.emoji

data class UIRegexEntity(
    val startIndex:Int,
    val endIndex:Int,
    val emojiTag:String,
    val emojiIcon:Int,
    var count:Int = 1
)
