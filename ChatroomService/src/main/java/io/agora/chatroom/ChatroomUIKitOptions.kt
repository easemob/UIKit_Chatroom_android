package io.agora.chatroom

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
    var enableDebug: Boolean = false,
    val autoLogin: Boolean = false,
    val useUserProperties: Boolean = true
)

data class UiOptions(
    var useGiftsInList: Boolean = false,
    val targetLanguageList:List<String> = mutableListOf(),
)