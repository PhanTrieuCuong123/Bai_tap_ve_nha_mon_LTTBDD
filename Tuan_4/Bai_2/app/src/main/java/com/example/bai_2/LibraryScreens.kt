package com.example.bai_2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun Bt2App() {
    val library = remember { LibraryManager() }
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val tabs = listOf(
        Triple("manage", "Quản lý", Icons.Default.Home),
        Triple("books", "DS Sách", Icons.AutoMirrored.Filled.List),
        Triple("students", "Sinh viên", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            nav.navigate(route) {
                                popUpTo("manage")
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "manage",
            modifier = Modifier.padding(padding)
        ) {
            composable("manage") { ManageScreen(library) }
            composable("books") { BookListScreen(library) }
            composable("students") { StudentListScreen(library) }
        }
    }
}

// ---------------- Tab 1: Quản lý ----------------
@Composable
fun ManageScreen(library: LibraryManager) {
    val student = library.currentStudent
    var showStudentDialog by remember { mutableStateOf(false) }
    var showBookDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            "Hệ thống\nQuản lý Thư viện",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(32.dp))

        Text("Sinh viên", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = student?.name ?: "",
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { showStudentDialog = true }, shape = RoundedCornerShape(8.dp)) {
                Text("Thay đổi")
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Danh sách sách", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFDDDDDD), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            val borrowed = student?.borrowedBooks ?: emptyList()
            if (borrowed.isEmpty()) {
                Text("Chưa mượn cuốn sách nào", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(borrowed, key = { it.id }) { book ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(24.dp))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Bỏ tick = trả sách
                            Checkbox(checked = true, onCheckedChange = { library.giveBack(book) })
                            Text(book.title, color = Color.Black)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { showBookDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally).width(200.dp)
        ) {
            Text("Thêm")
        }
    }

    if (showStudentDialog) {
        AlertDialog(
            onDismissRequest = { showStudentDialog = false },
            title = { Text("Chọn sinh viên") },
            text = {
                Column {
                    library.students.forEach { s ->
                        TextButton(onClick = {
                            library.changeStudent(s)
                            showStudentDialog = false
                        }) { Text(s.name) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStudentDialog = false }) { Text("Đóng") }
            }
        )
    }

    if (showBookDialog) {
        val available = library.books.filter { it.isAvailable }
        AlertDialog(
            onDismissRequest = { showBookDialog = false },
            title = { Text("Chọn sách để mượn") },
            text = {
                if (available.isEmpty()) {
                    Text("Không còn sách trống")
                } else {
                    Column {
                        available.forEach { b ->
                            TextButton(onClick = {
                                library.borrow(b)
                                showBookDialog = false
                            }) { Text(b.title) }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBookDialog = false }) { Text("Đóng") }
            }
        )
    }
}

// ---------------- Tab 2: DS Sách ----------------
@Composable
fun BookListScreen(library: LibraryManager) {
    var title by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Danh sách sách", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Tên sách mới") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (title.isNotBlank()) {
                    library.addBook(title.trim())
                    title = ""
                }
            }) { Text("Thêm") }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(library.books, key = { it.id }) { book ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(book.getInfo(), fontWeight = FontWeight.Bold)   // đa hình
                        val holder = book.borrowedBy
                        Text(
                            if (holder == null) "Còn sẵn" else "Đang được mượn bởi: ${holder.name}",
                            color = if (holder == null) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------- Tab 3: Sinh viên ----------------
@Composable
fun StudentListScreen(library: LibraryManager) {
    var name by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Danh sách sinh viên", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Tên sinh viên mới") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (name.isNotBlank()) {
                    library.addStudent(name.trim())
                    name = ""
                }
            }) { Text("Thêm") }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(library.students, key = { it.id }) { s ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(s.name, fontWeight = FontWeight.Bold)
                        Text("Đang mượn: ${s.borrowedBooks.size} sách", fontSize = 13.sp)
                        if (s.borrowedBooks.isNotEmpty()) {
                            Text(
                                s.borrowedBooks.joinToString { it.title },
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}