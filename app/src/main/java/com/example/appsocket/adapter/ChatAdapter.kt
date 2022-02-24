package com.example.appsocket.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsocket.R
import com.example.appsocket.model.Messages

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {

    private var chatMessages: MutableList<Messages> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setChatList(itemsCells: MutableList<Messages>) {
        this.chatMessages = itemsCells
        notifyDataSetChanged()

    }


    inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private lateinit var tvSender: TextView
        private lateinit var tvReceiver: TextView
        private lateinit var tvDate: TextView


        private fun sender(item: Messages.MsgSender) {
            tvSender = itemView.findViewById(R.id.tv_outGoing)
            tvSender.text = item.msgOutGoing

            tvDate = itemView.findViewById(R.id.tvDateSender)
            tvDate.text = android.text.format.DateFormat.format("hh:mm a", item.date)
        }


        private fun receiver(item: Messages.MsgReceiver) {
            tvReceiver = itemView.findViewById(R.id.tv_incoming)
            tvReceiver.text = item.msgIncoming


            tvDate = itemView.findViewById(R.id.tvDateReceiver)
            tvDate.text = android.text.format.DateFormat.format("hh:mm a", item.date)

        }

        fun bind(dataModel: Messages) {
            when (dataModel) {
                is Messages.MsgSender -> sender(dataModel)
                is Messages.MsgReceiver -> receiver(dataModel)

            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {


        val layout = when (viewType) {
            TYPE_SENDER -> R.layout.msg_outgoing
            TYPE_REC -> R.layout.msg_incoming

            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ItemViewHolder(view.rootView)


    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.bind(chatMessages[position])


    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)

        return when (chatMessages[position]) {
            is Messages.MsgReceiver -> TYPE_REC
            is Messages.MsgSender -> TYPE_SENDER
        }


    }


    override fun getItemCount(): Int {
        return chatMessages.size
    }

    companion object {
        private const val TYPE_SENDER = 1
        private const val TYPE_REC = 2

    }




}