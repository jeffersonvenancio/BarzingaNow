package com.barzinga.restClient

import com.barzinga.model.Product
import com.barzinga.model.User

/**
 * Created by diego.santos on 04/10/17.
 */
class UserRepository(val apiService: BarzingaService) {

    fun getProfile( user: String): io.reactivex.Observable<User> {
        return apiService.getProfile(user)
    }

}