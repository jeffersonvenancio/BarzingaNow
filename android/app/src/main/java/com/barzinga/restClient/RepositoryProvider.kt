package com.barzinga.restClient

/**
 * Created by diego.santos on 04/10/17.
 */
object RepositoryProvider {

    fun provideProductsRepository(): ProductsRepository {
        return ProductsRepository(BarzingaService.Factory.create())
    }

    fun provideUserRepository(): UserRepository {
        return UserRepository(BarzingaService.Factory.create(), RfidService.Factory.create())
    }
}