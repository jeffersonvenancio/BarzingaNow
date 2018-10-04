package com.barzinga.view.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.generated.callback.OnClickListener
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import com.barzinga.R
import com.barzinga.databinding.ItemCartProductBinding
import com.barzinga.databinding.ItemProductBinding
import com.barzinga.model.Product
import com.barzinga.view.adapter.ProductCartAdapter.ProductViewHolder
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.ProductViewModel
import com.bumptech.glide.Glide
import kotlin.coroutines.experimental.CoroutineContext

class ProductCartAdapter(val context: Context, var products: ArrayList<Product>, val listener: ProductListViewModel.ProductsListener): RecyclerView.Adapter<ProductViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProductViewHolder {
        val binding = DataBindingUtil.inflate<ItemCartProductBinding>(
                LayoutInflater.from(parent?.context),
                R.layout.item_cart_product,
                parent,
                false
        )

        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = mProducts.size

    override fun onBindViewHolder(holder: ProductViewHolder?, position: Int) {
        val binding = holder?.binding
        val product = mProducts?.get(position)

        var viewModel = product?.let { ProductViewModel(it) }
        binding?.viewModel = viewModel

        Glide.with(context).load(binding?.viewModel?.product?.image_url).into(binding?.mProductImage)
    }

    companion object {
        var mProducts = ArrayList<Product>()
        var mListener: ProductListViewModel.ProductsListener? = null
    }

    init {
        mProducts.clear()
        mProducts.addAll(products)
        mListener = listener
    }

    inner class ProductViewHolder(val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root) {
        var animation1 = AlphaAnimation(0.2f, 1.0f)

        init {
            binding.deleteButton.setOnClickListener({
                binding.deleteButton.startAnimation(animation1)
                mProducts.removeAt(position)
                updateList(position)
                mListener?.onProductsQuantityChanged()
            })
        }
    }

    fun updateList(position: Int) {
        notifyItemRemoved(position)
    }

    fun getChosenProducts(): List<Product> {
        val extraProducts = ArrayList<Product>()

        for (product in mProducts.orEmpty()) {
            if ((product.quantityOrdered ?: 0) > 0) {
                for (i in 0 until (product.quantityOrdered ?: 0)) {
                    extraProducts.add(product)
                }
            }
        }

        return extraProducts
    }

    fun getCurrentOrderPrice(): Double {
        val products = getChosenProducts()
        var currentOrderPrice: Double = 0.0

        for (product in products.orEmpty()) {
            product.price?.let { currentOrderPrice = currentOrderPrice?.plus(it) }
        }
        return currentOrderPrice
    }
}