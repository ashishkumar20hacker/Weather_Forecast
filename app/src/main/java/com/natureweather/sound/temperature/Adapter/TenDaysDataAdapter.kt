package com.natureweather.sound.temperature.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Model.HourlyData
import com.natureweather.sound.temperature.R


class TenDaysDataAdapter(var context: Context, tenDaysData: ArrayList<HourlyData>) :
    RecyclerView.Adapter<TenDaysDataAdapter.ViewHolder>() {
    var tenDaysData: ArrayList<HourlyData>
    lateinit var preferences: SharePreferences

    init {
        this.tenDaysData = tenDaysData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.ten_days_report_item, parent, false)
        preferences = SharePreferences(context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (tenDaysData.size != 0) {
            if (tenDaysData[position].temperature!!.contains("--/--")) {
                holder.temperature.setText("--/--")
            } else {
                holder.temperature.setText(
                    "${
                        Utils.checkAndSetTemperature(
                            preferences,
                            tenDaysData[position].temperature!!.substring(0, 3)
                        )
                    }/${
                        Utils.checkAndSetTemperature(
                            preferences,
                            tenDaysData[position].temperature!!.substring(
                                4,
                                tenDaysData[position].temperature!!.length - 1 
                            ).replace("/","")
                        )
                    }"
                )
            }
            holder.day.setText(tenDaysData[position].day)
            holder.date.setText(tenDaysData[position].date)
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
