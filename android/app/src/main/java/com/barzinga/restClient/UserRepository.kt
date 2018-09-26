package com.barzinga.restClient

import com.barzinga.model.Product
import com.barzinga.model.User
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by diego.santos on 04/10/17.
 */
class UserRepository(val apiService: BarzingaService, val rfidService: RfidService) {

    fun getProfile( user: String): io.reactivex.Observable<User> {
        return apiService.getProfile(user)
    }

    fun getProfileByRfid( rfid: String): io.reactivex.Observable<User> {
        return apiService.getProfileByRfid(rfid)
    }

    fun getRFID(): Observable<Response<ResponseBody>> {
        return rfidService.getRfid()
    }

}