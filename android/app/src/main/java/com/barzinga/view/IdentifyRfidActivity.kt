package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.barzinga.R
import com.barzinga.viewmodel.IdentifyRfidViewModel
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timerTask

class IdentifyRfidActivity : AppCompatActivity(), IdentifyRfidViewModel.RfidServiceListener {
    override fun onRequestSuccess(response: Response<ResponseBody>) {
        if(response.code() == 200){
            var rfid = response.body()?.string()
            TODO("obter objeto User a partir da API Barzinga")
        }
    }

    override fun onRequestFailure() {
        TODO("retry policy") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_rfid)

        val timer = Timer()
        timer.schedule(timerTask {
            MainActivity.start(this@IdentifyRfidActivity)
            finish()
        }, 4000)
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, IdentifyRfidActivity::class.java)
            context.startActivity(starter)
        }
    }
}