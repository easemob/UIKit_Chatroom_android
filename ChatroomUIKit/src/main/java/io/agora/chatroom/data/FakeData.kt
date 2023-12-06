package io.agora.chatroom.data

import android.content.Context
import com.google.gson.Gson
import io.agora.chatroom.model.UIComposeSheetItem
import io.agora.chatroom.model.gift.AUIGiftTabInfo
import io.agora.chatroom.uikit.R
import java.nio.charset.Charset

val initialLongClickMenu = listOf(
    UIComposeSheetItem(id = R.id.action_menu_translate, title = "Translate"),
    UIComposeSheetItem(id = R.id.action_menu_recall, title = "Delete"),
    UIComposeSheetItem(id = R.id.action_menu_mute, title = "Mute"),
    UIComposeSheetItem(id = R.id.action_menu_report, title = "Report", isError = true)
)

val testMarqueeList = mutableListOf(
    "Compose has finally added support for Marquee! It's soo easy to implement!",
    "Notification 1 Notification 1 Notification 1 Notification 1",
    "Notification 2 Notification 2 Notification 2 Notification 2",
)

val testMenuList = listOf(
    UIComposeSheetItem(id = 0, title = "Item 1"),
    UIComposeSheetItem(id = 1, title = "Item 2"),
    UIComposeSheetItem(id = 2, title = "Item 3")
)

val testMenuList1 = listOf(
    UIComposeSheetItem(id = 0, title = "Item 1"),
    UIComposeSheetItem(id = 1, title = "Item 2"),
    UIComposeSheetItem(id = 2, title = "Item 3"),
    UIComposeSheetItem(id = 3, title = "Item 4")
)

val reportTagList = listOf(
    "Unwelcome commercial content or spam",
    "Pornographic or explicit content",
    "Child abuse",
    "Hate speech or graphic violence",
    "Promote terrorism",
    "Child abuse",
    "Harassment or bullying",
)

enum class LanguageType(val code: String) {
    Chinese("zh-Hans"),
    Chinese_traditional("zh-Hant"),
    English("en"),
    Russian("ru"),
    German("de"),
    French("fr"),
    Japanese("ja"),
    Korean("ko"),
    Auto("auto")
}