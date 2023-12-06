package io.agora.chatroom.utils

import android.content.Context
import io.agora.chatroom.R
import io.agora.chatroom.http.baseAvatarUrl
import kotlin.random.Random

object UserInfoGenerator {
    private val roomImageMap: MutableMap<String, Int> = mutableMapOf()
    fun generateUserId(): String {
        return generateRandomChars(16, "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray(), Random)
    }

    private fun generateRandomChars(count: Int, chars: CharArray, random: Random): String {
        val result = StringBuilder()
        for (i in 1..count) {
            result.append(chars[random.nextInt(chars.size)])
        }
        return result.toString()
    }

    fun randomAvatarUrl(context: Context): String {
        return "$baseAvatarUrl${randomArray(context.resources.getStringArray(R.array.avatar_url_end))}"
    }

    fun randomNickname(context: Context): String {
        return randomArray(context.resources.getStringArray(R.array.nickname_array))
    }

    private fun randomArray(source:Array<String>): String {
        source.let {
            return it[Random.nextInt(it.size)]
        }
    }

    /**
     * Get a random room image.
     */
    private fun randomRoomImage(context: Context, size: Int = 10): Int {
        val drawableId = context.resources.getIdentifier("cover_${Random.nextInt(size)}", "drawable", context.applicationInfo.packageName)
        return if (drawableId == 0) R.drawable.cover_1 else drawableId
    }

    /**
     * Get a room image by room id.
     */
    fun getRoomImage(context: Context, roomId: String): Int {
        if (roomImageMap.containsKey(roomId)) {
            return roomImageMap[roomId]!!
        }
        val drawableId = randomRoomImage(context)
        roomImageMap[roomId] = drawableId
        return drawableId
    }

}