package com.example.bai_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Student(
    val name: String,
    val email: String?,
    val phone: String?
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentListScreen()
        }
    }
}

val navy = Color(0xFF2B4478)
val screenBg = Color(0xFFF1F3F8)

// Nếu value == null thì hiện "Chưa cập nhật" (toán tử ?:  giống ?? của Dart)
@Composable
fun FieldLine(label: String, value: String?) {
    val missing = value == null
    Row {
        Text("$label: ", fontSize = 13.sp, color = Color.DarkGray)
        Text(
            text = value ?: "Chưa cập nhật",
            fontSize = 13.sp,
            color = if (missing) Color(0xFFC62828) else Color.DarkGray,
            fontStyle = if (missing) FontStyle.Italic else FontStyle.Normal
        )
    }
}

@Composable
fun StudentCard(s: Student) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text(s.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        FieldLine("Email", s.email)
        Spacer(Modifier.height(2.dp))
        FieldLine("SĐT", s.phone)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen() {
    val students = listOf(
        Student("Nguyễn Văn An", "an@sv.edu.vn", "0901 234 567"),
        Student("Trần Thị Bình", null, "0912 888 999"),
        Student("Lê Minh Chi", "chi@sv.edu.vn", null),
        Student("Phạm Quốc Dũng", null, null),
        Student("Võ Thanh Em", "em@sv.edu.vn", "0933 111 222")
    )

    // Dùng for để đếm sinh viên thiếu email
    var missingEmail = 0
    for (s in students) {
        if (s.email == null) {
            missingEmail++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Danh sách sinh viên", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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
                .padding(14.dp)
        ) {
            // Dùng for để tạo các thẻ sinh viên
            for (s in students) {
                StudentCard(s)
                Spacer(Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE4E9F4))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text("Tổng: ", color = navy)
                    Text("${students.size} sinh viên", color = navy, fontWeight = FontWeight.Bold)
                }
                Row {
                    Text("Thiếu email: ", color = navy)
                    Text("$missingEmail", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun StudentListScreenPreview() {
    StudentListScreen()
}