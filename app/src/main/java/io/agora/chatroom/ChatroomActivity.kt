package io.agora.chatroom

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.bean.RoomDetailBean
import io.agora.chatroom.compose.ComposeChatroom
import io.agora.chatroom.compose.VideoPlayerCompose
import io.agora.chatroom.compose.avatar.Avatar
import io.agora.chatroom.compose.defaultMembersViewModelFactory
import io.agora.chatroom.compose.dialog.SimpleDialog
import io.agora.chatroom.compose.broadcast.ComposeGlobalBroadcast
import io.agora.chatroom.compose.utils.WindowConfigUtils
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.service.ChatError
import io.agora.chatroom.service.ChatroomChangeListener
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.transfer
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.ui.UISearchActivity
import io.agora.chatroom.utils.SPUtils
import io.agora.chatroom.viewmodel.ChatroomFactory
import io.agora.chatroom.viewmodel.ChatroomViewModel
import io.agora.chatroom.viewmodel.dialog.DialogViewModel
import io.agora.chatroom.viewmodel.gift.ComposeGiftListViewModel
import io.agora.chatroom.viewmodel.broadcast.GlobalBroadcastViewModel
import io.agora.chatroom.viewmodel.broadcast.GlobalBroadcastViewModelFactory
import io.agora.chatroom.viewmodel.member.MembersBottomSheetViewModel
import io.agora.chatroom.viewmodel.menu.RoomMemberMenuViewModel
import io.agora.chatroom.viewmodel.messages.MessagesViewModelFactory

class ChatroomActivity : ComponentActivity(), ChatroomResultListener, ChatroomChangeListener {

    private lateinit var room: RoomDetailBean
    private lateinit var service: UIChatroomService
    private val dialogViewModel by lazy { DialogViewModel() }

    private val roomViewModel by lazy {
        ViewModelProvider(this@ChatroomActivity as ComponentActivity,
            factory = ChatroomFactory(service = service)
        )[ChatroomViewModel::class.java]
    }

    private val giftViewModel by lazy {
        ViewModelProvider(this@ChatroomActivity as ComponentActivity,
            factory = MessagesViewModelFactory(context = this@ChatroomActivity, roomId = room.id,
                service = service))[ComposeGiftListViewModel::class.java]
    }

    private val memberViewModel by lazy {
        ViewModelProvider(this@ChatroomActivity as ComponentActivity,
            factory = MessagesViewModelFactory(context = this@ChatroomActivity, roomId = room.id,
                service = service))[RoomMemberMenuViewModel::class.java]
    }

    private val globalBroadcastModel by lazy {
        ViewModelProvider(this@ChatroomActivity as ComponentActivity,
            factory = GlobalBroadcastViewModelFactory(context = this@ChatroomActivity,
                service = service))[GlobalBroadcastViewModel::class.java]
    }

    private val launcherToSearch: ActivityResultLauncher<Intent> =
        (this@ChatroomActivity as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            roomViewModel.closeMemberSheet.value = result.resultCode == Activity.RESULT_OK
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        room = intent.getSerializableExtra(KEY_ROOM) as? RoomDetailBean ?: return


        val roomId = room.id
        if (roomId.isEmpty()) return

        val ownerId = room.owner
        if (ownerId.isEmpty()) return

        val roomName = room.name

        val owner = UserEntity(
            userId = ownerId,
            nickname = room.nickname,
            avatarURL = room.iconKey
        )

        Log.e("ChatroomActivity", "onCreate room: $room")
        Log.e("ChatroomActivity", "onCreate owner: $owner")

        val uiChatroomInfo = UIChatroomInfo(roomId, owner)
        service = UIChatroomService(uiChatroomInfo)

        ChatroomUIKitClient.getInstance().registerRoomResultListener(this)
        service.getChatService().bindListener(this)
        roomViewModel.registerChatroomChangeListener()
        giftViewModel.openAutoClear()
        globalBroadcastModel.registerChatroomChangeListener()

        roomViewModel.hideBg()

        setContent {
            ChatroomUIKitTheme{
                val isDarkTheme = SPUtils.getInstance(LocalContext.current.applicationContext as Application).getCurrentThemeStyle()
                WindowConfigUtils(
                    isDarkTheme = !isDarkTheme,
                    statusBarColor = Color.Transparent,
                    nativeBarColor = ChatroomUIKitTheme.colors.background
                )

                val membersBottomSheetViewModel: MembersBottomSheetViewModel = viewModel(MembersBottomSheetViewModel::class.java,
                factory = defaultMembersViewModelFactory(service.getRoomInfo().roomId, service,
                    ChatroomUIKitClient.getInstance().isCurrentRoomOwner(room.owner)))

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    VideoPlayerCompose(
                        uri = Uri.parse("android.resource://$packageName/${R.raw.video_example}"),
                        modifier = Modifier.fillMaxHeight()
                    )
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                ),
                                title = {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .height(38.dp)
                                            .background(
                                                color = ChatroomUIKitTheme.colors.barrageL20D10,
                                                shape = RoundedCornerShape(19.dp)
                                            ),
                                    ){

                                        Spacer(modifier = Modifier.width(3.dp))

                                        Avatar(
                                            imageUrl = room.iconKey,
                                            modifier = Modifier.size(32.dp, 32.dp),
                                            contentDescription = "owner avatar"
                                        )

                                        Column(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .wrapContentWidth()
                                                .wrapContentHeight(),
                                            verticalArrangement = Arrangement.Center

                                        ) {

                                            Text(
                                                text = roomName,
                                                modifier = Modifier
                                                    .wrapContentWidth()
                                                    .wrapContentHeight(),
                                                style = ChatroomUIKitTheme.typography.bodySmall.copy(
                                                    color = Color.White
                                                )
                                            )

                                            Text(
                                                text = if(room.nickname.isEmpty()) ownerId else room.nickname,
                                                modifier = Modifier
                                                    .wrapContentWidth()
                                                    .wrapContentHeight(),
                                                style = ChatroomUIKitTheme.typography.bodySmall.copy(
                                                    color = Color.White
                                                )
                                            )

                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = { checkIfOwner() }) {
                                        Icon(
                                            painter = painterResource(id = io.agora.chatroom.uikit.R.drawable.arrow_left),
                                            contentDescription = "callback navigation",
                                            tint = Color.White
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { membersBottomSheetViewModel.openDrawer() }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.icon_members),
                                            contentDescription = "chatroom member",
                                            tint = Color.White
                                        )
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        Surface(
                            modifier = Modifier
                                .padding(paddingValues = paddingValues)
                                .fillMaxHeight(),
                            color = Color.Transparent
                        ){
                            ComposeChatroom(
                                roomId = roomId,
                                roomOwner = owner.transfer(),
                                roomViewModel = roomViewModel,
                                giftListViewModel = giftViewModel,
                                memberMenuViewModel = memberViewModel,
                                service = service,
                                onMemberSheetSearchClick = {
                                        tab->
                                    launcherToSearch.launch(
                                        UISearchActivity.createIntent(
                                            this@ChatroomActivity,
                                            roomId = roomId,
                                            tab
                                        )
                                    )
                                }
                            )

                            ComposeGlobalBroadcast(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp)
                                ,
                                viewModel = globalBroadcastModel
                            )
                        }
                    }

                }

