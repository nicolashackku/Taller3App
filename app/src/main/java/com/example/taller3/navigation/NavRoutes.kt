package com.example.taller3.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Routes (keys) that identify the content the user can navigate to.
 * Each route is pushed onto / popped off the back stack managed by [NavDisplay].
 *
 * Routes are `@Serializable` and implement [NavKey] so the back stack can be
 * persisted via `rememberNavBackStack`, surviving configuration changes and
 * process death.
 */
@Serializable
sealed class Routes : NavKey {
    @Serializable
    data object Home : Routes()

    @Serializable
    data object Login : Routes()

    @Serializable
    data object Register : Routes()

    @Serializable
    data object Profile : Routes()
}
