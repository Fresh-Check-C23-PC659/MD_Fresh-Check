package com.example.freshcheck.ui.fragments

import ViewModelFactory
import android.content.Intent
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
import com.example.freshcheck.data.Product
import com.example.freshcheck.databinding.FragmentHomeBinding
import com.example.freshcheck.ui.activities.ProductDetailActivity
import com.example.freshcheck.ui.adapter.FruitProductsAdapter
import com.example.freshcheck.ui.adapter.ItemClickListener
import com.example.freshcheck.ui.adapter.ItemClickListener2
import com.example.freshcheck.ui.adapter.VegetableProductsAdapter
import com.example.freshcheck.ui.viewmodel.MainCategoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), ItemClickListener, ItemClickListener2 {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fruitProductsAdapter: FruitProductsAdapter
    private lateinit var vegetableProductsAdapter: VegetableProductsAdapter
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
        setupRvVegetables()
        launchRvFruits()
        launchRvVegetable()
    }

    private fun launchRvVegetable() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.vegetableProducts.collectLatest {
                    when (it) {
                        is ResultSealed.Loading -> {

                        }

                        is ResultSealed.Success -> {
                            vegetableProductsAdapter.differ.submitList(it.data)
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

    private fun launchRvFruits() {
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

    override fun onItemClick(product: Product) {
        navigateToProductDetail(product)
    }

    private fun navigateToProductDetail(product: Product) {
        val intent = Intent(activity, ProductDetailActivity::class.java)
        intent.putExtra("productImage", product.images.toTypedArray())
        intent.putExtra("productName", product.name)
        intent.putExtra("productDesc", product.description)
        intent.putExtra("productPrice", product.price)
        startActivity(intent)
    }

    private fun setupRvVegetables() {
        vegetableProductsAdapter = VegetableProductsAdapter(this)
        binding.rvItemVegetable.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = vegetableProductsAdapter
        }
    }

    private fun setupRvFruits() {
        fruitProductsAdapter = FruitProductsAdapter(this)
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