package com.example.videocaptureawsupload

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.VideoToUpload
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val REQUEST_CODE= 100
private lateinit var videoFile: File
private const val FILE_NAME = "test.mp4"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add the DataStore plugin and configure Amplify

        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            // Add these lines to add the AWSCognitoAuthPlugin and AWSS3StoragePlugin plugins
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
            Log.i("AWSApp", "Initialized Amplify Data")
        } catch (failure: AmplifyException) {
            Log.e("AWSApp", "Could not initialize Amplify", failure)
        }


        btnCaptureVideo.setOnClickListener {
            val captureVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoFile = getVideoFile(FILE_NAME)

            // Send the URI for the file as part of the Intent so that built-in camera app saves to that file
            // This app will get the file from that file location, hence higher quality image than
            //      receiving the video data from the intent bundle (intent bundle data has 1MB limit)
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
            val uploadVideoIntent = Intent(this, )
        }
        */


        // Querying the Amplify DataStore with a predicate for the Priority
        /*
        // Can create a VideoToUpload object and add it to the DataStore
        val videoItem: VideoToUpload = VideoToUpload.builder()
            .name("Build Android application")
            .priority(Priority.HIGH)
            .description("Build an Android application using Apmplify")
            .build()
        Amplify.DataStore.save(
            videoItem,
            { success -> Log.i("AWSApp", "Saved item: "+ success.item().name)},
            { error -> Log.e("AWSApp", "Could not save item to DataStore", error)}
        )

         Amplify.DataStore.query(
             VideoToUpload::class.java,
             Where.matches(
                 VideoToUpload.PRIORITY.eq(Priority.HIGH)
             ),
             { todos ->
                 while (todos.hasNext()) {
                     val todo = VideoToUpload.next()
                     val name = todo.name;
                     val priority: Priority? = todo.priority
                     val description: String? = todo.description

                     Log.i("Tutorial", "==== VideoToUpload ====")
                     Log.i("Tutorial", "Name: $name")

                     if (priority != null) {
                         Log.i("Tutorial", "Priority: $priority")
                     }

                     if (description != null) {
                         Log.i("Tutorial", "Description: $description")
                     }
                 }
             },
             { failure -> Log.e("Tutorial", "Could not query DataStore", failure) }
                )
         */
    }


    private fun getVideoFile(fileName: String): File {
        // Use getExternalFilesDir to accesss packagrer specific directories

        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File(storageDirectory, fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // val videoCap = data?.extras?.get("data")
            videoViewGui.setVideoPath(videoFile.absolutePath)
            videoViewGui.start()
            uploadFile()

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadFile() {
        // val exampleFile = File(applicationContext.filesDir, "ExampleKey")

        // exampleFile.writeText("Example file contents")

        Amplify.Storage.uploadFile(
            "ExampleKey",
            videoFile,
            { result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()) },
            { error -> Log.e("MyAmplifyApp", "Upload failed", error) }
        )
    }

}