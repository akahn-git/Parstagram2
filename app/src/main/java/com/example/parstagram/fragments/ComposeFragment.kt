package com.example.parstagram.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [ComposeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var etDescription:EditText
    lateinit var ivPreview:ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.bt_submit).setOnClickListener{
            //send post to server
            //get the description
            val description = view.findViewById<EditText>(R.id.et_description).text.toString()
            val user = ParseUser.getCurrentUser()
            if(photoFile!=null){
                submitPost(description,user,photoFile!!)
            }else{
                Log.e(MainActivity.TAG,"unable to upload picture")
            }

        }

        view.findViewById<Button>(R.id.bt_camera).setOnClickListener{
            //launch camera
            onLaunchCamera()
        }

        etDescription=view.findViewById<EditText>(R.id.et_description)
        ivPreview=view.findViewById<ImageView>(R.id.imageView)

    }

    //send a post object to our parse server
    private fun submitPost(description: String,user:ParseUser, file: File) {
        //create the post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))


        post.saveInBackground{ exception->
            if(exception!=null){
                Log.e(MainActivity.TAG,"error saving post")
                exception.printStackTrace()
            } else{
                Log.i(MainActivity.TAG,"successfully posted")
                //reset edit text field
                etDescription.text.clear()


                //reset image view
                ivPreview.setImageDrawable(null)

            }
        }

    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode== AppCompatActivity.RESULT_OK){
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val resized = Bitmap.createScaledBitmap(takenImage,500,500,true)
                ivPreview.setImageBitmap(resized)

            } else{

            }
        }
    }
}