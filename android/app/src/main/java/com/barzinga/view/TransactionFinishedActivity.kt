package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.barzinga.R
import com.barzinga.viewmodel.Constants
import java.util.*
import kotlin.concurrent.timerTask

class TransactionFinishedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_finished)

        val timer = Timer()
        timer.schedule(timerTask {

            MainActivity.start(this@TransactionFinishedActivity)
            finish()

        }, 4000)

    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, TransactionFinishedActivity::class.java)
            context.startActivity(starter)
        }
    }
}
