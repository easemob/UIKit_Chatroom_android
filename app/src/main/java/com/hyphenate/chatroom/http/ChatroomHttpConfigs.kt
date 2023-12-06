package com.hyphenate.chatroom.http

import com.hyphenate.chatroom.BuildConfig


val protocol: String = "https"
val baseUrl: String = "$protocol://${BuildConfig.REQUEST_HOST}/internal/appserver/"
val baseAvatarUrl: String = "https://a1.easemob.com/easemob/chatroom-uikit/chatfiles/"