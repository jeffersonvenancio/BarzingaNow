package com.barzinga.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.barzinga.R
import kotlinx.android.synthetic.development.activity_main.*


class MainActivity : AppCompatActivity() {
    private val RFID_KEY :String = "RFID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var rfid: String = loadRFID() ?: ""

        rfidEditText.setText(rfid)

        loginButton.setOnClickListener {
            rfid = rfidEditText.text.toString()
            saveRFID(rfid)
            login(rfid)
        }
    }

    private fun login(rfid: String) {
        if (rfid.isEmpty()) {
            rfidEditText.error = getString(R.string.field_required)
            return
        }

        val intent = Intent(this, IdentifyRfidActivity::class.java )
        intent.putExtra("rfid", rfid)
        startActivity(intent)
    }

    private fun saveRFID(rfid: String) {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        defaultSharedPreferences.edit().apply {
            if (saveRFIDCheck.isChecked) putString(RFID_KEY, rfid)
            else remove(RFID_KEY)

            apply()
        }
    }

    private fun loadRFID(): String? {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return defaultSharedPreferences.getString(RFID_KEY, null)
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }
}
