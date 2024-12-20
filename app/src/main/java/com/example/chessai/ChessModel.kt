package com.example.chessai

class ChessModel {
    private var piecesBox = mutableMapOf<Pair<Int, Int>, ChessPiece>()
    private val board: Array<Array<ChessPiece?>> = Array(8) { Array(8) { null } }

    init {
        reset()
    }

    private fun reset(){
        piecesBox.clear()

        piecesBox[Pair(0, 0)] = ChessPiece(0, 0, ChessPlayer.WHITE, ChessRank.ROOK)
        piecesBox[Pair(7, 0)] = ChessPiece(7, 0, ChessPlayer.WHITE, ChessRank.ROOK)
        piecesBox[Pair(0, 7)] = ChessPiece(0, 7, ChessPlayer.BLACK, ChessRank.ROOK)
        piecesBox[Pair(7, 7)] = ChessPiece(7, 7, ChessPlayer.BLACK, ChessRank.ROOK)

        piecesBox[Pair(1, 0)] = ChessPiece(1, 0, ChessPlayer.WHITE, ChessRank.KNIGHT)
        piecesBox[Pair(6, 0)] = ChessPiece(6, 0, ChessPlayer.WHITE, ChessRank.KNIGHT)
        piecesBox[Pair(1, 7)] = ChessPiece(1, 7, ChessPlayer.BLACK, ChessRank.KNIGHT)
        piecesBox[Pair(6, 7)] = ChessPiece(6, 7, ChessPlayer.BLACK, ChessRank.KNIGHT)

        piecesBox[Pair(2, 0)] = ChessPiece(2, 0, ChessPlayer.WHITE, ChessRank.BISHOP)
        piecesBox[Pair(5, 0)] = ChessPiece(5, 0, ChessPlayer.WHITE, ChessRank.BISHOP)
        piecesBox[Pair(2, 7)] = ChessPiece(2, 7, ChessPlayer.BLACK, ChessRank.BISHOP)
        piecesBox[Pair(5, 7)] = ChessPiece(5, 7, ChessPlayer.BLACK, ChessRank.BISHOP)

        piecesBox[Pair(3, 0)] = ChessPiece(3, 0, ChessPlayer.WHITE, ChessRank.QUEEN)
        piecesBox[Pair(3, 7)] = ChessPiece(3, 7, ChessPlayer.BLACK, ChessRank.QUEEN)

        piecesBox[Pair(4, 0)] = ChessPiece(4, 0, ChessPlayer.WHITE, ChessRank.KING)
        piecesBox[Pair(4, 7)] = ChessPiece(4, 7, ChessPlayer.BLACK, ChessRank.KING)

        for (col in 0..7) {
            piecesBox[Pair(col, 1)] = ChessPiece(col, 1, ChessPlayer.WHITE, ChessRank.PAWN)
            piecesBox[Pair(col, 6)] = ChessPiece(col, 6, ChessPlayer.BLACK, ChessRank.PAWN)
        }
    }

    fun pieceAt(col: Int, row: Int): ChessPiece? = piecesBox[Pair(col, row)]

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
        val piece = board[fromRow][fromCol]
        if (piece != null) {
            // Move the piece
            board[toRow][toCol] = piece
            board[fromRow][fromCol] = null
        }
    }
}
