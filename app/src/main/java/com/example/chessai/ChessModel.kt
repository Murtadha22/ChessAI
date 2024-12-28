package com.example.chessai

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import com.example.chessai.core.ChessPiece
import com.example.chessai.core.ChessPlayer
import com.example.chessai.core.ChessRank
import kotlin.math.abs

class ChessModel {

    @SuppressLint("MutableCollectionMutableState")
    private val _piecesBox = mutableStateOf<Map<Pair<Int, Int>, ChessPiece>>(emptyMap())
    private val piecesBox: Map<Pair<Int, Int>, ChessPiece> get() = _piecesBox.value

    // Track castling rights
    var canWhiteCastleKingSide = true
    var canWhiteCastleQueenSide = true
    var canBlackCastleKingSide = true
    var canBlackCastleQueenSide = true

    init {
        reset()
    }

    fun reset() {
        val initialPieces = mutableMapOf<Pair<Int, Int>, ChessPiece>()

        // Rooks
        initialPieces[0 to 0] = ChessPiece(0, 0, ChessPlayer.WHITE, ChessRank.ROOK, R.drawable.rook)
        initialPieces[7 to 0] = ChessPiece(7, 0, ChessPlayer.WHITE, ChessRank.ROOK, R.drawable.rook)
        initialPieces[0 to 7] = ChessPiece(0, 7, ChessPlayer.BLACK, ChessRank.ROOK, R.drawable.rook1)
        initialPieces[7 to 7] = ChessPiece(7, 7, ChessPlayer.BLACK, ChessRank.ROOK, R.drawable.rook1)

        // Knights
        initialPieces[1 to 0] = ChessPiece(1, 0, ChessPlayer.WHITE, ChessRank.KNIGHT, R.drawable.knight)
        initialPieces[6 to 0] = ChessPiece(6, 0, ChessPlayer.WHITE, ChessRank.KNIGHT, R.drawable.knight)
        initialPieces[1 to 7] = ChessPiece(1, 7, ChessPlayer.BLACK, ChessRank.KNIGHT, R.drawable.knight1)
        initialPieces[6 to 7] = ChessPiece(6, 7, ChessPlayer.BLACK, ChessRank.KNIGHT, R.drawable.knight1)

        // Bishops
        initialPieces[2 to 0] = ChessPiece(2, 0, ChessPlayer.WHITE, ChessRank.BISHOP, R.drawable.bishop)
        initialPieces[5 to 0] = ChessPiece(5, 0, ChessPlayer.WHITE, ChessRank.BISHOP, R.drawable.bishop)
        initialPieces[2 to 7] = ChessPiece(2, 7, ChessPlayer.BLACK, ChessRank.BISHOP, R.drawable.bishop1)
        initialPieces[5 to 7] = ChessPiece(5, 7, ChessPlayer.BLACK, ChessRank.BISHOP, R.drawable.bishop1)

        // Queens
        initialPieces[3 to 0] = ChessPiece(3, 0, ChessPlayer.WHITE, ChessRank.QUEEN, R.drawable.queen)
        initialPieces[3 to 7] = ChessPiece(3, 7, ChessPlayer.BLACK, ChessRank.QUEEN, R.drawable.queen1)

        // Kings
        initialPieces[4 to 0] = ChessPiece(4, 0, ChessPlayer.WHITE, ChessRank.KING, R.drawable.king)
        initialPieces[4 to 7] = ChessPiece(4, 7, ChessPlayer.BLACK, ChessRank.KING, R.drawable.king1)

        // Pawns
        for (col in 0..7) {
            initialPieces[col to 1] = ChessPiece(col, 1, ChessPlayer.WHITE, ChessRank.PAWN, R.drawable.pawn)
            initialPieces[col to 6] = ChessPiece(col, 6, ChessPlayer.BLACK, ChessRank.PAWN, R.drawable.pawn1)
        }

        canWhiteCastleKingSide = true
        canWhiteCastleQueenSide = true
        canBlackCastleKingSide = true
        canBlackCastleQueenSide = true

        _piecesBox.value = initialPieces
    }

    fun pieceAt(col: Int, row: Int): ChessPiece? = piecesBox[col to row]

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
        val piece = piecesBox[fromCol to fromRow] ?: return

        if (!isMoveValid(fromCol, fromRow, toCol, toRow)) return

        val updatedPieces = piecesBox.toMutableMap()

