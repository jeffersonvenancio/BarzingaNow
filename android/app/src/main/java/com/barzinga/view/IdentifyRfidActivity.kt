package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.barzinga.R
import com.barzinga.manager.RfidManager
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.IdentifyRfidViewModel
import com.barzinga.viewmodel.MainViewModel
import com.barzinga.viewmodel.RfidViewModel
import okhttp3.ResponseBody
import retrofit2.Response
import android.arch.lifecycle.ViewModelProviders
import java.util.*
import kotlin.concurrent.timerTask

class IdentifyRfidActivity : AppCompatActivity(), RfidManager.DataListener {
    lateinit var viewModelMain: RfidViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_rfid)
        viewModelMain = ViewModelProviders.of(this).get(RfidViewModel::class.java)

        viewModelMain.setListener(this)
        viewModelMain.getRfid()
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, IdentifyRfidActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onRfidSuccess(response: Response<ResponseBody>) {
        var intent = Intent(this, ProductsActivity::class.java)
        intent.putExtra("USER_RFID", response.body()!!.string())
        startActivity(intent)
        finish()
    }

    override fun onRfidFailure(error: String) {
        MainActivity.start(this@IdentifyRfidActivity)
        finish()
    }

}