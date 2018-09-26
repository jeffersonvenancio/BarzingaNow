package com.barzinga.restClient

import com.barzinga.model.Product
import com.barzinga.model.User
import com.barzinga.restClient.parameter.TransactionParameter
import com.barzinga.viewmodel.Constants
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response


/**
 * Created by diego.santos on 04/10/17.
 */

interface BarzingaService {

    @retrofit2.http.Headers("Bearer: Token Diego")
    @retrofit2.http.GET("product/category/")
    fun listProducts(): io.reactivex.Observable<ArrayList<Product>>

    @retrofit2.http.Headers("Bearer: Token Diego")
    @retrofit2.http.POST("transaction/app")
    fun buyProducts(@retrofit2.http.Body parameter: TransactionParameter): io.reactivex.Observable<Response<ResponseBody>>

    @retrofit2.http.Headers("Bearer: Token Diego")
    @retrofit2.http.GET("user/email/{user}")
    fun getProfile(@retrofit2.http.Path("user") user: String): io.reactivex.Observable<User>

    @retrofit2.http.Headers("Bearer: Token Diego")
    @retrofit2.http.GET("user/rfid/{rfid}")
    fun getProfileByRfid(@retrofit2.http.Path("rfid") rfid: String): io.reactivex.Observable<User>

    companion object Factory {
        fun create(): BarzingaService {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val httpClient = OkHttpClient.Builder()

            httpClient.addInterceptor(logging)

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