package com.example.bai_2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.text.KeyboardOptions
import coil.compose.AsyncImage
import com.google.firebase.crashlytics.FirebaseCrashlytics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.google.firebase.analytics.FirebaseAnalytics
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
        AppNotifications.ensureChannel(this)
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
fun AppRoot(
    vm: AuthViewModel = viewModel(),
    tvm: TaskViewModel = viewModel(),
    pvm: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val analytics = remember { FirebaseAnalytics.getInstance(context) }
    val nav = rememberNavController()
    val user by vm.user.collectAsState()
    val tasks by tvm.tasks.collectAsState()
    val welcome by tvm.welcome.collectAsState()
    val photo by pvm.photoUrl.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.events.collect { e ->
            when (e) {
                is AuthEvent.Success -> {
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
                    snackbar.showSnackbar(e.message)
                }
                is AuthEvent.Error -> snackbar.showSnackbar(e.message)
            }
        }
    }
    LaunchedEffect(Unit) {
        tvm.errors.collect { snackbar.showSnackbar(it) }
    }
    LaunchedEffect(Unit) {
        pvm.messages.collect { snackbar.showSnackbar(it) }
    }

    // FCM: xin quyền thông báo trên Android 13+
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    // Tự điều hướng + bật/tắt lắng nghe Firestore theo trạng thái đăng nhập
    LaunchedEffect(user) {
        if (user != null) tvm.start() else tvm.stop()
        pvm.onUserChanged(user != null)
        FirebaseCrashlytics.getInstance().setUserId(user?.uid ?: "")
        val target = if (user != null) "home" else "login"
        nav.navigate(target) { popUpTo(nav.graph.id) { inclusive = true } }
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
            composable("home") {
                HomeScreen(
                    vm = vm, tasks = tasks, welcome = welcome, photoUrl = photo,
                    onProfile = { nav.navigate("profile") },
                    onAdd = { title, tag, time ->
                        tvm.add(title, tag, time)
                        analytics.logEvent("add_task", null)
                    },
                    onToggle = { t ->
                        tvm.toggle(t)
                        if (!t.done) analytics.logEvent("complete_task", null)
                    },
                    onDelete = { tvm.delete(it) }
                )
            }
            composable("profile") { ProfileScreen(vm, pvm, tasks, onBack = { nav.popBackStack() }) }
        }
    }
}

// ====================== ĐĂNG NHẬP ======================
@Composable
fun LoginScreen(vm: AuthViewModel) {
    val context = LocalContext.current
    val loading by vm.loading.collectAsState()
    val webClientId = context.getString(R.string.default_web_client_id)

    Column(Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(Blue),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(32.dp)) }
        Spacer(Modifier.height(20.dp))
        Text(
            "Chào mừng đến với\nUTH Todo",
            fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextMain, lineHeight = 34.sp
        )
        Spacer(Modifier.height(8.dp))
        Text("Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.", fontSize = 14.sp, color = TextSub)
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

