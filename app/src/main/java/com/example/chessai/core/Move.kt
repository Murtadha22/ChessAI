package com.example.chessai.core

data class Move(
    val fromIndex: Int,
    val toIndex: Int,
    val movedPiece: Int,
    val capturedPiece: Int,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false,
    val isPromotion: Boolean = false,
    val promotionPiece: Int = 0
)
