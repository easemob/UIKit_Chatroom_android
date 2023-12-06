package io.agora.chatroom.viewmodel

import android.util.Log
import io.agora.chatroom.bean.CreateRoomReq
import io.agora.chatroom.bean.RequestListResp
import io.agora.chatroom.bean.RoomDetailBean
import io.agora.chatroom.http.ChatroomHttpManager
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatroomListViewModel :RequestListViewModel<RoomDetailBean>() {

    fun fetchRoomList(
        onSuccess: OnValueSuccess<List<RoomDetailBean>> = {},
        onError: OnError = { _, _ ->}
    ){
        clear()
        loading()
        val call = ChatroomHttpManager.getService().fetchRoomList()
        call.enqueue(object : Callback<RequestListResp<RoomDetailBean>> {
            override fun onResponse(
                call: Call<RequestListResp<RoomDetailBean>>,
                response: Response<RequestListResp<RoomDetailBean>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.e("MainActivity", "fetchRoomList: $body")
                    if (body != null) {
                        add(body.entities)
                        onSuccess.invoke(body.entities)
                    } else {
                        error(-1, "response body is null")
                        onError.invoke(-1, "response body is null")
                    }
                } else {
                    error(response.code(), response.message())
                    onError.invoke(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<RequestListResp<RoomDetailBean>>, t: Throwable) {
                error(-1, t.message)
                onError.invoke(-1, t.message)
            }
        })
    }

    /**
     * Create a chatroom
     */
    fun createChatroom(
        roomName: String,
        owner: String,
        onSuccess: OnValueSuccess<RoomDetailBean>,
        onError: OnError) {

        val call = ChatroomHttpManager.getService().createRoom(
            CreateRoomReq(name = roomName, owner = owner))
        call.enqueue(object : Callback<RoomDetailBean> {
            override fun onResponse(call: Call<RoomDetailBean>, response: Response<RoomDetailBean>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        onSuccess.invoke(result)
                    } else {
                        onError.invoke(-1, "response body is null")
                    }
                } else {
                    onError.invoke(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<RoomDetailBean>, t: Throwable) {
                onError.invoke(-1, t.message)
            }

        })

    }

}

