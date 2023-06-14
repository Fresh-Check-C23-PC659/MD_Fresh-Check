package com.example.freshcheck.ui.fragments

import ViewModelFactory
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.freshcheck.databinding.FragmentProfileBinding
import com.example.freshcheck.ui.activities.LoginActivity
import com.example.freshcheck.ui.fragments.DetectionFragment.Companion.PREF_FILE_PATH_KEY
import com.example.freshcheck.ui.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("filePath", Context.MODE_PRIVATE)


        binding.apply {
            btnLogoutProfile.setOnClickListener {
                viewModel.logout()
                clearSharedPreferences()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun clearSharedPreferences() {
        sharedPreferences.edit().remove(PREF_FILE_PATH_KEY).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}