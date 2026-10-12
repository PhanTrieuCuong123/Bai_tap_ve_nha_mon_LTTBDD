package com.example.bai_4

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
import java.util.Locale

class Student(
    val name: String,
    val score: Double?
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClassScoreScreen()
        }
    }
}

val navy = Color(0xFF2B4478)
val screenBg = Color(0xFFF1F3F8)

// Dùng lại hàm của Bài 2
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

fun gradeColor(grade: String): Color {
    if (grade == "Giỏi") {
        return Color(0xFF2E7D32)
    } else if (grade == "Khá") {
        return Color(0xFF1565C0)
    } else if (grade == "Trung bình") {
        return Color(0xFF9A6700)
    } else if (grade == "Yếu") {
        return Color(0xFFC62828)
    } else {
        return Color(0xFF757575)
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = Color(0xFFD0D8EA))
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun StudentItem(s: Student) {
    val score = s.score
    // score == null thì hiện "Chưa có điểm"
    val grade = if (score == null) "Chưa có điểm" else classify(score)
    val color = if (score == null) Color(0xFF757575) else gradeColor(grade)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(s.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(grade, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
        }
        Text(
            text = if (score == null) "—" else String.format(Locale.US, "%.1f", score),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (score == null) Color.Gray else Color(0xFF212121)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassScoreScreen() {
    val students = listOf(
        Student("Nguyễn Văn An", 9.0),
        Student("Trần Thị Bình", 7.2),
        Student("Lê Minh Chi", null),
        Student("Phạm Quốc Dũng", 4.5),
        Student("Võ Thanh Em", 5.8)
    )

    // Tính thống kê bằng for + if
    var total = 0.0
    var counted = 0
    var excellent = 0
    var noScore = 0
    var highest = 0.0

    for (s in students) {
        val sc = s.score
        if (sc == null) {
            noScore++
        } else {
            total += sc
            counted++
            if (classify(sc) == "Giỏi") {
                excellent++
            }
            if (sc > highest) {
                highest = sc
            }
        }
    }

    val average = if (counted == 0) 0.0 else total / counted

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Bảng điểm lớp", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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
                    .background(navy)
                    .padding(16.dp)
            ) {
                Text("Điểm trung bình lớp", fontSize = 13.sp, color = Color(0xFFD0D8EA))
                Spacer(Modifier.height(4.dp))
                // +1e-9 để tránh sai số số thực khi làm tròn (6.625 -> 6.63)
                Text(
                    String.format(Locale.US, "%.2f", average + 1e-9),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x33FFFFFF))
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem("Giỏi", "$excellent")
                    StatItem("Chưa có điểm", "$noScore")
                    StatItem("Cao nhất", String.format(Locale.US, "%.1f", highest))
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                "DANH SÁCH · ${students.size} SINH VIÊN",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(8.dp))

            for (s in students) {
                StudentItem(s)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ClassScoreScreenPreview() {
    ClassScoreScreen()
}