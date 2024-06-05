package com.dicoding.asclepius.view

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.TensorImage

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ImageClassifierHelper dengan konteks Activity
        imageClassifierHelper = ImageClassifierHelper(this)

        // Mendapatkan URI gambar dari intent
        val imageUri = intent.getParcelableExtra<Uri>("imageUri")

        // Menampilkan hasil gambar dari URI yang diterima dari intent
        binding.resultImage.setImageURI(imageUri)

        // Memanggil metode classifyStaticImage untuk mengklasifikasikan gambar
        imageUri?.let {
            classifyImage(it)
        }
    }

    private fun classifyImage(imageUri: Uri) {
        val result = imageClassifierHelper.classifyStaticImage(imageUri)

        result?.let { (resultLabel, resultScore) ->
            // Mengonversi probabilitas menjadi persentase
            val percentageScore = (resultScore).toInt()
            val resultText = "Prediction Cancer: $resultLabel\nConfidence Score: $percentageScore%"
            binding.resultText.text = resultText
        } ?: run {
            Toast.makeText(this, "Failed to classify image.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageClassifierHelper.closeClassifier()
    }
}