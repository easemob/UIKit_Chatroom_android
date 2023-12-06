package com.hyphenate.chatroom

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.chatroom.bean.RoomDetailBean
import com.hyphenate.chatroom.compose.ChatroomList
import com.hyphenate.chatroom.compose.avatar.Avatar
import com.hyphenate.chatroom.compose.indicator.LoadingIndicator
import com.hyphenate.chatroom.compose.switch
import com.hyphenate.chatroom.compose.utils.WindowConfigUtils
import com.hyphenate.chatroom.model.UserInfoProtocol
import com.hyphenate.chatroom.service.ChatLog
import com.hyphenate.chatroom.theme.ChatroomUIKitTheme
import com.hyphenate.chatroom.utils.SPUtils
import com.hyphenate.chatroom.viewmodel.ChatroomListViewModel
import com.hyphenate.chatroom.viewmodel.RequestState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private val roomListViewModel by lazy {
        ViewModelProvider(this@MainActivity as ComponentActivity)[ChatroomListViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val isDark = SPUtils.getInstance(LocalContext.current.applicationContext as Application).getCurrentThemeStyle()
            var isDarkTheme by rememberSaveable {
                mutableStateOf(isDark)
            }
            val userDetail = SPUtils.getInstance(LocalContext.current.applicationContext as Application).getUerInfo()
            SPUtils.getInstance(LocalContext.current.applicationContext as Application).saveCurrentThemeStyle(isDarkTheme)
            ChatroomUIKitTheme(isDarkTheme = isDarkTheme) {
                WindowConfigUtils(
                    isDarkTheme = !isDarkTheme,
                    statusBarColor = ChatroomUIKitTheme.colors.background,
                    nativeBarColor = ChatroomUIKitTheme.colors.background
                )
                Scaffold(
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .background(ChatroomUIKitTheme.colors.backgroundHigh)
                                .wrapContentHeight()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = resources.getString(R.string.chatroom_list),
                                style = ChatroomUIKitTheme.typography.headlineLarge,
                                color = ChatroomUIKitTheme.colors.onBackground
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            if (roomListViewModel.getState is RequestState.Loading) {
                                LoadingIndicator()
                            } else {
                                Box(modifier = Modifier.clickable { roomListViewModel.fetchRoomList() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_refresh),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = ChatroomUIKitTheme.colors.onBackground
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            switch(
                                checked = !isDark,
                                onCheckedChange = {
                                    isDarkTheme = !it
                                    ChatroomUIKitClient.getInstance().setCurrentTheme(isDarkTheme)
                                },
                                modifier = Modifier
                                    .size(width = 54.dp, height = 28.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(ChatroomUIKitTheme.colors.backgroundHighest))

                        }
                    },
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .shadow(
                                    elevation = 36.dp,
                                    spotColor = Color(0x26464E53),
                                    ambientColor = Color(0x26464E53)
                                )

                                .shadow(
                                    elevation = 24.dp,
                                    spotColor = Color(0x14171A1C),
                                    ambientColor = Color(0x14171A1C)
                                )

                                .border(
                                    width = 0.5.dp,
                                    color = ChatroomUIKitTheme.colors.background
                                )

                                .background(ChatroomUIKitTheme.colors.background)
                                .height(66.dp)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically) {

                            Avatar(
                                imageUrl = userDetail?.avatarURL?:"",
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = userDetail?.nickname?:userDetail?.userId ?: "",
                                style = ChatroomUIKitTheme.typography.bodyLarge,
                                color = ChatroomUIKitTheme.colors.onBackground
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(modifier = Modifier
                                .width(84.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(24.dp))
                                .clickable { createRoom(roomListViewModel, userDetail) }
                                .background(ChatroomUIKitTheme.colors.primary),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.video_camera_splus),
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                    tint = ChatroomUIKitTheme.colors.onSplashBg
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = resources.getString(R.string.chatroom_create),
                                    style = ChatroomUIKitTheme.typography.bodyMedium,
                                    color = ChatroomUIKitTheme.colors.onSplashBg
                                )
                            }

                        }
                    }
                ) { padding ->
                    Surface(modifier = Modifier.padding(padding)) {
                        ChatroomList(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ChatroomUIKitTheme.colors.backgroundHigh),
                            viewModel = roomListViewModel,
                            onItemClick = { roomDetail: RoomDetailBean ->
                                skipToChat(roomDetail)
                            }
                        )
                    }

                }
            }
        }
    }

    private fun createRoom(viewModel: ChatroomListViewModel, userDetail: UserInfoProtocol?) {
        if (userDetail == null) {
            return
        }
        viewModel.createChatroom(
            roomName = resources.getString(R.string.default_room_name, userDetail.nickname),
            owner = userDetail.userId,
            onSuccess = { roomDetail ->
                skipToChat(roomDetail)
            },
            onError = { code, message ->
                ChatLog.e("MainActivity", "createChatroom: $code, $message")
            }
        )
    }

    private fun skipToChat(roomDetail: RoomDetailBean) {
        startActivity(
            com.hyphenate.chatroom.ChatroomActivity.createIntent(
                context = this@MainActivity,
                room = roomDetail,
            )
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            delay(200L)
            roomListViewModel.fetchRoomList()
        }
        runBlocking {
            delay(300L)
        }
    }
}
