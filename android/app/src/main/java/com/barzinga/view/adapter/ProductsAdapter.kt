package com.barzinga.view.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import com.barzinga.R
import com.barzinga.databinding.ItemProductBinding

import com.barzinga.model.Product
import com.barzinga.viewmodel.ProductViewModel
import com.barzinga.viewmodel.ProductsListener
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList

/**
 * Created by diego.santos on 05/10/17.
 */
class ProductsAdapter(val context: Context, var products: ArrayList<Product>, val listener: ProductsListener) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>(){

    private var itemClick: ((Product) -> Unit)? = null
    companion object {
        var mProducts = ArrayList<Product>()
        var mListener: ProductsListener? = null
    }

    init {
        mProducts.addAll(products)
        mListener = listener
    }

    override fun getItemCount(): Int = mProducts?.size ?: 0

    override fun onBindViewHolder(holder: ProductViewHolder?, position: Int) {
        val binding = holder?.binding
        val product = mProducts?.get(position)

        var viewModel = product?.let { ProductViewModel(it) }
        binding?.viewModel = viewModel

        holder?.setClickListener(itemClick)

        Glide.with(context).load(binding?.viewModel?.product?.image_url).into(binding?.mProductImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProductViewHolder {
        val binding = DataBindingUtil.inflate<ItemProductBinding>(
                LayoutInflater.from(parent?.context),
                R.layout.item_product,
                parent,
                false
        )

        return ProductViewHolder(binding)
    }

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        var animation1 = AlphaAnimation(0.2f, 1.0f)
        var animation2 = AlphaAnimation(0.2f, 1.0f)

        fun setClickListener(callback: ((Product) -> Unit)?){
            binding.viewModel.clicks().subscribe {
                callback?.invoke(binding.viewModel.product)
            }
        }

        init {

            animation1.setDuration(1500)
            animation1.setFillAfter(true)

            animation2.setDuration(1500)
            animation2.setFillAfter(true)

            binding.decreaseQtde.setOnClickListener({

                binding.decreaseQtde.startAnimation(animation1)

                var i = Integer.parseInt(binding.mQtde.getText().toString())

                if (i > 0) {
                    i--
                    binding.mQtde.setText(i.toString())
                    mProducts?.get(position)?.quantityOrdered = i

                    mListener?.onProductsQuantityChanged()
                }
            })

            binding.increaseQtde.setOnClickListener({

                binding.increaseQtde.startAnimation(animation2)

                var i = Integer.parseInt(binding.mQtde.getText().toString())

                i++
                binding.mQtde.setText(i.toString())
                mProducts?.get(position)?.quantityOrdered = i

                mListener?.onProductsQuantityChanged()
            })
        }
    }

    fun setClickListener(itemClick: ((Product) -> Unit)?) {
        this.itemClick = itemClick
    }

    fun getChosenProducts(): List<Product> {
        val extraProducts = ArrayList<Product>()

        for (product in products.orEmpty()) {
            if ((product.quantityOrdered ?: 0) > 0) {
                for (i in 0 until (product.quantityOrdered ?: 0)) {
                    extraProducts.add(product)
                }
            }
        }

        return extraProducts
    }

    fun setCategory(category: String){
        mProducts.clear()

        for (product in products){
            if (product.category?.toUpperCase().equals(category.toUpperCase())){
                mProducts.add(product)
            }
        }

        notifyDataSetChanged()
    }
}