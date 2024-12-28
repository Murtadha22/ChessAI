package com.example.chessai


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chessai.components.CutScene
import com.example.chessai.screen.ChessBoardView


private const val Tag = "MainActivity"
class MainActivity : ComponentActivity() {
    private val chessModel : ChessModel = ChessModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavigationGraph(navController = navController)
        }
        Log.d(Tag, "Chess Board:\n${chessModel.toString()}")

    }
    @Composable
    fun NavigationGraph(navController: NavHostController) {
        NavHost(navController = navController, startDestination = "ChessBoardView") {
            composable("ChessBoardView") {
                ChessBoardView(navController = navController, chessModel = ChessModel())
            }
            composable("CutScene") {
                CutScene(navController = navController)
            }
        }
    }
}