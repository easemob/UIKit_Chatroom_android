package com.hyphenate.chatroom.service.serviceImpl

import android.util.Log
import com.hyphenate.chatroom.service.CallbackImpl
import com.hyphenate.chatroom.service.ChatClient
import com.hyphenate.chatroom.service.ChatUserInfo
import com.hyphenate.chatroom.service.ChatValueCallback
import com.hyphenate.chatroom.service.ChatroomUIKitClient
import com.hyphenate.chatroom.service.OnError
import com.hyphenate.chatroom.service.OnSuccess
import com.hyphenate.chatroom.service.OnValueSuccess
import com.hyphenate.chatroom.service.UserService
import com.hyphenate.chatroom.service.UserStateChangeListener
import com.hyphenate.chatroom.service.model.UserInfoProtocol
import com.hyphenate.chatroom.service.model.toUser
import com.hyphenate.chatroom.service.model.transfer
import com.hyphenate.chatroom.service.transfer


class UserServiceImpl: UserService {
    private val listeners = mutableListOf<UserStateChangeListener>()
    private val userInfoManager by lazy { ChatClient.getInstance().userInfoManager() }
    @Synchronized
    override fun bindUserStateChangeListener(listener: UserStateChangeListener) {
        if (listeners.contains(listener)) {
            listeners.add(listener)
            ChatroomUIKitClient.getInstance().updateChatroomUserStateChangeListener(listeners)
        }
    }

    @Synchronized
    override fun unbindUserStateChangeListener(listener: UserStateChangeListener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener)
            ChatroomUIKitClient.getInstance().updateChatroomUserStateChangeListener(listeners)
        }
    }

    override fun getUserInfo(userId: String,
                             onSuccess: OnValueSuccess<UserInfoProtocol>,
                             onError: OnError
    ) {
        getUserInfoList(arrayListOf(userId), onSuccess = { onSuccess.invoke(it[0]) }, onError)
    }

    override fun getUserInfoList(
        userIdList: List<String>,
        onSuccess: OnValueSuccess<List<UserInfoProtocol>>,
        onError: OnError
    ) {
        userInfoManager.fetchUserInfoByUserId(userIdList.toTypedArray(), object :ChatValueCallback<Map<String, ChatUserInfo>> {
            override fun onSuccess(value: Map<String, ChatUserInfo>?) {
                val userEntities = value?.map {
                    it.value.transfer().transfer()
                } ?: listOf()
                onSuccess.invoke(userEntities)
            }

            override fun onError(error: Int, errorMsg: String?) {
                onError.invoke(error, errorMsg)
            }
        })
    }

    override fun updateUserInfo(
        userEntity: UserInfoProtocol,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        userInfoManager.updateOwnInfo(userEntity.transfer(), object :ChatValueCallback<String> {
            override fun onSuccess(value: String?) {
                onSuccess.invoke()
            }

            override fun onError(error: Int, errorMsg: String?) {
                onError.invoke(error, errorMsg)
            }
        })
    }

    override fun login(userId: String, token: String, onSuccess: OnSuccess, onError: OnError) {
        ChatClient.getInstance().loginWithToken(userId, token, CallbackImpl(onSuccess, onError))
    }

    override fun login(
        user: UserInfoProtocol,
        token: String,
        onSuccess: OnSuccess,
        onError: OnError
    ) {
        ChatroomUIKitClient.getInstance().getChatroomUser().setUserInfo(user.userId, user.toUser())
        ChatClient.getInstance().loginWithToken(user.userId, token, CallbackImpl(onSuccess, onError))
    }

    override fun logout(onSuccess: OnSuccess, onError: OnError) {
        ChatClient.getInstance().logout(true, CallbackImpl(onSuccess, onError))
    }
}