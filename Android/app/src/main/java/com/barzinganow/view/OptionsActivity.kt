package com.barzinganow.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.barzinganow.R
import kotlinx.android.synthetic.main.activity_options.*

class OptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        llBuyButton.setOnClickListener({
            ProductsActivity.start(this)
        })
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, OptionsActivity::class.java)
            context.startActivity(starter)
        }
    }
}
