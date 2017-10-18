package com.barzinga.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.barzinga.restClient.RepositoryProvider

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by diego.santos on 03/10/17.
 */
class ProductListViewModel(application: Application) : AndroidViewModel(application) {

    fun listProducts(listener: ProductsListener) {

        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        val repository = RepositoryProvider.provideProductsRepository()

        compositeDisposable.add(
                repository.listProducts()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe ({
                            result ->
                                listener.onProductsListGotten(result)
//                                updateUi(result)
                        }, { error ->
                            error.printStackTrace()
//                                updateUi(null)
                        })
        )

    }
}