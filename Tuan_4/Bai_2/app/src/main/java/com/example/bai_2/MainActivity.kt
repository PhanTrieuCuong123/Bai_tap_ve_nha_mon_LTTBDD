package com.example.bai_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bai_2.ui.theme.Bai_2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Ép giao diện sáng, tắt dynamic color để nền luôn trắng như slide
            Bai_2Theme(darkTheme = false, dynamicColor = false) {
                // Nút và tab màu xanh dương như slide
                MaterialTheme(
                    colorScheme = MaterialTheme.colorScheme.copy(
                        primary = Color(0xFF2855B5),
                        background = Color.White,
                        surface = Color.White
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        Bt2App()
                    }
                }
            }
        }
    }
}