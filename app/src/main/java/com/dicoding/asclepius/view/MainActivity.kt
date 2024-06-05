package com.dicoding.asclepius.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var btnGallery : Button
    private lateinit var btnAnalyze : Button
    private lateinit var btnCamera : Button
    private lateinit var imageView : ImageView

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnGallery = binding.galleryButton
        btnCamera = binding.cameraButton
        btnAnalyze = binding.analyzeButton
        imageView = binding.previewImageView


        btnGallery.setOnClickListener {
            startGallery()
        }

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
            {
                takePicturePreview.launch(null)
            }
            else {
                requestPermissionCamera.launch(android.Manifest.permission.CAMERA)
            }
        }

        btnAnalyze.setOnClickListener {
            analyzeImage()
        }

    }

    private val requestPermissionCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePicturePreview.launch(null)
        } else {
            Toast.makeText(this, "Permission Denied for Camera!", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePicturePreview = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Convert bitmap to Uri
            val uri = bitmapToUri(it)
            uri?.let { currentImageUri = it }
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let {
                currentImageUri = it
                showImage()
            }
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        currentImageUri?.let { uri ->
            // TODO: Tampilkan gambar yang dipilih ke dalam ImageView menggunakan Glide atau library serupa.
            imageView.setImageURI(uri)
        } ?: showToast("No image selected")
    }

    private fun analyzeImage() {
        val uri = currentImageUri
        if (uri != null) {
            moveToResult(uri)
        } else {
            showToast("No image selected")
        }
    }

    private fun moveToResult(uri: Uri) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("imageUri", uri)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                currentImageUri = uri
                showImage()
            }
        }
    }


    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

}