package com.barzinga.model

/**
 * Created by diego.santos on 03/10/17.
 */
class Product(var category: String? = null,
              val description: String? = null,
              val price: Double? = null,
              var image_url: String? = null,
              var id: String? = null,
              var quantity: Int? = 0,
              var quantityOrdered: Int? = 0)