package com.jesan.tsk.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.jesan.tsk.data.remote.FirebaseAuthManager
import com.jesan.tsk.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations (Optional feature)
 */
@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuthManager: FirebaseAuthManager
) {
    
    /**
     * Get current user
     */
    fun getCurrentUser(): User? {
        return firebaseAuthManager.getCurrentUser()
    }
    
    /**
     * Sign in with Google
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return firebaseAuthManager.signInWithGoogle(account)
    }
    
    /**
     * Sign out
     */
    fun signOut() {
        firebaseAuthManager.signOut()
    }
    
    /**
     * Check if user is signed in
     */
    fun isSignedIn(): Boolean {
        return firebaseAuthManager.isSignedIn()
    }
}
