package com.jmepaz.simpletextrecognition

import android.renderscript.ScriptGroup.Input
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.RotationProvider.Listener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MyTextRecognition(private val listener:(result:Text)->Unit):ImageAnalysis.Analyzer {
    private var recognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    @OptIn(ExperimentalGetImage::class) override fun analyze(imageProxy: ImageProxy) {
        val mediaImg = imageProxy.image
        if(mediaImg!=null){
            val image = InputImage.fromMediaImage(mediaImg, imageProxy.imageInfo.rotationDegrees)
            val result = recognizer.process(image).addOnSuccessListener {
                visionText->
                listener(visionText)
                 }
                .addOnFailureListener {
                    e->
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
        else{
            imageProxy.close()
        }

    }

}