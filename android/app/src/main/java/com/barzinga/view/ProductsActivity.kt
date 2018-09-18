package com.barzinga.view

import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import com.barzinga.R
import com.barzinga.customViews.BarzingaEditText
import com.barzinga.databinding.ActivityProductsBinding
import com.barzinga.manager.UserManager
import com.barzinga.model.Item
import com.barzinga.model.Product
import com.barzinga.model.User
import com.barzinga.restClient.parameter.TransactionParameter
import com.barzinga.util.ConvertObjectsUtil
import com.barzinga.util.ConvertObjectsUtil.Companion.getStringFromObject
import com.barzinga.util.launchActivity
import com.barzinga.util.loadUrl
import com.barzinga.view.adapter.ProductsAdapter
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.Constants.CHECKOUT_REQUEST
import com.barzinga.viewmodel.MainViewModel
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.dialog_login.*
import kotlinx.android.synthetic.main.view_bottom_bar.*
import kotlinx.android.synthetic.main.view_top_bar.*
import  android.support.v7.widget.SearchView
import android.databinding.adapters.SearchViewBindingAdapter.setOnQueryTextListener
import android.content.Context.SEARCH_SERVICE
import android.R.menu
import android.content.Context
import android.support.v7.widget.Toolbar


class ProductsActivity : AppCompatActivity(), ItemsListFragment.OnItemSelectedListener, ProductListViewModel.ProductsListener,  UserManager.DataListener {

    var user: User? = null

    lateinit var viewModel: ProductListViewModel
    lateinit var viewModelMain: MainViewModel
    var searchView: SearchView? = null
    var rfid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityProductsBinding = DataBindingUtil.setContentView(this, R.layout.activity_products)

        rfid = getIntent().getStringExtra("USER_RFID")
        var mToolbar = findViewById<Toolbar>(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false);

        viewModel = ViewModelProviders.of(this).get(ProductListViewModel::class.java)
        viewModelMain = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModelMain.setListener(this)

        viewModel.listProducts(this)

        getUser(binding)

        llFinishOrder.setOnClickListener({
            logUser(rfid!!)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        getMenuInflater().inflate(R.menu.product_menus, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search)
                .actionView as SearchView
        searchView?.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()))
        searchView?.setMaxWidth(Integer.MAX_VALUE)

        // listening to search query text change
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                (products_list.adapter as ProductsAdapter).getFilter().filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                (products_list.adapter as ProductsAdapter).getFilter().filter(query)
                return false
            }
        })
        return true
    }

    private fun getUser(binding: ActivityProductsBinding) {
        if (intent?.hasExtra(Constants.USER_EXTRA) == true) {
            val userJson = intent.getStringExtra(Constants.USER_EXTRA)
            user = ConvertObjectsUtil.getUserFromJson(userJson)
            mUserPhotoProduct.loadUrl(user?.photoUrl)
            binding.viewmodel = user?.let { UserViewModel(it) }
        }
    }

    private fun openCheckout() {
        var products = (products_list.adapter as ProductsAdapter).getChosenProducts()
        var transactionProducts = ArrayList<Product>()

        var currentProduct: Product? = null

        for (product in products) {

            if (currentProduct == null) {
                currentProduct = product
            }

            if (!currentProduct.description.equals(product.description)) {

                product.quantity = product.quantityOrdered
                currentProduct.let { it1 -> transactionProducts.add(it1) }

                currentProduct = product

            }
        }

        currentProduct?.quantity = currentProduct?.quantityOrdered
        currentProduct?.let { it1 -> transactionProducts.add(it1) }

        val transactionParameter = TransactionParameter(user, "", transactionProducts)
        val transactionJson = getStringFromObject(transactionParameter)

        startActivityForResult(CheckoutActivity.startIntent(this, transactionJson), CHECKOUT_REQUEST)
    }

    private fun logUser(rfid : String) {
        viewModelMain.logUserWithRfid(rfid)
    }

    private fun setupRecyclerView(products: ArrayList<Product>) {

        var productsAdapter = ProductsAdapter(this, products, this)
        products_list.apply {
            adapter = productsAdapter
            setHasFixedSize(true)
            val gridLayout = GridLayoutManager(context, 3)
            layoutManager = gridLayout
        }

        mLoadingProgress.visibility = GONE

        determinePaneLayout(setCategories(products))
    }


    override fun onProductsQuantityChanged() {
        val products = (products_list.adapter as ProductsAdapter).getChosenProducts()
        var currentOrderPrice: Double? = 0.0

        for (product in products.orEmpty()) {
            product.price?.let { currentOrderPrice = currentOrderPrice?.plus(it) }
        }

        if ((currentOrderPrice ?: 0.0) > 0.0) {
            mOrderPrice.text = String.format("%.2f", currentOrderPrice)
            mBottomBar.visibility = VISIBLE
        } else {
            mBottomBar.visibility = GONE
        }
    }

    private fun setCategories(products: List<Product>?): ArrayList<Item> {

        val products = products?.sortedBy { it.category }

        var categories = ArrayList<Item>()
        var categoryName = "";

        for (product in products.orEmpty()) {
            if (categoryName.isEmpty()) {
                categoryName = product.category.toString()
            }

            if (!categoryName.equals(product.category)) {
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
        setupRecyclerView(products)
    }

    override fun onProductsListError() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHECKOUT_REQUEST && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    override fun onLogInSuccess(user: User) {
        openCheckout()
    }

    override fun onLogInFailure() {
        Toast.makeText(this, getString(R.string.login_failure), Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView?.isIconified()!!) {
            searchView?.setIconified(true)
            return
        }
        super.onBackPressed()
    }

}
