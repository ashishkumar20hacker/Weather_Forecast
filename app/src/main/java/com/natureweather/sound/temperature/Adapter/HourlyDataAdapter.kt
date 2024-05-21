package com.natureweather.sound.temperature.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils.checkAndSetTemperature
import com.natureweather.sound.temperature.Extras.Utils.checkAndSetTime
import com.natureweather.sound.temperature.Model.HourlyData
import com.natureweather.sound.temperature.R


class HourlyDataAdapter(var context: Context, hourlyData: ArrayList<HourlyData>, size: Int) :
    RecyclerView.Adapter<HourlyDataAdapter.ViewHolder>() {
    var hourlyData: ArrayList<HourlyData>
    var size: Int
    lateinit var preferences: SharePreferences

    init {
        this.hourlyData = hourlyData
        this.size = size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.todays_report_item, parent, false)
        preferences = SharePreferences(context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.setText(checkAndSetTime(context, preferences, hourlyData[position].time!!))
        holder.temperature.setText(checkAndSetTemperature(preferences,
            hourlyData[position].temperature!!.substring(
                0,
                hourlyData[position].temperature!!.length - 1
            )
        ))
        holder.status.setImageResource(hourlyData[position].statusImage)
    }

    override fun getItemCount(): Int {
        return size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView
        var temperature: TextView
        var status: ImageView

        init {
            time = itemView.findViewById<TextView>(R.id.time)
            temperature = itemView.findViewById<TextView>(R.id.temp)
            status = itemView.findViewById<ImageView>(R.id.status_iv)
        }
    }
}

