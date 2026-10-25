package com.example.bai_1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val UthBlue = Color(0xFF3B5BDB)
val UthSoft = Color(0xFFEEF0FB)

// ===== TRỪU TƯỢNG (Abstraction) =====
abstract class OnboardingPage(
    title: String,
    description: String
) {
    // ===== ĐÓNG GÓI (Encapsulation): bên ngoài chỉ đọc, chỉ lớp này được gán =====
    var title: String = title
        private set
    var description: String = description
        private set

    abstract val buttonText: String
    open val isLast: Boolean = false

    @Composable
    abstract fun Illustration()
}

// ===== KẾ THỪA (Inheritance) =====
// abstract vì chưa cài đặt Illustration(), để lớp con tự cài
abstract class StepPage(title: String, description: String) : OnboardingPage(title, description) {
    override val buttonText: String = "Tiếp tục"
}

class TaskListPage : StepPage(
    "Mọi việc, gọn trong một chỗ",
    "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào."
) {
    // ===== ĐA HÌNH (Polymorphism): mỗi trang vẽ minh họa riêng =====
    @Composable
    override fun Illustration() {
        IllustrationCard {
            repeat(3) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (i == 0) {
                        Icon(Icons.Default.CheckCircle, null, tint = UthBlue, modifier = Modifier.size(18.dp))
                    } else {
                        Box(Modifier.size(18.dp).border(1.5.dp, Color.LightGray, CircleShape))
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(5.dp)
                            .background(UthBlue.copy(alpha = 0.35f), CircleShape)
                    )
                }
            }
        }
    }
}

class CategoryPage : StepPage(
    "Chia theo Học tập và Cuộc sống",
    "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì."
) {
    @Composable
    override fun Illustration() {
        IllustrationCard {
            TagCard("Học tập", Color(0xFFE3E9FF), UthBlue)
            TagCard("Cuộc sống", Color(0xFFFFE9D6), Color(0xFFD9822B))
        }
    }
}

class ReminderPage : OnboardingPage(
    "Nhắc đúng lúc",
    "Bật thông báo để UTH Todo nhắc bạn trước hạn chót."
) {
    override val buttonText: String = "Bắt đầu ngay"
    override val isLast: Boolean = true

    @Composable
    override fun Illustration() {
        IllustrationCard {
            Box(
                modifier = Modifier.size(64.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, null, tint = UthBlue)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Ôn bài Toán lúc 19:00",
                fontSize = 12.sp,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

// Kho dữ liệu các trang onboarding
object OnboardingRepository {
    val pages: List<OnboardingPage> = listOf(TaskListPage(), CategoryPage(), ReminderPage())
}

// ---------- UI dùng chung ----------
@Composable
fun IllustrationCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .size(width = 200.dp, height = 240.dp)
            .background(UthSoft, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun TagCard(tag: String, tagBg: Color, tagColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(
            tag,
            fontSize = 11.sp,
            color = tagColor,
            modifier = Modifier
                .background(tagBg, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Color.LightGray.copy(alpha = 0.6f), CircleShape)
        )
    }
}