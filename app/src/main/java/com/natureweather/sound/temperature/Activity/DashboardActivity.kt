package com.natureweather.sound.temperature.Activity

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.natureweather.sound.temperature.Activity.SplashActivity.Companion.currentCondition
import com.natureweather.sound.temperature.Extras.ConnectionDetector
import com.natureweather.sound.temperature.Extras.Constants
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_ADDRESS
import com.natureweather.sound.temperature.Extras.DataFetcher
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.checkAndSetTemperature
import com.natureweather.sound.temperature.Extras.Utils.convertAddress
import com.natureweather.sound.temperature.Extras.Utils.getConditionBgImage
import com.natureweather.sound.temperature.Extras.Utils.getConditionGif
import com.natureweather.sound.temperature.Extras.Utils.getConditionImage
import com.natureweather.sound.temperature.Extras.Utils.getTipsForCondition
import com.natureweather.sound.temperature.Extras.Utils.isLocationPermissionGranted
import com.natureweather.sound.temperature.Extras.Utils.nextActivity
import com.natureweather.sound.temperature.Extras.Utils.rateApp
import com.natureweather.sound.temperature.Extras.Utils.requestLocationPermission
import com.natureweather.sound.temperature.Extras.Utils.shareApp
import com.natureweather.sound.temperature.Extras.Utils.showUnitsDialog
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivityDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


class DashboardActivity : AppCompatActivity() {

    private val TAG = "DashboardActivity"
    lateinit var binding: ActivityDashboardBinding
    lateinit var preferences: SharePreferences
    var connectionDetector: ConnectionDetector = ConnectionDetector(this)
    var latlong: String = ""
    var address: String = ""

    var condition: String = ""
    var temperature: String = ""
    var max: String = ""

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = SharePreferences(this)
        preferences.putBoolean(Constants.isFirstRun, value = false)

        backButtonPressedListener()

        binding.selectedLocation.setOnClickListener {
            nextActivity(this@DashboardActivity, LocationsActivity::class.java)
        }

        binding.weatherLl.setOnClickListener {
            nextActivity(this@DashboardActivity, DetailedWeatherActivity::class.java)
        }

