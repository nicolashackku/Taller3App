package com.example.taller3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.taller3.R
import com.example.taller3.data.UserInfo
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.taller3.ui.theme.AppTheme

@Composable
fun UserPortrait(
    user: UserInfo,
    lastLogin: String,
    modifier: Modifier = Modifier,
    email: String? = null,
) {
    val name = user.displayName
    val imageUrl = user.photo
    Row(
        modifier = modifier.padding(12.dp).height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.user_portrait_profile_picture_image, name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .shadow(6.dp, RoundedCornerShape(12.dp))
                .border(4.dp, Color.White, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!email.isNullOrBlank()) PortraitDetail(Icons.Outlined.Email, email)
            if (user.phone != 0L) PortraitDetail(Icons.Outlined.Phone, user.phone.toString())
            Text(
                text = stringResource(R.string.user_portrait_last_login_text, lastLogin),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Light,
            )
        }
    }
}

@Composable
private fun PortraitDetail(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun UserPortraitPreview() {
    AppTheme {
        UserPortrait(
            user = UserInfo(displayName = "Jane Doe", phone = 3131234567L),
            email = "jane@example.com",
            lastLogin = "Oct 4, 2026 10:55",
        )
    }
}
