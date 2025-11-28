package com.depi.bookdiscovery.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper


object Validation {
    fun isEmailValid(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isPasswordValid(password: String): Boolean =
        password.length >= 6

    fun doPasswordsMatch(p: String, confirm: String): Boolean = p == confirm
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}