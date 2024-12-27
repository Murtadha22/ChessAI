package com.example.chessai.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.chessai.R

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@Composable
fun getChessSquareColors(): Pair<Color, Color> {
    val colorOne = colorResource(R.color.one)
    val colorZero = colorResource(R.color.zero)
    return Pair(colorOne, colorZero)
}
val ChessSquareColorIsWhite = Color(0xFFF4DCF7).copy(alpha = 0.4f)
val ChessSquareColorIsNotWhite = Color(0xFFF4DCF7).copy(alpha = 0.4f)
val ChessSquareColorIsSelected = Color(0xE2B7B7B7).copy(alpha = 0.4f)
val ChessSquareColorIsValidMove = Color(0xE2B7B7B7).copy(alpha = 0.4f)
val TextColorBackground = Color(0xFF6882AF).copy(alpha = 0.4f)

val ColorSurface = Color.White
val ColorImageShadow = Color(0xFFfdc228)
val ColorWindForecast = Color.White.copy(alpha = 0.2f)
val ColorAirQualityIconTitle = Color(0xFFa09bf0)