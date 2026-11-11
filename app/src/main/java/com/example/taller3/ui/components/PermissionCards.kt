package com.example.taller3.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.taller3.R
import com.example.taller3.ui.theme.AppTheme

@Composable
fun NoPermissionCard(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.no_permission_card_text),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun RequestPermissionCard(modifier: Modifier = Modifier, onRequestPermission: () -> Unit = {}) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onTertiary,
        ),
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.request_permission_card_text),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            OutlinedButton(
                onClick = onRequestPermission,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary,
                ),
            ) {
                Text(text = stringResource(R.string.request_permission_card_button))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PermissionCardsPreview() {
    AppTheme {
        Column {
            NoPermissionCard()
            RequestPermissionCard()
        }
    }
}
