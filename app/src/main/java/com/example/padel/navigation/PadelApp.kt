package com.example.padel.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.padel.R
import com.example.padel.data.MatchRepository
import com.example.padel.screens.LoginScreen
import com.example.padel.ui.courts.CourtsScreen
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
fun PadelApp(repository: MatchRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    val showBottomBarAndFab = currentRoute != null &&
            currentRoute != PadelDestinations.LOGIN &&
            currentRoute != PadelDestinations.CREATE_MATCH &&
            !currentRoute.startsWith("match/")

    val tabs = listOf(
        BottomTab(PadelDestinations.HOME, R.string.nav_home, Icons.Default.Home),
        BottomTab(PadelDestinations.COURTS, R.string.nav_courts, Icons.Default.Place),
        BottomTab(PadelDestinations.MY_MATCHES, R.string.nav_my_matches, Icons.Default.CalendarMonth),
        BottomTab(PadelDestinations.PROFILE, R.string.nav_profile, Icons.Default.Person),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBarAndFab) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    tab.icon,
                                    contentDescription = stringResource(tab.labelRes),
                                )
                            },
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
        floatingActionButton = {
            if (showBottomBarAndFab) {
                FloatingActionButton(
                    onClick = { navController.navigate(PadelDestinations.CREATE_MATCH) },
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.fab_create_match),
                    )
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
                )
            }

            composable(PadelDestinations.HOME) {
                HomeScreen(Modifier.fillMaxSize())
            }

            composable(PadelDestinations.COURTS) {
                CourtsScreen(Modifier.fillMaxSize())
            }

            composable(PadelDestinations.MY_MATCHES) {
                MyMatchesScreen(

                    modifier = Modifier.fillMaxSize(),
                    onJoinMatch = { id ->
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_join_match, id),
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                    onOpenMatchDetail = { id ->
                        navController.navigate(PadelDestinations.matchDetail(id))
                    },
                )
            }

            composable(PadelDestinations.PROFILE) {
                ProfileScreen(Modifier.fillMaxSize())
            }

            composable(PadelDestinations.CREATE_MATCH) {
                CreateMatchScreen(
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(
                route = PadelDestinations.MATCH_DETAIL,
                arguments = listOf(
                    navArgument("matchId") { type = NavType.StringType },
                ),
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
