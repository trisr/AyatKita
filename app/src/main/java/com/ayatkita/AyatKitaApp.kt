package com.ayatkita

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ayatkita.ui.screens.BookmarkScreen
import com.ayatkita.ui.screens.HomeScreen
import com.ayatkita.ui.screens.SplashScreen
import com.ayatkita.ui.screens.SurahDetailScreen
import com.ayatkita.ui.theme.AyatKitaTheme
import com.ayatkita.viewmodel.BookmarkViewModel
import com.ayatkita.viewmodel.HomeViewModel
import com.ayatkita.viewmodel.SurahDetailViewModel
import kotlinx.coroutines.delay

private object AppRoutes {
    const val Splash = "splash"
    const val Home = "home"
    const val Bookmarks = "bookmarks"
    const val SurahDetail = "surah-detail"
}

@Composable
fun AyatKitaApp(container: AppContainer) {
    AyatKitaTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash
        ) {
            composable(route = AppRoutes.Splash) {
                SplashScreen()
                LaunchedEffect(Unit) {
                    delay(1200)
                    navController.navigate(AppRoutes.Home) {
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                    }
                }
            }

            composable(route = AppRoutes.Home) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.factory(container.quranRepository)
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onSurahClick = { surahNumber ->
                        navController.navigate("${AppRoutes.SurahDetail}/$surahNumber")
                    },
                    onBookmarksClick = {
                        navController.navigate(AppRoutes.Bookmarks)
                    }
                )
            }

            composable(route = AppRoutes.Bookmarks) {
                val bookmarkViewModel: BookmarkViewModel = viewModel(
                    factory = BookmarkViewModel.factory(container.quranRepository)
                )
                BookmarkScreen(
                    viewModel = bookmarkViewModel,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(
                route = "${AppRoutes.SurahDetail}/{surahNumber}",
                arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
            ) { backStackEntry ->
                val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                val detailViewModel: SurahDetailViewModel = viewModel(
                    factory = SurahDetailViewModel.factory(
                        surahNumber = surahNumber,
                        repository = container.quranRepository,
                        audioPlayer = container.audioPlayer
                    )
                )
                SurahDetailScreen(
                    viewModel = detailViewModel,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}
