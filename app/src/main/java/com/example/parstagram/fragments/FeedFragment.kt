package com.example.parstagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.PostAdapter
import com.example.parstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


open class FeedFragment : Fragment() {

    lateinit var rvPosts:RecyclerView

    lateinit var adapter:PostAdapter

    lateinit var swipeContainer:SwipeRefreshLayout

    var allPosts: MutableList<Post> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            allPosts.clear()
            queryPosts()
            swipeContainer.isRefreshing = false
        }

        rvPosts=view.findViewById(R.id.rvFeed)

        adapter= PostAdapter(requireContext(),allPosts)
        rvPosts.adapter=adapter

        rvPosts.layoutManager=LinearLayoutManager(requireContext())

        queryPosts()

    }

    //Query for all posts in server
    open fun queryPosts() {

        //specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        //find all post objects
        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")
        query.setLimit(20)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e!=null){
                    Log.e(TAG,"Error fetching posts")
                }else {
                    if(posts!=null){
                        for(post in posts){
                            Log.i(TAG,"Post: "+post.getDescription() + ", username: "+post.getUser()?.username)
                        }

                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }

    companion object{
        val TAG="FeedFragment"
    }

}
