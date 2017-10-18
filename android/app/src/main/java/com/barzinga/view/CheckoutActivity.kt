package com.barzinga.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.barzinga.R
import com.barzinga.restClient.TransactionParameter
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.TransactionViewModel
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : AppCompatActivity() {

    lateinit var viewModel: TransactionViewModel
    lateinit var transactionParameter: TransactionParameter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        viewModel = ViewModelProviders.of(this).get(TransactionViewModel::class.java)

        llFinishOrder.setOnClickListener({
            if (userToken.text.isNotEmpty()){
//                viewModel.buyProducts(transactionParameter)
                TransactionFinishedActivity.start(this)
            }
        })
    }

    companion object {
        fun start(context: Context, userJson: String) {
            val starter = Intent(context, CheckoutActivity::class.java)
            starter.putExtra(Constants.USER_EXTRA, userJson)
            context.startActivity(starter)
        }
    }
}
