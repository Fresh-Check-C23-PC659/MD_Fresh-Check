package com.example.freshcheck.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshcheck.databinding.FragmentHomeBinding
import com.example.freshcheck.ui.adapter.ItemAdapterTest

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vegetableRV = binding.rvItemVegetable
        val fruitsRV = binding.rvItemFruits
        val layoutManagerVegetables = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerFruits = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        vegetableRV.layoutManager = layoutManagerVegetables
        fruitsRV.layoutManager = layoutManagerFruits
        val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10")
        val adapter = ItemAdapterTest(items)
        vegetableRV.adapter = adapter
        fruitsRV.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}