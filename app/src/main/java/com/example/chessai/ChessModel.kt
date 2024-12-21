package com.example.chessai

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import kotlin.math.abs

class ChessModel {
    @SuppressLint("MutableCollectionMutableState")
    private val _piecesBox = mutableStateOf<Map<Pair<Int, Int>, ChessPiece>>(emptyMap())
    private val piecesBox: Map<Pair<Int, Int>, ChessPiece> get() = _piecesBox.value


    init {
        reset()
    }

    private fun reset(){
        val initialPieces = mutableMapOf(
            Pair(0, 0) to ChessPiece(0, 0, ChessPlayer.WHITE, ChessRank.ROOK, R.drawable.rook),
            Pair(7, 0) to ChessPiece(7, 0, ChessPlayer.WHITE, ChessRank.ROOK, R.drawable.rook),
            Pair(0, 7) to ChessPiece(0, 7, ChessPlayer.BLACK, ChessRank.ROOK, R.drawable.rook1),
            Pair(7, 7) to ChessPiece(7, 7, ChessPlayer.BLACK, ChessRank.ROOK, R.drawable.rook1),
            Pair(1, 0) to ChessPiece(1, 0, ChessPlayer.WHITE, ChessRank.KNIGHT, R.drawable.knight),
            Pair(6, 0) to ChessPiece(6, 0, ChessPlayer.WHITE, ChessRank.KNIGHT, R.drawable.knight),
            Pair(1, 7) to ChessPiece(1, 7, ChessPlayer.BLACK, ChessRank.KNIGHT, R.drawable.knight1),
            Pair(6, 7) to ChessPiece(6, 7, ChessPlayer.BLACK, ChessRank.KNIGHT, R.drawable.knight1),
            Pair(2, 0) to ChessPiece(2, 0, ChessPlayer.WHITE, ChessRank.BISHOP, R.drawable.bishop),
            Pair(5, 0) to ChessPiece(5, 0, ChessPlayer.WHITE, ChessRank.BISHOP, R.drawable.bishop),
            Pair(2, 7) to ChessPiece(2, 7, ChessPlayer.BLACK, ChessRank.BISHOP, R.drawable.bishop1),
            Pair(5, 7) to ChessPiece(5, 7, ChessPlayer.BLACK, ChessRank.BISHOP, R.drawable.bishop1),
            Pair(3, 0) to ChessPiece(3, 0, ChessPlayer.WHITE, ChessRank.QUEEN, R.drawable.queen),
            Pair(3, 7) to ChessPiece(3, 7, ChessPlayer.BLACK, ChessRank.QUEEN, R.drawable.queen1),
            Pair(4, 0) to ChessPiece(4, 0, ChessPlayer.WHITE, ChessRank.KING, R.drawable.king),
            Pair(4, 7) to ChessPiece(4, 7, ChessPlayer.BLACK, ChessRank.KING, R.drawable.king1)
        )
        for (col in 0..7) {
            initialPieces += Pair(col, 1) to ChessPiece(
                col,
                1,
                ChessPlayer.WHITE,
                ChessRank.PAWN,
                R.drawable.pawn
            )
            initialPieces += Pair(col, 6) to ChessPiece(
                col,
                6,
                ChessPlayer.BLACK,
                ChessRank.PAWN,
                R.drawable.pawn1
            )
        }
        _piecesBox.value = initialPieces
        }


    fun pieceAt(col: Int, row: Int): ChessPiece? = _piecesBox.value[Pair(col, row)]

    override fun toString(): String {
        val desc = StringBuilder()
        for (row in 7 downTo 0) {
            desc.append("$row ")
            for (col in 0..7) {
                val piece = pieceAt(col, row)
                desc.append(if (piece == null) " ." else " ${piece.rank.name.first()}")
            }
            desc.append("\n")
        }
        desc.append("  0 1 2 3 4 5 6 7")
        return desc.toString()
    }

    fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        val piece = _piecesBox.value[Pair(fromCol, fromRow)]
        if (piece != null && isMoveValid(fromCol, fromRow, toCol, toRow)) {
            val updatedPieces = piecesBox.toMutableMap().apply {
                this[Pair(toCol, toRow)] = piece.copy(col = toCol, row = toRow)
                remove(Pair(fromCol, fromRow))
            }
            _piecesBox.value = updatedPieces
        }
    }


    private fun isMoveValid(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = piecesBox[Pair(fromCol, fromRow)] ?: return false
        if (fromCol == toCol && fromRow == toRow) return false

        val targetPiece = piecesBox[Pair(toCol, toRow)]
        if (targetPiece != null && targetPiece.player == piece.player) return false

        //Logic for the all pieces
        return when (piece.rank) {
            ChessRank.PAWN -> {
                val direction = if (piece.player == ChessPlayer.WHITE) 1 else -1
                (toRow == fromRow + direction && toCol == fromCol && targetPiece == null) ||
                        (toRow == fromRow + 2 * direction && fromRow == (if (piece.player == ChessPlayer.WHITE) 1 else 6) &&
                                toCol == fromCol && targetPiece == null) ||
                        (toRow == fromRow + direction && abs(toCol - fromCol) == 1 && targetPiece != null)
            }

            ChessRank.ROOK -> {
                (fromCol == toCol || fromRow == toRow) && isPathClear(
                    fromCol,
                    fromRow,
                    toCol,
                    toRow
                )
            }

            ChessRank.BISHOP -> {
                abs(fromCol - toCol) == abs(fromRow - toRow) && isPathClear(
                    fromCol,
                    fromRow,
                    toCol,
                    toRow
                )
            }

            ChessRank.KNIGHT -> {
                (abs(fromCol - toCol) == 2 && abs(fromRow - toRow) == 1) ||
                        (abs(fromCol - toCol) == 1 && abs(fromRow - toRow) == 2)
            }

            ChessRank.QUEEN -> {
                (fromCol == toCol || fromRow == toRow || abs(fromCol - toCol) == abs(fromRow - toRow)) &&
                        isPathClear(fromCol, fromRow, toCol, toRow)
            }

            ChessRank.KING -> {
                abs(fromCol - toCol) <= 1 && abs(fromRow - toRow) <= 1
            }
        }
    }

    private fun isPathClear(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val colStep = Integer.signum(toCol - fromCol)
        val rowStep = Integer.signum(toRow - fromRow)
        var currentCol = fromCol + colStep
        var currentRow = fromRow + rowStep
        while (currentCol != toCol || currentRow != toRow) {
            if (piecesBox[Pair(currentCol, currentRow)] != null) {
                return false
            }
            currentCol += colStep
            currentRow += rowStep
        }
        return true
    }

    fun getValidMoves(piece: ChessPiece): List<Pair<Int, Int>> {
        val validMoves = mutableListOf<Pair<Int, Int>>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (isMoveValid(piece.col, piece.row, col, row)) {
                    validMoves.add(Pair(col, row))
                }
        }
    }
        return validMoves
    }
}
