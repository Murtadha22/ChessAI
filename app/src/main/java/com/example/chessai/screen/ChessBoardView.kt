package com.example.chessai.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.chessai.ChessModel
import com.example.chessai.ai.AiEngine
import com.example.chessai.components.ChessSquare
import com.example.chessai.components.NewGameButton
import com.example.chessai.core.ChessPlayer
import com.example.chessai.core.ChessSelectionState
import com.example.chessai.ui.theme.poppinsFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ChessBoardView(navController: NavHostController, chessModel: ChessModel) {
    val boardSize = 8
    var chessSelectionState by remember { mutableStateOf(ChessSelectionState()) }
    var currentPlayer by remember { mutableStateOf(ChessPlayer.WHITE) }
    var isAiCalculating by remember { mutableStateOf(false) }
    var gameOverMessage by remember { mutableStateOf<String?>(null) }
    var showGameOverDialog by remember { mutableStateOf(false) }
    var moveCount by remember { mutableIntStateOf(0) }

    // Timer state for both players
    var whiteTime by remember { mutableIntStateOf(600) }
    var blackTime by remember { mutableIntStateOf(600) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(currentPlayer) {
        isTimerRunning = true
        while (isTimerRunning) {
            delay(1000)
            if (currentPlayer == ChessPlayer.WHITE && whiteTime > 0) {
                whiteTime -= 1
            } else if (currentPlayer == ChessPlayer.BLACK && blackTime > 0) {
                blackTime -= 1
            }
        }
    }

    fun resetGame() {
        chessModel.reset()
        chessSelectionState = ChessSelectionState()
        currentPlayer = ChessPlayer.WHITE
        isAiCalculating = false
        gameOverMessage = null
        showGameOverDialog = false
        moveCount = 0
        whiteTime = 60
        blackTime = 60
        navController.navigate("CutScene")
    }

    fun checkEndGame(playerJustMoved: ChessPlayer) {
        val opponent = if (playerJustMoved == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
        when {
            chessModel.isCheckmate(opponent) -> {
                val winner = if (playerJustMoved == ChessPlayer.WHITE) "White" else "Black"
                gameOverMessage = "Checkmate! $winner wins."
                showGameOverDialog = true
            }
            chessModel.isStalemate(opponent) -> {
                gameOverMessage = "Stalemate! It's a draw."
                showGameOverDialog = true
            }
        }
    }

    LaunchedEffect(currentPlayer) {
        if (currentPlayer == ChessPlayer.BLACK && !isAiCalculating && gameOverMessage == null) {
            isAiCalculating = true
            val bestMove = withContext(Dispatchers.Default) {
                AiEngine.findBestMove(chessModel, maxDepth = 3, moveNumber = moveCount)
            }
            bestMove?.let { (from, to) ->
                chessModel.movePiece(from.first, from.second, to.first, to.second)
                moveCount++
                checkEndGame(ChessPlayer.BLACK)
            }
            if (gameOverMessage == null) {
                currentPlayer = ChessPlayer.WHITE
            }
            isAiCalculating = false
        }
    }

    if (showGameOverDialog) {
        AlertDialog(
            modifier = Modifier
                .border(width = 4.dp, color = Color.Black, shape = CutCornerShape(16.dp)),
            onDismissRequest = {},
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { resetGame() }) {
                        Text(
                            "New Game",
                            fontFamily = poppinsFontFamily,
                            fontSize = 25.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .border(width = 3.dp, color = Color.Black, shape = CutCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    "Game Over",
                    fontFamily = poppinsFontFamily,
                    fontSize = 45.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                val message = gameOverMessage ?: "Game Over!"
                Text(
                    message,
                    fontFamily = poppinsFontFamily,
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.width(250.dp).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (currentPlayer == ChessPlayer.WHITE) {
                    "White's Turn\nTime: $whiteTime"
                } else {
                    "Black's Turn\nTime: $blackTime"
                },
                color = Color.Black,
                fontFamily = poppinsFontFamily,
                fontSize = 30.sp
            )
        }

        NewGameButton(onResetGame = { resetGame() })
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .border(width = 6.dp, color = Color.Black, shape = CutCornerShape(14.dp))
                .padding(10.dp)
        ) {
            Column {
                for (row in 7 downTo 0) {
                    Row {
                        for (col in 0 until boardSize) {
                            val piece = chessModel.pieceAt(col, row)
                            val isValidMove = chessSelectionState.validMoves.contains(col to row)
                            val isOpponentPiece =
                                isValidMove && piece != null && piece.player != currentPlayer

                            ChessSquare(
                                piece = piece,
                                isWhite = ((row + col) % 2 == 0),
                                isSelected = (chessSelectionState.selectedSquare == (col to row)),
                                isValidMove = isValidMove,
                                isOpponentPiece = isOpponentPiece,
                                onClick = {
                                    if (currentPlayer == ChessPlayer.BLACK || isAiCalculating || gameOverMessage != null) {
                                        return@ChessSquare
                                    }
                                    if (chessSelectionState.selectedPiece == null) {
                                        if (piece != null && piece.player == currentPlayer) {
                                            chessSelectionState = chessSelectionState.copy(
                                                selectedPiece = piece,
                                                selectedSquare = (col to row),
                                                validMoves = chessModel.getValidMoves(piece)
                                            )
                                        }
                                    } else {
                                        if (isValidMove) {
                                            chessSelectionState.selectedPiece?.let {
                                                chessModel.movePiece(
                                                    chessSelectionState.selectedSquare?.first ?: 0,
                                                    chessSelectionState.selectedSquare?.second ?: 0,
                                                    col,
                                                    row
                                                )
                                                moveCount++
                                                checkEndGame(ChessPlayer.WHITE)
                                                if (gameOverMessage == null) {
                                                    currentPlayer = ChessPlayer.BLACK
                                                }
                                            }
                                        }
                                        chessSelectionState = ChessSelectionState()
                                    }
                                },
                                onMoveMade = {},
                                onPieceSelected = {}
                            )
                        }
                    }
                }
            }
        }
    }
}