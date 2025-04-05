package com.example.mealplanapp.ui.theme.screen.health

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
class AuthViewModel(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {  // Changed to regular ViewModel

    private val _signInState = mutableStateOf<AuthState>(AuthState.Idle)
    val signInState: State<AuthState> = _signInState

    private val _signUpState = mutableStateOf<AuthState>(AuthState.Idle)
    val signUpState: State<AuthState> = _signUpState

    fun signUp(
        name: String,
        email: String,
        password: String,
        healthGoals: String,
        dietaryPreferences: Set<String>,
        onComplete: () -> Unit = {}
    ) {
        _signUpState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "healthGoals" to healthGoals,
                        "dietaryPreferences" to dietaryPreferences.toList()
                    )

                    firestore.collection("users")
                        .document(auth.currentUser?.uid ?: "")
                        .set(user)
                        .addOnSuccessListener {
                            _signUpState.value = AuthState.Success
                            onComplete()
                        }
                        .addOnFailureListener {
                            _signUpState.value = AuthState.Error(it.message ?: "Failed to save user data")
                        }
                } else {
                    _signUpState.value = AuthState.Error(task.exception?.message ?: "Sign up failed")
                }
            }
    }

    fun signIn(email: String, password: String, onComplete: () -> Unit = {}) {
        _signInState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInState.value = AuthState.Success
                    onComplete()
                } else {
                    _signInState.value = AuthState.Error(task.exception?.message ?: "Sign in failed")
                }
            }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}