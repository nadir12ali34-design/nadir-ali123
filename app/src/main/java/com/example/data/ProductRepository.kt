package com.example.data

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    fun getProductsByCategory(category: String): Flow<List<Product>> {
        return productDao.getProductsByCategory(category)
    }

    fun searchProducts(query: String): Flow<List<Product>> {
        if (query.isBlank()) return productDao.getAllProducts()
        return productDao.searchProducts("%$query%")
    }

    suspend fun insert(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun update(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun updateStatus(productId: Int, status: String) {
        productDao.updateProductStatus(productId, status)
    }

    suspend fun delete(product: Product) {
        productDao.deleteProduct(product)
    }
}
