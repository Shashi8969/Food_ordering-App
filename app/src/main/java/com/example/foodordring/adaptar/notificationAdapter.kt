package com.example.foodordring.adaptar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodordring.databinding.NotificationItemBinding

class notificationAdapter(private var notification: ArrayList<String>, private var notificationImage: ArrayList<Int>): RecyclerView.Adapter<notificationAdapter.NoticicationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticicationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticicationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = notification.size

    inner class NoticicationViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
                binding.apply { notificationtextview.text = notification[position]
                notificationImageview.setImageResource(notificationImage[position])
                }
        }

    }
}