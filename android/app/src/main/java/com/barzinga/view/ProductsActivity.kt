package com.barzinga.view

import android.app.Activity
import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.barzinga.R
import com.barzinga.databinding.ActivityProductsBinding
import com.barzinga.model.Item
import com.barzinga.model.Product
import com.barzinga.model.User
import com.barzinga.restClient.parameter.TransactionParameter
import com.barzinga.util.ConvertObjectsUtil.Companion.getStringFromObject
import com.barzinga.util.loadUrl
import com.barzinga.view.adapter.ProductsAdapter
import com.barzinga.viewmodel.Constants
import com.barzinga.viewmodel.Constants.CHECKOUT_REQUEST
import com.barzinga.viewmodel.ProductListViewModel
import com.barzinga.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.view_bottom_bar.*
import kotlinx.android.synthetic.main.view_user_info.*


class ProductsActivity : AppCompatActivity(), ItemsListFragment.OnItemSelectedListener, ProductListViewModel.ProductsListener {

    private var user: User? = null

    lateinit var viewModel: ProductListViewModel
    lateinit var searchView: SearchView
    lateinit var binding: ActivityProductsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products)

        var mToolbar = findViewById<Toolbar>(R.id.toolbar)
        mToolbar.setBackgroundColor(resources.getColor(R.color.white))
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel = ViewModelProviders.of(this).get(ProductListViewModel::class.java)

        viewModel.listProducts(this)

        getUser()

        llFinishOrder.setOnClickListener({
            openCheckout()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        menuInflater.inflate(R.menu.product_menus, menu)

        setSearch(menu)
        return true
    }

    private fun setSearch(menu: Menu) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search)
                .actionView as SearchView
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(componentName))
        searchView.maxWidth = 5000
        searchView.findViewById<ImageView>(android.support.v7.appcompat.R.id.search_button).setColorFilter(Color.BLACK)
        searchView.findViewById<ImageView>(android.support.v7.appcompat.R.id.search_close_btn).setColorFilter(Color.BLACK)
        searchView.findViewById<View>(android.support.v7.appcompat.R.id.search_plate).background.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)

        val searchAutoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(android.support.v7.appcompat.R.id.search_src_text)
        searchAutoComplete?.setHintTextColor(Color.BLACK)
        searchAutoComplete?.setTextColor(Color.BLACK)
        searchAutoComplete?.setHint(R.string.search_product_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                (products_list.adapter as ProductsAdapter).filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                (products_list.adapter as ProductsAdapter).filter.filter(query)
                return false
            }
        })
    }

    private fun getUser() {
        if (intent?.hasExtra(Constants.USER_EXTRA) == true) {
            user = intent.getSerializableExtra(Constants.USER_EXTRA) as User?
            mUserPhoto.loadUrl(user?.photoUrl)
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

    private fun setupRecyclerView(products: ArrayList<Product>) {

        var productsAdapter = ProductsAdapter(this, products, this)
        products_list.apply {
            adapter = productsAdapter
            setHasFixedSize(true)
            val gridLayout = GridLayoutManager(context, 2)
            layoutManager = gridLayout
        }

        products_list.visibility = VISIBLE
        mLoadingProgress.visibility = GONE

        determinePaneLayout(setCategories(products))
    }


    override fun onProductsQuantityChanged() {
        var currentOrderPrice: Double? = (products_list.adapter as ProductsAdapter).getCurrentOrderPrice()

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
        var categoryName = ""

        for (product in products.orEmpty()) {
            if (categoryName.isEmpty()) {
                categoryName = product.category.toString()
            }

            if (categoryName != product.category) {
                categories.add(Item(categoryName, ""))
                categoryName = product.category.toString()
            }
        }

        categories.add(Item(categoryName, ""))

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
        invalidateOptionsMenu()
        hideKeyboard()
    }

    fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm!!.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onProductsListGotten(products: ArrayList<Product>) {
        setupRecyclerView(products)
    }

    override fun onProductsListError() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHECKOUT_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                finish()
            } else {
                mLoadingProgress.visibility = VISIBLE
                products_list.visibility = GONE
                mBottomBar.visibility = GONE
                viewModel.listProducts(this)
            }
        }
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
        super.onBackPressed()
    }
}