// ====================== TRANG CHỦ ======================
@Composable
fun HomeScreen(
    vm: AuthViewModel,
    tasks: List<TodoTask>,
    welcome: String,
    photoUrl: String?,
    onProfile: () -> Unit,
    onAdd: (String, String, String) -> Unit,
    onToggle: (TodoTask) -> Unit,
    onDelete: (String) -> Unit
) {
    val user by vm.user.collectAsState()
    val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "bạn"
    val dateText = remember {
        SimpleDateFormat("EEEE, d 'tháng' M", Locale("vi")).format(Date())
            .replaceFirstChar { it.uppercase() }
    }
    var showAdd by remember { mutableStateOf(false) }

    val doneCount = tasks.count { it.done }
    val progress = if (tasks.isEmpty()) 0f else doneCount.toFloat() / tasks.size
    val studyLeft = tasks.count { it.tag == "Học tập" && !it.done }
    val lifeLeft = tasks.count { it.tag == "Cuộc sống" && !it.done }

    if (showAdd) {
        AddTaskDialog(
            onDismiss = { showAdd = false },
            onConfirm = { title, tag, time -> onAdd(title, tag, time); showAdd = false }
        )
    }

    Column(Modifier.fillMaxSize().background(Bg)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(name = name, size = 44, photoUrl = photoUrl, onClick = onProfile)
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

            // Lời chào lấy từ Remote Config
            if (welcome.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Surface(color = BlueSoft, shape = RoundedCornerShape(10.dp)) {
                    Text(welcome, color = Blue, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Tiến độ hôm nay", fontSize = 12.sp, color = Color(0xCCFFFFFF))
                        Spacer(Modifier.height(4.dp))
                        Text("$doneCount/${tasks.size} việc", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

            if (tasks.isEmpty()) {
                Text(
                    "Chưa có việc nào. Bấm nút + để thêm.",
                    fontSize = 13.sp, color = TextSub,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
            tasks.forEach { t ->
                TaskRow(t, onToggle = { onToggle(t) }, onDelete = { onDelete(t.id) })
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(12.dp))
        }
        BottomBar(onAdd = { showAdd = true }, onSettings = onProfile)
    }
}

@Composable
private fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00") }
    var tag by remember { mutableStateOf("Học tập") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm việc mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Tên công việc") }, singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = time, onValueChange = { time = it },
                    label = { Text("Giờ (ví dụ 14:00)") }, singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = tag == "Học tập", onClick = { tag = "Học tập" }, label = { Text("Học tập") })
                    FilterChip(selected = tag == "Cuộc sống", onClick = { tag = "Cuộc sống" }, label = { Text("Cuộc sống") })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title.trim(), tag, time.trim()) },
                enabled = title.isNotBlank()
            ) { Text("Thêm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@Composable
private fun CategoryCard(
    title: String, sub: String, icon: ImageVector,
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
private fun TaskRow(t: TodoTask, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable { onToggle() }
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            if (t.done) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(Blue),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(15.dp)) }
            } else {
                Box(Modifier.size(24.dp).border(2.dp, Color(0xFFD9DCEA), CircleShape))
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
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = TextSub, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun BottomBar(onAdd: () -> Unit, onSettings: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem("Trang chủ", Icons.Default.Home, true) {}
            NavItem("Danh sách", Icons.Default.Menu, false) {}
            FloatingActionButton(
                onClick = onAdd,
                containerColor = Blue, contentColor = Color.White,
                shape = CircleShape, modifier = Modifier.size(52.dp)
            ) { Icon(Icons.Default.Add, null) }
            NavItem("Thông báo", Icons.Default.Notifications, false) {}
            NavItem("Cài đặt", Icons.Default.Settings, false, onSettings)
        }
    }
}

@Composable
private fun NavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
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
private fun Avatar(name: String, size: Int, photoUrl: String? = null, onClick: (() -> Unit)? = null) {
    Box(
        Modifier.size(size.dp).clip(CircleShape).background(BlueSoft)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl, contentDescription = "Ảnh đại diện",
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(initials(name), color = Blue, fontWeight = FontWeight.Bold, fontSize = (size / 3).sp)
        }
    }
}

// ====================== HỒ SƠ ======================
@Composable
fun ProfileScreen(vm: AuthViewModel, pvm: ProfileViewModel, tasks: List<TodoTask>, onBack: () -> Unit) {
    val user by vm.user.collectAsState()
    val photo by pvm.photoUrl.collectAsState()
    val note by pvm.note.collectAsState()
    val token by pvm.token.collectAsState()
    val uploading by pvm.uploading.collectAsState()
    val isAnon = user?.isAnonymous != false
    val name = user?.displayName?.takeIf { it.isNotBlank() } ?: "Khách"
    val email = user?.email?.takeIf { it.isNotBlank() } ?: "Chưa liên kết tài khoản"
    val done = tasks.count { it.done }
    val pending = tasks.size - done
    val percent = if (tasks.isEmpty()) 0 else done * 100 / tasks.size
    var noteText by remember(note) { mutableStateOf(note) }

    // Storage: chọn ảnh từ thư viện rồi upload
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) pvm.uploadAvatar(uri)
    }

    Column(Modifier.fillMaxSize().background(Bg).padding(20.dp)) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                "‹ Cài đặt", color = Blue, fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterStart).clickable { onBack() }.padding(vertical = 8.dp)
            )
            Text("Hồ sơ", fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.align(Alignment.Center))
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Avatar(name = name, size = 84, photoUrl = photo)
                Spacer(Modifier.height(6.dp))
                TextButton(
                    onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    enabled = !uploading
                ) { Text(if (uploading) "Đang tải ảnh lên..." else "Đổi ảnh đại diện", color = Blue) }
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

            // Realtime Database: ghi chú nhanh
            Spacer(Modifier.height(20.dp))
            Text("Ghi chú nhanh (Realtime Database)", fontWeight = FontWeight.Bold, color = TextMain, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = noteText, onValueChange = { noteText = it },
                modifier = Modifier.fillMaxWidth(), minLines = 2,
                placeholder = { Text("Nhập ghi chú...") },
                keyboardOptions = KeyboardOptions.Default
            )
            TextButton(onClick = { pvm.saveNote(noteText) }) { Text("Lưu ghi chú", color = Blue) }

            // Cloud Messaging: lấy token để gửi thử từ Firebase Console
            Spacer(Modifier.height(8.dp))
            Text("Thông báo đẩy (Cloud Messaging)", fontWeight = FontWeight.Bold, color = TextMain, fontSize = 14.sp)
            TextButton(onClick = { pvm.loadToken() }) { Text("Lấy FCM token", color = Blue) }
            if (token.isNotBlank()) {
                SelectionContainer { Text(token, fontSize = 11.sp, color = TextSub) }
            }

            // Crashlytics: gây lỗi có chủ đích để báo cáo lên Firebase
            Spacer(Modifier.height(8.dp))
            Text("Báo lỗi (Crashlytics)", fontWeight = FontWeight.Bold, color = TextMain, fontSize = 14.sp)
            TextButton(onClick = {
                FirebaseCrashlytics.getInstance().log("Người dùng bấm nút Test crash")
                throw RuntimeException("Test crash từ UTH Todo")
            }) { Text("Test crash", color = Color(0xFFD64545)) }
            Spacer(Modifier.height(12.dp))
        }

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