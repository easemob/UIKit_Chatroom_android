package com.hyphenate.chatroom.service

import android.content.Context
import com.hyphenate.chatroom.service.model.UIChatroomInfo

class UIChatroomContext{
    private lateinit var mChatroomInfo: UIChatroomInfo
    var context: Context? = null

    companion object {
        const val TAG = "UIChatroomContext"
    }

    fun setRoomContext(context: Context){
        this.context = context
    }

    fun setCurrentRoomInfo(info: UIChatroomInfo){
        mChatroomInfo = info
    }

    fun isCurrentOwner(ownerId:String?):Boolean{
        if (ownerId?.isEmpty() == true){
            mChatroomInfo.roomOwner?.let {
                return it.userId == ChatroomUIKitClient.getInstance().getCurrentUser().userId
            }
        }else{
            return ChatroomUIKitClient.getInstance().getCurrentUser().userId == ownerId
        }
        return false
    }

    fun getCurrentRoomInfo(): UIChatroomInfo{
        return mChatroomInfo
    }

}