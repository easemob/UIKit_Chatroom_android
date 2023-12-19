package io.agora.chatroom.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.UIChatroomService
import io.agora.chatroom.compose.dialog.SimpleDialog
import io.agora.chatroom.compose.drawer.ComposeMenuBottomSheet
import io.agora.chatroom.compose.input.SearchInputFiled
import io.agora.chatroom.compose.participant.MembersPage
import io.agora.chatroom.compose.participant.MutedListPage
import io.agora.chatroom.compose.utils.WindowConfigUtils
import io.agora.chatroom.service.ChatLog
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.dialog.DialogViewModel
import io.agora.chatroom.viewmodel.member.MemberListViewModel
import io.agora.chatroom.viewmodel.member.MemberViewModelFactory
import io.agora.chatroom.viewmodel.member.MutedListViewModel
import io.agora.chatroom.viewmodel.menu.MenuViewModelFactory
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel

class UISearchActivity : ComponentActivity() {
    private var roomId: String? = ""
    private var title: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        roomId = intent.getStringExtra(KEY_ROOM_ID)
        title = intent.getStringExtra(KEY_TITLE)
        if (roomId.isNullOrEmpty() || title.isNullOrEmpty()) {
            finish()
            return
        }

        setContent {
            ChatroomUIKitTheme {
                WindowConfigUtils(
                    isDarkTheme = !ChatroomUIKitClient.getInstance().getCurrentTheme(),
                    statusBarColor = Color.Transparent,
                    nativeBarColor = ChatroomUIKitTheme.colors.background,
                )
                SearchScaffold(this, roomId!!, title!!)
            }
        }
    }

    companion object {
        private const val KEY_ROOM_ID = "roomId"
        private const val KEY_TITLE = "title"
        const val TAG = "UISearchActivity"

        fun createIntent(
            context: Context,
            roomId: String,
            title: String
        ): Intent {
            return Intent(context, UISearchActivity::class.java).apply {
                putExtra(KEY_ROOM_ID, roomId)
                putExtra(KEY_TITLE, title)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchScaffold(context: Activity, roomId: String, title: String) {
        val service = UIChatroomService(ChatroomUIKitClient.getInstance().getContext().getCurrentRoomInfo())
        var isEmpty by rememberSaveable { mutableStateOf(false) }
        val isAdmin = ChatroomUIKitClient.getInstance().isCurrentRoomOwner()
        val viewModel = viewModel(MemberListViewModel::class.java, factory = MemberViewModelFactory(roomId = roomId, service = service, isRoomAdmin = isAdmin))
        val mutedViewModel = viewModel(MutedListViewModel::class.java, factory = MemberViewModelFactory(roomId = roomId, service = service, isRoomAdmin = isAdmin))
        val memberMenuViewModel = viewModel(RoomMemberMenuViewModel::class.java, factory = MenuViewModelFactory(isShowTitle = false))
        val dialogViewModel = DialogViewModel()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            containerColor = ChatroomUIKitTheme.colors.background,
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ChatroomUIKitTheme.colors.background,
                        titleContentColor = ChatroomUIKitTheme.colors.onBackground,
                    ),
                    title = {
                        SearchInputFiled(
                            value = "",
                            isRequestFocus = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                                .height(36.dp),
                            onValueChange = { newText ->
                                if (title == context.getString(R.string.member_management_participant)) {
                                    viewModel.searchUsers(newText, onSuccess = { result ->
                                        isEmpty = result.isEmpty() && newText.isNotEmpty()
                                    })
                                } else if (title == context.getString(R.string.member_management_mute)) {
                                    mutedViewModel.searchUsers(newText, true, onSuccess = { result ->
                                        isEmpty = result.isEmpty() && newText.isNotEmpty()
                                    })
                                }
                            },
                            onClearClick = {
                                isEmpty = true
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { context.finish() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_left),
                                tint = ChatroomUIKitTheme.colors.onBackground,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { innerPadding ->

            Surface(
                modifier = Modifier.padding(innerPadding).fillMaxSize()
                    .navigationBarsPadding(),
                color = ChatroomUIKitTheme.colors.background,
            ) {
                if (isEmpty) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.data_empty),
                            tint = ChatroomUIKitTheme.colors.onBackgroundHighest,
                            contentDescription = "Empty data"
                        )
                    }
                } else {
                    ComposeMenuBottomSheet(
                        modifier = Modifier.safeDrawingPadding(),
                        viewModel = memberMenuViewModel,
                        onListItemClick = { index,item ->
                            when(index){
                                0 -> {
                                    if (item.title == context.getString(R.string.menu_item_mute)){
                                        mutedViewModel.muteUser(memberMenuViewModel.user.userId,
                                            onSuccess = {
                                                memberMenuViewModel.closeDrawer()
                                                setOKResult(context)
                                            },
                                            onError = {code, error ->
                                                memberMenuViewModel.closeDrawer()
                                                ChatLog.e(TAG, "muteUser error: $code $error")
                                            }
                                        )
                                    }else if (item.title == context.getString(R.string.menu_item_unmute)){
                                        mutedViewModel.unmuteUser(memberMenuViewModel.user.userId,
                                            onSuccess = {
                                                memberMenuViewModel.closeDrawer()
                                                setOKResult(context)
                                            },
                                            onError = {code, error ->
                                                memberMenuViewModel.closeDrawer()
                                                ChatLog.e(TAG, "unmuteUser error: $code $error")
                                            }
                                        )
                                    }
                                }
                                1 -> {
                                    if (item.title == context.getString(R.string.menu_item_remove)){
                                        dialogViewModel.title = context.getString(R.string.dialog_title_remove_user,
                                            memberMenuViewModel.user.let { if (it.nickname.isNullOrEmpty()) it.userId else it.nickname }
                                        )
                                        dialogViewModel.showCancel = true
                                        dialogViewModel.showDialog()
                                    }
                                }
                            }
                        },
                        onDismissRequest = {
                            memberMenuViewModel.closeDrawer()
                        }
                    )

                    if (title == stringResource(id = R.string.member_management_participant)) {
                        MembersPage(
                            viewModel = viewModel,
                            tab = title,
                            onExtendClick = { tab, user ->
                                memberMenuViewModel.user = user
                                memberMenuViewModel.setMenuList(context, tab)
                                memberMenuViewModel.openDrawer()
                            },
                        )
                    } else if (title == stringResource(id = R.string.member_management_mute)) {
                        MutedListPage(
                            viewModel = mutedViewModel,
                            tab = title,
                            onExtendClick = { tab, user ->
                                memberMenuViewModel.user = user
                                memberMenuViewModel.setMenuList(context, tab)
                                memberMenuViewModel.openDrawer()
                            },
                        )
                    }

                }
            }

            SimpleDialog(
                viewModel = dialogViewModel,
                onConfirmClick = {
                    val model = if (title == context.getString(R.string.member_management_participant)) {
                        viewModel
                    } else {
                        mutedViewModel
                    }
                    model.removeUser(memberMenuViewModel.user.userId,
                        onSuccess = {
                            memberMenuViewModel.closeDrawer()
                            setOKResult(context)
                        },
                        onError = {code, error ->
                            memberMenuViewModel.closeDrawer()
                            ChatLog.e(TAG, "removeUser error: $code $error")
                        }
                    )
                    dialogViewModel.dismissDialog()
                },
                onCancelClick = {
                    dialogViewModel.dismissDialog()
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )
        }
    }

    private fun setOKResult(context: Activity) {
        context.setResult(Activity.RESULT_OK)
        context.finish()
    }
}


