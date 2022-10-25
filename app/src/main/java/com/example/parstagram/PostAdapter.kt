package com.example.parstagram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(val context: Context,val posts:List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts.get(position)
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvUserName : TextView
        val ivPost: ImageView
        val tvDescription:TextView
        //val tvCreatedAt:TextView

        init{
            tvUserName=itemView.findViewById(R.id.tvName)
            ivPost=itemView.findViewById(R.id.ivPost)
            tvDescription=itemView.findViewById(R.id.tvDescription)
            //tvCreatedAt=itemView.findViewById(R.id.tvCreated)
        }

        fun bind(post:Post){
            tvUserName.text=post.getUser()?.username
            tvDescription.text=post.getDescription()
            //tvCreatedAt.text= post.getCreated().toString()

            Glide.with(itemView.context).load(post.getImage()?.url).into(ivPost)
        }

    }

}