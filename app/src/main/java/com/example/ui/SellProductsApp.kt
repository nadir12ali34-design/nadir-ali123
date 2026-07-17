package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.Product
import java.util.Locale

@Composable
fun SellProductsApp(viewModel: ProductViewModel) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .navigationBarsPaddingForFab()
                    .testTag("add_product_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Sell an Item")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sell", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Title Bar
            HeaderSection()

            // Search Bar Component
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) }
            )

            // Horizontal Category Filters
            CategoryFilterSection(
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.updateSelectedCategory(it) }
            )

            // Product List Grid
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("products_list"),
                contentPadding = PaddingValues(bottom = 88.dp) // Generous padding to clear the FAB comfortably
            ) {
                // Feature Hero Banner at the top of the feed (shown only when category is All and query is empty)
                if (selectedCategory == "All" && searchQuery.isBlank()) {
                    item {
                        HeroBannerSection()
                    }
                }

                if (products.isEmpty()) {
                    item {
                        EmptyStateSection(searchQuery, selectedCategory)
                    }
                } else {
                    items(products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onCardClick = { selectedProductForDetail = product },
                            onMarkSoldClick = { viewModel.markAsSold(product.id) },
                            onMarkActiveClick = { viewModel.markAsActive(product.id) },
                            onEditClick = { productToEdit = product },
                            onDeleteClick = { viewModel.deleteProduct(product) }
                        )
                    }
                }
            }
        }
    }

    // Modal dialogs for CRUD operations
    if (showAddDialog) {
        AddEditProductDialog(
            product = null,
            onDismiss = { showAddDialog = false },
            onSubmit = { title, desc, price, cat, cond, contact ->
                viewModel.addProduct(title, desc, price, cat, cond, contact)
                showAddDialog = false
                Toast.makeText(context, "Listing added successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    productToEdit?.let { product ->
        AddEditProductDialog(
            product = product,
            onDismiss = { productToEdit = null },
            onSubmit = { title, desc, price, cat, cond, contact ->
                viewModel.updateProduct(
                    product.copy(
                        title = title,
                        description = desc,
                        price = price,
                        category = cat,
                        condition = cond,
                        contactInfo = contact
                    )
                )
                productToEdit = null
                Toast.makeText(context, "Listing updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    selectedProductForDetail?.let { product ->
        ProductDetailDialog(
            product = product,
            onDismiss = { selectedProductForDetail = null },
            onMarkSold = {
                viewModel.markAsSold(product.id)
                selectedProductForDetail = null
                Toast.makeText(context, "Item marked as Sold!", Toast.LENGTH_SHORT).show()
            },
            onEdit = {
                productToEdit = product
                selectedProductForDetail = null
            },
            onDelete = {
                viewModel.deleteProduct(product)
                selectedProductForDetail = null
                Toast.makeText(context, "Listing deleted.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// Extension function to add padding to the FAB depending on system navigation bars to prevent overlap
@Composable
fun Modifier.navigationBarsPaddingForFab(): Modifier {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return this.padding(bottom = if (navBarPadding > 0.dp) 0.dp else 16.dp, end = 16.dp)
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Sell Products",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Local Seller Marketplace",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { /* Profile or general action */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = "Shopping Bag icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search products, tech, clothes...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("search_field")
        )
    }
}

@Composable
fun CategoryFilterSection(selectedCategory: String, onCategorySelect: (String) -> Unit) {
    val categories = listOf("All", "Tech", "Fashion", "Home", "Books", "Sports", "Other")
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category
            val categoryIcon = getCategoryIcon(category)

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelect(category) },
                label = {
                    Text(
                        text = category,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = "$category icon",
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent,
                    borderWidth = 0.dp
                ),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("category_chip_$category")
            )
        }
    }
}

@Composable
fun HeroBannerSection() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.marketplace_hero),
                contentDescription = "Marketplace Header Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Scrim overlay to make text highly readable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = "Promo tag",
                        tint = Color(0xFF80DEEA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "READY TO SELL?",
                        color = Color(0xFF80DEEA),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Turn Clutter into Cash",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Post your unused electronics, fashion, or books locally in seconds.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EmptyStateSection(query: String, category: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 24.dp)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "No products found icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = if (query.isNotBlank()) "No Matching Listings" else "No Listings in $category",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (query.isNotBlank()) {
                "We couldn't find any listings matching \"$query\". Try adjusting your terms or filter."
            } else {
                "Be the first to post a product in the \"$category\" category! Tap the Sell button below."
            },
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    onCardClick: () -> Unit,
    onMarkSoldClick: () -> Unit,
    onMarkActiveClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isSold = product.status == "Sold"
    val categoryColors = getCategoryColors(product.category)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable(onClick = onCardClick)
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSold) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSold) 1.dp else 4.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColors.first)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(product.category),
                            contentDescription = "Category icon",
                            tint = categoryColors.second,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = product.category,
                            color = categoryColors.second,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    // Condition Badge
                    Text(
                        text = product.condition,
                        color = getConditionColor(product.condition),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(getConditionColor(product.condition).copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title and Price Block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isSold) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (isSold) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", product.price)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = if (isSold) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Truncated Description
                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isSold) 0.5f else 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Divider line
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Contact Info Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Contact Info Email",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = product.contactInfo,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Action Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSold) {
                            OutlinedButton(
                                onClick = onMarkActiveClick,
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("mark_active_btn_${product.id}"),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = "Activate listing",
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Relist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = onMarkSoldClick,
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("mark_sold_btn_${product.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Mark as Sold",
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mark Sold", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("edit_btn_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit product info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_btn_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete product listing",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Elegant overlay ribbon if product is sold
            if (isSold) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFBA1A1A)) // M3 Red Error color
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SOLD",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, price: Double, category: String, condition: String, contactInfo: String) -> Unit
) {
    val isEditing = product != null

    var title by remember { mutableStateOf(product?.title ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var priceStr by remember { mutableStateOf(product?.price?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Tech") }
    var condition by remember { mutableStateOf(product?.condition ?: "New") }
    var contactInfo by remember { mutableStateOf(product?.contactInfo ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var contactError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    val categories = listOf("Tech", "Fashion", "Home", "Books", "Sports", "Other")
    val conditions = listOf("New", "Like New", "Good", "Fair")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Edit Product Listing" else "List a New Product",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close form dialog")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Product Title") },
                    placeholder = { Text("e.g. iPhone 15, Leather Boots...") },
                    singleLine = true,
                    isError = titleError,
                    supportingText = {
                        if (titleError) {
                            Text("Title cannot be empty.", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_title")
                )

                // Price Input
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = {
                        priceStr = it
                        if (it.toDoubleOrNull() != null && it.toDouble() >= 0) priceError = false
                    },
                    label = { Text("Asking Price ($)") },
                    placeholder = { Text("e.g. 150.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = priceError,
                    supportingText = {
                        if (priceError) {
                            Text("Please enter a valid positive price.", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_price")
                )

                // Category Selection Chips
                Column {
                    Text(
                        text = "Category",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = category == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected icon",
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("form_category_$cat")
                            )
                        }
                    }
                }

                // Condition Selection Chips
                Column {
                    Text(
                        text = "Condition",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        conditions.forEach { cond ->
                            val isSelected = condition == cond
                            FilterChip(
                                selected = isSelected,
                                onClick = { condition = cond },
                                label = { Text(cond, fontSize = 12.sp) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected icon",
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("form_condition_$cond")
                            )
                        }
                    }
                }

                // Contact Information Input
                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = {
                        contactInfo = it
                        if (it.isNotBlank()) contactError = false
                    },
                    label = { Text("Contact Info (Email/Phone)") },
                    placeholder = { Text("e.g. seller@mail.com or 555-123-456") },
                    singleLine = true,
                    isError = contactError,
                    supportingText = {
                        if (contactError) {
                            Text("Contact info is required for buyers.", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_contact")
                )

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (it.isNotBlank()) descError = false
                    },
                    label = { Text("Product Description") },
                    placeholder = { Text("Describe condition, size, accessories included...") },
                    minLines = 3,
                    maxLines = 5,
                    isError = descError,
                    supportingText = {
                        if (descError) {
                            Text("Description cannot be empty.", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_description")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom CTA Action row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("form_cancel_button")
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val isTitleValid = title.isNotBlank()
                            val priceVal = priceStr.toDoubleOrNull()
                            val isPriceValid = priceVal != null && priceVal >= 0
                            val isContactValid = contactInfo.isNotBlank()
                            val isDescValid = description.isNotBlank()

                            titleError = !isTitleValid
                            priceError = !isPriceValid
                            contactError = !isContactValid
                            descError = !isDescValid

                            if (isTitleValid && isPriceValid && isContactValid && isDescValid) {
                                onSubmit(
                                    title.trim(),
                                    description.trim(),
                                    priceVal!!,
                                    category,
                                    condition,
                                    contactInfo.trim()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("form_submit_button")
                    ) {
                        Text(if (isEditing) "Save Changes" else "Post Product")
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onMarkSold: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val categoryColors = getCategoryColors(product.category)
    val isSold = product.status == "Sold"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top header controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColors.first)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(product.category),
                            contentDescription = "Category",
                            tint = categoryColors.second,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = product.category,
                            color = categoryColors.second,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close detailed view")
                    }
                }

                // Title Display
                Text(
                    text = product.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 28.sp,
                    textDecoration = if (isSold) TextDecoration.LineThrough else TextDecoration.None
                )

                // Price and Condition Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ASKING PRICE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", product.price)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CONDITION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = product.condition,
                            color = getConditionColor(product.condition),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(getConditionColor(product.condition).copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Listing Status Banner
                if (isSold) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFBA1A1A).copy(alpha = 0.15f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Sold out icon",
                            tint = Color(0xFFBA1A1A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "This item has been sold. Listing is archived.",
                            fontSize = 13.sp,
                            color = Color(0xFFBA1A1A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Full Description
                Column {
                    Text(
                        text = "Description",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = product.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                // Seller Contact Information panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Interested in this item?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = product.contactInfo,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${product.contactInfo}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Inquiry about \"${product.title}\"")
                                    putExtra(Intent.EXTRA_TEXT, "Hello, I am interested in buying your \"${product.title}\" listed for $${product.price}. Is it still available?")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // If email client not configured, copy contact info to clipboard
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Contact Info", product.contactInfo)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Contact info copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isSold,
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Contact", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action buttons row (Edit/Delete/Mark Sold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isSold) {
                        OutlinedButton(
                            onClick = onMarkSold,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Confirm sold status",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark Sold", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Listing Details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Listing Permanently",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// Category design mapping helpers
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase(Locale.US)) {
        "tech" -> Icons.Default.Devices
        "fashion" -> Icons.Default.Checkroom
        "home" -> Icons.Default.Home
        "books" -> Icons.Default.Book
        "sports" -> Icons.Default.SportsBasketball
        else -> Icons.Default.Category
    }
}

@Composable
fun getCategoryColors(category: String): Pair<Color, Color> {
    return when (category.lowercase(Locale.US)) {
        "tech" -> Pair(Color(0xFFE8DEF8), Color(0xFF21005D))
        "fashion" -> Pair(Color(0xFFFDE0DB), Color(0xFF410002))
        "home" -> Pair(Color(0xFFD1E8FF), Color(0xFF001D3D))
        "books" -> Pair(Color(0xFFE2F1E4), Color(0xFF0A3114))
        "sports" -> Pair(Color(0xFFFFE0B2), Color(0xFF4D2C00))
        else -> Pair(Color(0xFFE0F2F1), Color(0xFF004D40))
    }
}

fun getConditionColor(condition: String): Color {
    return when (condition.lowercase(Locale.US)) {
        "new" -> Color(0xFF2E7D32) // Forest green
        "like new" -> Color(0xFF7B1FA2) // Deep Purple
        "good" -> Color(0xFF1976D2) // Soft Blue
        "fair" -> Color(0xFFE65100) // Vibrant Orange
        else -> Color(0xFF455A64) // Slate
    }
}
