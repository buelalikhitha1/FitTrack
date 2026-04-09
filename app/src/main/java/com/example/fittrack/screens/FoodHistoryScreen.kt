package com.example.fittrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.Screen
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.viewmodels.FoodViewModel

@Composable
fun FoodHistoryScreen(
    navController: NavController,
    foodViewModel: FoodViewModel
) {
    // Food state
    val foods = foodViewModel.allFoods.value
    val todayCalories = foodViewModel.todayCalories.value

    Scaffold(
        // Bottom navbar
        bottomBar = { BottomNavBar(navController) },

        // Add food FAB
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.LogFood.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },

        // FAB position
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            // Main column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .height(84.dp)
                    .padding(innerPadding)
            ) {

                // Top header
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // Header content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Food Log History",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Food list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {

                    // Total calories
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            tonalElevation = 6.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {

                            // Total row
                            Row(
                                modifier = Modifier.padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Total text
                                Column {
                                    Text(
                                        "Today's Total",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        "${todayCalories.toInt()} kcal",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Fire icon
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }

                    // Food entries
                    itemsIndexed(foods) { index, food ->

                        // Date
                        val showDate = index == 0 || foods[index - 1].date != food.date

                        if (showDate) {
                            Text(
                                text = food.date,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Food card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp,
                            shadowElevation = 4.dp
                        ) {

                            // Card column
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {

                                // Top row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // Food details
                                    Column {
                                        Text(
                                            text = food.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = food.time,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Calories and delete
                                    Row(verticalAlignment = Alignment.CenterVertically) {

                                        // Calories text
                                        Text(
                                            text = "${food.calories.toInt()} kcal",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Spacer(Modifier.width(12.dp))

                                        // Delete button
                                        IconButton(
                                            onClick = { foodViewModel.deleteFood(food.id) }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Food",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                // Nutrition row
                                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    // Protein
                                    Text(
                                        text = "P: ${food.protein}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    // Carbs
                                    Text(
                                        text = "C: ${food.carbs}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    // Fat
                                    Text(
                                        text = "F: ${food.fat}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    // Bottom space
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    )
}