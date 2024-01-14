package com.jmepaz.simpletextrecognition

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.jmepaz.simpletextrecognition.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  val registerPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){permissionGranted ->
        if(permissionGranted){
            startCamera()
        }
        else{
            Toast.makeText(baseContext, "Camera Permission Request Denied", Toast.LENGTH_LONG).show()
        }

    }
    private  lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
    }

    private fun requestPermission(){
        registerPermission.launch(REQUIRED_PERMISSION)
    }
    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        val cameraExecutor = ContextCompat.getMainExecutor(this)
        cameraProviderFuture.addListener(
            {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.previewView.surfaceProvider)
                    }
//                //Image analyzer
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it ->
                        it.setAnalyzer(cameraExecutor,
                            MyTextRecognition{visionText->
                                   binding.textRes.text =visionText.text
                            })
                    }


                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalyzer)

                } catch(exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            }
        ,cameraExecutor)
    }

    companion object{
        private const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}