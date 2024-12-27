package com.example.chessai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessai.components.ChessSquare
import com.example.chessai.ui.theme.TextColorBackground
import com.example.chessai.ui.theme.poppinsFontFamily

@Composable
fun ChessBoardView(chessModel: ChessModel) {
    val boardSize = 8
    var chessSelectionState by remember { mutableStateOf(ChessSelectionState()) }
    var currentPlayer by remember { mutableStateOf(ChessPlayer.WHITE) }

    fun resetGame() {
        chessModel.reset()
        chessSelectionState = ChessSelectionState()
        currentPlayer = ChessPlayer.WHITE
    }

    fun onMoveMade() {}
    fun onPieceSelected() {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NewGameButton(onResetGame = { resetGame() })

        Spacer(modifier = Modifier.height(16.dp))
        for (row in 7 downTo 0) {
            Row {
                for (col in 0 until boardSize) {
                    val piece = chessModel.pieceAt(col, row)
                    val isValidMove = chessSelectionState.validMoves.contains(Pair(col, row))
                    val isOpponentPiece =
                        isValidMove && piece != null && piece.player != currentPlayer
                    ChessSquare(
                        piece = piece,
                        isWhite = (row + col) % 2 == 0,
                        onClick = {
                            if (chessSelectionState.selectedPiece == null) {
                                if (piece != null && piece.player == currentPlayer) {
                                    chessSelectionState = chessSelectionState.copy(
                                        selectedPiece = piece,
                                        selectedSquare = Pair(col, row),
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
                                        currentPlayer = if (currentPlayer == ChessPlayer.WHITE) {
                                            ChessPlayer.BLACK
                                        } else {
                                            ChessPlayer.WHITE
                                        }
                                    }
                                }
                                chessSelectionState = ChessSelectionState()
                            }
                        },
                        isSelected = (chessSelectionState.selectedSquare == Pair(col, row)),
                        isValidMove = isValidMove, onMoveMade = { onMoveMade() },
                        isOpponentPiece = isOpponentPiece,
                        onPieceSelected = { onPieceSelected() }
                    )
                }
            }
        }
    }
}
@Composable
fun NewGameButton(onResetGame: () -> Unit) {
    Button(
        onClick = { onResetGame() },
        shape = CutCornerShape(8.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = TextColorBackground
        ),
        modifier = Modifier
            .width(130.dp)
            .border(
                width = 2.dp, color = Color.White, shape = CutCornerShape(8.dp)
            )
    ) {
        Text(
            text = "New Game", fontFamily = poppinsFontFamily, color = Color.White,
            fontSize = 22.sp
        )
    }
}
