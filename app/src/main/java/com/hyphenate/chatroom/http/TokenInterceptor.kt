package com.hyphenate.chatroom.http

import android.app.Application
import com.hyphenate.chatroom.utils.SPUtils
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val context = com.hyphenate.chatroom.ChatroomUIKitClient.getInstance().getContext().context
            ?: return chain.proceed(chain.request())
        val token = SPUtils.getInstance(context.applicationContext as Application).getToken()
        if (token.isEmpty()) {
            return chain.proceed(chain.request())
        }
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}