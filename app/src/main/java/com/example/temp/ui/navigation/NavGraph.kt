package com.example.temp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.temp.data.repository.SettingsRepository
import com.example.temp.ui.screens.albums.AlbumsScreen
import com.example.temp.ui.screens.artists.ArtistsScreen
import com.example.temp.ui.screens.folders.FoldersScreen
import com.example.temp.ui.screens.home.HomeScreen
import com.example.temp.ui.screens.library.LibraryScreen
import com.example.temp.ui.screens.player.PlayerScreen
import com.example.temp.ui.screens.playlists.PlaylistsScreen
import com.example.temp.ui.screens.search.SearchScreen
import com.example.temp.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object Playlists : Screen("playlists")
    object Folders : Screen("folders")
    object Albums : Screen("albums")
    object Artists : Screen("artists")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Player : Screen("player/{trackId}") {
        fun createRoute(trackId: Long) = "player/$trackId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    settingsRepository: SettingsRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route)
                },
                onNavigateToPlaylists = {
                    navController.navigate(Screen.Playlists.route)
                },
                onNavigateToFolders = {
                    navController.navigate(Screen.Folders.route)
                },
                onNavigateToAlbums = {
                    navController.navigate(Screen.Albums.route)
                },
                onNavigateToArtists = {
                    navController.navigate(Screen.Artists.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Folders.route) {
            FoldersScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Albums.route) {
            AlbumsScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Artists.route) {
            ArtistsScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToPlayer = { trackId ->
                    navController.navigate(Screen.Player.createRoute(trackId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsRepository = settingsRepository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("trackId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId") ?: return@composable
            PlayerScreen(
                trackId = trackId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}