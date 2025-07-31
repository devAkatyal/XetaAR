package com.assignment.xetaar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.assignment.xetaar.screens.ARScreen
import com.assignment.xetaar.screens.DrillScreen
import com.assignment.xetaar.ui.theme.XetaARTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XetaARTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "drill_screen") {
                    composable("drill_screen") {
                        DrillScreen(onStartDrillClick = { navController.navigate("ar_screen") })
                    }
                    composable("ar_screen") { ARScreen(onClose = { navController.popBackStack() }) }
                }
            }
        }
    }
}
