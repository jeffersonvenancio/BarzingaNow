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

class IdentifyRfidActivity : AppCompatActivity(), RfidManager.DataListener, UserManager.DataListener {
    lateinit var viewModelRfid: RfidViewModel
    lateinit var viewModelMain: MainViewModel
    var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_rfid)
        viewModelRfid = ViewModelProviders.of(this).get(RfidViewModel::class.java)
        viewModelMain = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModelRfid.setListener(this)
        viewModelMain.setListener(this)

        viewModelRfid.getRfid()


//        val timer = Timer()
//        timer.schedule(timerTask {
//            MainActivity.start(this@IdentifyRfidActivity)
//            finish()
//        }, 10000)
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, IdentifyRfidActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onRfidSuccess(response: Response<ResponseBody>) {
        logUser(response.body()!!.string())
    }

    override fun onRfidFailure(error: String) {
        MainActivity.start(this@IdentifyRfidActivity)
        finish()
    }

    private fun logUser(rfid : String) {
        viewModelMain.logUserWithRfid(rfid)
    }


    override fun onLogInSuccess(user: User) {
        var intent = Intent(this, ProductsActivity::class.java)
        intent.putExtra("USER_EXTRA", user)
        startActivity(intent)
        finish()
    }

    override fun onLogInFailure() {
        Toast.makeText(this, getString(R.string.login_failure), Toast.LENGTH_SHORT).show()
        val timer = Timer()
        timer.schedule(timerTask {
            MainActivity.start(this@IdentifyRfidActivity)
            finish()
        }, 2000)
    }


}