package com.barzinga.model

import com.google.gson.annotations.SerializedName

/**
 * Created by diego.santos on 06/10/17.
 */
class User (val name: String,
            val admin: Boolean,
            val money: Double,
            val email: String,
            val id: Long,
            @SerializedName("photo_url") val photoUrl: String)