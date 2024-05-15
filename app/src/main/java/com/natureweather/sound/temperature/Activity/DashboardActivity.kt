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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.natureweather.sound.temperature.Activity.SplashActivity.Companion.condition
import com.natureweather.sound.temperature.Activity.SplashActivity.Companion.max
import com.natureweather.sound.temperature.Activity.SplashActivity.Companion.temperature
import com.natureweather.sound.temperature.Adapter.TipsAdapter
import com.natureweather.sound.temperature.Adapter.TipsAdapter.OnTipClickListener
import com.natureweather.sound.temperature.Extras.AppAsyncTask
import com.natureweather.sound.temperature.Extras.AppInterfaces
import com.natureweather.sound.temperature.Extras.ConnectionDetector
import com.natureweather.sound.temperature.Extras.Constants
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_ADDRESS
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.convertAddress
import com.natureweather.sound.temperature.Extras.Utils.convertToFarenhiet
import com.natureweather.sound.temperature.Extras.Utils.isLocationPermissionGranted
import com.natureweather.sound.temperature.Extras.Utils.nextActivity
import com.natureweather.sound.temperature.Extras.Utils.rateApp
import com.natureweather.sound.temperature.Extras.Utils.requestLocationPermission
import com.natureweather.sound.temperature.Extras.Utils.shareApp
import com.natureweather.sound.temperature.Model.TipsModel
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivityDashboardBinding
import com.natureweather.sound.temperature.stacklayoutmanager.DefaultAnimation
import com.natureweather.sound.temperature.stacklayoutmanager.DefaultLayout
import com.natureweather.sound.temperature.stacklayoutmanager.StackLayoutManager
import org.jsoup.select.Elements
import java.io.IOException
import java.util.Locale


class DashboardActivity : AppCompatActivity() {

    private val TAG = "DashboardActivity"
    lateinit var binding: ActivityDashboardBinding
    lateinit var preferences: SharePreferences
    var connectionDetector: ConnectionDetector = ConnectionDetector(this)
    var latlong: String = ""
    var address: String = ""

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

        binding.soundsLl.setOnClickListener {
            nextActivity(this@DashboardActivity, SoundsActivity::class.java)
        }

        binding.soundsLl.setOnClickListener {
            nextActivity(this@DashboardActivity, SoundsActivity::class.java)
        }

        if (connectionDetector.isConnectingToInternet) {
            if (!address.isEmpty()) {
                latlong = convertAddress(this, address)
                binding.selectedLocation.text = address
                getWeatherDetails(latlong)
            } else {
                getLastLocation()
                getWeatherDetails(latlong)
            }
            binding.weatherLl.setEnabled(true)
        } else {
            binding.weatherLl.setEnabled(false)
            Toast.makeText(this, "Please connect to internet!!", Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(this@DashboardActivity, "Units", Toast.LENGTH_SHORT)
                                    .show()
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
        val manager = StackLayoutManager(
            StackLayoutManager.ScrollOrientation.BOTTOM_TO_TOP, 2,
            DefaultAnimation::class.java,
            DefaultLayout::class.java
        )
        manager.setPagerMode(true) // Set to true if you want a ViewPager-like effect
        manager.setPagerFlingVelocity(2000) // Set the minimum fling velocity to trigger page changes
        manager.setItemOffset(150)
        binding.dashTipsRv.setLayoutManager(manager)
        val tipsModelArrayList = ArrayList<TipsModel>()
        tipsModelArrayList.add(
            TipsModel(
                "Tip ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of the ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "of the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        val tipsAdapter =
            TipsAdapter(this, tipsModelArrayList, "dashboard", object : OnTipClickListener {
                override fun onTipclick() {

                }
            })
        binding.dashTipsRv.setAdapter(tipsAdapter)
    }

    private fun getWeatherDetails(latlongs: String) {
        binding.mainLayout.setVisibility(View.GONE)
        binding.gifLoading.setVisibility(View.VISIBLE)
        try {
            val searchWeather =
                AppAsyncTask.SearchWeather(this, object : AppInterfaces.SearchWeatherInterface {
                    override fun getWeatherDetails(scrapedElementsList: Elements?) {
                        if (!scrapedElementsList!!.isEmpty()) {
                            temperature =
                                scrapedElementsList.select("span[class=CurrentConditions--tempValue--MHmYY]")
                                    .text()
                            condition =
                                scrapedElementsList.select("div[class=CurrentConditions--phraseValue--mZC_p]")
                                    .text()
                            val maxmin =
                                scrapedElementsList.select("div[class=CurrentConditions--tempHiLoValue--3T1DG]")
                                    .text()
                            max = "Max.: " + maxmin.substring(
                                4,
                                7
                            ) + "  Min.: " + maxmin.substring(maxmin.length - 3, maxmin.length)
                            if (preferences.getBoolean(Constants.IS_CELCIUS, defValue = true)) {
                                binding.temperatureTv.setText(temperature + "C")
                            } else {
                                val temp: String = temperature.substring(
                                    1,
                                    temperature.length - 1
                                )
                                binding.temperatureTv.setText(convertToFarenhiet(temp) + "ºF")
                            }
                            binding.conditionTv.setText(condition)
                            binding.maxMin.setText(max)
                            System.out.println("temperature>>>$temperature")
                            System.out.println("condition>>>$condition")
                            System.out.println("max>>>$max")
                            setContent()
                            fillList()
                        }
                    }
                }, latlongs /*"19.15,72.94"*/)
            searchWeather.execute()
        } catch (e: Exception) {
//            throw new RuntimeException(e);
            println("exception>>>>" + e.message)
        }
    }

    private fun setContent() {
        if (condition.toLowerCase().contains("rain") || condition.toLowerCase()
                .contains("shower") || condition.toLowerCase().contains("drizzle")
        ) {
            binding.conditionIv.setImageResource(R.drawable.light_rain)
            binding.mainLayout.setBackgroundResource(R.drawable.strom_bg)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.storm_gif).into(binding.conditionGif)
        } else if (condition.toLowerCase().contains("sunny") || condition.toLowerCase()
                .contains("smoke")|| condition.toLowerCase()
                .contains("haze")
        ) {
            binding.conditionIv.setImageResource(R.drawable.sunny_img)
            binding.mainLayout.setBackgroundResource(R.drawable.sunny_bg)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.sunny_gif).into(binding.conditionGif)
        } else if (condition.toLowerCase().contains("night")) {
            binding.conditionIv.setImageResource(R.drawable.night_img)
            binding.mainLayout.setBackgroundResource(R.drawable.night_bg)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.cloudy_night_gif).into(binding.conditionGif)
        } else if (condition.toLowerCase().contains("storm")) {
            binding.conditionIv.setImageResource(R.drawable.storme_img)
            binding.mainLayout.setBackgroundResource(R.drawable.strom_bg_img)
            binding.conditionGif.setVisibility(View.VISIBLE)
            Glide.with(this).load(R.drawable.storm_gif).into(binding.conditionGif)
        }
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
                    getWeatherDetails(latlong)
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

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.notification_nav -> Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT)
//                .show()
//
//            R.id.unit_nav -> Toast.makeText(this, "Units", Toast.LENGTH_SHORT).show()
//            R.id.widgets_nav -> Toast.makeText(this, "Widgets", Toast.LENGTH_SHORT).show()
//            R.id.rate_nav -> rateApp(this)
//            R.id.share_nav -> shareApp(this)
//        }
//        return super.onOptionsItemSelected(item)
//    }

}