                SimpleDialog(
                    viewModel = dialogViewModel,
                    onConfirmClick = {
                        roomViewModel.endLive(onSuccess = {
                            runOnUiThread{finish()}
                        })
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
    }

    private fun checkIfOwner() {
        if (ChatroomUIKitClient.getInstance().isCurrentRoomOwner(room.owner)) {
            dialogViewModel.title = getString(R.string.dialog_title_end_live)
            dialogViewModel.showCancel = true
            dialogViewModel.showDialog()
        } else {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            exitRoom()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    companion object {
        private const val KEY_ROOM = "room"

        fun createIntent(
            context: Context,
            room: RoomDetailBean,
        ): Intent {
            return Intent(context, ChatroomActivity::class.java).apply {
                putExtra(KEY_ROOM, room)
            }
        }
    }

    override fun onEventResult(event: ChatroomResultEvent, errorCode: Int, errorMessage: String?) {
        if (event == ChatroomResultEvent.DESTROY_ROOM){
            exitRoom()
        }else if (event == ChatroomResultEvent.REPORT){
            if (errorCode == ChatError.EM_NO_ERROR){
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_report_success), Toast.LENGTH_SHORT).show()
                }
            }
        }else if (event == ChatroomResultEvent.SEND_MESSAGE){
            if (errorCode == ChatError.USER_MUTED ){
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_mute), Toast.LENGTH_SHORT).show()
                }
            }
        }else if (event == ChatroomResultEvent.MUTE_MEMBER){
            if (errorCode == ChatError.EM_NO_ERROR){
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_action_mute_success), Toast.LENGTH_SHORT).show()
                }
            }else{
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_action_mute_fail,"$errorCode $errorMessage"), Toast.LENGTH_SHORT).show()
                }
            }
        }else if (event == ChatroomResultEvent.UNMUTE_MEMBER){
            if (errorCode == ChatError.EM_NO_ERROR){
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_action_unmute_success), Toast.LENGTH_SHORT).show()
                }
            }else{
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_action_unmute_fail,"$errorCode $errorMessage"), Toast.LENGTH_SHORT).show()
                }
            }
        } else if (event == ChatroomResultEvent.RECALL_MESSAGE){
            if (errorCode != ChatError.EM_NO_ERROR){
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_action_unmute_success), Toast.LENGTH_SHORT).show()
                }
            }
        }else if (event == ChatroomResultEvent.KICK_MEMBER){
            if (errorCode == ChatError.EM_NO_ERROR){
                val userInfo = ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(memberViewModel.user.userId)
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_action_kick_user_success,userInfo.nickname?:userInfo.userId), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onUserBeKicked(roomId: String, userId: String) {
        if (roomId == room.id){
            if (!ChatroomUIKitClient.getInstance().isCurrentRoomOwner()){
                runOnUiThread {
                    Toast.makeText(this,resources.getString(R.string.chatroom_kick), Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        service.getChatService().unbindListener(this)
        globalBroadcastModel.unRegisterChatroomChangeListener()
        ChatroomUIKitClient.getInstance().unregisterRoomResultListener(this)
    }

    private fun exitRoom(){
        if (ChatroomUIKitClient.getInstance().isCurrentRoomOwner()){
            roomViewModel.endLive(
                onSuccess = {
                    finish()
                },
                onError = {code,error->
                    runOnUiThread {
                        Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            )
        }else{
            roomViewModel.leaveChatroom(
                onSuccess = {
                    runOnUiThread {
                        Toast.makeText(this,getString(R.string.chatroom_action_ended), Toast.LENGTH_SHORT).show()
                    }
                    finish()
                },
                onError = {code, error ->
                    finish()
                }
            )
        }
    }
}