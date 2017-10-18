package com.barzinga.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.barzinga.restClient.RepositoryProvider
import com.barzinga.restClient.TransactionParameter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by diego.santos on 12/10/17.
 */
class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    fun buyProducts(transactionParameter: TransactionParameter) {

        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        val repository = RepositoryProvider.provideProductsRepository()

        compositeDisposable.add(
                repository.buyProducts(transactionParameter)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe ({
                            result ->

                        }, { error ->
                            error.printStackTrace()

                        })
        )

    }
}