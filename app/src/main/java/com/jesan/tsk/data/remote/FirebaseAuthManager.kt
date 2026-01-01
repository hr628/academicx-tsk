package com.jesan.tsk.data.remote

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.jesan.tsk.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Firebase Authentication (Optional feature)
 */
@Singleton
class FirebaseAuthManager @Inject constructor() {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    /**
     * Get current user
     */
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return firebaseUser?.toUser()
    }
    
    /**
     * Sign in with Google
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user?.toUser()
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sign out
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Check if user is signed in
     */
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Convert FirebaseUser to domain User model
     */
    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }
}
