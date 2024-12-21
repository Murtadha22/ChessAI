package com.example.chessai


import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chessai.ui.theme.getChessSquareColors

@Composable
fun ChessBoardView(chessModel: ChessModel) {
    val boardSize = 8
    var chessSelectionState by remember { mutableStateOf(ChessSelectionState()) }
    var currentPlayer by remember { mutableStateOf(ChessPlayer.WHITE) }

    fun onMoveMade() {}
    fun onPieceSelected() {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 7 downTo 0) {
            Row {
                for (col in 0 until boardSize) {
                    val piece = chessModel.pieceAt(col, row)
                    val isValidMove = chessSelectionState.validMoves.contains(Pair(col, row))

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
                        onPieceSelected = { onPieceSelected() }
                    )
                }
            }
        }
    }
}

@Composable
fun getPieceDrawable(piece: ChessPiece?): Int {
    return when (piece?.rank) {
        ChessRank.KING -> if (piece.player == ChessPlayer.WHITE) R.drawable.king else R.drawable.king1
        ChessRank.QUEEN -> if (piece.player == ChessPlayer.WHITE) R.drawable.queen else R.drawable.queen1
        ChessRank.BISHOP -> if (piece.player == ChessPlayer.WHITE) R.drawable.bishop else R.drawable.bishop1
        ChessRank.KNIGHT -> if (piece.player == ChessPlayer.WHITE) R.drawable.knight else R.drawable.knight1
        ChessRank.ROOK -> if (piece.player == ChessPlayer.WHITE) R.drawable.rook else R.drawable.rook1
        ChessRank.PAWN -> if (piece.player == ChessPlayer.WHITE) R.drawable.pawn else R.drawable.pawn1
        null -> 0
    }
}

@Composable
fun ChessSquare(
    isWhite: Boolean,
    piece: ChessPiece?,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isValidMove: Boolean = false,
    onMoveMade: () -> Unit,
    onPieceSelected: () -> Unit
) {
    val backgroundColor =
        when {
            isWhite -> getChessSquareColors().first
            else -> getChessSquareColors().second
        }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.2f else 1f, // Scale up when selected
        animationSpec = androidx.compose.animation.core.InfiniteRepeatableSpec(
            animation = androidx.compose.animation.core.keyframes {
                durationMillis = 1000
                1.2f at 0 using androidx.compose.animation.core.EaseInOut
                1f at 1000
            }
        ), label = ""
    )

    Box(
        modifier = Modifier
            .size(45.dp)
            .background(color = backgroundColor)
            .clickable {
                onClick()
                if (isSelected) {
                    onMoveMade()
                } else {
                    onPieceSelected()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isValidMove) {
            Image(
                painter = painterResource(id = R.drawable.ad_adjust_24),
                contentDescription = "Valid Move",
                modifier = Modifier
                    .customShadow(
                        color = Color.Black,
                        alpha = 0.3f,
                        shadowRadius = 16.dp,
                        borderRadius = 50.dp,
                        offsetY = 6.dp
                    )
                    .size(22.dp)
            )
        }

        piece?.let {
            val drawable = getPieceDrawable(it)
            Image(
                painter = painterResource(id = drawable),
                contentDescription = null,
                modifier = Modifier
                    .customShadow(
                        color = Color.Gray,
                        alpha = 0.7f,
                        shadowRadius = 16.dp,
                        borderRadius = 50.dp,
                        offsetY = 6.dp
                    )
                    .size(30.dp)
                    .scale(if (isSelected) animatedScale else 1f)
            )
        }
    }
}