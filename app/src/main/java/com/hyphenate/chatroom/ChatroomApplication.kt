package com.hyphenate.chatroom

import android.app.Application
import com.hyphenate.chatroom.commons.GlobalConfig
import com.hyphenate.chatroom.data.LanguageType
import com.hyphenate.chatroom.service.ChatSDKOptions
import com.hyphenate.chatroom.service.ChatroomUIKitClient
import com.hyphenate.chatroom.service.ChatroomUIKitOptions
import com.hyphenate.chatroom.service.UiOptions
import java.util.Locale

class ChatroomApplication : Application() {

    private val activityLifecycleCallbacks by lazy { UserActivityLifecycleCallbacks() }

    override fun onCreate() {
        super.onCreate()
        val locale: Locale = Locale.getDefault()
        val language: String = locale.language
        var currentLanguage = when (language) {
            "zh" -> {
                LanguageType.Chinese.code
            }
            "en" -> {
                LanguageType.English.code
            }
            else -> {
                GlobalConfig.targetLanguage.code
            }
        }
        for (value in LanguageType.values()) {
            if (language == value.code){
                currentLanguage = language
            }
        }
        val chatroomUIKitOptions = ChatroomUIKitOptions(
            chatOptions = ChatSDKOptions(enableDebug = true),
            uiOptions = UiOptions(
                targetLanguageList = listOf(currentLanguage),
                chatBarrageShowGift = false,
            )
        )

        ChatroomUIKitClient.getInstance().setUp(
            applicationContext = this,
            options = chatroomUIKitOptions,
            appKey = BuildConfig.CHATROOM_APP_KEY
        )

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun getUserActivityLifecycleCallbacks(): UserActivityLifecycleCallbacks {
        return activityLifecycleCallbacks
    }

}