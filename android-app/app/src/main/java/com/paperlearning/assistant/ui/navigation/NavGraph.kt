package com.paperlearning.assistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.paperlearning.assistant.ui.screens.detail.PaperDetailScreen
import com.paperlearning.assistant.ui.screens.home.HomeScreen
import com.paperlearning.assistant.ui.screens.search.SearchScreen

/**
 * 导航路由定义
 */
object Screen {
    const val HOME = "home"
    const val PAPER_DETAIL = "paper_detail"
    const val SEARCH = "search"
    const val LEARNING = "learning"
}

/**
 * 导航图
 * 
 * 定义应用的导航结构，包括所有可导航的屏幕及其参数
 */
@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        // 首页
        composable(route = Screen.HOME) {
            HomeScreen(
                onNavigateToSearch = {
                    navController.navigate(Screen.SEARCH)
                },
                onNavigateToPaperDetail = { paperId ->
                    navController.navigate("${Screen.PAPER_DETAIL}/$paperId")
                }
            )
        }

        // 论文详情页
        composable(
            route = "${Screen.PAPER_DETAIL}/{paperId}",
            arguments = listOf(
                navArgument("paperId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val paperId = backStackEntry.arguments?.getLong("paperId") ?: 0L
            PaperDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLearning = { paperId ->
                    navController.navigate("${Screen.LEARNING}/$paperId")
                }
            )
        }

        // 搜索页
        composable(route = Screen.SEARCH) {
            SearchScreen(
                onPaperClick = { paperId ->
                    // 导航到论文详情页
                    navController.navigate("${Screen.PAPER_DETAIL}/$paperId")
                }
            )
        }
    }
}
