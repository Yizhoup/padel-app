package com.example.padel.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.padel.R
import com.example.padel.screens.LoginScreen
import com.example.padel.screens.RegisterScreen
import com.example.padel.session.SessionManager
import com.example.padel.ui.home.HomeScreen
import com.example.padel.ui.match.CreateMatchScreen
import com.example.padel.ui.match.MatchDetailScreen
import com.example.padel.ui.mymatches.MyMatchesScreen
import com.example.padel.ui.profile.ProfileScreen

private data class BottomTab(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
)

@Composable
fun PadelApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != null &&
        currentRoute != PadelDestinations.LOGIN &&
        currentRoute != PadelDestinations.REGISTER &&
        !currentRoute.startsWith("match/")

    val tabs = listOf(
        BottomTab(PadelDestinations.HOME, R.string.nav_home, Icons.Outlined.Home),
        BottomTab(PadelDestinations.MY_MATCHES, R.string.nav_matches, Icons.Outlined.List),
        BottomTab(PadelDestinations.CREATE_MATCH, R.string.nav_create, Icons.Outlined.AddCircle),
        BottomTab(PadelDestinations.PROFILE, R.string.nav_profile, Icons.Outlined.Person),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = stringResource(tab.labelRes)) },
                            label = { Text(stringResource(tab.labelRes)) },
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PadelDestinations.LOGIN,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(PadelDestinations.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(PadelDestinations.HOME) {
                            popUpTo(PadelDestinations.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onOpenRegister = {
                        navController.navigate(PadelDestinations.REGISTER)
                    },
                )
            }

            composable(PadelDestinations.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = { email ->
                        SessionManager.prefilledEmail = email
                        navController.popBackStack()
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    },
                )
            }

            composable(PadelDestinations.HOME) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onJoinMatch = { id -> navController.navigate(PadelDestinations.matchDetail(id)) },
                    onOpenMatchDetail = { id -> navController.navigate(PadelDestinations.matchDetail(id)) },
                )
            }

            composable(PadelDestinations.MY_MATCHES) {
                MyMatchesScreen(
                    modifier = Modifier.fillMaxSize(),
                    onJoinMatch = { id -> navController.navigate(PadelDestinations.matchDetail(id)) },
                    onOpenMatchDetail = { id -> navController.navigate(PadelDestinations.matchDetail(id)) },
                )
            }

            composable(PadelDestinations.PROFILE) {
                ProfileScreen(Modifier.fillMaxSize())
            }

            composable(PadelDestinations.CREATE_MATCH) {
                CreateMatchScreen(
                    onBack = { navController.navigate(PadelDestinations.HOME) },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(
                route = PadelDestinations.MATCH_DETAIL,
                arguments = listOf(navArgument("matchId") { type = NavType.StringType }),
            ) { entry ->
                val matchId = entry.arguments?.getString("matchId").orEmpty()
                MatchDetailScreen(
                    matchId = matchId,
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
