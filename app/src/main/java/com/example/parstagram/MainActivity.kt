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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parstagram.fragments.ComposeFragment
import com.example.parstagram.fragments.FeedFragment
import com.example.parstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File

/**
 * let user create a post by taking a photo with their camera
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item->

            var fragmentToShow : Fragment?=null

            when(item.itemId){

                R.id.nav_home->{
                    fragmentToShow=FeedFragment()
                }

                R.id.nav_add->{
                    fragmentToShow=ComposeFragment()
                }

                R.id.nav_profile->{
                    fragmentToShow=ProfileFragment()
                }

            }

            if(fragmentToShow!=null){
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }

            true
        }

        // Set default selection
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_home


    }



    companion object{
        val TAG="MainActivity"
    }
}