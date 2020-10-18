package com.example.groupchatting.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groupchatting.R
import com.example.groupchatting.models.Group
import kotlinx.android.synthetic.main.item_groups.view.*

class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val bundle = Bundle().apply {
                    putParcelable("group",differ.currentList[position])
                }
                itemView.findNavController().navigate(
                        R.id.action_feedFragment_to_groupFragment,
                        bundle
                )
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Group>(){
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupId == newItem.groupId
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_groups,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(currentGroup.icon).into(groupIcon)
            groupName.text = currentGroup.name
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}