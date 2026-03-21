package com.idz.travelconnect.data.model.auth

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.base.ErrorCompletion

class FirebaseAuthModel {

    private val firebaseAuth = Firebase.auth

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun signIn(
        email: String,
        password: String,
        onSuccess: Completion,
        onError: ErrorCompletion
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidUserException -> "No account found with this email."
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect password or invalid credentials."
                    else -> exception.message ?: "Login failed."
                }
                onError(errorMessage)
            }
    }

    fun signUp(
        email: String,
        password: String,
        displayName: String,
        profileImage: String?,
        onSuccess: (String) -> Unit,
        onError: ErrorCompletion
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null && displayName.isNotBlank()) {
                    val profileUpdatesBuilder = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                    
                    if (!profileImage.isNullOrBlank()) {
                        profileUpdatesBuilder.setPhotoUri(Uri.parse(profileImage))
                    }
                    
                    val profileUpdates = profileUpdatesBuilder.build()
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

    fun signOut () {
        firebaseAuth.signOut()
    }
}
