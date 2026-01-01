package com.jesan.tsk.domain.model

/**
 * Domain model representing a user
 */
data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