        binding.soundsLl.setOnClickListener {
            nextActivity(this@DashboardActivity, SoundsActivity::class.java)
        }

    }

    private fun backButtonPressedListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                exitDialog()
            }
        })
    }

    private fun exitDialog() {
        val dialog = Dialog(this@DashboardActivity, R.style.SheetDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = LayoutInflater.from(this)
        val lay: View = inflater.inflate(R.layout.exit_dialog, null)
        val goback: TextView
        val exit: TextView
        val rateUs: ImageView
        goback = lay.findViewById<TextView>(R.id.goback)
        exit = lay.findViewById<TextView>(R.id.exit)
        rateUs = lay.findViewById<ImageView>(R.id.rate_us)
        dialog.setContentView(lay)
        goback.setOnClickListener { dialog.dismiss() }
        exit.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }
        rateUs.setOnClickListener {
            dialog.dismiss()
            rateApp(this@DashboardActivity)
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (!isLocationPermissionGranted(this)) {
            requestLocationPermission(this)
        } else {
            address = preferences.getString(SELECTED_ADDRESS, "")!!
            if (connectionDetector.isConnectingToInternet) {
                if (!address.isEmpty()) {
                    latlong = convertAddress(this, address)
                    binding.selectedLocation.text = address

                    lifecycleScope.launch(Dispatchers.IO) {
                        getWeatherDetails(latlong)
                    }

                } else {
                    getLastLocation()
                }
                binding.weatherLl.setEnabled(true)
            } else {
                binding.weatherLl.setEnabled(false)
                Toast.makeText(this, "Please connect to internet!!", Toast.LENGTH_SHORT).show()
            }


            binding.menu.setOnClickListener { view ->
                val popupMenu = PopupMenu(this, view)
                popupMenu.inflate(R.menu.menu)
                // implement on menu item click Listener
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        when (item?.itemId) {
                            R.id.notification_nav -> {
                                Toast.makeText(
                                    this@DashboardActivity,
                                    "Notifications",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return true
                            }

                            R.id.unit_nav -> {
                                showUnitsDialog(this@DashboardActivity)
                                return true
                            }

                            R.id.widgets_nav -> {
                                Toast.makeText(
                                    this@DashboardActivity,
                                    "Widgets",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return true
                            }

                            R.id.rate_nav -> {
                                rateApp(this@DashboardActivity)
                                return true
                            }

                            R.id.share_nav -> {
                                shareApp(this@DashboardActivity)
                                return true
                            }
                        }
                        return false
                    }
                })
                popupMenu.show()
            }
        }
    }

    private fun fillList() {
        val tipsModelArrayList = getTipsForCondition(condition)

        if (tipsModelArrayList.size > 1) {
            binding.title.text = tipsModelArrayList[0].title
            binding.tip.text = tipsModelArrayList[0].tip
            binding.title2.text = tipsModelArrayList[1].title
            binding.tip2.text = tipsModelArrayList[1].tip
            binding.card.visibility = View.VISIBLE
            binding.card2.visibility = View.VISIBLE
            binding.card3.visibility = View.GONE
        } else {
            binding.title3.text = tipsModelArrayList[0].title
            binding.tip3.text = tipsModelArrayList[0].tip
            binding.card.visibility = View.GONE
            binding.card2.visibility = View.GONE
            binding.card3.visibility = View.VISIBLE
        }

        binding.relative.setOnClickListener {
            nextActivity(this@DashboardActivity, TipsActivity::class.java)
        }

    }

    private fun getWeatherDetails(latlongs: String) {
        runOnUiThread {
            binding.mainLayout.visibility = View.GONE
            binding.gifLoading.visibility = View.VISIBLE
        }
        DataFetcher.searchWeather(latlongs) { elements ->
            // Handle the fetched weather data here
            if (elements != null) {
                // Process the elements
                if (!elements.isEmpty()) {
                    temperature =
                        elements.select("span[class=CurrentConditions--tempValue--MHmYY]")
                            .text()
                    condition =
                        elements.select("div[class=CurrentConditions--phraseValue--mZC_p]")
                            .text()
                    val maxmin =
                        elements.select("div[class=CurrentConditions--tempHiLoValue--3T1DG]")
                            .text()
                    max = "Max.: " + maxmin.substring(
                        4,
                        7
                    ) + "  Min.: " + maxmin.substring(maxmin.length - 3, maxmin.length)

                    runOnUiThread {
                        currentCondition = condition
                        binding.temperatureTv.text =
                            checkAndSetTemperature(preferences, temperature)
                        binding.conditionTv.text = condition
                        binding.maxMin.text =
                            "Max.: ${
                                checkAndSetTemperature(
                                    preferences,
                                    maxmin.substring(4, 7)
                                )
                            } Min.: ${
                                checkAndSetTemperature(
                                    preferences,
                                    maxmin.substring(maxmin.length - 3, maxmin.length)
                                )
                            }"
                        setContent()
                        fillList()
                    }
                }
            } else {
                // Handle error case
                println("Something went wrong!!")
            }
        }
    }

    private fun setContent() {
        getConditionImage(condition)?.let { binding.conditionIv.setImageResource(it) }
        getConditionBgImage(condition, true)?.let { binding.mainLayout.setBackgroundResource(it) }
        Glide.with(this).load(getConditionGif(condition)).into(binding.conditionGif)
        binding.conditionGif.setVisibility(View.VISIBLE)

        binding.gifLoading.setVisibility(View.GONE)
        binding.mainLayout.setVisibility(View.VISIBLE)
    }

    private fun getLastLocation() {
        if (isLocationPermissionGranted(this)) {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPS = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            Log.println(Log.ASSERT, TAG, "isGPS: $isGPS")
            if (isGPS) {
                locationListener = LocationListener { location ->
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val geocoder = Geocoder(this@DashboardActivity, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        val addresse = addresses!![0].subLocality
                        val cityName = addresses[0].locality
                        val stateName = addresses[0].adminArea
                        preferences.putString(SELECTED_ADDRESS, "$addresse, $cityName")
                        address = preferences.getString(SELECTED_ADDRESS, "")!!
                        preferences.addStringItem("$addresse , $cityName")
                        println("add>>$address")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    println("location>>>$latitude,$longitude")
                    latlong = "$latitude,$longitude"
                    lifecycleScope.launch(Dispatchers.IO) {
                        getWeatherDetails(latlong)
                    }
                    binding.weatherLl.setEnabled(true)
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
}