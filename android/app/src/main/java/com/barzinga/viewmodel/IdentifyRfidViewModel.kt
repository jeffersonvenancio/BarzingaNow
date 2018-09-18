package com.barzinga.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.barzinga.restClient.RfidService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class IdentifyRfidViewModel(application: Application) : AndroidViewModel(application) {
    fun identifyUser(rfid: String, listener: RfidServiceListener){
        val service = RfidService.create()

        rfid?.let{
            service.getRfid()
                    .observeOn((AndroidSchedulers.mainThread()))
                            .subscribeOn(Schedulers.io())
                            .subscribe ({ result ->
                                listener.onRequestSuccess(result)
                            }, { error ->
                                error.printStackTrace()
                                listener.onRequestFailure()
                            })
        }

    }

    interface RfidServiceListener{
        fun onRequestSuccess(response: Response<ResponseBody>)
        fun onRequestFailure()
    }
}