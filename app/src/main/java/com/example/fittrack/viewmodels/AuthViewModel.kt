package com.example.fittrack.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.dao.UserDao
import com.example.fittrack.data.model.UserEntity
import com.example.fittrack.data.model.UserProfile
import com.example.fittrack.notifications.ReminderScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val userDao: UserDao
) : AndroidViewModel(application) {

    // Firebase auth
    private val auth = FirebaseAuth.getInstance()
    // Database reference
    private val database = Firebase.database.reference
    // App context
    private val appContext = getApplication<Application>().applicationContext

    // Loading state
    val loading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null) // Error message
    val currentUser = mutableStateOf(auth.currentUser) // Current user
    val userProfile = mutableStateOf<UserProfile?>(null) // User profile

    // init
    init {
        observeUserProfile()
    }

    // Login Function
    fun login(
        email: String,
        password: String,
        onNavigate: (profileExists: Boolean) -> Unit
    ) {
        // validation
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "Email and password cannot be empty"
            return
        }

        loading.value = true

        // signin in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    observeUserProfile()
                    checkProfileExists { profileExists ->
                        onNavigate(profileExists)
                        // Schedule daily notifications after login
                        ReminderScheduler.scheduleDailyReminder(appContext)
                    }
                } else {
                    errorMessage.value = task.exception?.message // firebase error
                }
            }
    }

    // login with google function
    fun loginWithGoogle(
        idToken: String,
        onNavigate: (profileExists: Boolean) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        loading.value = true

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                loading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    observeUserProfile()
                    checkProfileExists { profileExists ->
                        onNavigate(profileExists)
                        // Schedule daily notifications after Google login
                        ReminderScheduler.scheduleDailyReminder(appContext)
                    }
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    // Signup function
    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        // validation
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
                    // Schedule daily notifications for new user
                    ReminderScheduler.scheduleDailyReminder(appContext)
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }

    // Save User Profile Function
    fun saveUserProfile(
        age: String,
        height: String,
        weight: String,
        goal: String,
        onSuccess: () -> Unit
    ) {
        val user = auth.currentUser ?: return
        val profileMap = mapOf(
            "age" to age,
            "height" to height,
            "weight" to weight,
            "goal" to goal
        )

        loading.value = true

        database.child("users")
            .child(user.uid)
            .setValue(profileMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        val entity = UserEntity(
                            uid = user.uid,
                            age = age,
                            height = height,
                            weight = weight,
                            goal = goal
                        )
                        userDao.insertUser(entity) // save local
                        userProfile.value = UserProfile(age, height, weight, goal)
                        loading.value = false
                        onSuccess()
                    }
                } else {
                    loading.value = false
                    errorMessage.value = task.exception?.message
                }
            }
    }

    //  Update Profile Function
    fun updateProfile(
        age: String,
        height: String,
        weight: String,
        goal: String
    ) {
        val user = auth.currentUser ?: return
        val profileMap = mapOf(
            "age" to age,
            "height" to height,
            "weight" to weight,
            "goal" to goal
        )

        loading.value = true

        database.child("users")
            .child(user.uid)
            .setValue(profileMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        val updatedUser = UserEntity(
                            uid = user.uid,
                            age = age,
                            height = height,
                            weight = weight,
                            goal = goal
                        )
                        userDao.insertUser(updatedUser) // update local
                        userProfile.value = UserProfile(age, height, weight, goal)
                        loading.value = false
                    }
                } else {
                    loading.value = false
                    errorMessage.value = task.exception?.message
                }
            }
    }

    // Observe Profile Function
    private fun observeUserProfile() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            userDao.getUserByUid(user.uid)
                .collectLatest { entity ->
                    entity?.let {
                        userProfile.value = UserProfile(it.age, it.height, it.weight, it.goal)
                    }
                }
        }
    }

    // Load User Profile
    fun loadUserProfile() {
        observeUserProfile()
    }

    // Check Profile Exists
    private fun checkProfileExists(
        onNavigate: (Boolean) -> Unit
    ) {
        val user = auth.currentUser ?: run {
            onNavigate(false)
            return
        }

        viewModelScope.launch {
            userDao.getUserByUid(user.uid)
                .collectLatest { entity ->
                    onNavigate(entity != null)
                }
        }
    }

    // Logout Function
    fun logout() {
        auth.signOut()
        currentUser.value = null
        userProfile.value = null
        // Cancel daily notifications on logout
        ReminderScheduler.cancelDailyReminder(appContext)
    }
}

// ViewmodelFactory
class AuthViewModelFactory(
    private val application: Application,
    private val userDao: UserDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(application, userDao) as T // return instance
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}