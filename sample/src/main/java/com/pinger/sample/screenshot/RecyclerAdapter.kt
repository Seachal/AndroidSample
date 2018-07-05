package com.pinger.sample.screenshot

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pinger.sample.R

/**
 * @author Pinger
 * @since 18-7-5 上午11:28
 *
 */

class RecyclerAdapter(private val context: Context, val data: ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerHolder {
        return RecyclerHolder(LayoutInflater.from(context).inflate(R.layout.holder_recycler,null))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        holder.tvDesc.text = data[position]
    }


    class RecyclerHolder(itemView :View) : RecyclerView.ViewHolder(itemView) {
        val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc)!!
    }
}