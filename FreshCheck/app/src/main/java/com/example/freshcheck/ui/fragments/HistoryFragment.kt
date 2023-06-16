package com.example.freshcheck.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshcheck.databinding.FragmentHistoryBinding
import com.example.freshcheck.ui.adapter.ItemHistoryAdapterTest

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvHistory = binding.rvItemHistory

        val tvEmptyState = binding.tvEmptyList


        val layoutManagerVegetables = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvHistory.layoutManager = layoutManagerVegetables
        val items = emptyList<String>()
        val adapter = ItemHistoryAdapterTest(items)
        rvHistory.adapter = adapter

        if (items.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
        } else {
            tvEmptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}