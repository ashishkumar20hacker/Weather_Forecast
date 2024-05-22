package com.natureweather.sound.temperature.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnTouchListener
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.natureweather.sound.temperature.Activity.SplashActivity.Companion.currentCondition
import com.natureweather.sound.temperature.Adapter.HourlyDataAdapter
import com.natureweather.sound.temperature.Adapter.TenDaysDataAdapter
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_ADDRESS
import com.natureweather.sound.temperature.Extras.DataFetcher
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.checkAndSetPressure
import com.natureweather.sound.temperature.Extras.Utils.checkAndSetTime
import com.natureweather.sound.temperature.Extras.Utils.checkAndSetVisibility
import com.natureweather.sound.temperature.Extras.Utils.convertAddress
import com.natureweather.sound.temperature.Extras.Utils.requestLocationPermission
import com.natureweather.sound.temperature.Model.HourlyData
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivityDetailedWeatherBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs


class DetailedWeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailedWeatherBinding
    var feelsLike: String = ""
    var windSpeed: String = ""
    var uv: String = ""
    var humidity: String = ""
    var visibility: String = ""
    var airpressure: String = ""
    var sunrise: String = ""
    var sunset: String = ""
    var latlong: String = ""
    var address: String = ""
    var condition: String = ""
    var temperature: String = ""
    var max: String = ""
    lateinit var preferences: SharePreferences
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityDetailedWeatherBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        preferences = SharePreferences(this)
        val c = Calendar.getInstance()
        val monthName = arrayOf(
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"
        )
        val month = monthName[c[Calendar.MONTH]]
        println("Month name:$month")
        val date = c[Calendar.DATE]
        binding.currentDate.setText(" $month,$date")
        binding.home.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            finish()
        })
        binding.selectedLocation.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    LocationsActivity::class.java
                )
            )
        })
        binding.seekArc.setOnTouchListener({ v, event ->
            true // Consume the touch event, preventing any action
        })

    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        address = preferences.getString(SELECTED_ADDRESS, "")!!
        if (!address!!.isEmpty()) {
            latlong = convertAddress(this, address)
            binding.selectedLocation.setText(address)
            lifecycleScope.launch(Dispatchers.IO) {
                runOnUiThread {
                    binding.gifLoading.visibility = View.VISIBLE
                }
                getWeatherDetails(latlong)
                getHourlyDetails(latlong)
                getTenDaysDetails(latlong)
                percipitation
                runOnUiThread {
                    binding.gifLoading.visibility = View.GONE
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                lastLocation
            }
        }
        binding.menu.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.menu)
            // implement on menu item click Listener
            popupMenu.setOnMenuItemClickListener(object : OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.notification_nav -> {
                            Toast.makeText(
                                this@DetailedWeatherActivity,
                                "Notifications",
                                Toast.LENGTH_SHORT
                            ).show()
                            return true
                        }

                        R.id.unit_nav -> {
                            Utils.showUnitsDialog(this@DetailedWeatherActivity)
                            return true
                        }

                        R.id.widgets_nav -> {
                            Toast.makeText(
                                this@DetailedWeatherActivity,
                                "Widgets",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            return true
                        }

                        R.id.rate_nav -> {
                            Utils.rateApp(this@DetailedWeatherActivity)
                            return true
                        }

                        R.id.share_nav -> {
                            Utils.shareApp(this@DetailedWeatherActivity)
                            return true
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }
    }

    private val percipitation: Unit
        private get() {
            DataFetcher.searchPrecipitationWeather(latlong) { elements ->
                // Handle the fetched weather data here
                if (elements != null) {
                    // Process the elements
                    if (!elements.isEmpty()) {
                        runOnUiThread {
                            val url = elements.attr("style").replace("background-image:url(", "https:").replace(")","")
                            Glide.with(this)
                                .load(url)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                                .into(binding.imageViewMap)
                            binding.precipitationLl.visibility = View.VISIBLE
                        }
                    }
                } else {
                    // Handle error case
                    println("Something went wrong!!")
                }
            }
        }

    fun setSunPosition(address: String) {
        var selectedDiv: Elements
        var search = removeDirectionsAndGetLocality(address).replace(" ", "_")
        val document = Jsoup.connect(DataFetcher.CURRENT_TIME_URL + search).get()
        selectedDiv = document.select("time[id=clock]")
        var formattedTime = selectedDiv.text().replace(":", "").substring(0, 4)

        try {
            val startTime = sunrise.replace(":", "").replace("Sun Rise ", "")
                .toInt() // Start time in milliseconds

            val endTime =
                sunset.replace(":", "").replace("Sunset ", "").toInt() // End time in milliseconds


            val currentTime =
                formattedTime.toInt()// Current time in milliseconds
            val maxProgress = 100 // Adjust the maximum progress value as needed

            val progress = when {
                currentTime < startTime -> 0
                currentTime > endTime -> maxProgress
                else -> ((currentTime - startTime) * maxProgress / (endTime - startTime)).coerceIn(
                    0,
                    100
                )
            }
            runOnUiThread {
                binding.seekArc.progress = abs(progress.toInt())
                binding.specificLl.setVisibility(View.VISIBLE)
            }
        } catch (e: NumberFormatException) {
            Log.e(TAG, "setSunPosition: ", e)
        }
    }

    fun removeDirectionsAndGetLocality(locality: String): String {
        // Regular expression to match common directions
        val directionsPattern = Regex(
            "\\b(north|south|east|west|northeast|northwest|southeast|southwest)\\b",
            RegexOption.IGNORE_CASE
        )

        // Remove directions from the locality string
        val localityWithoutDirections = directionsPattern.replace(locality, "")

        // Get locality after removing text before comma
        val localityParts = localityWithoutDirections.split(',')
        val localityCleaned = if (localityParts.size > 1) {
            localityParts[1].trim()
        } else {
            localityWithoutDirections.trim()
        }

        return localityCleaned
    }

    private fun getTenDaysDetails(latlongs: String?) {
        DataFetcher.searchTenDayWeather(latlongs!!) { elements ->
            // Handle the fetched weather data here
            if (elements != null) {
                // Process the elements
                if (!elements.isEmpty()) {
                    getTenDayData(elements)
                }
            } else {
                // Handle error case
                println("Something went wrong!!")
            }
        }
    }

    private fun getTenDayData(scrapedElementsList: Elements) {
        var tenData: HourlyData
        val tenDaysDataArrayList = ArrayList<HourlyData>()
        for (i in 1 until scrapedElementsList.size) {
            tenData = HourlyData()
            val daydate =
                scrapedElementsList[i].select("h2[class=DetailsSummary--daypartName--kbngc]").text()
            var day = ""
            var date = ""
            if (daydate.isNotEmpty()) {
                day = if (daydate.contains("Tue")) {
                    daydate.substring(0, 3) + "sday"
                } else if (daydate.contains("Wed")) {
                    daydate.substring(0, 3) + "nesday"
                } else if (daydate.contains("Thu")) {
                    daydate.substring(0, 3) + "rsday"
                } else if (daydate.contains("Sat")) {
                    daydate.substring(0, 3) + "urday"
                } else {
                    daydate.substring(0, 3) + "day"
                }
                date = "" + daydate.substring(4, daydate.length)
            }
            tenData.day = day
            tenData.date = date
            val temp = scrapedElementsList[i].select("div[data-testid=detailsTemperature]").text()
            tenData.temperature = temp.replace("°".toRegex(), "°C")
            //            tenData.setTemperature(scrapedElementsList.get(i).select("div[data-testid=detailsTemperature]").text());
            val status =
                scrapedElementsList[i].select("span[class=DetailsSummary--extendedData--307Ax]")
                    .text()
            if (status.lowercase().contains("rain") || status.contains("showers")) {
                tenData.statusImage = R.drawable.rain
            } else if (status.lowercase().contains("storms")) {
                tenData.statusImage = R.drawable.storme_img
            } else {
                tenData.statusImage = R.drawable.sunny_img
            }
            tenDaysDataArrayList.add(tenData)
        }

        runOnUiThread {
            binding.nextForecastRv.setLayoutManager(LinearLayoutManager(this@DetailedWeatherActivity, LinearLayoutManager.VERTICAL, false))
            val tenadapter = TenDaysDataAdapter(this, tenDaysDataArrayList)
            binding.nextForecastRv.setAdapter(tenadapter)
            binding.nextForecastLl.visibility = View.VISIBLE
        }
    }

    private fun setContent() {
        if (condition.lowercase().contains("rain") || condition.lowercase()
                .contains("shower") || condition.lowercase()
                .contains("drizzle") || condition.lowercase().contains("cloudy")
        ) {
            binding.conditionIv.setImageResource(R.drawable.light_rain)
            binding.mainLayout.setBackgroundResource(R.drawable.strom_bg_img)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.storm_gif).into(binding.conditionGif)
        } else if (condition.lowercase().contains("sunny") || condition.lowercase()
                .contains("smoke") || condition.lowercase()
                .contains("clear") || condition.lowercase().contains("haze")
        ) {
            binding.conditionIv.setImageResource(R.drawable.sunny_img)
            binding.mainLayout.setBackgroundResource(R.drawable.sunny_bg_img)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.sunny_gif).into(binding.conditionGif)
        } else if (condition.lowercase().contains("night")) {
            binding.conditionIv.setImageResource(R.drawable.night_img)
            binding.mainLayout.setBackgroundResource(R.drawable.night_bg_img)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.cloudy_night_gif).into(binding.conditionGif)
        } else if (condition.lowercase().contains("storm")) {
            binding.conditionIv.setImageResource(R.drawable.storme_img)
            binding.mainLayout.setBackgroundResource(R.drawable.strom_bg_img)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.storm_gif).into(binding.conditionGif)
        }
    }

    private fun getHourlyDetails(latlongs: String?) {
        DataFetcher.searchHourlyWeather(latlongs!!) { elements ->
            // Handle the fetched weather data here
            if (elements != null) {
                // Process the elements
                if (!elements.isEmpty()) {
                    getHourlyData(elements)
                }
            } else {
                // Handle error case
                println("Something went wrong!!")
            }
        }
    }

    private fun getHourlyData(scrapedElementsList: Elements) {
        var hourlyData: HourlyData
        var size = 0
        val hourlyDataArrayList = ArrayList<HourlyData>()
        for (i in scrapedElementsList.indices) {
            hourlyData = HourlyData()
            hourlyData.time =
                scrapedElementsList[i].select("h2[class=DetailsSummary--daypartName--kbngc]").text()
            val temp = scrapedElementsList[i].select("span[data-testid=TemperatureValue]").text()
            hourlyData.temperature = temp.replace("°".toRegex(), "°C")
            val status =
                scrapedElementsList[i].select("span[class=DetailsSummary--extendedData--307Ax]")
                    .text()
            if (status.lowercase().contains("rain") || status.contains("showers")) {
                hourlyData.statusImage = R.drawable.rain
            } else if (status.lowercase().contains("storms")) {
                hourlyData.statusImage = R.drawable.storme_img
            } else {
                hourlyData.statusImage = R.drawable.sunny_img
            }
            hourlyDataArrayList.add(hourlyData)
        }
        for (i in hourlyDataArrayList.indices) {
            val time = hourlyDataArrayList[i].time
            if (!time.isNullOrEmpty() && (time.contains("23:00") || time.contains("23:30"))) {
                size = i + 1
                break
            }
        }

        runOnUiThread {
            binding.weatherDetailsRv.setLayoutManager(LinearLayoutManager(this@DetailedWeatherActivity, LinearLayoutManager.HORIZONTAL, false))
            val adapter = HourlyDataAdapter(this, hourlyDataArrayList, size)
            binding.weatherDetailsRv.setAdapter(adapter)
            binding.currentForecastLl.setVisibility(View.VISIBLE)
        }
    }

    private fun getWeatherDetails(latlongs: String?) {
        DataFetcher.searchWeather(latlongs!!) { scrapedElementsList ->
            // Handle the fetched weather data here
            if (scrapedElementsList != null) {
                // Process the elements
                if (!scrapedElementsList.isEmpty()) {
                    temperature =
                        scrapedElementsList.select("span[class=CurrentConditions--tempValue--MHmYY]")
                            .text()
                    condition =
                        scrapedElementsList.select("div[class=CurrentConditions--phraseValue--mZC_p]")
                            .text()
                    val maxmin =
                        scrapedElementsList.select("div[class=CurrentConditions--tempHiLoValue--3T1DG]")
                            .text()
                    max = "Max.: " + maxmin.substring(4, 7) + "  Min.: " + maxmin.substring(
                        maxmin.length - 3, maxmin.length
                    )
                    feelsLike =
                        scrapedElementsList.select("span[class=TodayDetailsCard--feelsLikeTempValue--2icPt]")
                            .text()
                    uv = scrapedElementsList.select("span[data-testid=UVIndexValue]").text()
                    windSpeed = scrapedElementsList.select("span[data-testid=Wind]").text()
                    humidity =
                        scrapedElementsList.select("span[data-testid=PercentageValue]")
                            .text()
                    visibility =
                        scrapedElementsList.select("span[data-testid=VisibilityValue]")
                            .text()
                    airpressure =
                        scrapedElementsList.select("span[data-testid=PressureValue]").text()
                    sunrise =
                        scrapedElementsList.select("div[data-testid=SunriseValue]").text()
                    sunset =
                        scrapedElementsList.select("div[data-testid=SunsetValue]").text()
                    runOnUiThread {
                        currentCondition = condition
                        binding.currentTempOne.text = Utils.checkAndSetTemperature(preferences, temperature)
                        binding.conditionTvOne.text = condition
                        binding.maxMinOne.text = "Max.: ${Utils.checkAndSetTemperature(preferences, maxmin.substring(4, 7))} Min.: ${Utils.checkAndSetTemperature(preferences, maxmin.substring(maxmin.length - 3, maxmin.length))}"

                        binding.weatherLl.visibility = View.VISIBLE

                        binding.feelsLike.setText(Utils.checkAndSetTemperature(preferences, feelsLike))
                        binding.uv.setText(uv)
                        binding.windSpeedTxt.setText(Utils.checkAndSetWindSpeed(this,preferences, windSpeed.replace("Wind Direction", "").replace("km/h", "").trim()))
                        binding.humidity.setText(humidity)
                        binding.visibility.setText(checkAndSetVisibility(this, preferences, visibility.replace("km", "").trim()))
                        binding.sunriseTv.setText(
                            """
                                    ${checkAndSetTime(this, preferences,sunrise.replace("Sun Rise ", ""))}
                                    Sunrise
                                    """.trimIndent()
                        )
                        binding.sunsetTv.setText(
                            """
                                    ${checkAndSetTime(this, preferences,sunset.replace("Sunset", ""))}
                                    Sunset
                                    """.trimIndent()
                        )
                        if (airpressure.contains("Arrow Down")) {
                            binding.airPressure.setText(checkAndSetPressure(this, preferences, airpressure.replace("Arrow Down ", "").replace("mb", "").trim()))
                            binding.airPressure.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.arrow_down,
                                0,
                                0,
                                0
                            )
                        } else {
                            binding.airPressure.setText(checkAndSetPressure(this, preferences, airpressure.replace("Arrow Up ", "").replace("mb", "").trim()))
                            binding.airPressure.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.arrow_up,
                                0,
                                0,
                                0
                            )
                        }
                        setContent()
                    }
                    setSunPosition(address)
                }
            } else {
                // Handle error case
                println("Something went wrong!!")
            }
        }
    }


    private val lastLocation: Unit
        private get() {
            if (Utils.isLocationPermissionGranted(this)) {
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val isGPS = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                Log.println(Log.ASSERT, TAG, "isGPS: $isGPS")
                if (isGPS) {
                    locationListener = LocationListener { location ->
                        val latitude = location.latitude
                        val longitude = location.longitude
                        println("location>>>$latitude,$longitude")
                        latlong = "$latitude,$longitude"
                        val geocoder = Geocoder(this@DetailedWeatherActivity, Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                            val addresse = addresses!![0].subLocality
                            val cityName = addresses[0].locality
                            val stateName = addresses[0].adminArea
                            preferences.putString(SELECTED_ADDRESS, "$addresse, $cityName")
                            address = preferences.getString(SELECTED_ADDRESS, "")!!
                            println("add>>$address")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        getWeatherDetails(latlong)
                        getHourlyDetails(latlong)
                        getTenDaysDetails(latlong)
                        percipitation
                    }

                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestLocationPermission(this)
                        return
                    }
                    locationManager!!.requestSingleUpdate(
                        LocationManager.NETWORK_PROVIDER,
                        locationListener!!,
                        Looper.myLooper()
                    )
                }
            } else {
                requestLocationPermission(this)
            }
        }

    companion object {
        private const val TAG = "DetailedWeatherActivity"
    }
}