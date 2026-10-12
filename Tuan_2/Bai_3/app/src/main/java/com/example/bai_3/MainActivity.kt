package com.example.bai_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GradeScreen()
        }
    }
}

val navy = Color(0xFF2B4478)
val screenBg = Color(0xFFF1F3F8)

// Hàm xếp loại
fun classify(score: Double): String {
    if (score < 0 || score > 10) {
        return "Không hợp lệ"
    } else if (score >= 8.0) {
        return "Giỏi"
    } else if (score >= 6.5) {
        return "Khá"
    } else if (score >= 5.0) {
        return "Trung bình"
    } else {
        return "Yếu"
    }
}

// Màu chữ chọn bằng if
fun textColorOf(grade: String): Color {
    if (grade == "Giỏi") {
        return Color(0xFF2E7D32)
    } else if (grade == "Khá") {
        return Color(0xFF1565C0)
    } else if (grade == "Trung bình") {
        return Color(0xFFEF6C00)
    } else if (grade == "Yếu") {
        return Color(0xFFC62828)
    } else {
        return Color(0xFF616161)
    }
}

// Màu nền chọn bằng if
fun bgColorOf(grade: String): Color {
    if (grade == "Giỏi") {
        return Color(0xFFDDEFDD)
    } else if (grade == "Khá") {
        return Color(0xFFE3EEFB)
    } else if (grade == "Trung bình") {
        return Color(0xFFFDF0DC)
    } else if (grade == "Yếu") {
        return Color(0xFFFBE4E4)
    } else {
        return Color(0xFFE8E8EC)
    }
}

fun scoreLabel(score: Double): String {
    return if (score == Math.floor(score)) score.toInt().toString() else score.toString()
}

@Composable
fun ExtraRow(score: Double) {
    val grade = classify(score)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Điểm ${scoreLabel(score)}", fontSize = 14.sp)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(bgColorOf(grade))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                grade,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColorOf(grade)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeScreen() {
    val score = 8.5
    val grade = classify(score)
    val extra = listOf(9.0, 7.0, 5.5, 3.0, 11.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Xếp loại học lực", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = navy,
                    titleContentColor = Color.White
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBg)
                .padding(inner)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Điểm trung bình", fontSize = 13.sp, color = Color.DarkGray)
                Spacer(Modifier.height(4.dp))
                Text(score.toString(), fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColorOf(grade))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        grade,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorOf(grade)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "KIỂM TRA THÊM",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                for (s in extra) {
                    ExtraRow(s)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun GradeScreenPreview() {
    GradeScreen()
}