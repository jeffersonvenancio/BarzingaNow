package com.barzinga.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.barzinga.R
import com.barzinga.databinding.ActivityCheckoutBinding
import com.barzinga.restClient.parameter.TransactionParameter
import com.barzinga.util.ConvertObjectsUtil.Companion.getTransactionParameterFromJson
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.Constants.TRANSACTION_EXTRA
import com.barzinga.viewmodel.TransactionViewModel
import com.barzinga.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.view_top_bar.*

class CheckoutActivity : AppCompatActivity(), TransactionViewModel.TransactionListener {

    lateinit var viewModel: TransactionViewModel
    var transactionParameter: TransactionParameter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityCheckoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout)

        if (intent.hasExtra(TRANSACTION_EXTRA)){
            transactionParameter = getTransactionParameterFromJson(intent.getStringExtra(TRANSACTION_EXTRA))
        }

        Glide.with(this)
                .load(transactionParameter?.user?.photoUrl)
                .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                .into(mUserPhoto)

        binding.viewmodel = transactionParameter?.user?.let { UserViewModel(it) }

        viewModel = ViewModelProviders.of(this).get(TransactionViewModel::class.java)

        llFinishOrder.setOnClickListener({
            if (userToken.text.isNotEmpty()){
                viewModel.buyProducts(transactionParameter, this)
            }else{
                userToken.error = getString(R.string.invalid_token)
            }
        })
    }

    companion object {
        fun start(context: Context, transactionJson: String) {
            val starter = Intent(context, CheckoutActivity::class.java)
            starter.putExtra(TRANSACTION_EXTRA, transactionJson)
            context.startActivity(starter)
        }
    }

    override fun onTransactionSuccess() {
        TransactionFinishedActivity.start(this)
    }

    override fun onTransactionFailure() {

    }
}
