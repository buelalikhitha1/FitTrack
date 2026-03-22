package com.example.fittrack

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.UserDao
import com.example.fittrack.data.UserEntity
import com.example.fittrack.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    // State variables
    val loading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val currentUser = mutableStateOf(FirebaseAuth.getInstance().currentUser)
    val userProfile = mutableStateOf<UserProfile?>(null)

    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    // Login Function (Email & Password)
    fun login(email: String, password: String, onNavigate: (profileExists: Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Email and password cannot be empty"
            return
        }

        loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    fetchUserProfile { exists ->
                        onNavigate(exists)
                    }
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    // Signup Function (Email & Password)
    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Email and password cannot be empty"
            return
        }

        loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    onSuccess()
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    // Google Login Function
    fun loginWithGoogle(idToken: String, onNavigate: (profileExists: Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        loading.value = true
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                loading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    fetchUserProfile { exists ->
                        onNavigate(exists)
                    }
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    // Save User Profile Function (Firebase & Room Database)
    fun saveUserProfile(
        age: String,
        height: String,
        weight: String,
        goal: String,
        onSuccess: () -> Unit
    ) {
        val user = auth.currentUser ?: run {
            errorMessage.value = "User not logged in"
            return
        }

        val profileMap = mapOf(
            "age" to age,
            "height" to height,
            "weight" to weight,
            "goal" to goal
        )

        loading.value = true
        database.child("users").child(user.uid).setValue(profileMap)
            .addOnCompleteListener { task ->
                loading.value = false
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        // Save to Room
                        val entity = UserEntity(
                            id = 0,
                            uid = user.uid,
                            age = age,
                            height = height,
                            weight = weight,
                            goal = goal
                        )
                        userDao.insertUser(entity)

                        userProfile.value = UserProfile(age, height, weight, goal)
                        onSuccess()
                    }
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    // Get profile from Room by Firebase UID
    private fun fetchUserProfile(onResult: (exists: Boolean) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val entity = userDao.getUserByUid(user.uid)
            if (entity != null) {
                userProfile.value = UserProfile(
                    age = entity.age,
                    height = entity.height,
                    weight = entity.weight,
                    goal = entity.goal
                )
                onResult(true)
            } else {
                userProfile.value = null
                onResult(false)
            }
        }
    }

    // Logout Function
    fun logout() {
        auth.signOut()
        currentUser.value = null
        userProfile.value = null
    }
}