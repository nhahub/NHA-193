package com.depi.bookdiscovery.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper


object Validation {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")


    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    fun isPasswordValid(password: String): Boolean =
        password.length >= 6

    fun doPasswordsMatch(p: String, confirm: String): Boolean = p == confirm
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}