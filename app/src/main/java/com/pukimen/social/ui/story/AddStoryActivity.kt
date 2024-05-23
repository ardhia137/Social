package com.pukimen.social.ui.story

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pukimen.social.R
import com.pukimen.social.databinding.ActivityAddStoryBinding
import com.pukimen.social.utils.getImageUri
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.IOException
import android.Manifest
import android.view.View
import androidx.activity.viewModels
import com.pukimen.social.ui.ViewModelFactory
import com.pukimen.social.ui.auth.AuthViewModel
import com.pukimen.social.utils.Results

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private var currentImageUri: Uri? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.kameraButton.setOnClickListener { startCamera() }
        binding.postButton.setOnClickListener { postStory() }
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun startGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherGallery.launch(pickIntent)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                currentImageUri = uri
                val timeStamp = System.currentTimeMillis()
                val destinationFileName = "cropped_image_$timeStamp.jpg"
                val destinationUri = Uri.fromFile(File(cacheDir, destinationFileName))
                val options = UCrop.Options().apply {
                    setCompressionFormat(Bitmap.CompressFormat.JPEG)
                    setCompressionQuality(80)
                    setToolbarColor(ContextCompat.getColor(this@AddStoryActivity, R.color.primary))
                    setToolbarWidgetColor(ContextCompat.getColor(this@AddStoryActivity, R.color.white))
                    setActiveControlsWidgetColor(ContextCompat.getColor(this@AddStoryActivity, R.color.primary))
                }
                UCrop.of(uri, destinationUri)
                    .withOptions(options)
                    .start(this)
            } ?: Log.e("MainActivity", "Image selection failed")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = data?.let { UCrop.getOutput(it) }
            resultUri?.let {
                currentImageUri = resultUri
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.let {
                Log.e("UCrop", "UCrop error: $cropError")
                Toast.makeText(this, "Crop error: $cropError", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let { uri ->
                val timeStamp = System.currentTimeMillis()
                val destinationFileName = "cropped_image_$timeStamp.jpg"
                val destinationUri = Uri.fromFile(File(cacheDir, destinationFileName))
                val options = UCrop.Options().apply {
                    setCompressionFormat(Bitmap.CompressFormat.JPEG)
                    setCompressionQuality(80)
                    setToolbarColor(ContextCompat.getColor(this@AddStoryActivity, R.color.primary))
                    setToolbarWidgetColor(ContextCompat.getColor(this@AddStoryActivity, R.color.white))
                    setActiveControlsWidgetColor(ContextCompat.getColor(this@AddStoryActivity, R.color.primary))
                }
                UCrop.of(uri, destinationUri)
                    .withOptions(options)
                    .start(this)
            }
        } else {
            Toast.makeText(this, "Failed to take picture", Toast.LENGTH_SHORT).show()
        }
    }



    private fun showImage() {
        currentImageUri?.let { uri ->
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                binding.previewImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("MainActivity", "Error loading image from URI: $e")
            }
        }
    }

    private fun postStory(){
        val description = binding.descriptionEditText.text.toString().trim()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: StoryViewModel by viewModels {
            factory
        }

        val authViewModel: AuthViewModel by viewModels {
            factory
        }

        authViewModel.getSession().observe(this) { user ->
            val token = user.token
            currentImageUri?.let {
                viewModel.postStory(token,description, it,this).observe(this@AddStoryActivity) { result ->
                    if(result != null){
                        when(result){
                            is Results.Loading ->{
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is Results.Success<*> -> {
                                binding.progressBar.visibility = View.GONE
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }

                            is Results.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@AddStoryActivity,
                                    "Terjadi kesalahan" + result.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }



}