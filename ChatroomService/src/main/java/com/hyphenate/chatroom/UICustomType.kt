package com.hyphenate.chatroom

import android.text.TextUtils
import com.hyphenate.chatroom.model.UIConstant

enum class UICustomMsgType(name: String) {
    /**
     * 系统消息 成员加入
     */
    CHATROOMUIKITUSERJOIN(UIConstant.CHATROOM_UIKIT_USER_JOIN),

    /**
     * 礼物消息
     */
    CHATROOMUIKITGIFT(UIConstant.CHATROOM_UIKIT_GIFT);

    companion object {
        fun fromName(name: String?): UICustomMsgType? {
            for (type in UICustomMsgType.values()) {
                if (TextUtils.equals(type.name, name)) {
                    return type
                }
            }
            return null
        }
    }
}