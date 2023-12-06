package com.hyphenate.chatroom.viewmodel.report

import androidx.compose.runtime.mutableStateOf
import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.model.report.UIReportEntity
import com.hyphenate.chatroom.service.OnError
import com.hyphenate.chatroom.service.OnSuccess
import com.hyphenate.chatroom.viewmodel.menu.BottomSheetViewModel

class ComposeReportViewModel(
    private val reportTag:List<String>,
    private val service: UIChatroomService
): BottomSheetViewModel<String>(contentList = reportTag) {

    private val _msgId = mutableStateOf("")
    val reportMsgId = _msgId

    fun setReportMsgId(msgId:String){
        _msgId.value = msgId
    }

    /**
     * Report the message to the server.
     */
    fun reportMessageToServer(report: UIReportEntity, onSuccess: OnSuccess = {}, onError: OnError = {_, _ ->}) {
        service.getChatService().reportMessage(
            reportMsgId.value,
            report.tag,
            report.reason,
            onSuccess = {
                onSuccess.invoke()
            }, onError = {code, error ->
                onError.invoke(code, error)
            }
        )
    }

}