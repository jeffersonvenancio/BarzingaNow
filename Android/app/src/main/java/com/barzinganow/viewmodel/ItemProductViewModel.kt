package com.barzinganow.viewmodel

import android.app.Activity
import android.content.Context
import android.databinding.BaseObservable
import android.databinding.BindingAdapter
import android.widget.ImageView
import com.barzinganow.model.Product
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

/**
 * Created by diego.santos on 04/10/17.
 */
class ItemProductViewModel(mContext: Activity, internal var mProduct: Product) : BaseObservable() {

    internal var mContext: Context

    val image: String?
        get() = mProduct.image_url

    val name: String?
        get() = mProduct.description

    val price: String
        get() {

            val brLocale = Locale("pt", "BR")
            val `in` = NumberFormat.getCurrencyInstance(brLocale)

            return `in`.format(mProduct.price)
        }

    init {
        this.mContext = mContext
    }

    fun setProduct(Product: Product) {
        this.mProduct = Product
        notifyChange()
    }

    companion object {

        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String) {
            Glide.with(view.context).load(imageUrl).into(view)
        }
    }
}