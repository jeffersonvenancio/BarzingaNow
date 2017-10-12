package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.barzinga.R
import com.barzinga.viewmodel.Constants

class TransactionFinishedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_finished)
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, TransactionFinishedActivity::class.java)
            context.startActivity(starter)
        }
    }
}
