package com.barzinga.manager

import com.barzinga.model.User
import com.barzinga.restClient.RepositoryProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by diego.santos on 03/10/17.
 */
class RfidManager(internal var mListener: DataListener) {

    fun onViewDestroy() {

    }

    fun getRfid() {
        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        val repository = RepositoryProvider.provideUserRepository()

        compositeDisposable.add(
                repository.getRFID()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe ({
                            result ->
                            mListener.onRfidSuccess(result)
                        }, { error ->
                            error.printStackTrace()
                            mListener.onRfidFailure(error.toString())
                        })
        )
    }

    interface DataListener {
        fun onRfidSuccess(response: Response<ResponseBody>)
        fun onRfidFailure(error : String)
    }
}