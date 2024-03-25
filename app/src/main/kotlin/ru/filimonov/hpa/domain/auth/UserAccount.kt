package ru.filimonov.hpa.domain.auth

data class UserAccount(
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
)
