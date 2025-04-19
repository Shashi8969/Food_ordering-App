//package com.example.foodordring.adaptar
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.foodordring.model.RecentBuyModel
//
//class RecentBuyAdapter(
//    private var context: Context, private var recentBuyList: ArrayList<RecentBuyModel>,
//) : RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>() {
//    override fun onCreateViewHolder(
//        parent: ViewGroup, viewType: Int
//    ): RecentViewHolder {
//        val binding = RecentBuyItemBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return RecentViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(
//        holder: RecentViewHolder, position: Int
//    ) {
//        holder.binding.recentBuyItem = recentBuyList[position]
//    }
//
//    override fun getItemCount(): Int = recentBuyList.size
//
//    class RecentViewHolder(private val binding: RecentBuyItemBinding) :RecyclerView.ViewHolder(binding.root) {
//
//    }
//
//
//}