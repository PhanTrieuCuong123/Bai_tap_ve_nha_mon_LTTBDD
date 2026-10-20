package com.example.bai_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bai_1.ui.theme.Bai_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            Bai_1Theme {
                AppNavigation()
            }
        }
    }
}

object Routes {
    const val WELCOME = "welcome"
    const val LIST = "list"
    const val TEXT = "text"
    const val IMAGE = "image"
    const val TEXTFIELD = "textfield"
    const val PASSWORD = "password"
    const val COLUMN = "column"
    const val ROW = "row"
    const val MORE = "more"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) {
            WelcomeScreen(onReady = { nav.navigate(Routes.LIST) })
        }
        composable(Routes.LIST) {
            ComponentsListScreen(onItemClick = { route -> nav.navigate(route) })
        }
        composable(Routes.TEXT) { TextDetailScreen { nav.popBackStack() } }
        composable(Routes.IMAGE) { ImageDetailScreen { nav.popBackStack() } }
        composable(Routes.TEXTFIELD) { TextFieldDetailScreen { nav.popBackStack() } }
        composable(Routes.PASSWORD) { PasswordDetailScreen { nav.popBackStack() } }
        composable(Routes.COLUMN) { ColumnDetailScreen { nav.popBackStack() } }
        composable(Routes.ROW) { RowDetailScreen { nav.popBackStack() } }
        composable(Routes.MORE) { MoreComponentsScreen { nav.popBackStack() } }
    }
}