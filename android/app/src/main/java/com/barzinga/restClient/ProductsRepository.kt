package com.barzinga.restClient

import com.barzinga.model.Product

/**
 * Created by diego.santos on 04/10/17.
 */
class ProductsRepository(val apiService: BarzingaService) {

    fun listProducts(): io.reactivex.Observable<ArrayList<Product>> {
        return apiService.listProducts()
    }

    fun buyProducts(transactionParameter: TransactionParameter): io.reactivex.Observable<Void> {
        return apiService.buyProducts(transactionParameter)
    }

}