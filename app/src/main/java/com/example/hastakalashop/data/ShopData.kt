package com.example.hastakalashop.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var price: Double,
    var stock: Int,
    var soldCount: Int = 0,
    var category: String = "Handicraft",
    var imageRes: Int = 0,
    var isDeleted: Boolean = false
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var productName: String,
    var quantity: Int,
    var totalPrice: Double,
    var timestamp: Long = 0
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var amount: Double,
    var category: String = "Material",
    var timestamp: Long = System.currentTimeMillis()
)

@Dao
interface ShopDao {
    // Product Queries
    @Query("SELECT * FROM products WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE isDeleted = 1 ORDER BY name ASC")
    fun getArchivedProducts(): LiveData<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product): Long

    @Update
    fun updateProduct(product: Product)

    @Delete
    fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE stock < 3 AND isDeleted = 0")
    fun getLowStockProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE isDeleted = 0 ORDER BY soldCount DESC LIMIT 1")
    fun getBestSeller(): LiveData<List<Product>>

    @Query("SELECT SUM(price * stock) FROM products WHERE isDeleted = 0")
    fun getStockWorth(): LiveData<Double>

    // Sale Queries
    @Insert
    fun insertSale(sale: Sale): Long

    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    fun getAllSales(): LiveData<List<Sale>>

    @Query("SELECT COALESCE(SUM(totalPrice), 0.0) FROM sales")
    fun getTotalIncome(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM sales")
    fun getTotalProductsSold(): LiveData<Int>

    // Time-based Earnings
    @Query("SELECT COALESCE(SUM(totalPrice), 0.0) FROM sales WHERE timestamp >= :start")
    fun getEarningsSince(start: Long): LiveData<Double>

    // Expense Queries
    @Insert
    fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getTotalExpenses(): LiveData<Double>

    // Utility Queries
    @Query("DELETE FROM sales")
    fun deleteAllSales()

    @Query("DELETE FROM expenses")
    fun deleteAllExpenses()

    @Query("UPDATE products SET soldCount = 0, stock = 20")
    fun resetAllProductStock()
}

@Database(entities = [Product::class, Sale::class, Expense::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hastakala_database_premium"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
