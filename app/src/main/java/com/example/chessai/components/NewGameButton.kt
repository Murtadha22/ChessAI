package com.example.chessai.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessai.ui.theme.poppinsFontFamily

@Composable
fun NewGameButton(onResetGame: () -> Unit) {
    Button(
        onClick = { onResetGame() },
        shape = CutCornerShape(8.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.Black
        ),
        modifier = Modifier
            .width(130.dp)
            .border(
                width = 2.dp, color = Color.DarkGray, shape = CutCornerShape(8.dp)
            )
    ) {
        Text(
            text = "Restart", fontFamily = poppinsFontFamily, color = Color.White,
            fontSize = 22.sp
        )
    }
}
