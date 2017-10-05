package com.barzinganow.viewmodel

import com.barzinganow.model.Product
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by diego.santos on 05/10/17.
 */
class ProductViewModel(val product: Product){
    private val clicks = PublishSubject.create<Unit>()

    fun getDescription() = product.description
    fun getPrice() = String.format("%.2f", product.price)
    fun getQuantity() = product.quantity.toString()

    fun onClick() {
        clicks.onNext(Unit)
    }

    fun clicks(): Observable<Unit> = clicks.hide()
}