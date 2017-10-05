package com.barzinganow.viewmodel

import com.barzinganow.manager.UserManager

/**
 * Created by diego.santos on 03/10/17.
 */
class MainViewModel(internal var mListener: DataListener) : ViewModel, UserManager.DataListener {
    internal var userManager: UserManager

    init {

        userManager = UserManager(this)
    }

    override fun onViewDestroy() {
        userManager.onViewDestroy()
    }

    fun logUser() {
        userManager.logIn()
    }

    override fun onLogInSuccess() {
        mListener.onLogInSuccess()
    }

    override fun onLogInFailure() {
        mListener.onLogInFailure()
    }

    interface DataListener {
        fun onLogInSuccess()
        fun onLogInFailure()
    }
}