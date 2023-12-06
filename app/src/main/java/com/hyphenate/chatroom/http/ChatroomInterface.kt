package com.hyphenate.chatroom.http

import com.hyphenate.chatroom.bean.BroadcastReq
import com.hyphenate.chatroom.bean.LoginRes
import com.hyphenate.chatroom.bean.RequestListResp
import com.hyphenate.chatroom.bean.RoomDetailBean
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatroomInterface {
    @POST("liverooms/user/login")
    fun login(@Body req: Any): Call<LoginRes>

    @POST("liverooms")
    fun createRoom(@Body req: Any): Call<RoomDetailBean>

    @GET("liverooms")
    fun fetchRoomList(): Call<RequestListResp<RoomDetailBean>>

    @POST("liverooms/broadcast")
    fun broadcast(@Body req: Any): Call<BroadcastReq>

    @DELETE("liverooms/{liveroomid}")
    fun destroyRoom(@Path("liveroomid") id:String): Call<RoomDetailBean>
}