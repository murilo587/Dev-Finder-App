package com.example.devfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                                navController.navigate("profile/$username")})
                        }
                        composable("profile/{username}") { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: ""
                            val viewModel: ProfileViewModel = hiltViewModel()
                            ProfileScreen(viewModel = viewModel, username = username, onBackClick = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
