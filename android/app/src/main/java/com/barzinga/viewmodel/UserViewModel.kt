package com.barzinga.viewmodel

import com.barzinga.model.Product
import com.barzinga.model.User
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by diego.santos on 05/10/17.
 */
class UserViewModel(val user: User){

    fun getMoney() = String.format("%.2f", user.money)
}