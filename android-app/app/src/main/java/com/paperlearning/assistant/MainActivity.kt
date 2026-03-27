package com.paperlearning.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.paperlearning.assistant.ui.screens.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主界面入口
 * 使用 Hilt 注入依赖
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        onNavigateToSearch = { /* TODO: navigate to search screen */ },
                        onNavigateToPaperDetail = { paperId ->
                            // TODO: navigate to paper detail
                            android.util.Log.d("MainActivity", "Navigate to paper: $paperId")
                        }
                    )
                }
            }
        }
    }
}
