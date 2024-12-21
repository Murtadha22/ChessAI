package com.example.chessai


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge


private const val Tag = "MainActivity"
class MainActivity : ComponentActivity() {
    private val chessModel : ChessModel = ChessModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChessBoardView(chessModel)
        }
        Log.d(Tag, "Chess Board:\n${chessModel.toString()}")

    }
}

