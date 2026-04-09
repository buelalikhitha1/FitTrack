package com.example.fittrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.Screen
import com.example.fittrack.components.BottomNavBar
import com.example.fittrack.data.model.UserProfile
import com.example.fittrack.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    // Profile state
    val profile by authViewModel.userProfile
    val loading by authViewModel.loading

    // UI states
    var showEditDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        // Snackbar host
        snackbarHost = { SnackbarHost(snackbarHostState) },

        // Top header
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Header title
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    // Edit button
                    if (profile != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        },

        // Bottom navbar
        bottomBar = { BottomNavBar(navController) }

    ) { padding ->
        val scrollState = rememberScrollState()
        // Main column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Profile section
            Column {
                when {
                    // Loading state
                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // Profile card
                    profile != null -> ProfileCard(profile!!)

                    // Error text
                    else -> Text(
                        text = "Profile not found",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Logout button
            Button(
                onClick = {

                    // Logout user
                    authViewModel.logout()

                    // Navigate login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {

                // Logout icon
                Icon(Icons.Default.Logout, contentDescription = null)

                Spacer(Modifier.width(8.dp))

                // Logout text
                Text(
                    "Logout",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Edit dialog
        if (showEditDialog && profile != null) {
            EditProfileDialog(
                profile = profile!!,
                onDismiss = { showEditDialog = false },

                // Save profile
                onSave = { age, height, weight, goal ->

                    // Update profile
                    authViewModel.updateProfile(age, height, weight, goal)
                    showEditDialog = false
                    // profile updated success message
                    scope.launch {
                        snackbarHostState.showSnackbar("Profile Updated Successfully")
                    }
                }
            )
        }
    }
}

// Profile card
@Composable
fun ProfileCard(profile: UserProfile) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        // Card layout
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Age row
            ProfileRowPro(Icons.Default.Person, "Age", profile.age)

            // Height row
            ProfileRowPro(Icons.Default.Height, "Height", "${profile.height} cm")

            // Weight row
            ProfileRowPro(Icons.Default.MonitorWeight, "Weight", "${profile.weight} kg")

            // Goal row
            ProfileRowPro(Icons.Default.TrackChanges, "Goal", profile.goal)
        }
    }
}

@Composable
fun ProfileRowPro(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Icon surface
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                tonalElevation = 2.dp,
                modifier = Modifier.size(40.dp)
            ) {

                // Icon box
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            // Label text
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Value text
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// Edit dialog
@Composable
fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    // Field states
    var age by remember { mutableStateOf(profile.age) }
    var height by remember { mutableStateOf(profile.height) }
    var weight by remember { mutableStateOf(profile.weight) }
    var goal by remember { mutableStateOf(profile.goal) }

    // Scroll state
    val scrollState = rememberScrollState()

    // Alert dialog
    AlertDialog(
        onDismissRequest = onDismiss,

        // Save button
        confirmButton = {
            Button(
                onClick = {

                    // Validate fields
                    if (
                        age.isNotBlank() &&
                        height.isNotBlank() &&
                        weight.isNotBlank() &&
                        goal.isNotBlank()
                    ) {
                        // Save profile
                        onSave(age, height, weight, goal)
                    }
                }
            ) {
                Text("Save")
            }
        },

        // Cancel button
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },

        // Dialog title
        title = {
            Text("Edit Profile")
        },

        // Dialog content
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Age field
                OutlinedTextField(
                    age,
                    { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Height field
                OutlinedTextField(
                    height,
                    { height = it },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Weight field
                OutlinedTextField(
                    weight,
                    { weight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Goal field
                OutlinedTextField(
                    goal,
                    { goal = it },
                    label = { Text("Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    )
}