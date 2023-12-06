package com.hyphenate.chatroom.compose.utils

import android.content.Context
import com.google.gson.Gson
import com.hyphenate.chatroom.model.gift.AUIGiftTabInfo
import java.nio.charset.Charset


fun parsingGift(context: Context): List<AUIGiftTabInfo> {
    val assetManager = context.assets
    val jsonFile = "giftEntity.json"
    val inputStream = assetManager.open(jsonFile)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val jsonString = String(buffer, Charset.forName("UTF-8"))

    val gson = Gson()
    return gson.fromJson(jsonString, Array<AUIGiftTabInfo>::class.java).toList()
}