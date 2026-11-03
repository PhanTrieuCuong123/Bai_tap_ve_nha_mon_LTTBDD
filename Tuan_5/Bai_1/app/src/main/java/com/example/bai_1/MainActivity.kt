package com.example.bai_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------- Màu sắc ----------
private val Blue = Color(0xFF3B5BDB)
private val BlueSoft = Color(0xFFE7ECFF)
private val Bg = Color(0xFFF5F6FA)
private val TextMain = Color(0xFF1B1F3B)
private val TextSub = Color(0xFF7A7F9A)
private val Orange = Color(0xFFFFEBD6)
private val OrangeText = Color(0xFFD9822B)
private val GreenSoft = Color(0xFFE3F6EA)
private val GreenText = Color(0xFF2E9E5B)

// ---------- Dữ liệu giả ----------
data class TodoItem(val title: String, val tag: String, val time: String, val done: Boolean)

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(primary = Blue, background = Bg, surface = Color.White)
            ) {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot(vm: AuthViewModel = viewModel()) {
    val nav = rememberNavController()
    val user by vm.user.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val tasks = remember {
        mutableStateListOf(
            TodoItem("Tập thể dục 30 phút", "Cuộc sống", "06:30", true),
            TodoItem("Nộp bài tập Lập trình di động", "Học tập", "09:00", true),
            TodoItem("Ôn chương 3 - Cấu trúc dữ liệu", "Học tập", "14:00", false),
            TodoItem("Đi chợ mua đồ cho tuần", "Cuộc sống", "17:30", false)
        )
    }

    LaunchedEffect(Unit) {
        vm.events.collect { e ->
            when (e) {
                is AuthEvent.Success -> snackbar.showSnackbar(e.message)
                is AuthEvent.Error -> snackbar.showSnackbar(e.message)
            }
        }
    }

    // Tự điều hướng theo trạng thái đăng nhập
    LaunchedEffect(user) {
        val target = if (user != null) "home" else "login"
        nav.navigate(target) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    Scaffold(
        containerColor = Bg,
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = if (user != null) "home" else "login",
            modifier = Modifier.padding(padding)
        ) {
            composable("login") { LoginScreen(vm) }
            composable("home") { HomeScreen(vm, tasks, onProfile = { nav.navigate("profile") }) }
            composable("profile") { ProfileScreen(vm, tasks, onBack = { nav.popBackStack() }) }
        }
    }
}

// ====================== MÀN HÌNH ĐĂNG NHẬP ======================
@Composable
fun LoginScreen(vm: AuthViewModel) {
    val context = LocalContext.current
    val loading by vm.loading.collectAsState()
    // default_web_client_id do plugin google-services tự sinh
    val webClientId = context.getString(R.string.default_web_client_id)

    Column(
        Modifier.fillMaxSize().background(Color.White).padding(24.dp)
    ) {
        Spacer(Modifier.weight(1f))

        Box(
            Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(Blue),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Chào mừng đến với\nUTH Todo",
            fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextMain, lineHeight = 34.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
            fontSize = 14.sp, color = TextSub
        )

        Spacer(Modifier.weight(1.2f))

        OutlinedButton(
            onClick = { vm.signInWithGoogle(context, webClientId) },
            enabled = !loading,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFDDE0EE)),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Box(
                Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFF1F2F7)),
                contentAlignment = Alignment.Center
            ) { Text("G", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSub) }
            Spacer(Modifier.width(10.dp))
            Text("Tiếp tục với Google", color = TextMain, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE6E8F2))
            Text("  hoặc  ", fontSize = 12.sp, color = TextSub)
            HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE6E8F2))
        }
        Spacer(Modifier.height(6.dp))

        TextButton(
            onClick = { vm.signInAnonymously() },
            enabled = !loading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("Dùng thử không cần tài khoản", color = Blue, fontWeight = FontWeight.SemiBold) }

        if (loading) {
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).size(28.dp))
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            fontSize = 11.sp, color = TextSub, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ====================== MÀN HÌNH TRANG CHỦ ======================
@Composable
fun HomeScreen(vm: AuthViewModel, tasks: SnapshotStateList<TodoItem>, onProfile: () -> Unit) {
    val user by vm.user.collectAsState()
    val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "bạn"
    val dateText = remember {
        SimpleDateFormat("EEEE, d 'tháng' M", Locale("vi")).format(Date())
            .replaceFirstChar { it.uppercase() }
    }
    val doneCount = tasks.count { it.done }
    val progress = if (tasks.isEmpty()) 0f else doneCount.toFloat() / tasks.size
    val studyLeft = tasks.count { it.tag == "Học tập" && !it.done }
    val lifeLeft = tasks.count { it.tag == "Cuộc sống" && !it.done }

    Column(Modifier.fillMaxSize().background(Bg)) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(name = name, size = 44, onClick = onProfile)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(dateText, fontSize = 12.sp, color = TextSub)
                    Text("Chào $name!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextMain)
                }
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Notifications, null, tint = TextSub, modifier = Modifier.size(20.dp)) }
            }

            Spacer(Modifier.height(20.dp))
            // Thẻ tiến độ
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Tiến độ hôm nay", fontSize = 12.sp, color = Color(0xCCFFFFFF))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "$doneCount/${tasks.size} việc",
                            fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (progress >= 0.5f) "Đã hơn nửa chặng, cố lên!" else "Bắt đầu thôi nào!",
                            fontSize = 12.sp, color = Color(0xCCFFFFFF)
                        )
                    }
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(64.dp),
                            color = Color.White,
                            trackColor = Color(0x44FFFFFF),
                            strokeWidth = 6.dp
                        )
                        Text("${(progress * 100).toInt()}%", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            // 2 thẻ danh mục
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CategoryCard("Học tập", "$studyLeft việc còn lại", Icons.Default.Star, BlueSoft, Blue, Modifier.weight(1f))
                CategoryCard("Cuộc sống", "$lifeLeft việc còn lại", Icons.Default.Home, Orange, OrangeText, Modifier.weight(1f))
            }

            Spacer(Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Việc hôm nay", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.weight(1f))
                Text("Xem tất cả", fontSize = 13.sp, color = Blue, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(10.dp))

            tasks.forEachIndexed { index, t ->
                TaskRow(t) { tasks[index] = t.copy(done = !t.done) }
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(12.dp))
        }

        BottomBar(onSettings = onProfile)
    }
}

