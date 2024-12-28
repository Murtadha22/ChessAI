package com.example.chessai.fen

data class FenPosition(
    val board: IntArray,
    val activeColor: String,
    val castling: String,
    val enPassant: String,
    val halfMoveClock: Int,
    val fullMoveNumber: Int
)
