package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.barzinga.R
import com.barzinga.util.launchActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        llStart.setOnClickListener({
            launchActivity<IdentifyRfidActivity>()
        })
        llQRCode.setOnClickListener({
           val integrador =  IntentIntegrator(this)
            integrador.setCameraId(1)
            integrador.initiateScan()
        })
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            val bundle = Bundle()
            bundle.putString("rfid", result.contents)

            if (result.contents != null && !result.contents.isEmpty()) {
                val intent = Intent(this, IdentifyRfidActivity::class.java )
                intent.putExtra("rfid",result.contents)
                startActivity(intent)
            } else {
                startActivity(this.intent)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }
}
