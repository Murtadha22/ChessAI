package com.example.chessai.ai

import com.example.chessai.ChessModel
import com.example.chessai.core.ChessPlayer
import com.example.chessai.core.ChessRank

data class Opening(
    val name: String,
    val signature: String,
    val blackReplies: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>
)

object Openings {

    // A small sample from https://en.wikipedia.org/wiki/List_of_chess_openings
    val openingBook: Map<String, List<Pair<Pair<Int, Int>, Pair<Int, Int>>>> = mapOf(
        "whiteE2E4" to listOf(
            (4 to 6) to (4 to 4),
            (2 to 6) to (2 to 4),
            (5 to 6) to (5 to 5)
        ),
        "whiteC2C4" to listOf(
            (4 to 6) to (4 to 4),
            (2 to 6) to (2 to 4)
        ),
        "whiteG1F3" to listOf(
            (4 to 6) to (4 to 4),
            (6 to 6) to (6 to 4)
        ),
        "whiteB1C3" to listOf(
            (4 to 6) to (4 to 4),
            (1 to 6) to (1 to 5)
        ),
        "whiteF2F4" to listOf(
            (3 to 6) to (3 to 5),
            (4 to 6) to (4 to 5)
        ),
        "whiteG2G3" to listOf(
            (4 to 6) to (4 to 4),
            (6 to 6) to (6 to 4)
        ),
        "whiteA2A3" to listOf(
            (4 to 6) to (4 to 4),
            (0 to 6) to (0 to 5)
        ),
        "whiteA2A4" to listOf(
            (4 to 6) to (4 to 4),
            (0 to 6) to (0 to 5)
        ),
        "whiteB2B3" to listOf(
            (4 to 6) to (4 to 4),
            (1 to 6) to (1 to 5)
        ),
        "whiteB2B4" to listOf(
            (4 to 6) to (4 to 4),
            (1 to 6) to (1 to 5)
        ),
        "whiteC2C3" to listOf(
            (4 to 6) to (4 to 4),
            (2 to 6) to (2 to 5)
        ),
        "whiteD2D3" to listOf(
            (3 to 6) to (3 to 4),
            (4 to 6) to (4 to 5)
        ),
        "whiteD2D4" to listOf(
            (3 to 6) to (3 to 4),
            (4 to 6) to (4 to 5)
        ),
        "whiteE2E3" to listOf(
            (4 to 6) to (4 to 4),
            (5 to 6) to (5 to 5)
        ),
        "whiteF2F3" to listOf(
            (4 to 6) to (4 to 4),
            (5 to 6) to (5 to 5)
        ),
        "whiteG2G4" to listOf(
            (4 to 6) to (4 to 4),
            (6 to 6) to (6 to 4)
        ),
        "whiteH2H3" to listOf(
            (4 to 6) to (4 to 4),
            (7 to 6) to (7 to 5)
        ),
        "whiteH2H4" to listOf(
            (4 to 6) to (4 to 4),
            (7 to 6) to (7 to 5)
        ),
        "whiteG1H3" to listOf(
            (4 to 6) to (4 to 4),
            (6 to 6) to (6 to 4)
        ),
        "whiteB1A3" to listOf(
            (4 to 6) to (4 to 4),
            (1 to 6) to (1 to 5)
        ),
    )

    fun detectSimpleOpeningSignature(chessModel: ChessModel): String? {
        fun hasWhitePawnAt(col: Int, row: Int): Boolean {
            val piece = chessModel.pieceAt(col, row)
            return piece != null && piece.player == ChessPlayer.WHITE && piece.rank == ChessRank.PAWN
        }

        fun hasWhiteKnightAt(col: Int, row: Int): Boolean {
            val piece = chessModel.pieceAt(col, row)
            return piece != null && piece.player == ChessPlayer.WHITE && piece.rank == ChessRank.KNIGHT
        }

        val signatures = listOf(
            // Pawn Moves
            "whiteE2E4" to (hasWhitePawnAt(4, 3) && !hasWhitePawnAt(4, 2)),
            "whiteE2E3" to (hasWhitePawnAt(4, 2) && !hasWhitePawnAt(4, 3)),
            "whiteD2D4" to (hasWhitePawnAt(3, 3) && !hasWhitePawnAt(3, 2)),
            "whiteD2D3" to (hasWhitePawnAt(3, 2) && !hasWhitePawnAt(3, 3)),
            "whiteC2C4" to (hasWhitePawnAt(2, 3) && !hasWhitePawnAt(2, 2)),
            "whiteC2C3" to (hasWhitePawnAt(2, 2) && !hasWhitePawnAt(2, 3)),
            "whiteF2F4" to (hasWhitePawnAt(5, 3) && !hasWhitePawnAt(5, 2)),
            "whiteF2F3" to (hasWhitePawnAt(5, 2) && !hasWhitePawnAt(5, 3)),
            "whiteG2G3" to (hasWhitePawnAt(6, 2) && !hasWhitePawnAt(6, 3)),
            "whiteG2G4" to (hasWhitePawnAt(6, 4) && !hasWhitePawnAt(6, 3)),
            "whiteA2A3" to (hasWhitePawnAt(0, 2) && !hasWhitePawnAt(0, 3)),
            "whiteA2A4" to (hasWhitePawnAt(0, 3) && !hasWhitePawnAt(0, 2)),
            "whiteB2B3" to (hasWhitePawnAt(1, 2) && !hasWhitePawnAt(1, 3)),
            "whiteB2B4" to (hasWhitePawnAt(1, 3) && !hasWhitePawnAt(1, 2)),
            "whiteH2H3" to (hasWhitePawnAt(7, 2) && !hasWhitePawnAt(7, 3)),
            "whiteH2H4" to (hasWhitePawnAt(7, 3) && !hasWhitePawnAt(7, 2)),
            // Knight Moves
            "whiteG1F3" to (hasWhiteKnightAt(5, 2) && !hasWhiteKnightAt(6, 0)),
            "whiteG1H3" to (hasWhiteKnightAt(7, 2) && !hasWhiteKnightAt(6, 0)),
            "whiteB1C3" to (hasWhiteKnightAt(2, 2) && !hasWhiteKnightAt(1, 0)),
            "whiteB1A3" to (hasWhiteKnightAt(0, 2) && !hasWhiteKnightAt(1, 0))
        )

        for ((signature, condition) in signatures) {
            if (condition) {
                return signature
            }
        }

        return null
    }
}
