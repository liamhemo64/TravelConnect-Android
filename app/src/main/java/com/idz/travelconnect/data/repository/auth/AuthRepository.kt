package com.idz.travelconnect.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository private constructor() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Login failed.")
            }
    }

    fun register(
        email: String,
        password: String,
        displayName: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null && displayName.isNotBlank()) {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    user.updateProfile(profileUpdates).addOnCompleteListener {
                        onSuccess(user.uid)
                    }
                } else {
                    onSuccess(user?.uid ?: "")
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Registration failed.")
            }
    }

    companion object {
        val shared: AuthRepository by lazy { AuthRepository() }
    }
}