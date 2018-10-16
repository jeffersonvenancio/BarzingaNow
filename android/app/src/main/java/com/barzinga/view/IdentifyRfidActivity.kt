package com.barzinga.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.barzinga.R
import com.barzinga.manager.RfidManager
import com.barzinga.manager.UserManager
import com.barzinga.model.User
import com.barzinga.viewmodel.Constants.USER_EXTRA
import com.barzinga.viewmodel.MainViewModel
import com.barzinga.viewmodel.RfidViewModel
import okhttp3.ResponseBody
import retrofit2.Response
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
        val str = intent?.extras?.getString("rfid")
            if (str != null) {
                logUser(str)
                return
        }

       viewModelRfid.getRfid()
//logUser("0006915991")
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
        finish()
    }

    private fun logUser(rfid : String) {
        viewModelMain.logUserWithRfid(rfid)
    }


    override fun onLogInSuccess(user: User) {
        var intent = Intent(this, ProductsActivity::class.java)
        intent.putExtra(USER_EXTRA, user)
        startActivity(intent)
        finish()
    }

    override fun onLogInFailure() {
        Toast.makeText(this, getString(R.string.login_failure), Toast.LENGTH_SHORT).show()
        val timer = Timer()
        timer.schedule(timerTask {
            finish()
        }, 2000)
    }
}