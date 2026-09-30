package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ScoreAmber
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.ScoreRed

@Composable
fun ScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when {
        score >= 8 -> Triple(ScoreGreen.copy(alpha = 0.15f), ScoreGreen, ScoreGreen.copy(alpha = 0.4f))
        score >= 6 -> Triple(ScoreAmber.copy(alpha = 0.15f), ScoreAmber, ScoreAmber.copy(alpha = 0.4f))
        else -> Triple(ScoreRed.copy(alpha = 0.15f), ScoreRed, ScoreRed.copy(alpha = 0.4f))
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$score/10",
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
