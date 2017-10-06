package com.barzinga.restClient

/**
 * Created by diego.santos on 04/10/17.
 */
object ProductsRepositoryProvider {

    fun provideSearchRepository(): ProductsRepository {
        return ProductsRepository(BarzingaService.Factory.create())
    }

}