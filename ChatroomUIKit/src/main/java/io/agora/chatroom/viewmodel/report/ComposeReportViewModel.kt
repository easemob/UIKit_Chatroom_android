package io.agora.chatroom.viewmodel.report

import androidx.compose.runtime.mutableStateOf
import io.agora.chatroom.model.report.UIReportEntity
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnSuccess
import io.agora.chatroom.UIChatroomService
import io.agora.chatroom.viewmodel.menu.BottomSheetViewModel

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