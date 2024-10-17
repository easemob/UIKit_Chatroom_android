package com.hyphenate.chatroom.service

data class ChatroomUIKitOptions(
    var chatOptions: ChatSDKOptions = ChatSDKOptions(),
    var uiOptions: UiOptions = UiOptions()
)

/**
 * The options for Chatroom UIKit.
 * @param enableDebug Whether to enable debug mode.
 * @param autoLogin Whether to automatically log in.
 * @param useUserProperties Whether to use user properties.
 */
data class ChatSDKOptions(
    // Is debug mode enabled
    var enableDebug: Boolean = false,
    // is autoLogin
    val autoLogin: Boolean = false,
    // Whether to use user attributes.
    val useUserProperties: Boolean = true
)

data class UiOptions(
    // Whether to show the gift information in the chat barrage area.
    var chatBarrageShowGift: Boolean = false,
    // Translate target language list
    val targetLanguageList:List<String> = mutableListOf(),
)