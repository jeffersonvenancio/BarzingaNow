package com.barzinga.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.barzinga.restClient.RepositoryProvider
import com.barzinga.restClient.parameter.TransactionParameter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by diego.santos on 12/10/17.
 */
class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    fun buyProducts(transactionParameter: TransactionParameter?, listener: TransactionListener) {

        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        val repository = RepositoryProvider.provideProductsRepository()

        compositeDisposable.add(
                transactionParameter?.let {
                    repository.buyProducts(it)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe ({ result ->
                                    listener.onTransactionSuccess()
                            }, { error ->
                                error.printStackTrace()
                                listener.onTransactionFailure()
                            })
                }
        )

    }

    interface TransactionListener{
        fun onTransactionSuccess()
        fun onTransactionFailure()
    }
}