package com.example.taller3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller3.R
import com.example.taller3.data.Coordinate
import com.example.taller3.ui.components.MapComponent
import com.example.taller3.ui.components.NoPermissionCard
import com.example.taller3.ui.components.RequestPermissionCard
import com.example.taller3.viewmodels.AuthViewModel
import com.example.taller3.viewmodels.FirestoreEvent
import com.example.taller3.viewmodels.FirestoreViewModel
import com.example.taller3.viewmodels.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    firestoreViewModel: FirestoreViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
) {
    val context = LocalContext.current
    val locationPermission =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    val currentUser by authViewModel.currentUser.collectAsState()
    val firestoreState by firestoreViewModel.state.collectAsState()
    val locationState by locationViewModel.state.collectAsState()
    val uid = currentUser?.uid

    LaunchedEffect(Unit) {
        locationPermission.launchPermissionRequest()
    }
    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) locationViewModel.setup(context)
    }
    LaunchedEffect(firestoreViewModel) {
        firestoreViewModel.events.collect {
            if (it is FirestoreEvent.Error) {
                Toast.makeText(context, it.message.asString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Every new position of the tracking is stored in the database.
    LaunchedEffect(locationState.route.size) {
        val last = locationState.route.lastOrNull()
        if (uid != null && locationState.isRecording && last != null) {
            firestoreViewModel.updateUser(
                uid,
                mapOf("online" to true, "latitude" to last.latitude, "longitude" to last.longitude),
            )
        }
    }

    fun setTracking(enabled: Boolean) {
        if (uid == null) return
        if (enabled) {
            locationViewModel.startLocationUpdates()
            firestoreViewModel.updateUser(uid, mapOf("online" to true))
        } else {
            locationViewModel.stopLocationUpdates()
            locationViewModel.clearRoute()
            firestoreViewModel.updateUser(uid, mapOf("online" to false))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (locationPermission.status.isGranted) {
            MapComponent(
                tracking = locationState.isRecording,
                myName = firestoreState.user?.displayName.orEmpty(),
                myPhoto = firestoreState.user?.photo,
                myRoute = locationState.route.map { Coordinate(it.latitude, it.longitude) },
                others = firestoreState.onlineUsers,
                otherRoutes = firestoreState.routes,
            )
            ElevatedCard(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (locationState.isRecording) Icons.Filled.LocationOn
                        else Icons.Filled.LocationOff,
                        contentDescription = null,
                        tint = if (locationState.isRecording) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                    )
                    Text(
                        text = stringResource(
                            if (locationState.isRecording) R.string.home_screen_tracking_on_text
                            else R.string.home_screen_tracking_off_text
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Switch(
                        checked = locationState.isRecording,
                        onCheckedChange = ::setTracking,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (locationPermission.status.shouldShowRationale) {
                    RequestPermissionCard(onRequestPermission = {
                        locationPermission.launchPermissionRequest()
                    })
                } else {
                    NoPermissionCard()
                }
            }
        }
    }
}
