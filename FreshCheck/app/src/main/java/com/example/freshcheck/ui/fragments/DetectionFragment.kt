package com.example.freshcheck.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.freshcheck.R
import com.example.freshcheck.data.remote.retrofit.ApiConfig
import com.example.freshcheck.data.remote.retrofit.ApiService
import com.example.freshcheck.databinding.FragmentDetectionBinding
import com.example.freshcheck.ui.activities.ViewFinderActivity
import com.example.freshcheck.utils.rotateFile
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DetectionFragment : Fragment() {

    private var _binding: FragmentDetectionBinding? = null
    private val binding get() = _binding!!
    private var getFile: File? = null
    private lateinit var apiService: ApiService

    private lateinit var sharedPreferences: SharedPreferences


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImage: Uri? = data?.data
                selectedImage?.let { uri ->
                    val contentResolver = requireContext().contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(uri, takeFlags)

                    val inputStream = contentResolver.openInputStream(uri)
                    val file = File(requireContext().cacheDir, "temp_image.jpg")
                    file.outputStream().use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }

                    sharedPreferences.edit().putString(PREF_FILE_PATH_KEY, file.path).apply()

                    getFile = file
                    binding.ivDetectionFragment.setImageBitmap(BitmapFactory.decodeFile(file.path))

                    getFile?.let { uploadFile(it) }
                }
            }
        }


    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { it ->
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivDetectionFragment.setImageBitmap(BitmapFactory.decodeFile(file.path))

                sharedPreferences.edit().putString(PREF_FILE_PATH_KEY, file.path).apply()

                getFile?.let { uploadFile(it) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        apiService = ApiConfig.getApiService()
        sharedPreferences =
            requireActivity().getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE)

        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            btnCameraDetection.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { openGallery() }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
        }
        galleryLauncher.launch(galleryIntent)
    }

    private fun startCamera() {
        val cameraIntent = Intent(requireContext(), ViewFinderActivity::class.java)
        cameraLauncher.launch(cameraIntent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }


    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data?.getSerializableExtra("picture", File::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        data?.getSerializableExtra("picture")
                    } as? File

                    val isBackCamera =
                        data?.getBooleanExtra("isBackCamera", true) ?: true

                    myFile?.let { file ->
                        rotateFile(file, isBackCamera)
                        getFile = file
                        binding.ivDetectionFragment.setImageBitmap(BitmapFactory.decodeFile(getFile?.path))
                    }
                }
            }
        }
    }


    private fun uploadFile(file: File) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        showLoading(true)

        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = apiService.uploadFile(filePart)

                val dialogView =
                    LayoutInflater.from(requireContext()).inflate(R.layout.result_dialog, null)

                val img = dialogView.findViewById<ImageView>(R.id.iv_image_dialog)
                val name = dialogView.findViewById<TextView>(R.id.tv_name_dialog)
                val level = dialogView.findViewById<TextView>(R.id.tv_level_dialog)
                val btnProceed = dialogView.findViewById<Button>(R.id.btn_proceed)

                Glide.with(dialogView)
                    .load(file.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(img)
                name.text = response.name
                level.text = response.prediction

                val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
                dialog.setContentView(dialogView)
                dialog.setTitle("Detection Result")
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                dialog.show()

                btnProceed.setOnClickListener {
                    dialog.dismiss()
                }

            } catch (e: Exception) {
                Log.e("Detection FG", e.message.toString())
                showSnackbar("Upload failed: ${e.message}")
            } finally {
                showLoading(false)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()

        if (getFile != null) {
            sharedPreferences.edit().putString(PREF_FILE_PATH_KEY, getFile?.path).apply()
        }
    }


    override fun onResume() {
        super.onResume()
        val filePath = sharedPreferences.getString(PREF_FILE_PATH_KEY, null)
        if (!filePath.isNullOrEmpty()) {
            val file = File(filePath)
            if (file.exists()) {
                binding.ivDetectionFragment.setImageBitmap(BitmapFactory.decodeFile(file.path))
            } else {
                binding.ivDetectionFragment.setImageResource(R.drawable.ic_image_24dp)
            }
        } else {
            binding.ivDetectionFragment.setImageResource(R.drawable.ic_image_24dp)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                pbDetection.visibility = View.VISIBLE
            } else {
                pbDetection.visibility = View.GONE
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private const val REQUEST_CODE_CAMERA = 102
        private const val REQUEST_CODE_PERMISSIONS = 1001
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val PREF_FILE_KEY = "MyPrefs"
        const val PREF_FILE_PATH_KEY = "filePath"
    }
}