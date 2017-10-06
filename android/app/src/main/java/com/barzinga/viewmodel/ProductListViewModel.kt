package com.barzinga.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.barzinga.model.Product
import com.barzinga.restClient.ProductsRepositoryProvider

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call


/**
 * Created by diego.santos on 03/10/17.
 */
class ProductListViewModel(application: Application) : AndroidViewModel(application) {

    fun listProducts(listener: ProductsListener) {

        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        val repository = ProductsRepositoryProvider.provideSearchRepository()

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