        // Handle castling
        if (piece.rank == ChessRank.KING && abs(fromCol - toCol) == 2 && fromRow == toRow) {
            // King side
            if (toCol == 6) {
                val rook = updatedPieces[7 to fromRow]
                if (rook != null && rook.rank == ChessRank.ROOK) {
                    updatedPieces.remove(7 to fromRow)
                    updatedPieces[5 to fromRow] = rook.copy(col = 5)
                }
            }
            // Queen side
            else if (toCol == 2) {
                val rook = updatedPieces[0 to fromRow]
                if (rook != null && rook.rank == ChessRank.ROOK) {
                    updatedPieces.remove(0 to fromRow)
                    updatedPieces[3 to fromRow] = rook.copy(col = 3)
                }
            }
        }

        // Move piece
        updatedPieces.remove(fromCol to fromRow)
        updatedPieces[toCol to toRow] = piece.copy(col = toCol, row = toRow)

        // Pawn promotion
        if (piece.rank == ChessRank.PAWN) {
            if ((piece.player == ChessPlayer.WHITE && toRow == 7) ||
                (piece.player == ChessPlayer.BLACK && toRow == 0)
            ) {
                val newPiece = piece.copy(
                    col = toCol,
                    row = toRow,
                    rank = ChessRank.QUEEN,
                    resId = if (piece.player == ChessPlayer.WHITE) R.drawable.queen else R.drawable.queen1
                )
                updatedPieces[toCol to toRow] = newPiece
            }
        }

        // Update castling rights
        when {
            piece.player == ChessPlayer.WHITE && piece.rank == ChessRank.KING -> {
                canWhiteCastleKingSide = false
                canWhiteCastleQueenSide = false
            }
            piece.player == ChessPlayer.WHITE && piece.rank == ChessRank.ROOK -> {
                if (fromCol == 0 && fromRow == 0) canWhiteCastleQueenSide = false
                if (fromCol == 7 && fromRow == 0) canWhiteCastleKingSide = false
            }
            piece.player == ChessPlayer.BLACK && piece.rank == ChessRank.KING -> {
                canBlackCastleKingSide = false
                canBlackCastleQueenSide = false
            }
            piece.player == ChessPlayer.BLACK && piece.rank == ChessRank.ROOK -> {
                if (fromCol == 0 && fromRow == 7) canBlackCastleQueenSide = false
                if (fromCol == 7 && fromRow == 7) canBlackCastleKingSide = false
            }
        }

