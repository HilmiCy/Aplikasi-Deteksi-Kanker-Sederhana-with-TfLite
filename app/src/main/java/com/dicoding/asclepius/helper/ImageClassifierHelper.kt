package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.TensorImage


class ImageClassifierHelper(private val context: Context) {
    private var cancerModel: CancerClassification? = null

    private fun setupImageClassifier() {
        cancerModel = CancerClassification.newInstance(context)
    }

    fun classifyStaticImage(imageUri: Uri): Pair<String, Float>? {
        if (cancerModel == null) {
            setupImageClassifier()
        }

        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val tfImageCancer = TensorImage.fromBitmap(bitmap)

        val outputs = cancerModel?.process(tfImageCancer)
            ?.probabilityAsCategoryList?.apply {
                sortByDescending { it.score }
            }

        outputs?.let {
            val topProbabilityOutput = it[0]
            val resultLabel = topProbabilityOutput.label
            val resultScore = topProbabilityOutput.score * 100 // Convert to percentage
            return Pair(resultLabel, resultScore)
        }
        return null
    }

    fun closeClassifier() {
        cancerModel?.close()
    }
}