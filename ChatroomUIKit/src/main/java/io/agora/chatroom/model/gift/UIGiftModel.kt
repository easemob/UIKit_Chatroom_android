package io.agora.chatroom.model.gift

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import io.agora.chatroom.service.GiftEntityProtocol
import java.io.Serializable

data class AUIGiftTabInfo constructor(
    @SerializedName("tabId") val tabId: Int,
    @SerializedName("displayName") val tabName: String,
    @SerializedName("gifts") val gifts: List<GiftEntityProtocol>
): Serializable

private val selectMap = mutableMapOf<String, Boolean>()
internal var GiftEntityProtocol.selected: Boolean
    @RequiresApi(Build.VERSION_CODES.N)
    get() = selectMap.getOrDefault(giftId, false)
    set(value) = selectMap.set(giftId, value)