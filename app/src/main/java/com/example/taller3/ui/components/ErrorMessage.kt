package com.example.taller3.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.taller3.R
import com.example.taller3.ui.theme.AppTheme

@Composable
fun ErrorMessage(
    message: String?,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    animationMillis: Int = 500,
) {
    // Keep the last message so it stays visible while the exit animation plays
    var lastMessage by remember { mutableStateOf(message.orEmpty()) }
    if (message != null) lastMessage = message

    AnimatedVisibility(
        visible = message != null,
        modifier = modifier.padding(4.dp),
        enter = slideInVertically(tween(animationMillis)) { -it } + fadeIn(tween(animationMillis)),
        exit = slideOutVertically(tween(animationMillis)) { -it } + fadeOut(tween(animationMillis)),
    ) {
        ErrorCard(lastMessage, onDismiss)
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.error_message_dismiss_icon),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
fun ErrorMessagePreview() {
    AppTheme {
        ErrorMessage(message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
    }
}
