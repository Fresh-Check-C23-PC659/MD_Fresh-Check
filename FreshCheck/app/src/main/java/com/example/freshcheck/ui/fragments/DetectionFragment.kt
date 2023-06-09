package com.example.freshcheck.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.freshcheck.data.remote.retrofit.ApiConfig
import com.example.freshcheck.data.remote.retrofit.ApiService
import com.example.freshcheck.databinding.FragmentDetectionBinding
import com.example.freshcheck.ui.activities.ViewFinderActivity
import com.example.freshcheck.utils.rotateFile
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

    private val REQUEST_CODE_PERMISSIONS = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

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
                    getFile = file
                    binding.ivDetectionFragment.setImageBitmap(BitmapFactory.decodeFile(file.path))

                    getFile?.let { uploadFile(it) }
                }
            }
        }


    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
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
                        binding.ivDetectionFragment.setImageBitmap(BitmapFactory.decodeFile(file.path))
                    }
                }
            }
        }
    }

    private fun uploadFile(file: File) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        lifecycleScope.launch {
            val response = apiService.uploadFile(filePart)
            if (response.prediction != 0) {
                Toast.makeText(
                    requireContext(),
                    "File Successfully Uploaded, Prediction : ${response.prediction}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed To Upload File, Error : ${response.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private const val REQUEST_CODE_CAMERA = 102
    }

}