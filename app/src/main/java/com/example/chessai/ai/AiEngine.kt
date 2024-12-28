package com.example.chessai.ai

import android.util.Log
import com.example.chessai.ChessModel
import com.example.chessai.core.ChessPlayer
import com.example.chessai.core.ChessRank
import com.example.chessai.core.Move
import kotlin.math.max
import kotlin.math.min

object AiEngine {
    private val pieceValues = mapOf(
        ChessRank.PAWN to 100,
        ChessRank.KNIGHT to 300,
        ChessRank.BISHOP to 300,
        ChessRank.ROOK to 500,
        ChessRank.QUEEN to 900,
        ChessRank.KING to 10000
    )

    fun evaluateBoard(chessModel: ChessModel): Int {
        var score = 0
        for (piece in chessModel.getAllPieces()) {
            val value = pieceValues[piece.rank] ?: 0
            if (piece.player == ChessPlayer.BLACK) score += value else score -= value
        }
        return score
    }

    fun findBestMove(chessModel: ChessModel, maxDepth: Int = 3, moveNumber: Int = 1): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
        if (moveNumber == 1) {
            val signature = Openings.detectSimpleOpeningSignature(chessModel)
            if (signature != null && Openings.openingBook.containsKey(signature)) {
                val possibleMoves = Openings.openingBook[signature]!!
                if (possibleMoves.isNotEmpty()) return possibleMoves.random()
            }
        }
        var bestMove: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
        var bestEval = Int.MIN_VALUE
        val moves = chessModel.generateMovesForPlayer(true)
        if (moves.isEmpty()) return null
        for (m in moves) {
            chessModel.makeMoveSilent(m)
            val eval = alphaBeta(chessModel, 1, maxDepth, Int.MIN_VALUE, Int.MAX_VALUE, false)
            chessModel.unmakeMoveSilent(m)
            if (eval > bestEval) {
                bestEval = eval
                val fromCol = m.fromIndex % 8
                val fromRow = m.fromIndex / 8
                val toCol = m.toIndex % 8
                val toRow = m.toIndex / 8
                bestMove = (fromCol to fromRow) to (toCol to toRow)
            }
        }
        return bestMove
    }

    private fun alphaBeta(model: ChessModel, depth: Int, maxDepth: Int, alpha: Int, beta: Int, isBlackTurn: Boolean): Int {
        var a = alpha
        var b = beta
        if (depth >= maxDepth) return evaluateBoard(model)
        val moves = model.generateMovesForPlayer(isBlackTurn)
        if (moves.isEmpty()) return if (isBlackTurn) Int.MIN_VALUE / 2 else Int.MAX_VALUE / 2
        return if (isBlackTurn) {
            var bestScore = Int.MIN_VALUE
            for (m in moves) {
                model.makeMoveSilent(m)
                val score = alphaBeta(model, depth + 1, maxDepth, a, b, false)
                model.unmakeMoveSilent(m)
                bestScore = max(bestScore, score)
                a = max(a, bestScore)
                if (b <= a) break
            }
            bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            for (m in moves) {
                model.makeMoveSilent(m)
                val score = alphaBeta(model, depth + 1, maxDepth, a, b, true)
                model.unmakeMoveSilent(m)
                bestScore = min(bestScore, score)
                b = min(b, bestScore)
                if (b <= a) break
            }
            bestScore
        }
    }
}
