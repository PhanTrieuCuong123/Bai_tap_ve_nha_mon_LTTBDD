package com.example.bai_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorScreen()
        }
    }
}

fun formatNumber(v: Double): String {
    return if (v == Math.floor(v)) v.toLong().toString() else v.toString()
}

@Composable
fun OpButton(op: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(op, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NumberInput(value: String, onChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
    )
}

@Composable
fun CalculatorScreen() {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    fun calculate(op: String) {
        val x = a.trim().toDoubleOrNull()
        val y = b.trim().toDoubleOrNull()
        if (x == null || y == null) {
            result = "Vui lòng nhập số hợp lệ"
            return
        }
        result = when (op) {
            "+" -> formatNumber(x + y)
            "-" -> formatNumber(x - y)
            "*" -> formatNumber(x * y)
            else -> if (y == 0.0) "Không thể chia cho 0" else formatNumber(x / y)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(120.dp))
        Text(
            "Thực hành 03",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(80.dp))
        NumberInput(a) { a = it }
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OpButton("+", Color(0xFFE04B4B)) { calculate("+") }
            OpButton("-", Color(0xFFDDA94B)) { calculate("-") }
            OpButton("*", Color(0xFF5B3FE0)) { calculate("*") }
            OpButton("/", Color(0xFF1E1E1E)) { calculate("/") }
        }
        Spacer(Modifier.height(24.dp))
        NumberInput(b) { b = it }
        Spacer(Modifier.height(16.dp))
        Text("Kết quả: $result", fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen()
}