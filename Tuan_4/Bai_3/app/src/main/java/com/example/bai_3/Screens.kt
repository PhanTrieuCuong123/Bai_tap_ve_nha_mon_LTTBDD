package com.example.bai_3

import android.net.Uri
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Màn 1 có 3 tham số tùy chọn: dữ liệu quay về từ màn 4
private const val FORGOT_ROUTE = "forgot?email={email}&otp={otp}&pass={pass}"

private fun stringArg(name: String) = navArgument(name) {
    type = NavType.StringType
    defaultValue = ""
}

@Composable
fun Bt3App() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = FORGOT_ROUTE) {

        // ----- Màn 1: nhập email (nhận lại email/otp/pass từ màn 4) -----
        composable(
            route = FORGOT_ROUTE,
            arguments = listOf(stringArg("email"), stringArg("otp"), stringArg("pass"))
        ) { entry ->
            ForgotScreen(
                resultEmail = entry.arguments?.getString("email").orEmpty(),
                resultOtp = entry.arguments?.getString("otp").orEmpty(),
                resultPass = entry.arguments?.getString("pass").orEmpty(),
                onNext = { email -> nav.navigate("verify/${Uri.encode(email)}") }
            )
        }

        // ----- Màn 2: nhập OTP (nhận email) -----
        composable("verify/{email}") { entry ->
            val email = entry.arguments?.getString("email").orEmpty()
            VerifyScreen(
                onBack = { nav.popBackStack() },
                onNext = { otp -> nav.navigate("reset/${Uri.encode(email)}/$otp") }
            )
        }

        // ----- Màn 3: tạo mật khẩu mới (nhận email + otp) -----
        composable("reset/{email}/{otp}") { entry ->
            val email = entry.arguments?.getString("email").orEmpty()
            val otp = entry.arguments?.getString("otp").orEmpty()
            ResetScreen(
                onBack = { nav.popBackStack() },
                onNext = { pass ->
                    nav.navigate("confirm/${Uri.encode(email)}/$otp/${Uri.encode(pass)}")
                }
            )
        }

        // ----- Màn 4: xác nhận (nhận email + otp + pass) -----
        composable("confirm/{email}/{otp}/{pass}") { entry ->
            val email = entry.arguments?.getString("email").orEmpty()
            val otp = entry.arguments?.getString("otp").orEmpty()
            val pass = entry.arguments?.getString("pass").orEmpty()
            ConfirmScreen(
                email = email,
                otp = otp,
                pass = pass,
                onBack = { nav.popBackStack() },
                onSubmit = {
                    // Gửi cả 3 dữ liệu về màn 1
                    nav.navigate(
                        "forgot?email=${Uri.encode(email)}&otp=$otp&pass=${Uri.encode(pass)}"
                    ) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}

// ---------------- Màn 1 ----------------
@Composable
fun ForgotScreen(
    resultEmail: String,
    resultOtp: String,
    resultPass: String,
    onNext: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AuthScaffold(
        title = "Forget Password?",
        subtitle = "Enter your Email, we will send you a verification code."
    ) {
        AppField(
            value = email,
            onValueChange = { email = it; error = "" },
            placeholder = "Your Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        ErrorText(error)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Next") {
            if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) onNext(email.trim())
            else error = "Email không hợp lệ"
        }

        // Khung dữ liệu nhận về từ màn hình 4
        if (resultEmail.isNotEmpty()) {
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9E8FA), RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val red = Color(0xFFD32F2F)
                Text("email: $resultEmail", color = red, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text("otp: $resultOtp", color = red, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text("pass: $resultPass", color = red, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text("(từ màn hình 4)", color = red, fontSize = 18.sp)
            }
        }
    }
}

// ---------------- Màn 2 ----------------
@Composable
fun VerifyScreen(onBack: () -> Unit, onNext: (String) -> Unit) {
    var otp by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AuthScaffold(
        title = "Verify Code",
        subtitle = "Enter the the code\nwe just sent you on your registered Email",
        onBack = onBack
    ) {
        OtpField(value = otp, onValueChange = { otp = it; error = "" })
        ErrorText(error)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Next") {
            if (otp.length == 5) onNext(otp) else error = "Vui lòng nhập đủ 5 số"
        }
    }
}

// ---------------- Màn 3 ----------------
@Composable
fun ResetScreen(onBack: () -> Unit, onNext: (String) -> Unit) {
    var pass by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AuthScaffold(
        title = "Create new password",
        subtitle = "Your new password must be different form\npreviously used password",
        onBack = onBack
    ) {
        AppField(
            value = pass,
            onValueChange = { pass = it; error = "" },
            placeholder = "Password",
            icon = Icons.Default.Lock,
            isPassword = true
        )
        Spacer(Modifier.height(12.dp))
        AppField(
            value = confirm,
            onValueChange = { confirm = it; error = "" },
            placeholder = "Confirm Password",
            icon = Icons.Default.Lock,
            isPassword = true
        )
        ErrorText(error)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Next") {
            when {
                pass.length < 6 -> error = "Mật khẩu tối thiểu 6 ký tự"
                pass != confirm -> error = "Mật khẩu xác nhận không khớp"
                else -> onNext(pass)
            }
        }
    }
}

// ---------------- Màn 4 ----------------
@Composable
fun ConfirmScreen(
    email: String,
    otp: String,
    pass: String,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    AuthScaffold(
        title = "Confirm",
        subtitle = "We are here to help you!",
        onBack = onBack
    ) {
        AppField(value = email, placeholder = "Email", icon = Icons.Default.Person, readOnly = true)
        Spacer(Modifier.height(12.dp))
        AppField(value = otp, placeholder = "OTP", icon = Icons.Default.Email, readOnly = true)
        Spacer(Modifier.height(12.dp))
        AppField(
            value = "*".repeat(pass.length),
            placeholder = "Password",
            icon = Icons.Default.Lock,
            readOnly = true
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Submit", onSubmit)
    }
}