package com.barzinganow.manager

/**
 * Created by diego.santos on 03/10/17.
 */
class UserManager(internal var mListener: DataListener) {

    fun onViewDestroy() {

    }

    fun logIn() {

    }

    fun logOut() {

    }

    interface DataListener {
        fun onLogInSuccess()
        fun onLogInFailure()
    }
}