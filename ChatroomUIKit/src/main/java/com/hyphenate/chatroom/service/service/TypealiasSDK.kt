package com.hyphenate.chatroom.service

// manager
typealias ChatClient = com.hyphenate.chat.EMClient
typealias ChatRoomManager = com.hyphenate.chat.EMChatRoomManager
typealias ChatUserInfoManager = com.hyphenate.chat.EMUserInfoManager
typealias ChatOptions = com.hyphenate.chat.EMOptions

// callback
typealias ChatCallback = com.hyphenate.EMCallBack
typealias ChatValueCallback<T> = com.hyphenate.EMValueCallBack<T>
typealias ChatCursorResult<T> = com.hyphenate.chat.EMCursorResult<T>
typealias ChatPageResult<T> = com.hyphenate.chat.EMPageResult<T>

typealias ChatException = com.hyphenate.exceptions.HyphenateException
typealias ChatError =  com.hyphenate.EMError
typealias ChatLog = com.hyphenate.util.EMLog
// java bean
typealias Chatroom =  com.hyphenate.chat.EMChatRoom
typealias ChatUserInfo = com.hyphenate.chat.EMUserInfo

// ChatMessage
typealias ChatMessage = com.hyphenate.chat.EMMessage
typealias ChatType = com.hyphenate.chat.EMMessage.ChatType
typealias ChatMessageType = com.hyphenate.chat.EMMessage.Type
typealias ChatTextMessageBody = com.hyphenate.chat.EMTextMessageBody
typealias ChatCustomMessageBody = com.hyphenate.chat.EMCustomMessageBody

// Listeners
typealias ChatConnectionListener = com.hyphenate.EMConnectionListener
typealias ChatMessageListener = com.hyphenate.EMMessageListener
typealias ChatRoomChangeListener = com.hyphenate.EMChatRoomChangeListener