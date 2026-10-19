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
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF3DDC84)),
            contentAlignment = Alignment.Center
        ) {
            Text("</>", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
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
    val url = "https://giaothongvantaitphcm.edu.vn/wp-content/uploads/2025/01/Logo-GTVT.png"

    // Tìm ảnh tên "campus" trong res/drawable. Không có ảnh thì không báo lỗi build.
    val context = LocalContext.current
    val campusId = remember {
        context.resources.getIdentifier("campus", "drawable", context.packageName)
    }

    DetailScaffold("Images", onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = url,
                contentDescription = "Ảnh từ URL",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(6.dp))
            Text(url, fontSize = 11.sp, textAlign = TextAlign.Center, textDecoration = TextDecoration.Underline)

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
fun MoreComponentsScreen(onBack: () -> Unit) {
    var checked by remember { mutableStateOf(true) }
    var switched by remember { mutableStateOf(false) }
    var radio by remember { mutableStateOf(0) }
    var slider by remember { mutableStateOf(0.4f) }
    var clicks by remember { mutableStateOf(0) }

    DetailScaffold("Tự tìm hiểu", onBack) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Button", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { clicks++ }) { Text("Button ($clicks)") }
                OutlinedButton(onClick = { clicks++ }) { Text("Outlined") }
                TextButton(onClick = { clicks++ }) { Text("Text") }
            }

            Text("Checkbox", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = checked, onCheckedChange = { checked = it })
                Text(if (checked) "Đã chọn" else "Chưa chọn")
            }

            Text("Switch", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = switched, onCheckedChange = { switched = it })
                Spacer(Modifier.width(8.dp))
                Text(if (switched) "Bật" else "Tắt")
            }

            Text("RadioButton", fontWeight = FontWeight.Bold)
            listOf("Lựa chọn A", "Lựa chọn B", "Lựa chọn C").forEachIndexed { i, label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = radio == i, onClick = { radio = i })
                    Text(label)
                }
            }

            Text("Slider: ${"%.0f".format(slider * 100)}", fontWeight = FontWeight.Bold)
            Slider(value = slider, onValueChange = { slider = it })

            Text("ProgressIndicator", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { slider }, modifier = Modifier.fillMaxWidth())
            CircularProgressIndicator()

            Text("Card", fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("Nội dung trong Card", modifier = Modifier.padding(16.dp))
            }

            Text("Divider", fontWeight = FontWeight.Bold)
            HorizontalDivider()

            Text("Box", fontWeight = FontWeight.Bold)
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