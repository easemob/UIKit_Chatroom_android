package io.agora.chatroom.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.agora.chatroom.R
import io.agora.chatroom.theme.ChatroomUIKitTheme

@Composable
fun switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable {
        mutableStateOf(checked)
    }

    Row(modifier = modifier
        .clickable {
            selected = !selected
            onCheckedChange?.invoke(selected)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

        val selectedModifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(ChatroomUIKitTheme.colors.primary)
        val unSelectedModifier = Modifier
            .size(24.dp)

        Spacer(modifier = Modifier.width(2.dp))
        Box(modifier = if (selected) selectedModifier else unSelectedModifier,
            contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = R.drawable.icon_sun),
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
                tint = if (selected) Color.White else Color.Gray
            )
        }

        Box(modifier = if (selected) unSelectedModifier else selectedModifier,
            contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = R.drawable.icon_moon),
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
                tint = if (selected) Color.Gray else Color.White
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
    }
}