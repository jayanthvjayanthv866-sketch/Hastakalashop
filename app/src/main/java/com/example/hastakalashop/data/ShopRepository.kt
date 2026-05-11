package com.example.hastakalashop.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopRepository(private val shopDao: ShopDao) {
    // Products
    val allProducts: LiveData<List<Product>> = shopDao.getAllProducts()
    val archivedProducts: LiveData<List<Product>> = shopDao.getArchivedProducts()
    val lowStockProducts: LiveData<List<Product>> = shopDao.getLowStockProducts()
    val bestSeller: LiveData<List<Product>> = shopDao.getBestSeller()
    val stockWorth: LiveData<Double> = shopDao.getStockWorth()

    // Sales
    val allSales: LiveData<List<Sale>> = shopDao.getAllSales()
    val totalIncome: LiveData<Double> = shopDao.getTotalIncome()
    val totalProductsSold: LiveData<Int> = shopDao.getTotalProductsSold()

    // Expenses
    val allExpenses: LiveData<List<Expense>> = shopDao.getAllExpenses()
    val totalExpenses: LiveData<Double> = shopDao.getTotalExpenses()

    // Time-based Earnings
    fun getEarningsSince(start: Long): LiveData<Double> = shopDao.getEarningsSince(start)

    // Operations
    suspend fun insertSale(sale: Sale) = withContext(Dispatchers.IO) {
        shopDao.insertSale(sale)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        shopDao.updateProduct(product)
    }
    
    suspend fun insertProduct(product: Product) = withContext(Dispatchers.IO) {
        shopDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        shopDao.deleteProduct(product)
    }

    suspend fun insertExpense(expense: Expense) = withContext(Dispatchers.IO) {
        shopDao.insertExpense(expense)
    }

    suspend fun resetData() = withContext(Dispatchers.IO) {
        shopDao.deleteAllSales()
        shopDao.deleteAllExpenses()
        shopDao.resetAllProductStock()
    }
}
