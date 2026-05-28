package com.example.devfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.devfinder.feature.favorites.FavoritesScreen
import com.example.devfinder.feature.favorites.FavoritesViewModel
import com.example.devfinder.feature.profile.ProfileScreen
import com.example.devfinder.feature.profile.ProfileViewModel
import com.example.devfinder.feature.search.SearchScreen
import com.example.devfinder.feature.search.SearchViewModel
import com.example.devfinder.ui.theme.DevFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "search") {
                        composable("search") {
                            val viewModel: SearchViewModel = hiltViewModel()
                            SearchScreen(viewModel = viewModel, onUserClick = { username ->
                                navController.navigate("profile/$username")}, navigateToFavorites = {navController.navigate("favorites")})
                        }
                        composable(
                            route = "profile/{username}?isSaved={isSaved}",
                            arguments = listOf(
                                navArgument("username") {
                                    type = NavType.StringType
                                },
                                navArgument("isSaved") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                }
                            )
                        ) {
                            val viewModel: ProfileViewModel = hiltViewModel()

                            ProfileScreen(
                                viewModel = viewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("favorites") {
                            val viewModel: FavoritesViewModel = hiltViewModel()
                            FavoritesScreen(
                                viewModel = viewModel,
                                onUserClick = { username -> navController.navigate("profile/$username?isSaved=true") })
                        }
                    }
                }
            }
        }
    }
}
