package com.example.chessai

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import com.example.chessai.core.ChessPiece
import com.example.chessai.core.ChessPlayer
import com.example.chessai.core.ChessRank
import com.example.chessai.core.Move
import com.example.chessai.fen.FenPosition
import com.example.chessai.fen.FenUtils
import kotlin.math.abs

class ChessModel {
    private val initialFen = "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr w KQkq - 0 1"
    private val _fenPosition = mutableStateOf(FenUtils.parseFen(initialFen))
    private val moveHistory = mutableListOf<Move>()
    private val undoneMoves = mutableListOf<Move>()
    var canWhiteCastleKingSide = true
    var canWhiteCastleQueenSide = true
    var canBlackCastleKingSide = true
    var canBlackCastleQueenSide = true

    init {
        reset()
    }

    fun reset() {
        _fenPosition.value = FenUtils.parseFen(initialFen)
        canWhiteCastleKingSide = true
        canWhiteCastleQueenSide = true
        canBlackCastleKingSide = true
        canBlackCastleQueenSide = true
        moveHistory.clear()
        undoneMoves.clear()
    }

    fun pieceAt(col: Int, row: Int): ChessPiece? {
        val code = _fenPosition.value.board[row * 8 + col]
        if (code == 0) return null
        val player = if (code > 0) ChessPlayer.WHITE else ChessPlayer.BLACK
        val absCode = abs(code)
        val rank = when (absCode) {
            1 -> ChessRank.PAWN
            2 -> ChessRank.ROOK
            3 -> ChessRank.KNIGHT
            4 -> ChessRank.BISHOP
            5 -> ChessRank.QUEEN
            6 -> ChessRank.KING
            else -> ChessRank.PAWN
        }
        val resId = when (rank) {
            ChessRank.KING -> if (player == ChessPlayer.WHITE) R.drawable.king else R.drawable.king1
            ChessRank.QUEEN -> if (player == ChessPlayer.WHITE) R.drawable.queen else R.drawable.queen1
            ChessRank.ROOK -> if (player == ChessPlayer.WHITE) R.drawable.rook else R.drawable.rook1
            ChessRank.BISHOP -> if (player == ChessPlayer.WHITE) R.drawable.bishop else R.drawable.bishop1
            ChessRank.KNIGHT -> if (player == ChessPlayer.WHITE) R.drawable.knight else R.drawable.knight1
            ChessRank.PAWN -> if (player == ChessPlayer.WHITE) R.drawable.pawn else R.drawable.pawn1
        }
        return ChessPiece(col, row, player, rank, resId)
    }

    override fun toString(): String {
        return FenUtils.toFen(_fenPosition.value)
    }

    fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        val fromIndex = fromRow * 8 + fromCol
        val toIndex = toRow * 8 + toCol
        val board = _fenPosition.value.board
        val piece = board[fromIndex]
        if (piece == 0) return
        if (!isMoveValid(fromCol, fromRow, toCol, toRow)) return
        val captured = board[toIndex]
        val move = Move(fromIndex, toIndex, piece, captured)
        makeMoveUI(move)
        undoneMoves.clear()
        updateFenAfterMove()
    }

