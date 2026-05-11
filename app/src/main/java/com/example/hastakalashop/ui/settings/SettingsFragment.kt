package com.example.hastakalashop.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hastakalashop.databinding.FragmentSettingsBinding
import com.example.hastakalashop.ui.ShopViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE)

        binding.themeRoyal.setOnClickListener {
            prefs.edit().putInt("theme", com.example.hastakalashop.R.style.Theme_HastaKalaShop_Royal).apply()
            requireActivity().recreate()
        }

        binding.themeModern.setOnClickListener {
            prefs.edit().putInt("theme", com.example.hastakalashop.R.style.Theme_HastaKalaShop_Modern).apply()
            requireActivity().recreate()
        }

        binding.themeEarthy.setOnClickListener {
            prefs.edit().putInt("theme", com.example.hastakalashop.R.style.Theme_HastaKalaShop_Earthy).apply()
            requireActivity().recreate()
        }

        binding.btnResetData.setOnClickListener {
            viewModel.resetData()
            Toast.makeText(requireContext(), "All Data Reset Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
