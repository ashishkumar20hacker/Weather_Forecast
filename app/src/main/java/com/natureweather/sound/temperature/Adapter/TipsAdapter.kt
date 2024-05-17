package com.natureweather.sound.temperature.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Model.TipsModel
import com.natureweather.sound.temperature.R


class TipsAdapter(
    var activity: Activity,
    tipsModelArrayList: List<TipsModel>,
) :
    RecyclerView.Adapter<TipsAdapter.ViewHolder>() {
    var tipsModelArrayList: MutableList<TipsModel>

    init {
        this.tipsModelArrayList = tipsModelArrayList.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tips, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(tipsModelArrayList[position].title)
        holder.tip.setText(tipsModelArrayList[position].tip)
    }

    override fun getItemCount(): Int {
        return tipsModelArrayList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var tip: TextView
        var cardView: LinearLayout

        init {
            title = itemView.findViewById<TextView>(R.id.title)
            tip = itemView.findViewById<TextView>(R.id.tip)
            cardView = itemView.findViewById<LinearLayout>(R.id.card)
        }
    }
}

