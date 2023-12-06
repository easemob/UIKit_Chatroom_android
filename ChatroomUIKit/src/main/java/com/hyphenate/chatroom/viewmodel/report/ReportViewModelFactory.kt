package com.hyphenate.chatroom.viewmodel.report

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.chatroom.UIChatroomService
import com.hyphenate.chatroom.uikit.R

class ReportViewModelFactory(
    private val context: Context,
    private val service: UIChatroomService
): ViewModelProvider.Factory {
    /**
     * The list of factories that can build [ViewModel]s that our Report feature components use.
     */
    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        ComposeReportViewModel::class.java to {
            ComposeReportViewModel(
                reportTag = context.resources.getStringArray(R.array.report_reason).toList(),
                service = service
            )
        }
    )

    /**
     * Creates the required [ViewModel] for our use case, based on the [factories] we provided.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException(
                "ReportViewModelFactory can only create instances of " +
                        "the following classes: ${factories.keys.joinToString { it.simpleName }}"
            )

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}