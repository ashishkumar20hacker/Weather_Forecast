package com.natureweather.sound.temperature.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.natureweather.sound.temperature.Activity.SplashActivity
import com.natureweather.sound.temperature.Extras.AppAsyncTask
import com.natureweather.sound.temperature.Extras.AppInterfaces
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_ADDRESS
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_POSITION
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils.convertAddress
import com.natureweather.sound.temperature.R
import org.jsoup.select.Elements


class LocationsAdapter(var context: Activity, list: MutableList<String>) :
    RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {
    var list: MutableList<String>
    var mPos = 0
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
        holder.cityName.setText(list[position])
        mPos = preferences!!.getInt(SELECTED_POSITION, 0)
        val latlng: String = convertAddress(context, list[position])
        try {
            val searchWeather =
                AppAsyncTask.SearchWeather(context, object : AppInterfaces.SearchWeatherInterface {
                    override fun getWeatherDetails(scrapedElementsList: Elements?) {
                        if (!scrapedElementsList!!.isEmpty()) {
                            val temperature =
                                scrapedElementsList.select("span[class=CurrentConditions--tempValue--MHmYY]")
                                    .text()
                            val condition =
                                scrapedElementsList.select("div[class=CurrentConditions--phraseValue--mZC_p]")
                                    .text()
                            val maxmin =
                                scrapedElementsList.select("div[class=CurrentConditions--tempHiLoValue--3T1DG]")
                                    .text()
                            val max = "Max.: " + maxmin.substring(
                                4,
                                7
                            )
                            val min =
                                "  Min.: " + maxmin.substring(
                                    maxmin.length - 3, maxmin.length
                                )
                            holder.conditionTv.text = condition
                            holder.max.text = max
                            holder.min.text = min
                            holder.temperature.text = temperature
                            setContent(holder)
                        }
                    }
                }, latlng)
            searchWeather.execute()
        } catch (e: Exception) {
            println("exception>>>>" + e.message)
        }
        holder.itemView.setOnClickListener {
            mPos = position
            preferences!!.putString(SELECTED_ADDRESS, list[mPos])
            preferences!!.putInt(SELECTED_POSITION, position)
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
        if ( binding.conditionTv.text.toString().lowercase().contains("rain") ||  binding.conditionTv.text.toString().lowercase()
                .contains("shower") ||  binding.conditionTv.text.toString().lowercase().contains("drizzle")
        ) {
            binding.conditionIv.setImageResource(R.drawable.light_rain)
        } else if ( binding.conditionTv.text.toString().lowercase().contains("sunny") ||  binding.conditionTv.text.toString().lowercase()
                .contains("smoke")||  binding.conditionTv.text.toString().lowercase()
                .contains("clear")
        ) {
            binding.conditionIv.setImageResource(R.drawable.sunny_img)
        } else if ( binding.conditionTv.text.toString().lowercase().contains("night")) {
            binding.conditionIv.setImageResource(R.drawable.night_img)
        } else if ( binding.conditionTv.text.toString().lowercase().contains("storm")) {
            binding.conditionIv.setImageResource(R.drawable.storme_img)
        }
    }
}
