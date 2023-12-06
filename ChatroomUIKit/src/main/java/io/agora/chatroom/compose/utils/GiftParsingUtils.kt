package io.agora.chatroom.compose.utils

import android.content.Context
import com.google.gson.Gson
import io.agora.chatroom.model.gift.AUIGiftTabInfo
import java.nio.charset.Charset


fun parsingGift(context: Context): List<AUIGiftTabInfo> {
    val assetManager = context.assets
    val jsonFile = "giftEntity.json" // JSON文件名
    // 读取JSON文件到字符串
    val inputStream = assetManager.open(jsonFile)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val jsonString = String(buffer, Charset.forName("UTF-8"))

    // 使用Gson解析JSON字符串
    val gson = Gson()
    return gson.fromJson(jsonString, Array<AUIGiftTabInfo>::class.java).toList()
}