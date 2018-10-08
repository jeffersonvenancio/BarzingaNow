package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.barzinga.R
import kotlinx.android.synthetic.main.activity_transaction_finished.*
import java.util.*
import kotlin.concurrent.timerTask

class TransactionFinishedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_finished)

        val money = intent.getStringExtra(MONEY)
        transactionCompleteMessage.text = getString(R.string.transaction_finished, money)

        val timer = Timer()
        timer.schedule(timerTask {

            MainActivity.start(this@TransactionFinishedActivity)
            finish()

        }, 4000)

    }

    companion object {
        private const val MONEY = "MONEY"

        fun start(context: Context, money: String) {
            val starter = Intent(context, TransactionFinishedActivity::class.java)
            starter.putExtra(MONEY, money)
            context.startActivity(starter)
        }
    }
}
