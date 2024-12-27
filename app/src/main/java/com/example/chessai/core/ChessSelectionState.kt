package com.example.chessai.core

data class ChessSelectionState(
    val selectedPiece: ChessPiece? = null,
    val selectedSquare: Pair<Int, Int>? = null,
    val validMoves: List<Pair<Int, Int>> = emptyList()
)
