package com.example.chessai.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chessai.ChessPiece
import com.example.chessai.R
import com.example.chessai.customShadow
import com.example.chessai.getPieceDrawable

@Composable
fun ChessSquare(
    isWhite: Boolean,
    piece: ChessPiece?,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isValidMove: Boolean = false
) {
    val backgroundColor =
        when {
            isSelected -> Color.Blue
            isValidMove -> Color.Green
            isWhite -> colorResource(id = R.color.one)
            else -> colorResource(id = R.color.zero)
        }
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(color = backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ){
        piece?.let {
            val drawable = getPieceDrawable(it)
            Image(
                painter = painterResource(id = drawable),
                contentDescription = null,
                modifier = Modifier
                    .customShadow(
                        color = Color.Gray,
                        alpha = 0.7f,
                        shadowRadius = 16.dp,
                        borderRadius = 50.dp,
                        offsetY = 6.dp
                    )
                    .size(30.dp)
            )
        }
    }
}