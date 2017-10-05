package com.barzinganow.restClient

import com.barzinganow.model.Product
import com.barzinganow.viewmodel.Constants
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path


/**
 * Created by diego.santos on 04/10/17.
 */

interface BarzingaService {

    @retrofit2.http.Headers("Bearer: Token Diego")
    @retrofit2.http.GET("product/category/")
    fun listProducts(): io.reactivex.Observable<List<Product>>

    companion object Factory {
        fun create(): BarzingaService {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val httpClient = OkHttpClient.Builder()

            httpClient.addInterceptor(logging)  // <-- this is the important line!

            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .client(httpClient.build())
                    .baseUrl(Constants.BASE_URL)
                    .build()

            return retrofit.create(BarzingaService::class.java);
        }
    }
}