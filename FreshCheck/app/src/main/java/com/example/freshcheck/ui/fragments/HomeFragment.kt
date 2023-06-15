package com.example.freshcheck.ui.fragments

import ViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshcheck.ResultSealed
import com.example.freshcheck.databinding.FragmentHomeBinding
import com.example.freshcheck.ui.adapter.FruitProductsAdapter
import com.example.freshcheck.ui.adapter.ItemAdapterTest
import com.example.freshcheck.ui.viewmodel.MainCategoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"
@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fruitProductsAdapter: FruitProductsAdapter
    private val viewModel: MainCategoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvFruits()
        launchSession()

        val vegetableRV = binding.rvItemVegetable
        val layoutManagerVegetables =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        vegetableRV.layoutManager = layoutManagerVegetables
        val items = listOf(
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5",
            "Item 6",
            "Item 7",
            "Item 8",
            "Item 9",
            "Item 10"
        )
        val adapter = ItemAdapterTest(items)
        vegetableRV.adapter = adapter
    }

    private fun launchSession() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fruitProducts.collectLatest {
                    when (it) {
                        is ResultSealed.Loading -> {

                        }

                        is ResultSealed.Success -> {
                            fruitProductsAdapter.differ.submitList(it.data)
                        }
                        is ResultSealed.Error -> {
                            Log.e(TAG, it.error)
                            Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupRvFruits() {
        fruitProductsAdapter = FruitProductsAdapter()
        binding.rvItemFruits.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = fruitProductsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}