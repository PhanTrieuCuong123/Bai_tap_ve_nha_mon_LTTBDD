package com.example.bai_1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val Blue = Color(0xFF2196F3)
private val LightBlue = Color(0xFFBBDEFB)
private val LightRed = Color(0xFFF8B4B4)

/* ---------------- Màn hình 1: Welcome ---------------- */

@Composable
fun WelcomeScreen(onReady: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        ComposeLogo(Modifier.size(120.dp))
        Spacer(Modifier.height(24.dp))
        Text("Jetpack Compose", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(
            "Jetpack Compose is a modern UI toolkit for building native Android applications using a declarative programming approach.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onReady,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("I'm ready")
        }
    }
}

/* ---------------- Màn hình 2: UI Components List ---------------- */

@Composable
fun ComponentsListScreen(onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "UI Components List",
            color = Blue,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        SectionTitle("Display")
        ComponentItem("Text", "Displays text", LightBlue) { onItemClick(Routes.TEXT) }
        ComponentItem("Image", "Displays an image", LightBlue) { onItemClick(Routes.IMAGE) }

        SectionTitle("Input")
        ComponentItem("TextField", "Input field for text", LightBlue) { onItemClick(Routes.TEXTFIELD) }
        ComponentItem("PasswordField", "Input field for passwords", LightBlue) { onItemClick(Routes.PASSWORD) }

        SectionTitle("Layout")
        ComponentItem("Column", "Arranges elements vertically", LightBlue) { onItemClick(Routes.COLUMN) }
        ComponentItem("Row", "Arranges elements horizontally", LightBlue) { onItemClick(Routes.ROW) }

        Spacer(Modifier.height(8.dp))
        ComponentItem("Tự tìm hiểu", "Tìm ra tất cả các thành phần UI Cơ bản", LightRed) { onItemClick(Routes.MORE) }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
}

@Composable
private fun ComponentItem(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Text(subtitle, fontSize = 14.sp)
    }
}

/* ---------------- Khung chung cho màn hình chi tiết ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, color = Blue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Blue)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            content = content
        )
    }
}

/* ---------------- Text Detail ---------------- */

@Composable
fun TextDetailScreen(onBack: () -> Unit) {
    val styled: AnnotatedString = buildAnnotatedString {
        append("The ")
        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append("quick ") }
        withStyle(SpanStyle(color = Color(0xFFCC7A00), fontSize = 40.sp)) { append("Brown\n") }
        withStyle(SpanStyle(letterSpacing = 6.sp)) { append("fox jumps ") }
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) { append("over\n") }
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("the ") }
        withStyle(SpanStyle(fontFamily = FontFamily.Cursive, fontStyle = FontStyle.Italic)) { append("lazy ") }
        append("dog.")
    }
    DetailScaffold("Text Detail", onBack) {
        Spacer(Modifier.weight(1f))
        Text(styled, fontSize = 32.sp, lineHeight = 48.sp)
        Spacer(Modifier.weight(1f))
    }
}

/* ---------------- Images ---------------- */

