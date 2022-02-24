package com.example.appsocket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appsocket.databinding.ItemGroupBinding

import com.example.appsocket.model.Group


class GroupAdapter(val data: ArrayList<Group>, private val onClick: OnClickItem) :
    RecyclerView.Adapter<GroupAdapter.ItemViewHolder>() {


    inner class ItemViewHolder(val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val user = data[position]

        holder.binding.tvGroupName.text = data[position].name

        holder.itemView.apply {
            setOnClickListener {
                onClick.onClick(user)
            }
        }

    }

    override fun getItemCount() = data.size

    interface OnClickItem {
        fun onClick(group: Group)
    }

}