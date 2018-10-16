package com.barzinga.view.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Filter
import android.widget.Filterable
import com.barzinga.R
import com.barzinga.databinding.ItemProductBinding
import com.barzinga.model.Product
import com.barzinga.view.ProductsActivity
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.ProductViewModel
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

/**
 * Created by diego.santos on 05/10/17.
 */
class ProductsAdapter(val context: Context, var products: ArrayList<Product>, val listener: ProductListViewModel.ProductsListener) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>(), Filterable{
    override fun getFilter(): Filter {
        return ProductFilter(this)
    }

    private var itemClick: ((Product) -> Unit)? = null

    companion object {
        var mProducts = ArrayList<Product>()
        var mListener: ProductListViewModel.ProductsListener? = null
        var mProductsFiltered = ArrayList<Product>()
        val allProducts = ArrayList<Product>()
        var currentCategory = ""
    }

    init {
        mProducts.addAll(products)
        mListener = listener
        allProducts.clear()
        allProducts.addAll(products)
    }

    @Suppress("UNCHECKED_CAST")
    class ProductFilter(private val productsAdapter: ProductsAdapter) : Filter(){
        override fun performFiltering(p0: CharSequence?): FilterResults {
            if (p0.isNullOrEmpty()){
                resetCurrentCategoryList()
            } else {
                val filteredList = ArrayList<Product>()
                for (product in allProducts) {
                    if (product.description!!.toLowerCase().contains(p0.toString().toLowerCase())) {
                        filteredList.add(product)
                    }
                    mProductsFiltered = filteredList
                }
            }
            val filterResults = FilterResults()
            filterResults.values = mProductsFiltered
            return filterResults
        }

        private fun resetCurrentCategoryList() {
            mProducts.clear()

            for (product in allProducts){
                if (product.category?.toUpperCase().equals(currentCategory)){
                    mProducts.add(product)
                }
            }
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            mProducts = p1?.values as ArrayList<Product>
            productsAdapter.notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int = mProducts.size

    override fun onBindViewHolder(holder: ProductViewHolder?, position: Int) {
        val binding = holder?.binding
        val product = mProducts[position]

        val viewModel = product.let { ProductViewModel(it) }
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
            binding.viewModel!!.clicks().subscribe {
                callback?.invoke(binding.viewModel!!.product)
            }
        }

        init {
            animation2.duration = 500
            animation2.fillAfter = true

            binding.increaseQtde.setOnClickListener({

                binding.increaseQtde.isEnabled = false

                val timer = Timer()
                timer.schedule(timerTask {

                    val context = binding.root.context
                    if (context is ProductsActivity) {
                        context.runOnUiThread({
                            binding.increaseQtde.isEnabled = true
                        })
                    }
                }, 500)

                binding.increaseQtde.startAnimation(animation2)

                mProducts[position].quantityOrdered = mProducts[position].quantityOrdered?.plus(1)

                mListener?.onProductsQuantityChanged()
            })
        }
    }

    fun getChosenProducts(): List<Product> {
        val extraProducts = ArrayList<Product>()

        for (product in products) {
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
                currentCategory = product.category.toString()
            }
        }

        notifyDataSetChanged()
    }

    fun getCurrentOrderPrice(): Double? {
        val products = getChosenProducts()
        var currentOrderPrice: Double? = 0.0

        for (product in products) {
            product.price?.let { currentOrderPrice = currentOrderPrice?.plus(it) }
        }
        return currentOrderPrice
    }
}