@Composable
private fun CategoryCard(
    title: String, sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color, iconTint: Color, modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(Modifier.padding(14.dp)) {
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 15.sp)
            Text(sub, fontSize = 12.sp, color = TextSub)
        }
    }
}

@Composable
private fun TaskRow(t: TodoItem, onToggle: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable { onToggle() }
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(24.dp).clip(CircleShape)
                    .background(if (t.done) Blue else Color.White)
                    .then(if (!t.done) Modifier.background(Color.Transparent) else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (t.done) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(15.dp))
                else Box(Modifier.size(22.dp).clip(CircleShape).background(Color(0xFFD9DCEA)).padding(2.dp)
                    .clip(CircleShape).background(Color.White))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    t.title, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                    color = if (t.done) TextSub else TextMain,
                    textDecoration = if (t.done) TextDecoration.LineThrough else null
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (bg, fg) = if (t.tag == "Học tập") BlueSoft to Blue else Orange to OrangeText
                    Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
                        Text(t.tag, color = fg, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(t.time, fontSize = 12.sp, color = TextSub)
                }
            }
        }
    }
}

@Composable
private fun BottomBar(onSettings: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem("Trang chủ", Icons.Default.Home, selected = true, onClick = {})
            NavItem("Danh sách", Icons.Default.Menu, selected = false, onClick = {})
            FloatingActionButton(
                onClick = {},
                containerColor = Blue, contentColor = Color.White,
                shape = CircleShape, modifier = Modifier.size(52.dp)
            ) { Icon(Icons.Default.Add, null) }
            NavItem("Thông báo", Icons.Default.Notifications, selected = false, onClick = {})
            NavItem("Cài đặt", Icons.Default.Settings, selected = false, onClick = onSettings)
        }
    }
}

@Composable
private fun NavItem(
    label: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean, onClick: () -> Unit
) {
    val c = if (selected) Blue else TextSub
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(horizontal = 6.dp)
    ) {
        Icon(icon, null, tint = c, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 10.sp, color = c)
    }
}

@Composable
private fun Avatar(name: String, size: Int, onClick: (() -> Unit)? = null) {
    Box(
        Modifier.size(size.dp).clip(CircleShape).background(BlueSoft)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(initials(name), color = Blue, fontWeight = FontWeight.Bold, fontSize = (size / 3).sp)
    }
}

// ====================== MÀN HÌNH HỒ SƠ ======================
@Composable
fun ProfileScreen(vm: AuthViewModel, tasks: SnapshotStateList<TodoItem>, onBack: () -> Unit) {
    val user by vm.user.collectAsState()
    val isAnon = user?.isAnonymous != false
    val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "Khách"
    val email = user?.email?.takeIf { it.isNotBlank() } ?: "Chưa liên kết tài khoản"
    val done = tasks.count { it.done }
    val pending = tasks.size - done
    val percent = if (tasks.isEmpty()) 0 else done * 100 / tasks.size

    Column(Modifier.fillMaxSize().background(Bg).padding(20.dp)) {
        // Thanh trên
        Box(Modifier.fillMaxWidth()) {
            Text(
                "‹ Cài đặt", color = Blue, fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterStart).clickable { onBack() }.padding(vertical = 8.dp)
            )
            Text("Hồ sơ", fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Avatar(name = name, size = 84)
            Spacer(Modifier.height(10.dp))
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextMain)
            Text(email, fontSize = 13.sp, color = TextSub)
            Spacer(Modifier.height(8.dp))
            Surface(color = if (isAnon) Orange else GreenSoft, shape = RoundedCornerShape(8.dp)) {
                Text(
                    if (isAnon) "Tài khoản dùng thử (ẩn danh)" else "✓ Đã liên kết tài khoản Google",
                    color = if (isAnon) OrangeText else GreenText,
                    fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox("$done", "Việc đã xong", Modifier.weight(1f))
            StatBox("$pending", "Đang chờ", Modifier.weight(1f))
            StatBox("$percent%", "Hoàn thành", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Row {
                    Text("Tuần này", fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.weight(1f))
                    Text("Việc xong mỗi ngày", fontSize = 11.sp, color = TextSub)
                }
                Spacer(Modifier.height(16.dp))
                val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                val heights = listOf(40, 24, 3, 3, 3, 3, 3)
                Row(
                    Modifier.fillMaxWidth().height(70.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    labels.forEachIndexed { i, _ ->
                        Box(
                            Modifier.width(18.dp).height(heights[i].dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (i == 1) Blue else if (i == 0) Color(0xFFB8C4F5) else Color(0xFFE3E6F0))
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    labels.forEach { Text(it, fontSize = 10.sp, color = TextSub, modifier = Modifier.width(18.dp), textAlign = TextAlign.Center) }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = { vm.signOut() },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.dp, Color.Transparent),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Đăng xuất", color = Blue, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun StatBox(value: String, label: String, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextMain)
            Text(label, fontSize = 11.sp, color = TextSub)
        }
    }
}