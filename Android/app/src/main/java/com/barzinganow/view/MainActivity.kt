package com.barzinganow.view

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.barzinganow.R
import com.barzinganow.customViews.BarzingaEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_options.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        llStart.setOnClickListener({
            logUser()
        })
    }

    private fun logUser() {
        val dialog = Dialog(this, R.style.MyDialogTheme)
        dialog.setContentView(R.layout.dialog_login)

        dialog.findViewById<View>(R.id.rateApp).setOnClickListener {
            if (!(dialog.findViewById<View>(R.id.userEmail) as BarzingaEditText).text.toString().isEmpty()) {
                //                    mainViewModel.logUser();
                OptionsActivity.start(this)
                dialog.dismiss()
            } else {
                (dialog.findViewById<View>(R.id.userEmail) as EditText).error = getString(R.string.invalid_user_error)
            }
        }

        dialog.show()
    }
}