@Composable
fun ImageDetailScreen(onBack: () -> Unit) {
    val url =
        "https://vi.wikipedia.org/wiki/Special:FilePath/%E1%BA%A2nh_m%E1%BA%B7t_tr%C6%B0%E1%BB%9Bc_%C4%90H_GTVT_TP_HCM.jpg?width=1000"

    // Tìm ảnh tên "campus" trong res/drawable. Không có ảnh thì không báo lỗi build.
    val context = LocalContext.current
    val campusId = remember {
        context.resources.getIdentifier("campus", "drawable", context.packageName)
    }
    var status by remember { mutableStateOf("Đang tải ảnh...") }

    DetailScaffold("Images", onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = coil.request.ImageRequest.Builder(context)
                    .data(url)
                    .addHeader("User-Agent", "Bai1App/1.0 (Android; bai tap ve nha UTH)")
                    .crossfade(true)
                    .build(),
                contentDescription = "Ảnh từ URL",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFEFEF)),
                contentScale = ContentScale.Crop,
                onSuccess = { status = "Tải ảnh thành công" },
                onError = { status = "Lỗi tải ảnh: ${it.result.throwable}" }
            )
            Spacer(Modifier.height(6.dp))
            Text(status, fontSize = 12.sp, color = Color.Red, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(
                "https://vi.wikipedia.org/wiki/Special:FilePath/Ảnh_mặt_trước_ĐH_GTVT_TP_HCM.jpg",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline
            )

            Spacer(Modifier.height(24.dp))

            if (campusId != 0) {
                Image(
                    painter = painterResource(id = campusId),
                    contentDescription = "Ảnh trong app",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Chưa có ảnh campus trong res/drawable", fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("In app", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

/* ---------------- TextField ---------------- */

@Composable
fun TextFieldDetailScreen(onBack: () -> Unit) {
    var text by remember { mutableStateOf("") }
    DetailScaffold("TextField", onBack) {
        Spacer(Modifier.height(80.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Thông tin nhập") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (text.isEmpty()) "Tự động cập nhật dữ liệu theo textfield" else text,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

/* ---------------- PasswordField ---------------- */

@Composable
fun PasswordDetailScreen(onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    DetailScaffold("PasswordField", onBack) {
        Spacer(Modifier.height(80.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { visible = !visible }) {
                    Text(if (visible) "Ẩn" else "Hiện")
                }
            }
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Độ dài mật khẩu: ${password.length}",
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

/* ---------------- Column Layout ---------------- */

@Composable
fun ColumnDetailScreen(onBack: () -> Unit) {
    DetailScaffold("Column Layout", onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFEFEF))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (index % 2 == 0) Color(0xFF9DB7F0) else Color(0xFF4F86F7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Item ${index + 1}", color = Color.White)
                }
            }
        }
    }
}

/* ---------------- Row Layout ---------------- */

@Composable
fun RowDetailScreen(onBack: () -> Unit) {
    DetailScaffold("Row Layout", onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFEFEF))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) { col ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (col == 1) Color(0xFF4F86F7) else Color(0xFF9DB7F0))
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- Tự tìm hiểu: các thành phần UI cơ bản khác ---------------- */

@Composable
private fun MoreGroup(title: String) {
    Text(
        title,
        color = Blue,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 12.dp)
    )
    HorizontalDivider(color = Blue)
}

@Composable
private fun MoreLabel(text: String) {
    Text(text, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreComponentsScreen(onBack: () -> Unit) {
    // Input
    var switched by remember { mutableStateOf(false) }
    var slider by remember { mutableStateOf(0.4f) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()

    // Selection
    var clicks by remember { mutableStateOf(0) }
    var checked by remember { mutableStateOf(true) }
    var radio by remember { mutableStateOf(0) }
    val cities = listOf("TP.HCM", "Hà Nội", "Đà Nẵng", "Cần Thơ")
    var pickerOpen by remember { mutableStateOf(false) }
    var city by remember { mutableStateOf(cities[0]) }

    // Navigation
    val tabs = listOf("Home", "Search", "Profile")
    var tab by remember { mutableStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }
    var menuResult by remember { mutableStateOf("Chưa chọn hành động nào") }

    // Content
    val fruits = listOf("Xoài", "Chuối", "Cam", "Dưa hấu", "Ổi", "Nho", "Táo", "Mận")

    val dateText = dateState.selectedDateMillis?.let { millis ->
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        formatter.format(java.util.Date(millis))
    } ?: "Chưa chọn ngày"

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    DetailScaffold("Tự tìm hiểu", onBack) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* ----- Display ----- */
            MoreGroup("Display")
            Text("Text và Image: xem 2 màn hình riêng ở danh sách phía trước.", fontSize = 13.sp, color = Color.Gray)

            /* ----- Input ----- */
            MoreGroup("Input")
            Text("TextField và PasswordField: xem 2 màn hình riêng ở danh sách phía trước.", fontSize = 13.sp, color = Color.Gray)

            MoreLabel("Toggle (Switch)")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = switched, onCheckedChange = { switched = it })
                Spacer(Modifier.width(8.dp))
                Text(if (switched) "Bật" else "Tắt")
            }

            MoreLabel("Slider: ${"%.0f".format(slider * 100)}")
            Slider(value = slider, onValueChange = { slider = it })
            LinearProgressIndicator(progress = { slider }, modifier = Modifier.fillMaxWidth())

            MoreLabel("DatePicker")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { showDatePicker = true }) { Text("Chọn ngày") }
                Spacer(Modifier.width(12.dp))
                Text(dateText)
            }

            /* ----- Selection ----- */
            MoreGroup("Selection")

            MoreLabel("Button")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { clicks++ }) { Text("Button ($clicks)") }
                OutlinedButton(onClick = { clicks++ }) { Text("Outlined") }
                TextButton(onClick = { clicks++ }) { Text("Text") }
            }

            MoreLabel("Checkbox")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = checked, onCheckedChange = { checked = it })
                Text(if (checked) "Đã chọn" else "Chưa chọn")
            }

            MoreLabel("RadioButton")
            listOf("Lựa chọn A", "Lựa chọn B", "Lựa chọn C").forEachIndexed { i, label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = radio == i, onClick = { radio = i })
                    Text(label)
                }
            }

            MoreLabel("Picker (chọn 1 giá trị từ danh sách)")
            Box {
                OutlinedButton(onClick = { pickerOpen = true }) { Text("Thành phố: $city  ▾") }
                DropdownMenu(expanded = pickerOpen, onDismissRequest = { pickerOpen = false }) {
                    cities.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c) },
                            onClick = {
                                city = c
                                pickerOpen = false
                            }
                        )
                    }
                }
            }

            /* ----- Navigation ----- */
            MoreGroup("Navigation")

            MoreLabel("NavigationView (tiêu đề + nút Back)")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightBlue)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Blue)
                Spacer(Modifier.width(12.dp))
                Text("Tiêu đề màn hình", color = Blue)
            }

            MoreLabel("TabBar")
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
                }
            }
            Text("Nội dung của tab: ${tabs[tab]}", color = Color.Gray)

            MoreLabel("Menu")
            Box {
                Button(onClick = { menuOpen = true }) { Text("Mở Menu  ⋮") }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    listOf("Sửa", "Chia sẻ", "Xóa").forEach { action ->
                        DropdownMenuItem(
                            text = { Text(action) },
                            onClick = {
                                menuResult = "Đã chọn: $action"
                                menuOpen = false
                            }
                        )
                    }
                }
            }
            Text(menuResult, fontSize = 12.sp, color = Color.Gray)

            /* ----- Content ----- */
            MoreGroup("Content")

            MoreLabel("List")
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFEFEF))
            ) {
                fruits.forEach { fruit ->
                    item {
                        Text(fruit, modifier = Modifier.fillMaxWidth().padding(12.dp))
                        HorizontalDivider()
                    }
                }
            }

            MoreLabel("Card")
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("Nội dung trong Card", modifier = Modifier.padding(16.dp))
            }

            MoreLabel("Divider")
            HorizontalDivider()

            /* ----- Khác ----- */
            MoreGroup("Khác")
            MoreLabel("CircularProgressIndicator")
            CircularProgressIndicator()

            MoreLabel("Box")
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Blue),
                contentAlignment = Alignment.Center
            ) {
                Text("A", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}