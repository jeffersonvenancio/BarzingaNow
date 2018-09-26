package com.barzinga.viewmodel

import com.barzinga.manager.UserManager
import com.barzinga.model.User
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.barzinga.manager.RfidManager
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by diego.santos on 03/10/17.
 */
class RfidViewModel(application: Application) : AndroidViewModel(application), RfidManager.DataListener {
    private var rfidManager: RfidManager = RfidManager(this)
    var mListener: RfidManager.DataListener? = null

    fun setListener(listener: RfidManager.DataListener){
        mListener = listener
    }

    fun getRfid() {
        rfidManager.getRfid()
    }

    override fun onRfidSuccess(response: Response<ResponseBody>) {
        mListener?.onRfidSuccess(response)
    }

    override fun onRfidFailure(error: String) {
        mListener?.onRfidFailure(error)
    }
}