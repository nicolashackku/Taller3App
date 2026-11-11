package com.example.taller3.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LocationState(
    val isRecording: Boolean = false,
    val route: List<Location> = emptyList()
)

class LocationViewModel : ViewModel() {
    private val interval = 3000L
    private val minDistance = 3F
    private val fastestInterval = 1000L

    private val _uiState = MutableStateFlow(LocationState())
    val state: StateFlow<LocationState> = _uiState.asStateFlow()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }

    fun setup(context: Context) {
        if (::fusedLocationClient.isInitialized) return
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).apply {
            setMinUpdateDistanceMeters(minDistance)
            setMinUpdateIntervalMillis(fastestInterval)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        }.build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                _uiState.update { currentState ->
                    currentState.copy(route = currentState.route.plus(locationResult.locations))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (_uiState.value.isRecording) return
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        _uiState.update { it.copy(isRecording = true) }
    }

    fun stopLocationUpdates() {
        if (!::fusedLocationClient.isInitialized) return
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _uiState.update { it.copy(isRecording = false) }
    }

    fun clearRoute() {
        _uiState.update { it.copy(route = emptyList()) }
    }
}
