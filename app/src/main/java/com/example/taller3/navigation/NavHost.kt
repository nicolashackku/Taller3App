package com.example.taller3.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.taller3.R
import com.example.taller3.data.UserInfo
import com.example.taller3.ui.components.UserPortrait
import com.example.taller3.ui.screens.HomeScreen
import com.example.taller3.ui.screens.LoginScreen
import com.example.taller3.ui.screens.ProfileScreen
import com.example.taller3.ui.screens.RegisterScreen
import com.example.taller3.utils.DateUtils
import com.example.taller3.viewmodels.AuthViewModel
import com.example.taller3.viewmodels.FirestoreViewModel
import com.example.taller3.viewmodels.LocationViewModel
import kotlinx.coroutines.launch

private fun NavBackStack<NavKey>.navigate(route: Routes, clearStack: Boolean = false) {
    if (lastOrNull() == route) return
    if (clearStack) clear()
    add(route)
}

/**
 * Root navigation host. Owns the back stack and displays the
 * current entry using [NavDisplay].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHost(modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = viewModel()
    val firestoreViewModel: FirestoreViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    val profile by firestoreViewModel.state.collectAsState()

    DisposableEffect(currentUser?.uid) {
        currentUser?.uid?.let {
            firestoreViewModel.observeUser(it)
            firestoreViewModel.observeOnlineUsers(it)
        }
        onDispose { firestoreViewModel.stopObserving() }
    }

    val backStack = rememberNavBackStack(
        if (currentUser != null) Routes.Home else Routes.Login
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val publicRoutes = listOf(Routes.Login, Routes.Register)
        if (authViewModel.currentUser.value == null && backStack.lastOrNull() !in publicRoutes) {
            backStack.navigate(Routes.Login, clearStack = true)
        }
    }

    val isAuthScreen = backStack.lastOrNull().let { it == Routes.Login || it == Routes.Register }

    // The user is marked as offline before the session is closed, while the rules still allow the write.
    fun logout() {
        locationViewModel.stopLocationUpdates()
        locationViewModel.clearRoute()
        scope.launch { drawerState.close() }
        val uid = currentUser?.uid
        if (uid == null) {
            authViewModel.logout()
            backStack.navigate(Routes.Login, clearStack = true)
        } else {
            firestoreViewModel.updateUser(uid, mapOf("online" to false)) {
                authViewModel.logout()
                backStack.navigate(Routes.Login, clearStack = true)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isAuthScreen,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        UserPortrait(
                            user = profile.user ?: UserInfo(
                                displayName = currentUser?.email
                                    ?: stringResource(R.string.nav_host_user_fallback_text),
                            ),
                            email = currentUser?.email,
                            lastLogin = currentUser?.metadata?.lastSignInTimestamp?.let(DateUtils::format) ?: stringResource(R.string.nav_host_last_login_fallback_text),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(R.string.nav_host_profile_item)) },
                            icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                            selected = backStack.lastOrNull() == Routes.Profile,
                            onClick = {
                                scope.launch { drawerState.close() }
                                backStack.navigate(Routes.Profile)
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(R.string.nav_host_logout_button)) },
                            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                            selected = false,
                            onClick = ::logout
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                AnimatedVisibility(
                    !isAuthScreen,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { -it }
                ) {
                    TopAppBar(
                        title = {
                            Text(stringResource(
                                if (backStack.lastOrNull() == Routes.Profile) R.string.nav_host_profile_item
                                else R.string.nav_host_map_title
                            ))
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = stringResource(R.string.nav_host_open_menu_icon)
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            },
        ) { paddingValues ->
            NavDisplay(
                modifier = Modifier.padding(paddingValues),
                backStack = backStack,
                onBack = {
                    backStack.removeLastOrNull()
                },
                transitionSpec = {
                    slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it })
                },
                predictivePopTransitionSpec = {
                    slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                        targetOffsetX = { it })
                },
                popTransitionSpec = {
                    slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                        targetOffsetX = { it })
                },
                entryProvider = { key ->
                    when (key) {
                        is Routes.Login -> NavEntry(key) {
                            LoginScreen(
                                goHome = {
                                    backStack.navigate(Routes.Home, clearStack = true)
                                },
                                goRegister = { backStack.navigate(Routes.Register) },
                                viewModel = authViewModel
                            )
                        }

                        is Routes.Home -> NavEntry(key) {
                            HomeScreen(
                                authViewModel = authViewModel,
                                firestoreViewModel = firestoreViewModel,
                                locationViewModel = locationViewModel
                            )
                        }

                        is Routes.Register -> NavEntry(key) {
                            RegisterScreen(
                                goHome = { backStack.navigate(Routes.Home, clearStack = true) },
                                authViewModel = authViewModel,
                                firestoreViewModel = firestoreViewModel
                            )
                        }

                        is Routes.Profile -> NavEntry(key) {
                            ProfileScreen(
                                viewModel = authViewModel,
                                firestoreViewModel = firestoreViewModel
                            )
                        }

                        else -> {
                            error("Unknown route: $key")
                        }
                    }
                }
            )
        }
    }
}
