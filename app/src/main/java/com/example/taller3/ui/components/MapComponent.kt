package com.example.taller3.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taller3.R
import com.example.taller3.data.Coordinate
import com.example.taller3.data.UserInfo
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotationGroup
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.annotationAnchors
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.maps.ViewAnnotationAnchor

private fun Coordinate.toPoint() = Point.fromLngLat(longitude, latitude)

@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    defaultZoom: Double = 16.0,
    tracking: Boolean = false,
    myName: String = "",
    myPhoto: String? = null,
    myRoute: List<Coordinate> = emptyList(),
    others: Map<String, UserInfo> = emptyMap(),
    otherRoutes: Map<String, List<Coordinate>> = emptyMap(),
) {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(defaultZoom)
            center(Point.fromLngLat(-74.0641630119028, 4.627389316647156))
        }
    }
    val standardStyleState = rememberStandardStyleState {
        configurationsState.apply {
            show3dObjects = BooleanValue(true)
            showPointOfInterestLabels = BooleanValue(false)
            showRoadLabels = BooleanValue(true)
        }
    }

    val myPosition = myRoute.lastOrNull()

    // The first position of a session centers the camera on the user.
    LaunchedEffect(myPosition == null) {
        myPosition?.let {
            mapViewportState.flyTo(cameraOptions { center(it.toPoint()); zoom(defaultZoom) })
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            scaleBar = { },
            compass = { },
            style = {
                MapboxStandardStyle(standardStyleState = standardStyleState)
            },
            logo = { },
            attribution = { },
        ) {
            // The puck shows the position while the tracking is off; the photo marker replaces it afterwards.
            MapEffect(tracking) { mapView ->
                mapView.location.updateSettings {
                    pulsingEnabled = true
                    puckBearing = PuckBearing.COURSE
                    locationPuck = createDefault2DPuck(withBearing = true)
                    enabled = !tracking
                    puckBearingEnabled = true
                }
            }

            if (myRoute.size > 1) {
                RoutePolyline(myRoute, MaterialTheme.colorScheme.primary)
            }
            otherRoutes.forEach { (uid, route) ->
                if (route.size > 1) RoutePolyline(route, colorForUser(uid))
            }

            if (tracking && myPosition != null) {
                MarkerAnnotation(myPosition) {
                    UserMarker(myName, myPhoto, MaterialTheme.colorScheme.primary)
                }
            }
            others.forEach { (uid, user) ->
                MarkerAnnotation(Coordinate(user.latitude, user.longitude)) {
                    UserMarker(user.displayName, user.photo, colorForUser(uid))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (myPosition != null) {
                    mapViewportState.flyTo(cameraOptions { center(myPosition.toPoint()); zoom(defaultZoom) })
                } else {
                    mapViewportState.transitionToFollowPuckState()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.MyLocation,
                contentDescription = stringResource(R.string.map_component_center_icon),
            )
        }
    }
}

@Composable
private fun RoutePolyline(route: List<Coordinate>, color: Color) {
    PolylineAnnotationGroup(
        annotations = listOf(PolylineAnnotationOptions().withPoints(route.map { it.toPoint() }))
    ) {
        lineCap = LineCap.ROUND
        lineWidth = 8.0
        lineColor = color
        lineBorderWidth = 2.0
        lineBorderColor = Color.White
        lineJoin = LineJoin.ROUND
    }
}

@Composable
private fun MarkerAnnotation(position: Coordinate, content: @Composable () -> Unit) {
    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(position.toPoint())
            allowOverlap(true)
            annotationAnchors({ anchor(ViewAnnotationAnchor.CENTER) })
        },
        content = content,
    )
}
