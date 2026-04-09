package com.example.fittrack.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fittrack.Screen

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        // home tab
        BottomItem("Dashboard", Screen.Landing.route, Icons.Default.Home),

        // food tab
        BottomItem("Food", Screen.LogFood.route, Icons.Default.Fastfood),

        // workouts tab
        BottomItem("Workouts", Screen.Workout.route, Icons.Default.FitnessCenter),

        // profile tab
        BottomItem("Profile", Screen.Profile.route, Icons.Default.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // background color
        tonalElevation = 8.dp
    ) {
        // current route
        val navBackStackEntry = navController.currentBackStackEntryAsState()

        // extract route
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            // highlight selected
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Landing.route)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                },
                // consistent label display
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

// Bottom Item
data class BottomItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)