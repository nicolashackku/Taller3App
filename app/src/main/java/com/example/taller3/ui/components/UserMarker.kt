package com.example.taller3.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.taller3.R
import com.example.taller3.ui.theme.AppTheme

private val MarkerColors = listOf(
    Color(0xFFE53935), Color(0xFF8E24AA), Color(0xFF3949AB), Color(0xFF00897B),
    Color(0xFF43A047), Color(0xFFF4511E), Color(0xFF6D4C41), Color(0xFFD81B60),
)

fun colorForUser(uid: String): Color = MarkerColors[Math.floorMod(uid.hashCode(), MarkerColors.size)]

@Composable
fun UserMarker(
    name: String,
    photoUrl: String?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (photoUrl.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = stringResource(R.string.user_marker_photo_image, name),
                tint = Color.White,
                modifier = Modifier
                    .size(52.dp)
                    .shadow(6.dp, CircleShape)
                    .border(3.dp, color, CircleShape)
                    .clip(CircleShape)
                    .background(color)
                    .padding(8.dp),
            )
        } else {
            AsyncImage(
                model = photoUrl,
                contentDescription = stringResource(R.string.user_marker_photo_image, name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .shadow(6.dp, CircleShape)
                    .border(3.dp, color, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
        }
        Surface(
            shape = CircleShape,
            color = color,
            contentColor = Color.White,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .widthIn(max = 96.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserMarkerPreview() {
    AppTheme {
        UserMarker(name = "Jane", photoUrl = null, color = MarkerColors[0], modifier = Modifier.padding(16.dp))
    }
}
