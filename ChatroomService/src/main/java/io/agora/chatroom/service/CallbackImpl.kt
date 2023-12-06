package io.agora.chatroom.service

import io.agora.chatroom.ChatroomResultEvent
import io.agora.chatroom.ChatroomUIKitClient


typealias OnSuccess = () -> Unit
typealias OnValueSuccess<T> = (value: T) -> Unit
typealias OnError = (code: Int, error: String?) -> Unit
typealias OnProgress = (progress: Int) -> Unit
class CallbackImpl(private val onSuccess: OnSuccess,
                   private val onError: OnError,
                   private val onProgress: OnProgress = {},
                   private val event: ChatroomResultEvent? = null):
    ChatCallback {
    override fun onSuccess() {
        onSuccess.invoke()
        event?.let { ChatroomUIKitClient.getInstance().callbackEvent(it, ChatError.EM_NO_ERROR, "") }
    }

    override fun onError(code: Int, error: String?) {
        onError.invoke(code, error)
        event?.let { ChatroomUIKitClient.getInstance().callbackEvent(it, code, error) }
    }

    override fun onProgress(progress: Int, status: String?) {
        onProgress.invoke(progress)
    }
}

class ValueCallbackImpl<T>(private val onSuccess: OnValueSuccess<T>,
                           private val onError: OnError,
                           private val onProgress: OnProgress = {},
                           private val event: ChatroomResultEvent? = null): ChatValueCallback<T> {
    override fun onSuccess(value: T) {
        onSuccess.invoke(value)
        event?.let { ChatroomUIKitClient.getInstance().callbackEvent(it, ChatError.EM_NO_ERROR, "") }
    }

    override fun onError(error: Int, errorMsg: String?) {
        onError.invoke(error, errorMsg)
        event?.let { ChatroomUIKitClient.getInstance().callbackEvent(it, error, errorMsg) }
    }

    override fun onProgress(progress: Int, status: String?) {
        onProgress.invoke(progress)
    }
}