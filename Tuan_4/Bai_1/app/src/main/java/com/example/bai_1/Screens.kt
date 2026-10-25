package com.example.bai_1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay

@Composable
fun Bt1App() {
    val nav = rememberNavController()
    val pages = OnboardingRepository.pages

    val goHome = {
        nav.navigate("home") { popUpTo(0) { inclusive = true } }
    }

    NavHost(navController = nav, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                nav.navigate("onboarding/0") { popUpTo(0) { inclusive = true } }
            }
        }
        composable(
            route = "onboarding/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { entry ->
            val index = entry.arguments?.getInt("index") ?: 0
            val page = pages[index]
            OnboardingScreen(
                page = page,
                index = index,
                total = pages.size,
                onNext = {
                    if (page.isLast) goHome() else nav.navigate("onboarding/${index + 1}")
                },
                onSkip = { goHome() }
            )
        }
        composable("home") { HomeScreen() }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }
    Box(Modifier.fillMaxSize().background(UthBlue)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(104.dp).background(Color.White, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = UthBlue, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("UTH Todo", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(
                "Việc học, việc nhà – gọn gàng mỗi ngày",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier.width(120.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(8.dp))
            Text("Phiên bản 2.1.0", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    index: Int,
    total: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(UthBlue, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text("UTH Todo", fontWeight = FontWeight.Bold, color = UthBlue)
            }
            Text(
                "Bỏ qua",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onSkip() }
            )
        }

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            page.Illustration()   // đa hình: mỗi trang tự vẽ minh họa
        }

        Text(page.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(page.description, fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(24.dp))

        Row {
            repeat(total) { i ->
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .height(6.dp)
                        .width(if (i == index) 24.dp else 6.dp)
                        .background(if (i == index) UthBlue else Color.LightGray, CircleShape)
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UthBlue)
        ) {
            Text(page.buttonText)
        }
    }
}

@Composable
fun HomeScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Trang chính UTH Todo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}