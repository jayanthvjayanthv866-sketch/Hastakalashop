package com.example.hastakalashop.ui.dashboard

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hastakalashop.LoginActivity
import com.example.hastakalashop.R
import com.example.hastakalashop.data.Product
import com.example.hastakalashop.databinding.FragmentDashboardBinding
import com.example.hastakalashop.ui.ShopViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProductAdapter(
            onSellClick = { product ->
                if (product.stock > 0) {
                    viewModel.sellProduct(product, 1)
                    Toast.makeText(requireContext(), "${product.name} Sold!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Out of Stock!", Toast.LENGTH_SHORT).show()
                }
            },
            onLongClick = { product ->
                showProductActionsDialog(product)
            },
            onEditStockClick = { product ->
                showUpdateStockDialog(product)
            }
        )

        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter

        // Logout
        binding.btnLogout.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        // Set Date
        val sdf = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        binding.txtDate.text = sdf.format(Date())

        binding.btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }

        // Profile FAB
        binding.fabProfile.setOnClickListener {
            showProfileBottomSheet()
        }

        // Add Expense Dialog on long press of Profit Card
        binding.cardProfit.setOnLongClickListener {
            showAddExpenseDialog()
            true
        }

        // Observers
        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            
            if (products.isEmpty()) {
                binding.rvProducts.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.VISIBLE
            } else {
                binding.rvProducts.visibility = View.VISIBLE
                binding.layoutEmptyState.visibility = View.GONE
            }

            val lowStockCount = products.count { it.stock < 3 }
            if (lowStockCount > 0) {
                binding.txtLowStockAlert.text = "$lowStockCount Alert"
                binding.txtLowStockAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
            } else {
                binding.txtLowStockAlert.text = "Healthy"
                binding.txtLowStockAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.accentNeon))
            }
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.txtTotalIncome.text = "₹${String.format("%.2f", income)}"
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { expenses ->
            binding.txtTotalExpenses.text = "₹${String.format("%.2f", expenses)}"
        }

        viewModel.netProfit.observe(viewLifecycleOwner) { profit ->
            binding.txtNetProfit.text = "₹${String.format("%.2f", profit)}"
            binding.txtNetProfit.setTextColor(
                if (profit >= 0) ContextCompat.getColor(requireContext(), R.color.accentNeon)
                else ContextCompat.getColor(requireContext(), R.color.errorColor)
            )
        }

        viewModel.totalProductsSold.observe(viewLifecycleOwner) { sold ->
            binding.txtTotalSold.text = sold.toString()
        }

        viewModel.stockWorth.observe(viewLifecycleOwner) { worth ->
            binding.txtStockWorth.text = "₹${String.format("%.0f", worth ?: 0.0)}"
        }

        viewModel.bestSeller.observe(viewLifecycleOwner) { bestList ->
            binding.txtBestSeller.text = bestList.firstOrNull()?.name ?: "None"
        }
    }

    private fun showProfileBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_profile, null)
        
        val txtName = view.findViewById<TextView>(R.id.txtProfileName)
        val txtPhone = view.findViewById<TextView>(R.id.txtProfilePhone)
        val txtToday = view.findViewById<TextView>(R.id.txtTodayEarn)
        val txtWeek = view.findViewById<TextView>(R.id.txtWeekEarn)
        val txtMonth = view.findViewById<TextView>(R.id.txtMonthEarn)
        
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("owner_name", "Artisan Owner")
        val savedPhone = sharedPref.getString("owner_phone", "+91 9876543210")
        
        txtName.text = "Name: $savedName"
        txtPhone.text = "Phone: $savedPhone"
        
        viewModel.todayEarnings.observe(viewLifecycleOwner) { earn ->
            txtToday.text = "₹${String.format("%.2f", earn ?: 0.0)}"
        }
        viewModel.weekEarnings.observe(viewLifecycleOwner) { earn ->
            txtWeek.text = "₹${String.format("%.2f", earn ?: 0.0)}"
        }
        viewModel.monthEarnings.observe(viewLifecycleOwner) { earn ->
            txtMonth.text = "₹${String.format("%.2f", earn ?: 0.0)}"
        }

        view.findViewById<View>(R.id.btnEditProfile).setOnClickListener {
            dialog.dismiss()
            showEditProfileDialog()
        }

        view.findViewById<View>(R.id.btnViewArchive).setOnClickListener {
            dialog.dismiss()
            showArchiveDialog()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showEditProfileDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_expense, null) // Reuse layout
        val edtName = view.findViewById<EditText>(R.id.edtExpenseTitle)
        val edtPhone = view.findViewById<EditText>(R.id.edtExpenseAmount)
        
        edtName.hint = "Your Name"
        edtPhone.hint = "Mobile Number"
        edtPhone.inputType = android.text.InputType.TYPE_CLASS_PHONE

        builder.setView(view)
            .setTitle("Update Profile")
            .setPositiveButton("Save") { _, _ ->
                val name = edtName.text.toString()
                val phone = edtPhone.text.toString()
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("owner_name", name)
                        putString("owner_phone", phone)
                        apply()
                    }
                    Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddExpenseDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_expense, null)
        val edtTitle = dialogView.findViewById<EditText>(R.id.edtExpenseTitle)
        val edtAmount = dialogView.findViewById<EditText>(R.id.edtExpenseAmount)

        builder.setView(dialogView)
            .setTitle("Add Shop Expense")
            .setPositiveButton("Add") { _, _ ->
                val title = edtTitle.text.toString()
                val amount = edtAmount.text.toString().toDoubleOrNull() ?: 0.0
                if (title.isNotEmpty() && amount > 0) {
                    viewModel.addExpense(title, amount, "Material")
                    Toast.makeText(requireContext(), "Expense Added", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUpdateStockDialog(product: Product) {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_expense, null) // Reuse layout
        val edtStock = view.findViewById<EditText>(R.id.edtExpenseAmount)
        val edtTitle = view.findViewById<EditText>(R.id.edtExpenseTitle)
        
        edtTitle.setText(product.name)
        edtTitle.isEnabled = false
        edtStock.hint = "Enter New Stock Quantity"
        edtStock.setText(product.stock.toString())
        edtStock.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        builder.setView(view)
            .setTitle("Update Product Stock")
            .setPositiveButton("Update") { _, _ ->
                val newStock = edtStock.text.toString().toIntOrNull()
                if (newStock != null && newStock >= 0) {
                    viewModel.updateProductStock(product, newStock)
                    Toast.makeText(requireContext(), "Stock Updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Invalid Stock Quantity", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddProductDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_expense, null) // Reuse layout
        val edtName = view.findViewById<EditText>(R.id.edtExpenseTitle)
        val edtPrice = view.findViewById<EditText>(R.id.edtExpenseAmount)
        
        edtName.hint = "Product Name (e.g., Silk Scarf)"
        edtPrice.hint = "Price (₹)"
        edtPrice.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        builder.setView(view)
            .setTitle("Add New Product")
            .setPositiveButton("Create") { _, _ ->
                val name = edtName.text.toString()
                val price = edtPrice.text.toString().toDoubleOrNull() ?: 0.0
                if (name.isNotEmpty() && price > 0) {
                    viewModel.addProduct(name, price, 10) // Default 10 stock
                    Toast.makeText(requireContext(), "$name added to shop", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showProductActionsDialog(product: Product) {
        val options = arrayOf("Update Stock", "Archive Product")
        AlertDialog.Builder(requireContext())
            .setTitle(product.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showUpdateStockDialog(product)
                    1 -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Archive ${product.name}?")
                            .setMessage("Are you sure you want to hide this product? You can restore it from your profile.")
                            .setPositiveButton("Archive") { _, _ ->
                                viewModel.softDeleteProduct(product)
                                Toast.makeText(requireContext(), "Product Archived", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            }
            .show()
    }

    private fun showArchiveDialog() {
        viewModel.archivedProducts.observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                Toast.makeText(requireContext(), "No archived products", Toast.LENGTH_SHORT).show()
                return@observe
            }
            
            val names = products.map { it.name }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setTitle("Archived Products")
                .setItems(names) { _, which ->
                    val productToRestore = products[which]
                    AlertDialog.Builder(requireContext())
                        .setTitle("Restore ${productToRestore.name}?")
                        .setMessage("Do you want to restore this product to the shop?")
                        .setPositiveButton("Restore") { _, _ ->
                            viewModel.restoreProduct(productToRestore)
                            Toast.makeText(requireContext(), "Product Restored", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
                .setNegativeButton("Close", null)
                .show()
                
            // Remove observer after showing to prevent dialog loops if data changes
            viewModel.archivedProducts.removeObservers(viewLifecycleOwner)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
