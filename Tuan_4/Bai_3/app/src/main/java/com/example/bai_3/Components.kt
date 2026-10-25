package com.example.bai_3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SmartBlue = Color(0xFF4B96EA)
val FieldBg = Color(0xFFF7F8FC)
val FieldBorder = Color(0xFFE3E5EC)

// Logo UTH vẽ bằng chữ (muốn dùng ảnh thật thì thay bằng Image(painterResource(...)))
@Composable
fun UthLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "UTH",
            color = Color(0xFF2E8B8B),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )
        Text(
            "UNIVERSITY\nOF TRANSPORT\nHOCHIMINH CITY",
            color = Color(0xFFC0392B),
            fontSize = 8.sp,
            lineHeight = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// Khung chung cho cả 4 màn hình: nút back, logo, SmartTasks, tiêu đề, mô tả
@Composable
fun AuthScaffold(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(SmartBlue, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        UthLogo()
        Spacer(Modifier.height(8.dp))
        Text("SmartTasks", color = SmartBlue, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(28.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
        Spacer(Modifier.height(8.dp))
        Text(subtitle, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        content()
    }
}

@Composable
fun AppField(
    value: String,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    isPassword: Boolean = false,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = true,
        readOnly = readOnly,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedBorderColor = SmartBlue,
            unfocusedBorderColor = FieldBorder,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SmartBlue)
    ) {
        Text(text, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun ErrorText(message: String) {
    if (message.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        Text(message, color = Color(0xFFD32F2F), fontSize = 13.sp)
    }
}

// Ô nhập OTP 5 số (5 ô vuông)
@Composable
fun OtpField(value: String, onValueChange: (String) -> Unit, length: Int = 5) {
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= length && it.all(Char::isDigit)) onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                repeat(length) { i ->
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 52.dp)
                            .background(FieldBg, RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                if (i == value.length) SmartBlue else FieldBorder,
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            value.getOrNull(i)?.toString() ?: "",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    )
}