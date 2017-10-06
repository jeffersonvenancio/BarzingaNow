package com.barzinga.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View.GONE
import android.widget.Toast
import com.barzinga.R
import com.barzinga.model.Product
import com.barzinga.view.adapter.ProductsAdapter
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.ProductsListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.view_top_bar.*


class ProductsActivity : AppCompatActivity(), ProductsListener {

    override fun onProductsListGotten(products: List<Product>?) {
       if (products != null){
           setupRecyclerView(products)
       }
    }

    lateinit var viewModel: ProductListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        viewModel = ViewModelProviders.of(this).get(ProductListViewModel::class.java)

        viewModel.listProducts(this)

        Glide.with(this)
                .load("http://s2.glbimg.com/TbCBsuYDmw_um_YM8tBbq0G8RFA=/200x200/s.glbimg.com/et/gs/f/original/2015/07/30/camila-queiroz-verdades-secretas.jpg")
                .apply(bitmapTransform(CropCircleTransformation()))
                .into(mUserPhoto)
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, ProductsActivity::class.java)
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
