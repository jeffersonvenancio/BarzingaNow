package com.barzinga.restClient

import com.barzinga.model.Product
import com.barzinga.restClient.parameter.TransactionParameter
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by diego.santos on 04/10/17.
 */
class ProductsRepository(val apiService: BarzingaService) {

    fun listProducts(): io.reactivex.Observable<ArrayList<Product>> {
        return apiService.listProducts()
    }

    fun buyProducts(transactionParameter: TransactionParameter): io.reactivex.Observable<Response<ResponseBody>> {
        return apiService.buyProducts(transactionParameter)
    }

}