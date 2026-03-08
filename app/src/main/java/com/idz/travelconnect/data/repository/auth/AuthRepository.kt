package com.idz.travelconnect.data.repository.auth

import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.base.ErrorCompletion
import com.idz.travelconnect.data.model.auth.FirebaseAuthModel

class AuthRepository private constructor() {

    private val firebaseAuthModel = FirebaseAuthModel()

    val currentUser: FirebaseUser?
        get() = firebaseAuthModel.currentUser

    fun signIn(
        email: String,
        password: String,
        onSuccess: Completion,
        onError: ErrorCompletion
    ) {
        firebaseAuthModel.signIn(email, password, onSuccess, onError)
    }

    fun signUp(
        email: String,
        password: String,
        displayName: String,
        profileImage: String?,
        onSuccess: (String) -> Unit,
        onError: ErrorCompletion
    ) {
        firebaseAuthModel.signUp(email, password, displayName, profileImage, onSuccess, onError)
    }

    companion object {
        val shared: AuthRepository by lazy { AuthRepository() }
    }
}