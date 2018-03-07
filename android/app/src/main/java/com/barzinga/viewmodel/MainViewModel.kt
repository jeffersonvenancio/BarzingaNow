package com.barzinga.viewmodel

import com.barzinga.manager.UserManager
import com.barzinga.model.User
import android.app.Application
import android.arch.lifecycle.AndroidViewModel

/**
 * Created by diego.santos on 03/10/17.
 */
class MainViewModel(application: Application) : AndroidViewModel(application), UserManager.DataListener {
    private var userManager: UserManager = UserManager(this)
    var mListener: UserManager.DataListener? = null

    fun setListener(listener: UserManager.DataListener){
        mListener = listener
    }

//    override fun onViewDestroy() {
//        userManager.onViewDestroy()
//    }

    fun logUser(user: String) {
        userManager.logIn(user)
    }

    override fun onLogInSuccess(user: User) {
        mListener?.onLogInSuccess(user)
    }

    override fun onLogInFailure() {
        mListener?.onLogInFailure()
    }
}