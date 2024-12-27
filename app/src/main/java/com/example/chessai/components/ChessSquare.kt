package com.example.chessai.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chessai.ChessPiece
import com.example.chessai.R
import com.example.chessai.customShadow
import com.example.chessai.ui.theme.getChessSquareColors

@Composable
fun ChessSquare(
    isWhite: Boolean,
    piece: ChessPiece?,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isValidMove: Boolean = false,
    isOpponentPiece: Boolean = false,
    onMoveMade: () -> Unit,
    onPieceSelected: () -> Unit
) {
    val backgroundColor =
        when {
            isWhite -> getChessSquareColors().first
            else -> getChessSquareColors().second
        }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = androidx.compose.animation.core.InfiniteRepeatableSpec(
            animation = androidx.compose.animation.core.keyframes {
                durationMillis = 1000
                1.2f at 0 using androidx.compose.animation.core.EaseInOut
                1f at 1000
            }
        ), label = ""
    )
    Box(modifier = Modifier
        .size(45.dp)
        .background(color = backgroundColor)
        .clickable {
            onClick()
            if (isSelected) {
                onMoveMade()
            } else {
                onPieceSelected()
            }
        },
        contentAlignment = Alignment.Center
    ) {
        when {
            isOpponentPiece -> {
                Box(
                    modifier = Modifier
                        .customShadow(
                            color = Color.Red,
                            alpha = 1f,
                            shadowRadius = 16.dp,
                            borderRadius = 50.dp,
                            offsetY = 6.dp
                        )
                        .size(30.dp)
                )
            }
            isValidMove -> {
                Image(
                    painter = painterResource(id = R.drawable.ad_adjust_24),
                    contentDescription = "Valid Move",
                    modifier = Modifier
                        .customShadow(
                            color = Color.Black,
                            alpha = 0.3f,
                            shadowRadius = 16.dp,
                            borderRadius = 50.dp,
                            offsetY = 6.dp
                        )
                        .size(22.dp)
                )
            }
        }

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
                    .scale(if (isSelected) animatedScale else 1f)
            )
        }
    }
}
