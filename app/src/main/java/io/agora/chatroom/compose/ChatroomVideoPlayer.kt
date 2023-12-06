package io.agora.chatroom.compose

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.widget.CustomVideoView
import kotlinx.coroutines.launch

@Composable
fun VideoPlayerCompose(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ChatroomUIKitTheme.colors.background,
        modifier = modifier
    ) {
        val height = (LocalContext.current.resources.displayMetrics.heightPixels * 1.03).toInt()
        val width = LocalContext.current.resources.displayMetrics.widthPixels
        val scope = rememberCoroutineScope()
        var videoView: CustomVideoView? = null
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        var isVideoInited by rememberSaveable {mutableStateOf(false)}
        var isVideoPlaying by rememberSaveable {mutableStateOf(false)}

        AndroidView(
            factory = { context ->
                videoView = CustomVideoView(context)
                videoView!!.apply {
                    setVideoURI(uri)
                    val radio = height * 1.0 / width
                    val videoRadio = 16.0 / 9.0
                    if (radio > videoRadio) {
                        val videoWidth = (height / videoRadio).toInt()
                        setVideoSize(videoWidth, height)
                    }else {
                        val videoHeight = (width * videoRadio).toInt()
                        setVideoSize(width, videoHeight)
                    }
                    setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.isLooping = true
                    }
                    isVideoInited = true
                    start()
                    isVideoPlaying = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(null) {
            scope.launch {
                lifecycle.addObserver(object : DefaultLifecycleObserver {

                    override fun onStop(owner: LifecycleOwner) {
                        super.onStop(owner)
                        if (isVideoInited && isVideoPlaying) {
                            videoView?.apply {
                                if (isPlaying) {
                                    pause()
                                }
                                isVideoPlaying = false
                            }
                        }
                    }

                    override fun onResume(owner: LifecycleOwner) {
                        super.onResume(owner)
                        if (isVideoInited && !isVideoPlaying) {
                            videoView?.apply {
                                if (!isPlaying) {
                                    start()
                                    isVideoPlaying = true
                                }
                            }
                        }
                    }

                    override fun onDestroy(owner: LifecycleOwner) {
                        super.onDestroy(owner)
                        videoView?.stopPlayback()
                    }
                })
            }
        }

    }
}

