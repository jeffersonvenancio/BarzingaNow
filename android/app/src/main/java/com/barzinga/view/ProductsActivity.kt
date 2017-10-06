package com.barzinga.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View.GONE
import android.widget.Toast
import com.barzinga.R
import com.barzinga.databinding.ActivityOptionsBinding
import com.barzinga.databinding.ActivityProductsBinding
import com.barzinga.model.Product
import com.barzinga.model.User
import com.barzinga.util.ConvertObjectsUtil
import com.barzinga.view.adapter.ProductsAdapter
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.ProductsListener
import com.barzinga.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.view_top_bar.*


class ProductsActivity : AppCompatActivity(), ProductsListener {

    var user: User? = null

    override fun onProductsListGotten(products: List<Product>?) {
       if (products != null){
           setupRecyclerView(products)
       }
    }

    lateinit var viewModel: ProductListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityProductsBinding = DataBindingUtil.setContentView(this, R.layout.activity_products)

        viewModel = ViewModelProviders.of(this).get(ProductListViewModel::class.java)

        viewModel.listProducts(this)

        if (intent?.hasExtra(Constants.USER_EXTRA) == true){
            val userJson = intent.getStringExtra(Constants.USER_EXTRA)

            user = ConvertObjectsUtil.getUserFromJson(userJson)

            Glide.with(this)
                    .load(user?.photoUrl)
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(mUserPhoto)

            binding.viewmodel = user?.let { UserViewModel(it) }
        }
    }

    companion object {
        fun start(context: Context, userJson: String) {
            val starter = Intent(context, ProductsActivity::class.java)
            starter.putExtra(Constants.USER_EXTRA, userJson)
            context.startActivity(starter)
        }
    }

    private fun setupRecyclerView(products: List<Product>?) {

        var productsAdapter = ProductsAdapter(this, products)
        productsAdapter.setClickListener {
            onItemClick(it)
        }

        products_list.apply {
            adapter = productsAdapter
            setHasFixedSize(true)
            val linearLayout = GridLayoutManager(context, 3)
            layoutManager = linearLayout
        }

        mLoadingProgress.visibility = GONE
    }

    private fun onItemClick(product: Product) {
        Toast.makeText(this, "Product " + product.description, Toast.LENGTH_SHORT).show()
    }
}
