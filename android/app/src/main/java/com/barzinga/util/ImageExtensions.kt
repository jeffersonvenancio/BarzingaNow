package com.barzinga.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropCircleTransformation

/**
 * Created by rafaela.araujo on 08/11/17.
 */

fun ImageView.loadUrl(photoUrl: String?){
    Glide.with(this)
            .load(photoUrl)
            .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
            .into(this)
}
