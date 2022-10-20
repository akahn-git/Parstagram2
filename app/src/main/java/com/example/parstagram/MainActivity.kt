package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File

/**
 * let user create a post by taking a photo with their camera
 */
class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_submit).setOnClickListener{
            //send post to server
            //get the description
            val description = findViewById<EditText>(R.id.et_description).text.toString()
            val user = ParseUser.getCurrentUser()
            if(photoFile!=null){
                submitPost(description,user,photoFile!!)
            }else{
                Log.e(TAG,"unable to upload picture")
            }

        }

        findViewById<Button>(R.id.bt_camera).setOnClickListener{
            //launch camera
            onLaunchCamera()
        }

        findViewById<Button>(R.id.bt_logout).setOnClickListener{
            logout()
        }


        //queryPosts()
    }

    private fun logout() {
        ParseUser.logOut()
        val intent = Intent(this@MainActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    //send a post object to our parse server
    private fun submitPost(description: String,user:ParseUser, file: File) {
        //create the post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))

        val pb = findViewById<ProgressBar>(R.id.pbLoading)
        pb.visibility= ProgressBar.VISIBLE

        post.saveInBackground{ exception->
            if(exception!=null){
                Log.e(TAG,"error saving post")
                exception.printStackTrace()
                Toast.makeText(this,"Unable to post",Toast.LENGTH_SHORT).show()
            } else{
                Log.i(TAG,"successfully posted")
                //reset edit text field
                val et_description=findViewById<EditText>(R.id.et_description)
                et_description.text.clear()

                //reset image view
                val iv_picture=findViewById<ImageView>(R.id.imageView)
                iv_picture.setImageDrawable(null)
            }
        }

        pb.visibility= ProgressBar.INVISIBLE
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode== RESULT_OK){
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val ivPreview:ImageView=findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            } else{
                Toast.makeText(this,"Picture not taken",Toast.LENGTH_SHORT).show()
            }
        }
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
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    //Query for all posts in server
    private fun queryPosts() {

        //specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        //find all post objects
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post>{
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e!=null){
                    Log.e(TAG,"Error fetching posts")
                }else {
                    if(posts!=null){
                        for(post in posts){
                            Log.i(TAG,"Post: "+post.getDescription() + ", username: "+post.getUser()?.username)
                        }
                    }
                }
            }

        })
    }

    companion object{
        val TAG="MainActivity"
    }
}