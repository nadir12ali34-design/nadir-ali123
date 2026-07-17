package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Product
import com.example.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Combined stream of products sorted reactively based on search query and selected category
    val products: StateFlow<List<Product>> = combine(
        repository.allProducts,
        _searchQuery,
        _selectedCategory
    ) { allProducts, query, category ->
        var filtered = allProducts
        if (category != "All") {
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            try {
                val currentList = repository.allProducts.first()
                if (currentList.isEmpty()) {
                    seedInitialProducts()
                }
            } catch (e: Exception) {
                // If first() throws or has issues, seed anyway
                seedInitialProducts()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addProduct(
        title: String,
        description: String,
        price: Double,
        category: String,
        condition: String,
        contactInfo: String
    ) {
        viewModelScope.launch {
            val newProduct = Product(
                title = title,
                description = description,
                price = price,
                category = category,
                condition = condition,
                contactInfo = contactInfo
            )
            repository.insert(newProduct)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.update(product)
        }
    }

    fun markAsSold(productId: Int) {
        viewModelScope.launch {
            repository.updateStatus(productId, "Sold")
        }
    }

    fun markAsActive(productId: Int) {
        viewModelScope.launch {
            repository.updateStatus(productId, "Active")
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }

    private suspend fun seedInitialProducts() {
        val presetProducts = listOf(
            Product(
                title = "iPhone 13 Pro 128GB",
                description = "Sierra Blue color, unlocked to all networks. Battery health is at 88%. Always used with a glass screen protector and high-quality case. Includes original box and unused USB-C to Lightning charging cable.",
                price = 549.0,
                category = "Tech",
                condition = "Like New",
                contactInfo = "techseller@example.com"
            ),
            Product(
                title = "Vintage Leather Bomber Jacket",
                description = "Genuine vintage brown leather bomber jacket. Size Men's L. Extremely warm with quilted inner lining. Heavyweight brass zippers. Excellent distressed patina look with no rips or stains.",
                price = 115.0,
                category = "Fashion",
                condition = "Good",
                contactInfo = "clotheshub@example.com"
            ),
            Product(
                title = "Ergonomic Office Chair",
                description = "Premium ergonomic desk chair with mesh back, high lumbar support, 3D adjustable armrests, and headrest. Soft polyurethane wheels that won't scratch hardwood floors. Pristine condition.",
                price = 140.0,
                category = "Home",
                condition = "Like New",
                contactInfo = "furnitureguy@example.com"
            ),
            Product(
                title = "Classic Sci-Fi Trilogy Set",
                description = "A collector's paperback box set of the foundational classic Space Opera series. Brand new, unread condition. Includes slipcase and custom bookmark.",
                price = 28.0,
                category = "Books",
                condition = "New",
                contactInfo = "bookworm@example.com"
            ),
            Product(
                title = "All-Terrain Mountain Bike",
                description = "24-speed trail-ready mountain bike with a lightweight aluminum frame. Features front mechanical suspension fork, rapid-fire shifters, and disc brakes. Freshly tuned and chain lubed.",
                price = 290.0,
                category = "Sports",
                condition = "Good",
                contactInfo = "bikefanatic@example.com"
            )
        )
        for (prod in presetProducts) {
            repository.insert(prod)
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
