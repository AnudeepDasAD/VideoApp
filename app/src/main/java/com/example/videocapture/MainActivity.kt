package com.example.videocapture

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val REQUEST_CODE= 100
private lateinit var videoFile: File
private const val FILE_NAME = "test.mp4"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCaptureVideo.setOnClickListener {
            val captureVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoFile = getVideoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(this, "com.example.fileprovider", videoFile)
            captureVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (captureVideoIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(captureVideoIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to use video", Toast.LENGTH_SHORT).show()
            }
        }

        /*
        btnUploadVideo.setOnClickListener {
            val playVideoIntent = Intent(this, )
        }
        */
    }

    private fun getVideoFile(fileName: String): File {
        // Use getExternalFilesDir to accesss packagrer specific directories

        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File(storageDirectory, fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get the data?
            // val videoCap = data?.extras?.get("data")
            videoViewGui.setVideoPath(videoFile.absolutePath)
            videoViewGui.start()

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}