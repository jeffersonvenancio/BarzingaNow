package com.barzinga.util

import com.barzinga.model.User
import com.barzinga.restClient.parameter.TransactionParameter
import com.google.gson.Gson

/**
 * Created by diego.santos on 06/10/17.
 */
class ConvertObjectsUtil{
    companion object {
        fun getStringFromObject(anyObject: Any?) : String {
            val gson = Gson()
            return gson.toJson(anyObject)
        }

        fun getUserFromJson(userJson: String): User? {
            var user: User? = null

            if (userJson.isNotEmpty()) {
                val userGson = Gson()

                user = userGson.fromJson<User>(userJson, User::class.java!!)
            }

            return user
        }

        fun getTransactionParameterFromJson(transactionJson: String): TransactionParameter? {
            var transactionParameter: TransactionParameter? = null

            if (transactionJson.isNotEmpty()) {
                val transactionGson = Gson()

                transactionParameter = transactionGson.fromJson<TransactionParameter>(transactionJson, TransactionParameter::class.java!!)
            }

            return transactionParameter
        }
    }
}