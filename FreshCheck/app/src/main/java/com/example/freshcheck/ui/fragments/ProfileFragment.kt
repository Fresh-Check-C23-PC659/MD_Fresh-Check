package com.example.freshcheck.ui.fragments

import ViewModelFactory
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.freshcheck.R
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
            cvLogoutProfile.setOnClickListener {
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout_confirmation, null)
                val dialogBuilder = AlertDialog.Builder(requireContext())
                    .setTitle("Logout Confirmation")
                    .setView(dialogView)
                    .setPositiveButton("Logout") { dialog, _ ->
                        viewModel.logout()
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                val dialog = dialogBuilder.create()
                dialog.show()
            }
        }

        fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}