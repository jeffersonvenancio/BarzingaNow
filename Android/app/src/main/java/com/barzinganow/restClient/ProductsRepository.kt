package com.barzinganow.restClient

import com.barzinganow.model.Product

/**
 * Created by diego.santos on 04/10/17.
 */
class ProductsRepository(val apiService: BarzingaService) {

    fun listProducts(): io.reactivex.Observable<List<Product>> {
        return apiService.listProducts()
    }

}