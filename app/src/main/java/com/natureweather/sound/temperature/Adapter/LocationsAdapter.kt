package com.natureweather.sound.temperature.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_ADDRESS
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Model.LocationModel
import com.natureweather.sound.temperature.R


class LocationsAdapter(var context: Activity, list: MutableList<LocationModel>) :
    RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {
    var list: MutableList<LocationModel>
    var mPos = -1
    var preferences: SharePreferences? = null

    init {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_locations, parent, false)
        preferences = SharePreferences(context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locality = list[position].locality
        val temperature = list[position].temperature
        val condition = list[position].condition
        val max = list[position].maxTemp
        val min = list[position].minTemp

        holder.cityName.setText(locality)
        if (locality.equals(preferences!!.getString(SELECTED_ADDRESS, ""))) {
            mPos = position
        }
        holder.conditionTv.text = condition
        holder.max.text = max
        holder.min.text = min
        holder.temperature.text = temperature
        Utils.getConditionImage(holder.conditionTv.text.toString())?.let { holder.conditionIv.setImageResource(it) }

        holder.itemView.setOnClickListener {
            mPos = position
            preferences!!.putString(SELECTED_ADDRESS, locality)
            notifyDataSetChanged()
        }

        if (mPos == position) {
            holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.selected))
        } else {
            holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.grey))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cityName: TextView
        var conditionTv: TextView
        var temperature: TextView
        var max: TextView
        var min: TextView
        var conditionIv: ImageView
        var cardView: CardView

        init {
            cityName = itemView.findViewById<TextView>(R.id.cityName)
            conditionTv = itemView.findViewById<TextView>(R.id.condition_tv)
            temperature = itemView.findViewById<TextView>(R.id.temperature)
            max = itemView.findViewById<TextView>(R.id.maxTemp)
            min = itemView.findViewById<TextView>(R.id.minTemp)
            conditionIv = itemView.findViewById<ImageView>(R.id.condition_iv)
            cardView = itemView.findViewById<CardView>(R.id.card)
        }
    }

    private fun setContent(binding: ViewHolder) {
        Utils.getConditionImage(binding.conditionTv.text.toString())?.let { binding.conditionIv.setImageResource(it) }
            }
}
