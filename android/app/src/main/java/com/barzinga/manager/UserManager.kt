package com.barzinga.manager

import com.barzinga.model.User
import com.barzinga.restClient.RepositoryProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by diego.santos on 03/10/17.
 */
class UserManager(internal var mListener: DataListener) {

    fun onViewDestroy() {

    }

    fun logIn(user: String) {
        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        val repository = RepositoryProvider.provideUserRepository()

        compositeDisposable.add(
                repository.getProfile(user)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe ({
                            result ->
                            mListener.onLogInSuccess(result)
                        }, { error ->
                            error.printStackTrace()
                            mListener.onLogInFailure()
                        })
        )
    }

    fun logOut() {

    }

    interface DataListener {
        fun onLogInSuccess(user: User)
        fun onLogInFailure()
    }
}