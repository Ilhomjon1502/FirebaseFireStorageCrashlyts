package com.example.firebasefilestorage.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasefilestorage.Models.User
import com.example.firebasefilestorage.databinding.ItemUserBinding
import com.squareup.picasso.Picasso

class UserAdapter (val list: List<User>):RecyclerView.Adapter<UserAdapter.Vh>(){

    inner class Vh(var itemRv:ItemUserBinding):RecyclerView.ViewHolder(itemRv.root){
        fun onBind(user: User){
            itemRv.txt1.text = user.name
            itemRv.txt2.text = user.age.toString()

            if (user.imageUri != null){
                Picasso.get().load(user.imageUri).into(itemRv.imageItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}