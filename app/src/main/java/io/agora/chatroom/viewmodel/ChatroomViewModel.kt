package io.agora.chatroom.viewmodel

import android.util.Log
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.http.ChatroomHttpManager
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.UIChatroomService
import io.agora.chatroom.bean.RoomDetailBean
import io.agora.chatroom.service.ChatLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatroomViewModel(
    private val service: UIChatroomService,
    private val isDarkTheme:Boolean?,
):UIRoomViewModel(service,isDarkTheme) {

    private fun destroyRoom(
        onSuccess: OnSuccess = {},
        onError: OnError = { _, _ ->}
    ){
        val roomId = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId
        val call = ChatroomHttpManager.getService().destroyRoom(roomId)
        call.enqueue(object : Callback<RoomDetailBean> {
            override fun onResponse(call: Call<RoomDetailBean>, response: Response<RoomDetailBean>) {
                if (response.isSuccessful) {
                    onSuccess.invoke()
                    ChatLog.e("destroyRoom","destroyRoom onSuccess")
                }else{
                    onError.invoke(-1,"Service exception")
                }
            }

            override fun onFailure(call: Call<RoomDetailBean>, t: Throwable) {
                onError.invoke(-1, t.message)
            }
        })
    }

    /**
     * Finish living.
     */
    fun endLive(
        onSuccess: OnSuccess = {},
        onError: OnError = { _, _ ->}
    ){
        destroyRoom(onSuccess,onError)
//        service.getChatService().destroyChatroom(
//            roomId = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId,
//            onSuccess = {
//                Log.e("apex","endLive onSuccess")
//            },
//            onError = {code, error ->
//                Log.e("apex","endLive onError $code $error")
//                onError.invoke(code, error)
//            }
//        )
    }

    fun leaveChatroom(
        onSuccess: OnSuccess = {},
        onError: OnError = { _, _ ->}
    ){
        if (!ChatroomUIKitClient.getInstance().isCurrentRoomOwner()){
            service.getChatService().leaveChatroom(
                roomId = ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo().roomId,
                userId = ChatroomUIKitClient.getInstance().getCurrentUser().userId,
                onSuccess = {
                    onSuccess.invoke()
                },
                onError = {code, error ->
                    onError.invoke(code, error)
                }
            )
        }
    }
}