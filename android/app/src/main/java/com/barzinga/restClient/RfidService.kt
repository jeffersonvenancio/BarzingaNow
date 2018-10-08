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
 * Created by matheus.primo
 */

interface RfidService {

    @retrofit2.http.GET("/")
    fun getRfid(): io.reactivex.Observable<Response<ResponseBody>>

    companion object Factory {
        fun create(): RfidService {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()

            httpClient.addInterceptor(logging)

            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .client(httpClient.build())
                    .baseUrl(Constants.BASE_RFID_URL)
                    .build()

            return retrofit.create(RfidService::class.java)
        }
    }
}