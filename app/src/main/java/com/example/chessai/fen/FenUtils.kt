package com.example.chessai.fen

object FenUtils {
    fun parseFen(fen: String): FenPosition {
        val parts = fen.split(" ")
        val boardPart = parts[0]
        val activeColor = parts.getOrNull(1) ?: "w"
        val castling = parts.getOrNull(2) ?: "KQkq"
        val enPassant = parts.getOrNull(3) ?: "-"
        val halfMoveClock = parts.getOrNull(4)?.toIntOrNull() ?: 0
        val fullMoveNumber = parts.getOrNull(5)?.toIntOrNull() ?: 1
        val boardArray = IntArray(64) { 0 }
        var rankIndex = 0
        for (rank in boardPart.split("/")) {
            var fileIndex = 0
            for (ch in rank) {
                if (ch.isDigit()) {
                    val count = ch.digitToInt()
                    repeat(count) {
                        boardArray[rankIndex * 8 + fileIndex] = 0
                        fileIndex++
                    }
                } else {
                    boardArray[rankIndex * 8 + fileIndex] = pieceCharToCode(ch)
                    fileIndex++
                }
            }
            rankIndex++
        }
        return FenPosition(
            board = boardArray,
            activeColor = activeColor,
            castling = castling,
            enPassant = enPassant,
            halfMoveClock = halfMoveClock,
            fullMoveNumber = fullMoveNumber
        )
    }

    fun toFen(pos: FenPosition): String {
        val sb = StringBuilder()
        for (r in 0..7) {
            var emptyCount = 0
            for (c in 0..7) {
                val piece = pos.board[r * 8 + c]
                if (piece == 0) {
                    emptyCount++
                } else {
                    if (emptyCount > 0) {
                        sb.append(emptyCount)
                        emptyCount = 0
                    }
                    sb.append(pieceCodeToChar(piece))
                }
            }
            if (emptyCount > 0) sb.append(emptyCount)
            if (r != 7) sb.append("/")
        }
        sb.append(" ")
            .append(pos.activeColor)
            .append(" ")
            .append(pos.castling)
            .append(" ")
            .append(pos.enPassant)
            .append(" ")
            .append(pos.halfMoveClock)
            .append(" ")
            .append(pos.fullMoveNumber)
        return sb.toString()
    }

    fun pieceCharToCode(ch: Char): Int {
        return when (ch) {
            'r' -> -2
            'n' -> -3
            'b' -> -4
            'q' -> -5
            'k' -> -6
            'p' -> -1
            'R' -> 2
            'N' -> 3
            'B' -> 4
            'Q' -> 5
            'K' -> 6
            'P' -> 1
            else -> 0
        }
    }

    fun pieceCodeToChar(code: Int): Char {
        return when (code) {
            -2 -> 'r'
            -3 -> 'n'
            -4 -> 'b'
            -5 -> 'q'
            -6 -> 'k'
            -1 -> 'p'
            2 -> 'R'
            3 -> 'N'
            4 -> 'B'
            5 -> 'Q'
            6 -> 'K'
            1 -> 'P'
            else -> ' '
        }
    }
}
