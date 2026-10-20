package com.example.bai_1

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun ComposeLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val r = size.minDimension / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val dx = r * 0.866f

        val top = Offset(cx, cy - r)
        val upperRight = Offset(cx + dx, cy - r / 2f)
        val lowerRight = Offset(cx + dx, cy + r / 2f)
        val bottom = Offset(cx, cy + r)
        val lowerLeft = Offset(cx - dx, cy + r / 2f)
        val upperLeft = Offset(cx - dx, cy - r / 2f)
        val center = Offset(cx, cy)

        fun face(color: Color, points: List<Offset>) {
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
                close()
            }
            drawPath(path, color)
        }

        // 3 mặt của khối lục giác
        face(Color(0xFF3DDC84), listOf(top, upperRight, center, upperLeft))
        face(Color(0xFF073042), listOf(upperLeft, center, bottom, lowerLeft))
        face(Color(0xFF4285F4), listOf(upperRight, lowerRight, bottom, center))

        // Lục giác sáng ở giữa
        val ir = r * 0.45f
        val idx = ir * 0.866f
        val hole = Path().apply {
            moveTo(cx, cy - ir)
            lineTo(cx + idx, cy - ir / 2f)
            lineTo(cx + idx, cy + ir / 2f)
            lineTo(cx, cy + ir)
            lineTo(cx - idx, cy + ir / 2f)
            lineTo(cx - idx, cy - ir / 2f)
            close()
        }
        drawPath(hole, Color(0xFFD6EEFF))
    }
}