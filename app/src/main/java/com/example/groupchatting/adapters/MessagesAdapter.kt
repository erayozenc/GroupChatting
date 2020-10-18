package com.example.groupchatting.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groupchatting.R
import com.example.groupchatting.models.Message
import com.example.groupchatting.models.User
import kotlinx.android.synthetic.main.item_message.view.*

class MessagesAdapter(val currentUserUid : String) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {
    inner class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    }

    private val differCallback = object : DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_message,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = differ.currentList[position]
        var previousMessage : Message? = null
        if (position > 0) {
            previousMessage = differ.currentList[position - 1]
        }
        holder.itemView.apply {
            if (previousMessage != null){
                if(currentMessage.user!!.uid == currentUserUid){
                    tvUserMessage.text = currentMessage.message
                    tvReceiverMessage.visibility = View.GONE
                    ivReceiverPp.visibility = View.GONE
                }else if(currentMessage.user.uid != currentUserUid && previousMessage.user!!.uid != currentMessage.user.uid){
                    Glide.with(this).load(currentMessage.user.profilePhoto).into(ivReceiverPp)
                    tvReceiverMessage.text = currentMessage.message
                    tvUserMessage.visibility = View.GONE
                }else{
                    tvReceiverMessage.text = currentMessage.message
                    tvUserMessage.visibility = View.GONE
                    ivReceiverPp.visibility = View.GONE
                }
            }else{
                if(currentMessage.user!!.uid == currentUserUid){
                    tvUserMessage.text = currentMessage.message
                    tvReceiverMessage.visibility = View.GONE
                    ivReceiverPp.visibility = View.GONE
                }else {
                    Glide.with(this).load(currentMessage.user.profilePhoto).into(ivReceiverPp)
                    tvReceiverMessage.text = currentMessage.message
                    tvUserMessage.visibility = View.GONE
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}