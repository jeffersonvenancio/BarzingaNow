package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.barzinga.R
import com.barzinga.databinding.ActivityOptionsBinding
import com.barzinga.model.User
import com.barzinga.util.ConvertObjectsUtil.Companion.getStringFromObject
import com.barzinga.util.ConvertObjectsUtil.Companion.getUserFromJson
import com.barzinga.util.launchActivity
import com.barzinga.util.loadUrl
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.Constants.USER_EXTRA
import com.barzinga.viewmodel.UserViewModel
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
            mUserPhoto.loadUrl(user?.photoUrl)

            binding.viewmodel = user?.let { UserViewModel(it) }
        }

        llBuyButton.setOnClickListener{
            launchActivity<ProductsActivity> {
                putExtra(Constants.USER_EXTRA, getStringFromObject(user))
            }
        }
    }

    companion object {
        fun start(context: Context, userJson: String) {
            val starter = Intent(context, OptionsActivity::class.java)
            starter.putExtra(USER_EXTRA, userJson)
            context.startActivity(starter)
        }
    }
}
