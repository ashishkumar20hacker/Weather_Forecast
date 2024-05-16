package com.natureweather.sound.temperature.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Model.HourlyData
import com.natureweather.sound.temperature.R


class TenDaysDataAdapter(var context: Context, tenDaysData: ArrayList<HourlyData>) :
    RecyclerView.Adapter<TenDaysDataAdapter.ViewHolder>() {
    var tenDaysData: ArrayList<HourlyData>

    init {
        this.tenDaysData = tenDaysData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.ten_days_report_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (tenDaysData.size != 0) {
            holder.day.setText(tenDaysData[position].day)
            holder.date.setText(tenDaysData[position].date)
            holder.temperature.setText(tenDaysData[position].temperature)
            holder.status.setImageResource(tenDaysData[position].statusImage)
        } else {
            println("No Data Found")
        }
    }

    override fun getItemCount(): Int {
        return if (tenDaysData.size < 10) {
            tenDaysData.size
        } else {
            10
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var day: TextView
        var date: TextView
        var temperature: TextView
        var status: ImageView

        init {
            day = itemView.findViewById<TextView>(R.id.day)
            date = itemView.findViewById<TextView>(R.id.date)
            temperature = itemView.findViewById<TextView>(R.id.temp)
            status = itemView.findViewById<ImageView>(R.id.status_iv)
        }
    }
}
