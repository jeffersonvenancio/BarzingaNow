package com.barzinga.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import com.barzinga.R
import com.barzinga.databinding.ActivityProductsBinding
import com.barzinga.model.Item
import com.barzinga.model.Product
import com.barzinga.model.User
import com.barzinga.restClient.parameter.TransactionParameter
import com.barzinga.util.ConvertObjectsUtil
import com.barzinga.util.ConvertObjectsUtil.Companion.getStringFromObject
import com.barzinga.view.adapter.ProductsAdapter
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.Constants.CHECKOUT_REQUEST
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.view_bottom_bar.*
import kotlinx.android.synthetic.main.view_top_bar.*


class ProductsActivity : AppCompatActivity(), ItemsListFragment.OnItemSelectedListener, ProductListViewModel.ProductsListener {

    var user: User? = null

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

        llFinishOrder.setOnClickListener({
            var products = (products_list.adapter as ProductsAdapter).getChosenProducts()
            var transactionProducts = ArrayList<Product>()

            var currentProduct: Product? = null

            for (product in products.orEmpty()){

                if (currentProduct == null){
                    currentProduct = product
                }

                if (!currentProduct.description.equals(product.description)){

                    product.quantity = product.quantityOrdered
                    currentProduct.let { it1 -> transactionProducts.add(it1) }

                    currentProduct = product

                }
            }

            currentProduct?.quantity = currentProduct?.quantityOrdered
            currentProduct?.let { it1 -> transactionProducts.add(it1) }

            val transactionParameter = TransactionParameter(user, "", transactionProducts)
            var transactionJson = getStringFromObject(transactionParameter)

            startActivityForResult(CheckoutActivity.startIntent(this, transactionJson), CHECKOUT_REQUEST)
        })
    }

    companion object {
        fun start(context: Context, userJson: String) {
            val starter = Intent(context, ProductsActivity::class.java)
            starter.putExtra(Constants.USER_EXTRA, userJson)
            context.startActivity(starter)
        }
    }

    private fun setupRecyclerView(products: ArrayList<Product>) {

        var productsAdapter = ProductsAdapter(this, products, this)
        productsAdapter.setClickListener {
            onItemClick(it)
        }

        products_list.apply {
            adapter = productsAdapter
            setHasFixedSize(true)
            val gridLayout = GridLayoutManager(context, 3)
            layoutManager = gridLayout
        }

        mLoadingProgress.visibility = GONE

        determinePaneLayout(setCategories(products))
    }

    private fun onItemClick(product: Product) {
//        Toast.makeText(this, "Product " + product.description, Toast.LENGTH_SHORT).show()
    }

    override fun onProductsQuantityChanged() {
        val products = (products_list.adapter as ProductsAdapter).getChosenProducts()
        var currentOrderPrice: Double? = 0.0

        for (product in products.orEmpty()){
            product.price?.let { currentOrderPrice = currentOrderPrice?.plus(it) }
        }

        if((currentOrderPrice ?: 0.0) > 0.0){
            mOrderPrice.text = String.format("%.2f", currentOrderPrice)
            mBottomBar.visibility = VISIBLE
        }else{
            mBottomBar.visibility = GONE
        }
    }

    private fun setCategories(products: List<Product>?) : ArrayList<Item>{

        val products = products?.sortedBy { it.category }

        var categories = ArrayList<Item>()
        var categoryName = "";

        for (product in products.orEmpty()){
            if (categoryName.isEmpty()){
                categoryName = product.category.toString()
            }

            if (!categoryName.equals(product.category)){
                categories.add(Item(categoryName, ""))
                categoryName = product.category.toString()
            }
        }

        return categories
    }

    private fun determinePaneLayout(items: ArrayList<Item>) {

        val fragmentItem = ItemsListFragment.newInstance(items)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentItemsList, fragmentItem)
        ft.commit()

        (products_list.adapter as ProductsAdapter).setCategory(items.get(0).title)
    }

    override fun onItemSelected(item: Item) {
        (products_list.adapter as ProductsAdapter).setCategory(item.title)
        products_list.scrollToPosition(0)
    }

    override fun onProductsListGotten(products: ArrayList<Product>) {
        if (products != null){
            setupRecyclerView(products)
        }
    }

    override fun onProductsListError() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHECKOUT_REQUEST && resultCode == Activity.RESULT_OK){
            finish()
        }
    }
}
