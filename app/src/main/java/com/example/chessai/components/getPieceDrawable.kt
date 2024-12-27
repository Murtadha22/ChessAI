package com.example.chessai.components

import androidx.compose.runtime.Composable
import com.example.chessai.ChessPiece
import com.example.chessai.ChessPlayer
import com.example.chessai.ChessRank
import com.example.chessai.R

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
