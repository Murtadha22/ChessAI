package com.example.chessai.ai

import com.example.chessai.ChessModel
import com.example.chessai.core.ChessPlayer
import com.example.chessai.core.ChessRank
import kotlin.math.max
import kotlin.math.min

object AiEngine {

    private val pieceValues = mapOf(
        ChessRank.PAWN   to 100,
        ChessRank.KNIGHT to 300,
        ChessRank.BISHOP to 300,
        ChessRank.ROOK   to 500,
        ChessRank.QUEEN  to 900,
        ChessRank.KING   to 10000
    )

    // Evaluate the board from Black's perspective.
    // Positive score = better for Black. Negative score = better for White.
    fun evaluateBoard(chessModel: ChessModel): Int {
        var score = 0
        val allPieces = chessModel.getAllPieces()
        for (piece in allPieces) {
            val value = pieceValues[piece.rank] ?: 0
            if (piece.player == ChessPlayer.BLACK) {
                score += value
            } else {
                score -= value
            }
        }
        return score
    }

    // If we are within the first few moves and have a known opening => pick that move. Otherwise, proceed with alpha-beta search.
    fun findBestMove(
        chessModel: ChessModel,
        maxDepth: Int = 3,
        moveNumber: Int = 1,
    ): Pair<Pair<Int, Int>, Pair<Int, Int>>? {

        if (moveNumber == 1) {
            val signature = Openings.detectSimpleOpeningSignature(chessModel)

            if (signature != null && Openings.openingBook.containsKey(signature)) {
                val possibleMoves = Openings.openingBook[signature]!!
                if (possibleMoves.isNotEmpty()) {
                    return possibleMoves.random()
                }
            }
        }

        var bestMove: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
        var bestEval = Int.MIN_VALUE

        // Generate all Black moves
        val blackPieces = chessModel.getAllPieces().filter { it.player == ChessPlayer.BLACK }
        val blackMoves = blackPieces.flatMap { piece ->
            val validMoves = chessModel.getValidMoves(piece)
            validMoves.map { to -> (piece.col to piece.row) to to }
        }

        // If no moves => checkmate/stalemate
        if (blackMoves.isEmpty()) {
            return null
        }
        val copy = chessModel.copyOf()
        for (move in blackMoves) {
            val from = move.first
            val to = move.second
            // Copy the board so we can simulate the future moves on
            copy.movePiece(from.first, from.second, to.first, to.second)

            val eval = alphaBeta(
                copy,
                depth = 1,
                maxDepth = maxDepth,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                isBlackTurn = false
            )

            if (eval > bestEval) {
                bestEval = eval
                bestMove = move
            }
        }

        return bestMove
    }

    // https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
    private fun alphaBeta(
        model: ChessModel,
        depth: Int,
        maxDepth: Int,
        alpha: Int,
        beta: Int,
        isBlackTurn: Boolean
    ): Int {
        var alphaVar = alpha
        var betaVar = beta

        // Base case: reached max depth
        if (depth >= maxDepth) {
            return evaluateBoard(model)
        }

        val currentPieces = model.getAllPieces().filter {
            if (isBlackTurn) it.player == ChessPlayer.BLACK else it.player == ChessPlayer.WHITE
        }

        val possibleMoves = currentPieces.flatMap { piece ->
            val validMoves = model.getValidMoves(piece)
            validMoves.map { to -> (piece.col to piece.row) to to }
        }

        // If no moves => checkmate or stalemate scenario
        if (possibleMoves.isEmpty()) {
            return if (isBlackTurn) (Int.MIN_VALUE / 2) else (Int.MAX_VALUE / 2)
        }
        if (isBlackTurn) {
            // Maximize for black
            var bestScore = Int.MIN_VALUE
            for (move in possibleMoves) {
                model.movePiece(move.first.first, move.first.second, move.second.first, move.second.second)

                val score = alphaBeta(
                    model,
                    depth + 1,
                    maxDepth,
                    alphaVar,
                    betaVar,
                    isBlackTurn = false
                )
                bestScore = max(bestScore, score)
                alphaVar = max(alphaVar, bestScore)
                if (betaVar <= alphaVar) {
                    break // Pruning
                }
            }
            return bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            for (move in possibleMoves) {
                model.movePiece(move.first.first, move.first.second, move.second.first, move.second.second)
                val score = alphaBeta(
                    model,
                    depth + 1,
                    maxDepth,
                    alphaVar,
                    betaVar,
                    isBlackTurn = true
                )
                bestScore = min(bestScore, score)
                betaVar = min(betaVar, bestScore)
                if (betaVar <= alphaVar) {
                    break // Pruning
                }
            }
            return bestScore
        }
    }
}
