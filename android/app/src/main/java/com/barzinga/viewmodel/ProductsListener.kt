package com.barzinga.viewmodel

import com.barzinga.model.Product

/**
 * Created by diego.santos on 05/10/17.
 */
interface ProductsListener{
    fun onProductsListGotten(products: List<Product>?)
    fun onProductsQuantityChanged()
}