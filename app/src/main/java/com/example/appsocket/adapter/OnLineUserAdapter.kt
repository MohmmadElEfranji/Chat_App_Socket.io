package com.example.appsocket.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appsocket.databinding.OnlineUserRvBinding
import com.example.appsocket.model.User

object OnLineUserAdapter : RecyclerView.Adapter<OnLineUserAdapter.ItemViewHolder>() {

    private lateinit var mlistener: OnItemClickListener
    var list = ArrayList<User>()



    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mlistener = listener
    }


    class ItemViewHolder(
        val binding: OnlineUserRvBinding,
        private val listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.cardUserCell.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val binding =
            OnlineUserRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, mlistener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val user = list[position]
        holder.binding.tvName.text = list[position].email

        if (user.isOnline) {
            holder.binding.OnlineStatus.visibility = View.VISIBLE
            holder.binding.OfflineStatus.visibility = View.GONE
        } else {
            holder.binding.OfflineStatus.visibility = View.VISIBLE
            holder.binding.OnlineStatus.visibility = View.GONE
        }





    }

    override fun getItemCount() = list.size



    init {
        Log.d("OnlineAdapter", "${this.hashCode()}")
    }


}