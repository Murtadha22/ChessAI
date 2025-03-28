package com.example.chessai.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    fun resetGame() {
        navController.navigate("CutScene")
        chessModel.reset()
        chessSelectionState = ChessSelectionState()
        currentPlayer = ChessPlayer.WHITE
        isAiCalculating = false
        gameOverMessage = null
        showGameOverDialog = false
        moveCount = 0
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Unspecified),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (currentPlayer == ChessPlayer.WHITE) "White's Turn" else "Black's Turn",
                color = Color.Black,
                fontFamily = poppinsFontFamily,
                fontSize = 40.sp
            )
        }

        NewGameButton(onResetGame = { resetGame() })
        Spacer(modifier = Modifier.height(16.dp))

        gameOverMessage?.let {
        }

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
                                    }
                                    else {
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
