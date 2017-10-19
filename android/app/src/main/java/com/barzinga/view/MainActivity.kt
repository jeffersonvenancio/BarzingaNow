package com.barzinga.view

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.barzinga.R
import com.barzinga.customViews.BarzingaEditText
import com.barzinga.model.User
import com.barzinga.restClient.RepositoryProvider
import com.barzinga.util.ConvertObjectsUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

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
                dialog.dismiss()

                getProfile((dialog.findViewById<View>(R.id.userEmail) as BarzingaEditText).text.toString())
            } else {
                (dialog.findViewById<View>(R.id.userEmail) as EditText).error = getString(R.string.invalid_user_error)
            }
        }

        dialog.show()
    }

    fun getProfile(user: String) {

        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        val repository = RepositoryProvider.provideUserRepository()

        compositeDisposable.add(
                repository.getProfile(user)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe ({
                            result ->
                            onUserRequestSuccess(result)
                        }, { error ->
                            error.printStackTrace()
                            onUserRequestFailure()
                        })
        )

    }

    private fun onUserRequestFailure() {
        Toast.makeText(this, "Falha no login! Por favor tente novamente.", Toast.LENGTH_SHORT).show();
    }

    private fun onUserRequestSuccess(result: User?) {
        if (result != null){
            OptionsActivity.start(this, ConvertObjectsUtil.getStringFromObject(result))
        }
    }
}