//    fun undoMove() {
//        if (moveHistory.isEmpty()) return
//        val lastMove = moveHistory.removeLast()
//        unmakeMoveUI(lastMove)
//        undoneMoves.add(lastMove)
//        updateFenAfterMove()
//    }
//
//    fun redoMove() {
//        if (undoneMoves.isEmpty()) return
//        val move = undoneMoves.removeLast()
//        makeMoveUI(move)
//        moveHistory.add(move)
//        updateFenAfterMove()
//    }

    private fun makeMoveUI(move: Move) {
        val newBoard = _fenPosition.value.board.clone()
        newBoard[move.toIndex] = move.movedPiece
        newBoard[move.fromIndex] = 0
        _fenPosition.value = _fenPosition.value.copy(board = newBoard)
        moveHistory.add(move)
    }

    private fun unmakeMoveUI(move: Move) {
        val newBoard = _fenPosition.value.board.clone()
        newBoard[move.fromIndex] = move.movedPiece
        newBoard[move.toIndex] = move.capturedPiece
        _fenPosition.value = _fenPosition.value.copy(board = newBoard)
    }

    fun makeMoveSilent(move: Move) {
        val b = _fenPosition.value.board
        val temp = b[move.toIndex]
        b[move.toIndex] = move.movedPiece
        b[move.fromIndex] = 0
    }

    fun unmakeMoveSilent(move: Move) {
        val b = _fenPosition.value.board
        b[move.fromIndex] = move.movedPiece
        b[move.toIndex] = move.capturedPiece
    }

    private fun updateFenAfterMove() {
        val p = _fenPosition.value
        val nextColor = if (p.activeColor == "w") "b" else "w"
        _fenPosition.value = p.copy(activeColor = nextColor)
    }

    fun generateMovesForPlayer(isBlack: Boolean): List<Move> {
        val result = mutableListOf<Move>()
        val pieces = getAllPieces().filter { if (isBlack) it.player == ChessPlayer.BLACK else it.player == ChessPlayer.WHITE }
        for (p in pieces) {
            val valid = getValidMoves(p)
            for (v in valid) {
                val fromIndex = p.row * 8 + p.col
                val toIndex = v.second * 8 + v.first
                val b = _fenPosition.value.board
                val captured = b[toIndex]
                result.add(Move(fromIndex, toIndex, b[fromIndex], captured))
            }
        }
        return result
    }

    private fun isMoveValid(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = pieceAt(fromCol, fromRow) ?: return false
        if (fromCol == toCol && fromRow == toRow) return false
        val target = pieceAt(toCol, toRow)
        if (target != null && target.player == piece.player) return false
        if (!isMovePatternValid(fromCol, fromRow, toCol, toRow)) return false
        val testMove = Move(
            fromRow * 8 + fromCol,
            toRow * 8 + toCol,
            _fenPosition.value.board[fromRow * 8 + fromCol],
            _fenPosition.value.board[toRow * 8 + toCol]
        )
        makeMoveSilent(testMove)
        val kingInCheck = isKingInCheck(piece.player)
        unmakeMoveSilent(testMove)
        return !kingInCheck
    }

    private fun isMovePatternValid(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = pieceAt(fromCol, fromRow) ?: return false
        val targetPiece = pieceAt(toCol, toRow)
        return when (piece.rank) {
            ChessRank.PAWN -> {
                val direction = if (piece.player == ChessPlayer.WHITE) 1 else -1
                val startRow = if (piece.player == ChessPlayer.WHITE) 1 else 6
                val forwardMove = (toRow == fromRow + direction && toCol == fromCol && targetPiece == null)
                val doubleMove = (fromRow == startRow && toRow == fromRow + 2 * direction && toCol == fromCol && targetPiece == null && pieceAt(fromCol, fromRow + direction) == null)
                val diagonalCapture = (toRow == fromRow + direction && abs(toCol - fromCol) == 1 && targetPiece != null)
                forwardMove || doubleMove || diagonalCapture
            }
            ChessRank.ROOK -> (fromCol == toCol || fromRow == toRow) && isPathClear(fromCol, fromRow, toCol, toRow)
            ChessRank.BISHOP -> abs(fromCol - toCol) == abs(fromRow - toRow) && isPathClear(fromCol, fromRow, toCol, toRow)
            ChessRank.KNIGHT -> (abs(fromCol - toCol) == 2 && abs(fromRow - toRow) == 1) || (abs(fromCol - toCol) == 1 && abs(fromRow - toRow) == 2)
            ChessRank.QUEEN -> (fromCol == toCol || fromRow == toRow || abs(fromCol - toCol) == abs(fromRow - toRow)) && isPathClear(fromCol, fromRow, toCol, toRow)
            ChessRank.KING -> {
                if (abs(fromCol - toCol) <= 1 && abs(fromRow - toRow) <= 1) true
                else if (fromRow == toRow && abs(fromCol - toCol) == 2) canCastle(fromCol, fromRow, toCol, toRow)
                else false
            }
        }
    }

    private fun canCastle(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = pieceAt(fromCol, fromRow) ?: return false
        if (piece.rank != ChessRank.KING) return false
        val isWhite = (piece.player == ChessPlayer.WHITE)
        val row = if (isWhite) 0 else 7
        if (row != fromRow) return false
        if (toCol == 6 && fromCol == 4) {
            if (isWhite && !canWhiteCastleKingSide) return false
            if (!isWhite && !canBlackCastleKingSide) return false
            if (pieceAt(5, row) != null || pieceAt(6, row) != null) return false
            if (isKingInCheck(piece.player)) return false
            if (wouldSquareBeAttacked(5, row, piece.player) || wouldSquareBeAttacked(6, row, piece.player)) return false
            return true
        }
        if (toCol == 2 && fromCol == 4) {
            if (isWhite && !canWhiteCastleQueenSide) return false
            if (!isWhite && !canBlackCastleQueenSide) return false
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
            if (pieceAt(currentCol, currentRow) != null) return false
            currentCol += colStep
            currentRow += rowStep
        }
        return true
    }

    fun getValidMoves(piece: ChessPiece): List<Pair<Int, Int>> {
        val validMoves = mutableListOf<Pair<Int, Int>>()
        for (r in 0..7) {
            for (c in 0..7) {
                if (isMoveValid(piece.col, piece.row, c, r)) validMoves.add(c to r)
            }
        }
        return validMoves
    }

    fun getAllPieces() = (0 until 64).mapNotNull {
        val code = _fenPosition.value.board[it]
        if (code == 0) null
        else {
            val player = if (code > 0) ChessPlayer.WHITE else ChessPlayer.BLACK
            val absCode = abs(code)
            val rank = when (absCode) {
                1 -> ChessRank.PAWN
                2 -> ChessRank.ROOK
                3 -> ChessRank.KNIGHT
                4 -> ChessRank.BISHOP
                5 -> ChessRank.QUEEN
                6 -> ChessRank.KING
                else -> ChessRank.PAWN
            }
            val col = it % 8
            val row = it / 8
            val resId = when (rank) {
                ChessRank.KING -> if (player == ChessPlayer.WHITE) R.drawable.king else R.drawable.king1
                ChessRank.QUEEN -> if (player == ChessPlayer.WHITE) R.drawable.queen else R.drawable.queen1
                ChessRank.ROOK -> if (player == ChessPlayer.WHITE) R.drawable.rook else R.drawable.rook1
                ChessRank.BISHOP -> if (player == ChessPlayer.WHITE) R.drawable.bishop else R.drawable.bishop1
                ChessRank.KNIGHT -> if (player == ChessPlayer.WHITE) R.drawable.knight else R.drawable.knight1
                ChessRank.PAWN -> if (player == ChessPlayer.WHITE) R.drawable.pawn else R.drawable.pawn1
            }
            ChessPiece(col, row, player, rank, resId)
        }
    }

    fun copyOf(): ChessModel {
        val newModel = ChessModel()
        val fen = FenUtils.toFen(_fenPosition.value)
        newModel.setFen(fen)
        newModel.canWhiteCastleKingSide = canWhiteCastleKingSide
        newModel.canWhiteCastleQueenSide = canWhiteCastleQueenSide
        newModel.canBlackCastleKingSide = canBlackCastleKingSide
        newModel.canBlackCastleQueenSide = canBlackCastleQueenSide
        return newModel
    }

    fun setFen(fen: String) {
        _fenPosition.value = FenUtils.parseFen(fen)
    }

    fun isKingInCheck(player: ChessPlayer): Boolean {
        val kingPos = getAllPieces().firstOrNull { it.player == player && it.rank == ChessRank.KING } ?: return false
        return wouldSquareBeAttacked(kingPos.col, kingPos.row, player)
    }

    private fun wouldSquareBeAttacked(col: Int, row: Int, player: ChessPlayer): Boolean {
        val opponent = if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
        for (opp in getAllPieces().filter { it.player == opponent }) {
            if (canAttackSquare(opp.col, opp.row, col, row)) return true
        }
        return false
    }

    private fun canAttackSquare(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): Boolean {
        val piece = pieceAt(fromCol, fromRow) ?: return false
        val target = pieceAt(toCol, toRow)
        if (target != null && target.player == piece.player) return false
        return isMovePatternValid(fromCol, fromRow, toCol, toRow)
    }

    fun isCheckmate(player: ChessPlayer): Boolean {
        if (!isKingInCheck(player)) return false
        val pieces = getAllPieces().filter { it.player == player }
        for (p in pieces) {
            if (getValidMoves(p).isNotEmpty()) return false
        }
        return true
    }

    fun isStalemate(player: ChessPlayer): Boolean {
        if (isKingInCheck(player)) return false
        val pieces = getAllPieces().filter { it.player == player }
        for (p in pieces) {
            if (getValidMoves(p).isNotEmpty()) return false
        }
        return true
    }
}
