package com.barzinga.viewmodel

import com.barzinga.manager.UserManager
import com.barzinga.model.User
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.barzinga.manager.RfidManager

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


    fun logUserWithRfid(rfid: String) {
        userManager.logInWithRfid(rfid)
    }

    override fun onLogInSuccess(user: User) {
        mListener?.onLogInSuccess(user)
    }

    override fun onLogInFailure() {
        mListener?.onLogInFailure()
    }
}