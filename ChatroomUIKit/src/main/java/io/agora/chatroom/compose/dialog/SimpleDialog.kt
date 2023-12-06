package io.agora.chatroom.compose.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.agora.chatroom.theme.ChatroomUIKitTheme
import io.agora.chatroom.theme.ChatroomUIKitTheme.colors
import io.agora.chatroom.theme.ChatroomUIKitTheme.typography
import io.agora.chatroom.uikit.R
import io.agora.chatroom.viewmodel.dialog.DialogViewModel


@Composable
fun SimpleDialog(
    modifier: Modifier = Modifier,
    viewModel: DialogViewModel,
    onConfirmClick: () -> Unit,
    onCancelClick: (() -> Unit)? = null,
    shape: Shape = ChatroomUIKitTheme.shapes.large,
    containerColor: Color = ChatroomUIKitTheme.colors.background,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    val showDialog = viewModel.isShowDialog
    if (!showDialog) {
        return
    }
    BaseDialog(
        onDismissRequest = { viewModel.dismissDialog() },
        confirmButton = {
            Button(
                modifier = Modifier
                    .padding(6.dp)
                    .sizeIn(minWidth = 80.dp, maxWidth = 150.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                ),
                onClick = { onConfirmClick() }) {
                Text(text = viewModel.confirmText.ifEmpty { stringResource(id = R.string.confirm) }, color = colors.onSplashBg)
            }
        },
        modifier = modifier,
        dismissButton = if (viewModel.showCancel){
            {
                OutlinedButton(
                    border = BorderStroke(
                        width = dimensionResource(id = R.dimen.outline_width),
                        color = colors.outline,
                    ),
                    modifier = Modifier
                        .padding(6.dp)
                        .sizeIn(minWidth = 80.dp, maxWidth = 150.dp),
                    onClick = { onCancelClick?.invoke() }) {
                    Text(text = viewModel.cancelText.ifEmpty { stringResource(id = R.string.cancel) }, color = colors.onBackground)
                }
            }
        } else null,
        icon = if (viewModel.icon != 0){
            {
                Icon(
                    painter = painterResource(id = viewModel.icon),
                    contentDescription = null
                )
            }
        }else null,
        title = if (viewModel.title.isNotBlank()){
            {
                Text(
                    text = viewModel.title,
                    style = typography.titleLarge,
                    color = colors.onBackground
                )
            }
        } else null,
        text = if (viewModel.text.isNotBlank()){
            {
                Text(
                    text = viewModel.text,
                    style = typography.bodyLarge,
                    color = colors.onBackground
                )
            }
        } else null,
        shape = shape,
        containerColor = containerColor,
        tonalElevation = tonalElevation,
        properties = properties
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = properties,
    ) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(PaddingValues(all = 16.dp))
            ) {
                icon?.let {
                    Box(
                        Modifier
                            .padding(PaddingValues(bottom = 16.dp))
                            .align(Alignment.CenterHorizontally)
                    ) {
                        icon()
                    }
                }
                title?.let {
                    Box(
                        // Align the title to the center when an icon is present.
                        Modifier
                            .padding(PaddingValues(bottom = 16.dp, top = 24.dp))
                            .align(Alignment.CenterHorizontally)
                    ) {
                        title()
                    }
                }
                text?.let {
                    Box(
                        Modifier
                            .weight(weight = 1f, fill = false)
                            .padding(PaddingValues(bottom = 24.dp))
                            .align(Alignment.Start)
                    ) {
                        text()
                    }
                }
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

@Preview
@Composable
fun previewSimpleDialog() {
    ChatroomUIKitTheme {
        SimpleDialog(
            viewModel = DialogViewModel(title = "Title", showCancel = true),
            onConfirmClick = {},
            onCancelClick = {},
        )
    }
}

