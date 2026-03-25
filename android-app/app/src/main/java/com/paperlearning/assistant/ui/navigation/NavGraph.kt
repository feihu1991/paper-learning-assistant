package com.paperlearning.assistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.paperlearning.assistant.ui.screens.home.HomeScreen

/**
 * 导航路由定义
 */
object Screen {
    const val HOME = "home"
    const val PAPER_DETAIL = "paper_detail"
    const val SEARCH = "search"
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
            val paperId = backStackEntry.arguments?.getLong("paperId") ?: return@composable
            // TODO: 实现论文详情页
            // PaperDetailScreen(
            //     paperId = paperId,
            //     onNavigateBack = { navController.popBackStack() }
            // )
        }

        // 搜索页
        composable(route = Screen.SEARCH) {
            // TODO: 实现搜索页
            // SearchScreen(
            //     onNavigateBack = { navController.popBackStack() },
            //     onNavigateToPaperDetail = { paperId ->
            //         navController.navigate("${Screen.PAPER_DETAIL}/$paperId")
            //     }
            // )
        }
    }
}
