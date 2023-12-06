package io.agora.chatroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.agora.chatroom.compose.input.InputField
import io.agora.chatroom.compose.gift.LazyColumnList
import io.agora.chatroom.compose.utils.WindowConfigUtils
import io.agora.chatroom.service.ChatCallback
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.service.ChatConnectionListener
import io.agora.chatroom.service.ChatPageResult
import io.agora.chatroom.service.ChatValueCallback
import io.agora.chatroom.service.Chatroom
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.viewmodel.RequestListViewModel

class ChatroomListActivity: ComponentActivity() {
    private val hideLogin by lazy { mutableStateOf(false) }
    private val chatRoomListViewModel by lazy { RoomListViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatroomUIKitTheme {
                WindowConfigUtils()
                if (hideLogin.value) {
                    // show room list
                    showRoomList()
                } else {
                    if (ChatClient.getInstance().options.autoLogin && ChatClient.getInstance().isLoggedInBefore) {
                        hideLogin.value = true
                        showRoomList()
                    } else {
                        login()
                    }
                }

            }
        }

        initListener()
    }

    private fun initListener() {
        ChatClient.getInstance().addConnectionListener(object : ChatConnectionListener{
            override fun onConnected() {

            }

            override fun onDisconnected(errorCode: Int) {

            }

            override fun onLogout(errorCode: Int, info: String?) {
                super.onLogout(errorCode, info)
                ChatClient.getInstance().logout(true, object : ChatCallback {
                    override fun onSuccess() {
                        hideLogin.value = false
                        (application as ChatroomApplication).getUserActivityLifecycleCallbacks().finishAll()
                    }

                    override fun onError(error: Int, errorMsg: String) {
                        Log.d("Chatroom", "logout failed $error $errorMsg")
                    }
                })
            }

        })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun showRoomList() {
        chatRoomListViewModel.fetchPublicRoom()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ChatroomUIKitTheme.colors.primary,
                        titleContentColor = ChatroomUIKitTheme.colors.onPrimary,
                    ),
                    title = {
                        Text(
                            "Room List",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = { logout() }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) {
            LazyColumnList(
                modifier = Modifier.fillMaxSize().padding(it),
                viewModel = chatRoomListViewModel,
                onScrollChange = { listState ->
                    if (listState.isScrollInProgress && !listState.canScrollForward) {
                        chatRoomListViewModel.fetchMorePublicRoom()
                    }
                },
                contentPadding = PaddingValues(10.dp),
            ) { index, item ->

                Surface(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clickable {
                            skipToTarget(item)
                        },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                        .fillMaxWidth()
                        .background(ChatroomUIKitTheme.colors.background)
                        .height(50.dp)
                        .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center) {
                        Text(text = item.name, style = ChatroomUIKitTheme.typography.bodyLarge)
                        Text(text = item.id, style = ChatroomUIKitTheme.typography.bodyMedium)
                    }
                }

            }
        }
    }

    private fun logout() {
        ChatClient.getInstance().logout(true, object : ChatCallback {
            override fun onSuccess() {
                hideLogin.value = false
                (application as ChatroomApplication).getUserActivityLifecycleCallbacks().finishAll()
            }

            override fun onError(error: Int, errorMsg: String) {
                Log.d("Chatroom", "logout failed $error $errorMsg")
            }
        })
    }

    private fun skipToTarget(
        chatroom: Chatroom
    ) {
    }

    @Composable
    fun login() {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            var userId by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var passwordHidden by rememberSaveable { mutableStateOf(true) }

            InputField(
                value = userId,
                onValueChange = { newValue->
                    userId = newValue
                },
                placeholder = {
                    if (userId.isBlank()) {
                        Text(text = "UserId",
                            color = ChatroomUIKitTheme.colors.inputHint,
                            style = ChatroomUIKitTheme.typography.bodyLarge)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp))

            Spacer(modifier = Modifier.height(20.dp))

            InputField(
                value = password,
                onValueChange = { newValue->
                    password = newValue
                },
                visualTransformation =
                if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                placeholder = {
                    if (password.isBlank()) {
                        Text(text = "Password",
                            color = ChatroomUIKitTheme.colors.inputHint,
                            style = ChatroomUIKitTheme.typography.bodyLarge)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon =
                            if (passwordHidden) Icons.Filled.MailOutline else Icons.Filled.Email
                        // Please provide localized description for accessibility services
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Icon(imageVector = visibilityIcon, contentDescription = description)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp))

            Button(onClick = { login(userId, password) }, modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()) {
                Text(text = "Login")
            }

        }

    }

    private fun login(userId: String, password: String) {
        ChatClient.getInstance().login(userId, password, object : ChatCallback {
            override fun onSuccess() {
                // login success
                hideLogin.value = true
            }

            override fun onError(error: Int, errorMsg: String) {
                // login failed
            }
        })
    }
}

class RoomListViewModel(
    private val pageSize: Int = 50
): RequestListViewModel<Chatroom>() {

    private val pageNum by lazy { mutableIntStateOf(1) }
    private val hasMore by lazy { mutableStateOf(false) }

    fun fetchPublicRoom(onValueSuccess: OnValueSuccess<ChatPageResult<Chatroom>> = {}, onError: OnError = { _, _ ->}) {
        loading()
        clear()
        pageNum.intValue = 1
        ChatClient.getInstance().chatroomManager().asyncFetchPublicChatRoomsFromServer(pageNum.intValue, pageSize, object : ChatValueCallback<ChatPageResult<Chatroom>> {
            override fun onSuccess(value: ChatPageResult<Chatroom>) {
                hasMore.value = value.data.size >= pageSize
                add(value.data)
                onValueSuccess.invoke(value)
            }

            override fun onError(error: Int, errorMsg: String?) {
                error(error, errorMsg)
                onError.invoke(error, errorMsg)
            }
        })
    }

    fun fetchMorePublicRoom(onValueSuccess: OnValueSuccess<ChatPageResult<Chatroom>> = {}, onError: OnError = { _, _ ->}) {
        if (!hasMore.value) {
            onValueSuccess.invoke(ChatPageResult())
            return
        }
        loadMore()
        pageNum.intValue ++
        ChatClient.getInstance().chatroomManager().asyncFetchPublicChatRoomsFromServer(pageNum.intValue, pageSize, object : ChatValueCallback<ChatPageResult<Chatroom>> {
            override fun onSuccess(value: ChatPageResult<Chatroom>) {
                hasMore.value = value.data.size >= pageSize
                addMore(value.data)
                onValueSuccess.invoke(value)
            }

            override fun onError(error: Int, errorMsg: String?) {
                error(error, errorMsg)
                onError.invoke(error, errorMsg)
            }
        })
    }


}