        _piecesBox.value = updatedPieces
    }

    private fun isMoveValid(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = piecesBox[fromCol to fromRow] ?: return false
        if (fromCol == toCol && fromRow == toRow) return false

        // Can't capture own piece
        val targetPiece = piecesBox[toCol to toRow]
        if (targetPiece != null && targetPiece.player == piece.player) return false

        // Check piece-specific pattern
        if (!isMovePatternValid(fromCol, fromRow, toCol, toRow)) {
            return false
        }

        // Simulate the move in a copy => check if your king is in check
        val simulated = copyOf()
        simulated.forceMove(fromCol, fromRow, toCol, toRow)
        if (simulated.isKingInCheck(piece.player)) {
            return false
        }

        return true
    }

    private fun isMovePatternValid(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = piecesBox[fromCol to fromRow] ?: return false
        val targetPiece = piecesBox[toCol to toRow]

        return when (piece.rank) {
            ChessRank.PAWN -> {
                val direction = if (piece.player == ChessPlayer.WHITE) 1 else -1
                val startRow = if (piece.player == ChessPlayer.WHITE) 1 else 6
                val forwardMove = (toRow == fromRow + direction && toCol == fromCol && targetPiece == null)
                val doubleMove = (fromRow == startRow && toRow == fromRow + 2 * direction &&
                        toCol == fromCol && targetPiece == null &&
                        pieceAt(fromCol, fromRow + direction) == null)
                val diagonalCapture = (toRow == fromRow + direction && abs(toCol - fromCol) == 1 && targetPiece != null)

                forwardMove || doubleMove || diagonalCapture
            }

            ChessRank.ROOK -> {
                (fromCol == toCol || fromRow == toRow) && isPathClear(fromCol, fromRow, toCol, toRow)
            }

            ChessRank.BISHOP -> {
                abs(fromCol - toCol) == abs(fromRow - toRow) &&
                        isPathClear(fromCol, fromRow, toCol, toRow)
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
                if (abs(fromCol - toCol) <= 1 && abs(fromRow - toRow) <= 1) {
                    true
                }
                else if (fromRow == toRow && abs(fromCol - toCol) == 2) {
                    canCastle(fromCol, fromRow, toCol, toRow)
                } else {
                    false
                }
            }
        }
    }

    private fun canCastle(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = piecesBox[fromCol to fromRow] ?: return false
        if (piece.rank != ChessRank.KING) return false

        val isWhite = (piece.player == ChessPlayer.WHITE)
        val row = if (isWhite) 0 else 7
        if (row != fromRow) return false

        // King side
        if (toCol == 6 && fromCol == 4) {
            if (isWhite && !canWhiteCastleKingSide) return false
            if (!isWhite && !canBlackCastleKingSide) return false
            // squares (5, row), (6, row) must be free & not attacked
            if (pieceAt(5, row) != null || pieceAt(6, row) != null) return false
            if (isKingInCheck(piece.player)) return false
            if (wouldSquareBeAttacked(5, row, piece.player) || wouldSquareBeAttacked(6, row, piece.player)) return false
            return true
        }
        // Queen side
        if (toCol == 2 && fromCol == 4) {
            if (isWhite && !canWhiteCastleQueenSide) return false
            if (!isWhite && !canBlackCastleQueenSide) return false
            // squares (3, row), (2, row), (1, row) must be free & not attacked
            if (pieceAt(3, row) != null || pieceAt(2, row) != null || pieceAt(1, row) != null) return false
            if (isKingInCheck(piece.player)) return false
            if (wouldSquareBeAttacked(3, row, piece.player) || wouldSquareBeAttacked(2, row, piece.player)) return false
            return true
        }
        return false
    }

    private fun isPathClear(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val colStep = Integer.signum(toCol - fromCol)
        val rowStep = Integer.signum(toRow - fromRow)
        var currentCol = fromCol + colStep
        var currentRow = fromRow + rowStep
        while (currentCol != toCol || currentRow != toRow) {
            if (piecesBox[currentCol to currentRow] != null) {
                return false
            }
            currentCol += colStep
            currentRow += rowStep
        }
        return true
    }

    fun getValidMoves(piece: ChessPiece): List<Pair<Int, Int>> {
        val validMoves = mutableListOf<Pair<Int, Int>>()
        for (r in 0..7) {
            for (c in 0..7) {
                if (isMoveValid(piece.col, piece.row, c, r)) {
                    validMoves.add(c to r)
                }
            }
        }
        return validMoves
    }

    fun getAllPieces(): List<ChessPiece> {
        return piecesBox.values.toList()
    }

    fun copyOf(): ChessModel {
        val newModel = ChessModel()
        newModel.setPieces(this.piecesBox.toMutableMap())
        newModel.canWhiteCastleKingSide = this.canWhiteCastleKingSide
        newModel.canWhiteCastleQueenSide = this.canWhiteCastleQueenSide
        newModel.canBlackCastleKingSide = this.canBlackCastleKingSide
        newModel.canBlackCastleQueenSide = this.canBlackCastleQueenSide
        return newModel
    }

    internal fun setPieces(newPieces: Map<Pair<Int, Int>, ChessPiece>) {
        _piecesBox.value = newPieces
    }

    private fun forceMove(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        val updated = piecesBox.toMutableMap()
        val piece = updated[fromCol to fromRow] ?: return
        updated.remove(fromCol to fromRow)
        updated[toCol to toRow] = piece.copy(col = toCol, row = toRow)
        _piecesBox.value = updated
    }

    fun isKingInCheck(player: ChessPlayer): Boolean {
        val kingPos = piecesBox.entries.firstOrNull {
            it.value.player == player && it.value.rank == ChessRank.KING
        }?.key ?: return false

        return wouldSquareBeAttacked(kingPos.first, kingPos.second, player)
    }

    private fun wouldSquareBeAttacked(col: Int, row: Int, player: ChessPlayer): Boolean {
        val opponent = if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
        for ((pos, oppPiece) in piecesBox) {
            if (oppPiece.player == opponent) {
                if (canAttackSquare(oppPiece.col, oppPiece.row, col, row)) {
                    return true
                }
            }
        }
        return false
    }

    private fun canAttackSquare(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = piecesBox[fromCol to fromRow] ?: return false
        val tempTarget = piecesBox[toCol to toRow]
        // If it's your own piece, you can't attack it
        if (tempTarget != null && tempTarget.player == piece.player) {
            return false
        }
        return isMovePatternValid(fromCol, fromRow, toCol, toRow)
    }

    fun isCheckmate(player: ChessPlayer): Boolean {
        // Must be in check
        if (!isKingInCheck(player)) {
            return false
        }
        // Check if any valid moves exist
        val pieces = getAllPieces().filter { it.player == player }
        for (p in pieces) {
            val moves = getValidMoves(p)
            if (moves.isNotEmpty()) {
                return false
            }
        }
        // In check + no moves => checkmate
        return true
    }

    fun isStalemate(player: ChessPlayer): Boolean {
        // King not in check
        if (isKingInCheck(player)) {
            return false
        }
        // If no moves at all => stalemate
        val pieces = getAllPieces().filter { it.player == player }
        for (p in pieces) {
            val moves = getValidMoves(p)
            if (moves.isNotEmpty()) {
                return false
            }
        }
        return true
    }
}
