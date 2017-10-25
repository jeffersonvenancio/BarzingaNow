package com.barzinga.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.barzinga.R
import com.barzinga.databinding.ActivityOptionsBinding
import com.barzinga.model.User
import com.barzinga.util.ConvertObjectsUtil.Companion.getStringFromObject
import com.barzinga.util.ConvertObjectsUtil.Companion.getUserFromJson
import com.barzinga.viewmodel.Constants.USER_EXTRA
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_options.*
import kotlinx.android.synthetic.main.view_top_bar.*

class OptionsActivity : AppCompatActivity() {

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityOptionsBinding = DataBindingUtil.setContentView(this, R.layout.activity_options)

        if (intent?.hasExtra(USER_EXTRA) == true){
            val userJson = intent.getStringExtra(USER_EXTRA)

            user = getUserFromJson(userJson)

            Glide.with(this)
                    .load(user?.photoUrl)
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(mUserPhoto)

            binding.viewmodel = user?.let { UserViewModel(it) }
        }

        llBuyButton.setOnClickListener({
            ProductsActivity.start(this, getStringFromObject(user))
            finish()
        })
    }

    companion object {
        fun start(context: Context, userJson: String) {
            val starter = Intent(context, OptionsActivity::class.java)
            starter.putExtra(USER_EXTRA, userJson)
            context.startActivity(starter)
        }
    }
}
