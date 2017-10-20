package com.barzinga.view

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.widget.EditText
import android.widget.Toast
import com.barzinga.R
import com.barzinga.customViews.BarzingaEditText
import com.barzinga.manager.UserManager
import com.barzinga.model.User
import com.barzinga.restClient.RepositoryProvider
import com.barzinga.util.ConvertObjectsUtil
import com.barzinga.viewmodel.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.view.KeyEvent.KEYCODE_BACK
import android.content.DialogInterface
import android.view.KeyEvent


class MainActivity : AppCompatActivity(), UserManager.DataListener {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.setListener(this)
        
        llStart.setOnClickListener({

            disableButton()
            logUser()
        })
    }

    private fun disableButton() {
        btStartTitle.visibility = INVISIBLE
        pbLoading.visibility = VISIBLE
        llStart.isEnabled = false
    }

    private fun logUser() {
        val dialog = Dialog(this, R.style.MyDialogTheme)
        dialog.setContentView(R.layout.dialog_login)

        dialog.findViewById<View>(R.id.rateApp).setOnClickListener {
            if (!(dialog.findViewById<View>(R.id.userEmail) as BarzingaEditText).text.toString().isEmpty()) {
                viewModel.logUser((dialog.findViewById<View>(R.id.userEmail) as BarzingaEditText).text.toString());
                dialog.dismiss()

            } else {
                (dialog.findViewById<View>(R.id.userEmail) as EditText).error = getString(R.string.invalid_user_error)
            }
        }

        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {

            override fun onKey(arg0: DialogInterface, keyCode: Int,
                               event: KeyEvent): Boolean {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    enableButton()

                    dialog.dismiss()
                }
                return true
            }
        })

        dialog.show()
    }

    override fun onLogInSuccess(user: User) {
        OptionsActivity.start(this, ConvertObjectsUtil.getStringFromObject(user))
        finish()
    }

    override fun onLogInFailure() {
        Toast.makeText(this, getString(R.string.login_failure), Toast.LENGTH_SHORT).show();

        enableButton()
    }

    private fun enableButton() {
        btStartTitle.visibility = VISIBLE
        pbLoading.visibility = INVISIBLE

        llStart.isEnabled = true
    }



}
