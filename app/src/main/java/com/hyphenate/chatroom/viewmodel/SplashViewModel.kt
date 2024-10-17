package com.hyphenate.chatroom.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hyphenate.chatroom.bean.LoginRes
import com.hyphenate.chatroom.http.ChatroomHttpManager
import com.hyphenate.chatroom.http.LoginReq
import com.hyphenate.chatroom.http.toLoginReq
import com.hyphenate.chatroom.service.ChatError
import com.hyphenate.chatroom.service.ChatroomUIKitClient
import com.hyphenate.chatroom.service.OnError
import com.hyphenate.chatroom.service.OnSuccess
import com.hyphenate.chatroom.service.OnValueSuccess
import com.hyphenate.chatroom.service.model.UserInfoProtocol
import com.hyphenate.chatroom.utils.SPUtils
import com.hyphenate.chatroom.utils.UserInfoGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(
    private val context: Application
):ViewModel() {
    private val _loading = mutableStateOf(true)

    fun login(username:String,token:String,onSuccess: OnSuccess = {}, onError: OnError = { _, _ -> }){
        _loading.value = true
        val userInfoProtocol = UserInfoProtocol(
            username,
            UserInfoGenerator.randomNickname(context),
            UserInfoGenerator.randomAvatarUrl(context)
        )
        ChatroomUIKitClient.getInstance().login(
            userInfoProtocol,
            token,
            onSuccess = {
                ChatroomUIKitClient.getInstance().updateUserInfo(
                    userInfoProtocol,
                    onSuccess = {
                        Log.e("SplashViewModel","updateUserInfo onSuccess")
                    },
                    onError = { error,code->
                        Log.e("SplashViewModel","updateUserInfo onError $error $code")
                    }
                )
                onSuccess.invoke()
            },
            onError= { code, msg ->
                if (code == ChatError.USER_ALREADY_LOGIN) {
                    ChatroomUIKitClient.getInstance().logout(onSuccess = {
                        ChatroomUIKitClient.getInstance().login(
                            userInfoProtocol,
                            token,
                            onSuccess = {
                                onSuccess.invoke()
                            }, onError = {code, error ->
                                onError.invoke(code, error)
                            })
                    }, onError = { code, error ->
                        onError.invoke(code, error)
                    })
                }else {
                    onError.invoke(code, msg)
                }
                Log.e("SplashViewModel", "onError: $code $msg")
            })
    }

    fun login(onValueSuccess: OnValueSuccess<LoginRes> = {}, onError: OnError = { _, _ -> }) {
        _loading.value = true
        var loginReq = LoginReq("", "", "")
        SPUtils.getInstance(context).getUerInfo()?.let {
            loginReq = it.toLoginReq()
        }
        Log.e("SplashViewModel", "login loginReq: $loginReq")
        if (loginReq.username.isEmpty()) {
            loginReq = LoginReq(UserInfoGenerator.generateUserId(),
                UserInfoGenerator.randomNickname(context),
                UserInfoGenerator.randomAvatarUrl(context))
            SPUtils.getInstance(context).saveUserInfo(loginReq)
        }
        val call = ChatroomHttpManager.getService().login(loginReq)
        call.enqueue(object : Callback<LoginRes> {
            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                val body = response.body()
                _loading.value = false
                if (body != null) {
                    Log.e("SplashViewModel", "onResponse: $body")
                    SPUtils.getInstance(context).saveToken(body.access_token)
                    val userInfoProtocol = UserInfoProtocol(loginReq.username, loginReq.nickname, loginReq.icon_key)
                    ChatroomUIKitClient.getInstance().login(
                        userInfoProtocol,
                        body.access_token,
                        onSuccess = {
                            ChatroomUIKitClient.getInstance().updateUserInfo(
                                userInfoProtocol,
                                onSuccess = {
                                    Log.e("SplashViewModel","updateUserInfo onSuccess")
                                },
                                onError = { error,code->
                                    Log.e("SplashViewModel","updateUserInfo onError $error $code")
                                }
                            )
                            onValueSuccess.invoke(body)
                        },
                        onError= { code, msg ->
                            if (code == ChatError.USER_ALREADY_LOGIN) {
                                ChatroomUIKitClient.getInstance().logout(onSuccess = {
                                    ChatroomUIKitClient.getInstance().login(
                                        UserInfoProtocol(loginReq.username, loginReq.nickname, loginReq.icon_key),
                                        body.access_token,
                                        onSuccess = {
                                            onValueSuccess.invoke(body)
                                        }, onError = {code, error ->
                                            onError.invoke(code, error)
                                        })
                                }, onError = { code, error ->
                                    onError.invoke(code, error)
                                })
                            }else {
                                onError.invoke(code, msg)
                            }
                        Log.e("SplashViewModel", "onError: $code $msg")
                    })
                } else {
                    Log.e("SplashViewModel", "onResponse: response body is null")
                    onError.invoke(-1, "response body is null")
                }
            }

            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                Log.e("SplashViewModel", "onFailure: ${t.message}")
                _loading.value = false
                onError.invoke(-1, t.message)
            }
        })
    }

    fun isLoading(): Boolean {
        return _loading.value
    }
}