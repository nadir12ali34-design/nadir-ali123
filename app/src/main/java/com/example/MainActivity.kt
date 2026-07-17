package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.ProductDatabase
import com.example.data.ProductRepository
import com.example.ui.ProductViewModel
import com.example.ui.ProductViewModelFactory
import com.example.ui.SellProductsApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize edge-to-edge full bleed drawing
        enableEdgeToEdge()

        // Room database and repository setup
        val database = ProductDatabase.getDatabase(this)
        val repository = ProductRepository(database.productDao())

        // ViewModel creation using the factory
        val viewModel: ProductViewModel by viewModels {
            ProductViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SellProductsApp(viewModel = viewModel)
                }
            }
        }
    }
}

