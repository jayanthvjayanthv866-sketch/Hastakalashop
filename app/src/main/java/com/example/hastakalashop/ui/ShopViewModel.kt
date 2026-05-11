package com.example.hastakalashop.ui

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.asFlow
import com.example.hastakalashop.data.*
import com.example.hastakalashop.R
import kotlinx.coroutines.launch
import java.util.*

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ShopRepository
    
    val allProducts: LiveData<List<Product>>
    val archivedProducts: LiveData<List<Product>>
    val allSales: LiveData<List<Sale>>
    val allExpenses: LiveData<List<Expense>>
    
    val totalIncome: LiveData<Double>
    val totalExpenses: LiveData<Double>
    val totalProductsSold: LiveData<Int>
    val bestSeller: LiveData<List<Product>>
    val lowStockProducts: LiveData<List<Product>>
    val stockWorth: LiveData<Double>

    // Time-based Earnings
    val todayEarnings: LiveData<Double>
    val weekEarnings: LiveData<Double>
    val monthEarnings: LiveData<Double>

    // Calculated Profit
    val netProfit: MediatorLiveData<Double> = MediatorLiveData()

    init {
        val shopDao = AppDatabase.getDatabase(application).shopDao()
        repository = ShopRepository(shopDao)
        
        allProducts = repository.allProducts
        archivedProducts = repository.archivedProducts
        allSales = repository.allSales
        allExpenses = repository.allExpenses
        
        totalIncome = repository.totalIncome
        totalExpenses = repository.totalExpenses
        totalProductsSold = repository.totalProductsSold
        bestSeller = repository.bestSeller
        lowStockProducts = repository.lowStockProducts
        stockWorth = repository.stockWorth

        // Setup time ranges
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val weekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        todayEarnings = repository.getEarningsSince(todayStart)
        weekEarnings = repository.getEarningsSince(weekStart)
        monthEarnings = repository.getEarningsSince(monthStart)

        // Profit Calculation Logic
        netProfit.addSource(totalIncome) { income ->
            val expenses = totalExpenses.value ?: 0.0
            netProfit.value = (income ?: 0.0) - expenses
        }
        netProfit.addSource(totalExpenses) { expenses ->
            val income = totalIncome.value ?: 0.0
            netProfit.value = income - (expenses ?: 0.0)
        }
        
        // Initial population
        viewModelScope.launch {
            repository.allProducts.asFlow().collect { products ->
                if (products.isEmpty()) {
                    val initialProducts = listOf(
                        Product(name = "Blue Bag", price = 450.0, stock = 15, imageRes = R.drawable.product_blue_bag),
                        Product(name = "Red Bag", price = 450.0, stock = 12, imageRes = R.drawable.product_red_bag),
                        Product(name = "Handmade Basket", price = 300.0, stock = 10, imageRes = R.drawable.product_basket),
                        Product(name = "Keychain", price = 50.0, stock = 50, imageRes = R.drawable.product_keychain),
                        Product(name = "Bamboo Craft", price = 800.0, stock = 5, imageRes = R.drawable.product_bamboo)
                    )
                    for (product in initialProducts) {
                        repository.insertProduct(product)
                    }
                }
            }
        }
    }

    fun sellProduct(product: Product, quantity: Int) {
        if (product.stock >= quantity) {
            viewModelScope.launch {
                val totalPrice = product.price * quantity
                product.stock -= quantity
                product.soldCount += quantity
                repository.updateProduct(product)
                
                val sale = Sale(
                    productName = product.name,
                    quantity = quantity,
                    totalPrice = totalPrice,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertSale(sale)
            }
        }
    }

    fun updateProductStock(product: Product, newStock: Int) {
        viewModelScope.launch {
            product.stock = newStock
            repository.updateProduct(product)
        }
    }

    fun addProduct(name: String, price: Double, stock: Int) {
        viewModelScope.launch {
            repository.insertProduct(Product(name = name, price = price, stock = stock))
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun softDeleteProduct(product: Product) {
        viewModelScope.launch {
            product.isDeleted = true
            repository.updateProduct(product)
        }
    }

    fun restoreProduct(product: Product) {
        viewModelScope.launch {
            product.isDeleted = false
            repository.updateProduct(product)
        }
    }

    fun addExpense(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            repository.insertExpense(Expense(title = title, amount = amount, category = category))
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetData()
        }
    }
}
