package com.barzinga.util

/**
 * Created by diego.santos on 03/10/17.
 */
object Validator {
    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}