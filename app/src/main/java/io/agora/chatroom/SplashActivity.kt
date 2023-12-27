package io.agora.chatroom

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import io.agora.chatroom.compose.dialog.SimpleDialog
import io.agora.chatroom.compose.indicator.LoadingIndicator
import io.agora.chatroom.compose.utils.WindowConfigUtils
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.utils.SPUtils
import io.agora.chatroom.viewmodel.SplashViewModel
import io.agora.chatroom.viewmodel.dialog.DialogViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ChatroomUIKitTheme {
                val isDarkTheme = SPUtils.getInstance(LocalContext.current.applicationContext as Application).getCurrentThemeStyle()
                WindowConfigUtils(
                    isDarkTheme = !isDarkTheme,
                    statusBarColor = Color.Transparent,
                    nativeBarColor = Color.Transparent
                )
                val viewModel = SplashViewModel(LocalContext.current.applicationContext as Application)
                val dialogViewModel = viewModel(DialogViewModel::class.java)
                dialogViewModel.title = stringResource(id = R.string.login_result_failed)
                dialogViewModel.confirmText = stringResource(id = R.string.re_login)
                viewModel.login(onValueSuccess = { loginRes ->
                    skipToMain()
                }, onError = { code, msg ->
                    dialogViewModel.showDialog()
                })
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillHeight,
                    painter = painterResource(id = R.drawable.splash_bg),
                    contentDescription = "splash background")

                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (indicator) = createRefs()
                    if (viewModel.isLoading()) {
                        LoadingIndicator(
                            modifier = Modifier.constrainAs(indicator) {
                                bottom.linkTo(parent.bottom, margin = 106.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                        )
                    }
                }

                SimpleDialog(viewModel = dialogViewModel,
                    onConfirmClick = {
                        viewModel.login(onValueSuccess = { loginRes ->
                            skipToMain()
                        }, onError = { code, msg ->
                            dialogViewModel.showDialog()
                        })
                        dialogViewModel.dismissDialog()
                    })

            }
        }
    }

    private fun skipToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}