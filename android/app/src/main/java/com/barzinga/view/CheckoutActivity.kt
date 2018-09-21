package com.barzinga.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.view.View.VISIBLE

import com.barzinga.R
import com.barzinga.databinding.ActivityCheckoutBinding
import com.barzinga.model.Product
import com.barzinga.restClient.parameter.TransactionParameter
import com.barzinga.util.ConvertObjectsUtil.Companion.getTransactionParameterFromJson
import com.barzinga.view.adapter.ProductCartAdapter
import com.barzinga.view.adapter.ProductsAdapter
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.Constants.TRANSACTION_EXTRA
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.TransactionViewModel
import com.barzinga.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.view_top_bar.*
import okhttp3.ResponseBody
import retrofit2.Response
import android.app.AlertDialog
import android.content.DialogInterface
import java.util.*
import kotlin.concurrent.timerTask


class CheckoutActivity : AppCompatActivity(), TransactionViewModel.TransactionListener, ProductListViewModel.ProductsListener {
    lateinit var binding: ActivityCheckoutBinding
    lateinit var viewModel: TransactionViewModel
    var transactionParameter: TransactionParameter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout)

        if (intent.hasExtra(TRANSACTION_EXTRA)){
            transactionParameter = getTransactionParameterFromJson(intent.getStringExtra(TRANSACTION_EXTRA))
        }

        Glide.with(this)
                .load(transactionParameter?.user?.photoUrl)
                .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                .into(mUserPhoto)

        binding.viewmodel = transactionParameter?.user?.let { UserViewModel(it) }

        transactionParameter?.products?.let {
            setupRecyclerView(it)
        }

        viewModel = ViewModelProviders.of(this).get(TransactionViewModel::class.java)

        updatePrice()

        llFinishOrder.setOnClickListener({
            disableButton()
            disableList()
            transactionParameter?.pin = "Token Diego"
            transactionParameter?.products = (cartProducts.adapter as ProductCartAdapter).getChosenProducts()
            viewModel.buyProducts(transactionParameter, this)
        })
    }

    private fun updatePrice() {
        val price = (binding.cartProducts.adapter as ProductCartAdapter).getCurrentOrderPrice()

        if (price != null && price > 0.0) {
            binding.mOrderPrice.text = String.format("%.2f", price)
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    companion object {
        fun start(context: Context, transactionJson: String) {
            val starter = Intent(context, CheckoutActivity::class.java)
            starter.putExtra(TRANSACTION_EXTRA, transactionJson)
            context.startActivity(starter)
        }

        fun startIntent(context: Context, transactionJson: String) : Intent{
            val starter = Intent(context, CheckoutActivity::class.java)
            starter.putExtra(TRANSACTION_EXTRA, transactionJson)
            return starter
        }
    }

    override fun onTransactionSuccess(response: Response<ResponseBody>) {
        if (response.code() == 200){
            var response = response.body()?.string()
            TransactionFinishedActivity.start(this)
            enableButton()
            setResult(Activity.RESULT_OK)
            FinishedPurchaseActivity.start(this@CheckoutActivity)
            finish()
        }else{
            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage("Bazinga! Algo deu errado!") //Só quero dizer que barzinga é um nome muito ruim, e TBBT é um show ruim também
            builder1.setCancelable(true)

            val alert11 = builder1.create()
            alert11.show()

            val timer = Timer()
            timer.schedule(timerTask {
                MainActivity.start(this@CheckoutActivity)
                finish()
            }, 3000)
        }
    }

    override fun onTransactionFailure() {
        enableButton()
    }

    private fun disableButton() {
        btFinishOrder.visibility = View.INVISIBLE
        pbLoading.visibility = View.VISIBLE
        llFinishOrder.isEnabled = false
    }

    private fun disableList() {
        blockListView.visibility = VISIBLE
    }

    private fun enableButton() {
        btFinishOrder.visibility = View.VISIBLE
        pbLoading.visibility = View.INVISIBLE

        llFinishOrder.isEnabled = true
    }

    private fun setupRecyclerView(products: List<Product>) {

        var productsAdapter = ProductCartAdapter(this, products as ArrayList<Product>, this)
        cartProducts.apply {
            adapter = productsAdapter
            setHasFixedSize(true)
        }

        pbLoading.visibility = View.GONE
    }

    override fun onProductsListGotten(products: ArrayList<Product>) {

    }

    override fun onProductsListError() {

    }

    override fun onProductsQuantityChanged() {
        updatePrice()
    }
}
