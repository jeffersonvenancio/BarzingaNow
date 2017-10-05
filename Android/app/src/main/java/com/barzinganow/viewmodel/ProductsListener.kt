package com.barzinganow.viewmodel

import com.barzinganow.model.Product

/**
 * Created by diego.santos on 05/10/17.
 */
interface ProductsListener{
    fun onProductsListGotten(products: List<Product>?)
}