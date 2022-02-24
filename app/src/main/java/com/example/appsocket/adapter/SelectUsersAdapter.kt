package com.example.appsocket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appsocket.databinding.SelectUsersBinding
import com.example.appsocket.model.User

class SelectUsersAdapter(val data: ArrayList<User>) :
    RecyclerView.Adapter<SelectUsersAdapter.ItemViewHolder>() {


    private lateinit var mlistener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mlistener = listener
    }


    inner class ItemViewHolder(
        val binding: SelectUsersBinding,
        private val listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.checkBox.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val binding = SelectUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, mlistener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.binding.checkBox.text = data[position].email


    }

    override fun getItemCount() = data.size


}