package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.barzinga.R
import com.barzinga.manager.RfidManager
import com.barzinga.viewmodel.MainViewModel
import com.barzinga.viewmodel.RfidViewModel
import okhttp3.ResponseBody
import retrofit2.Response
import android.arch.lifecycle.ViewModelProviders
import android.widget.Toast
import com.barzinga.manager.UserManager
import com.barzinga.model.User
import com.barzinga.viewmodel.UserViewModel
import java.util.*
import kotlin.concurrent.timerTask

class FinishedPurchaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_purchase)


        val timer = Timer()
        timer.schedule(timerTask {
            MainActivity.start(this@FinishedPurchaseActivity)
            finish()
        }, 3000)
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, FinishedPurchaseActivity::class.java)
            context.startActivity(starter)
        }
    }

}