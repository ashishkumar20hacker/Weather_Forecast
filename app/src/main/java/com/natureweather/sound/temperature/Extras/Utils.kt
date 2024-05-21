package com.natureweather.sound.temperature.Extras

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.natureweather.sound.temperature.BuildConfig
import com.natureweather.sound.temperature.Model.TipsModel
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.UnitsDialogBinding
import java.text.SimpleDateFormat
import java.util.Locale


object Utils {

    const val STORAGE_PERMISSION_REQ_CODE = 100
    private val LOCATION_PERMISSION = 123

    // Method to check if storage permission is granted
    fun isLocationPermissionGranted(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //         Method to request storage permission
    fun requestLocationPermission(activity: Activity) {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                arrayOf<String>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATION_PERMISSION
            )
        }
    }

    fun convertAddress(context: Context?, address: String?): String {
        var latlong = ""
        val geoCoder = Geocoder(context!!)
        if (address != null && !address.isEmpty()) {
            try {
                val addressList = geoCoder.getFromLocationName(address, 1)
                if (addressList != null && addressList.size > 0) {
                    val lat = addressList[0].latitude
                    val lng = addressList[0].longitude
                    latlong = "$lat,$lng"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } // end catch
        } // end if
        return latlong
    }

    fun convertToFarenhiet(value: String): String {
        val inputInDouble = value.replace("º".toRegex(), "").toDouble()
        val outputInFahrenheit = (inputInDouble * 9.0 / 5.0) + 32.0
        return outputInFahrenheit.toString()
    }

    fun checkAndSetTemperature(preferences: SharePreferences, value: String): String {
        return if (preferences.getBoolean(Constants.IS_CELCIUS, defValue = true)) {
            value + "C"
        } else {
            val temp: String
            temp = value.substring(
                    0,
                    value.length - 1
                )
            convertToFarenhiet(temp) + "ºF"
        }
    }

    fun checkAndSetWindSpeed(
        context: Context,
        preferences: SharePreferences,
        value: String
    ): String {
        return when (preferences.getString(Constants.WIND_UNIT, context.getString(R.string.km_h))) {
            context.getString(R.string.m_s) -> {
                val speedInMs = value.toDouble() / 3.6
                "$speedInMs ${context.getString(R.string.m_s)}"
            }

            context.getString(R.string.mph) -> {
                val speedInMph = value.toDouble() * 0.621371
                "$speedInMph ${context.getString(R.string.mph)}"
            }

            else -> {
                "$value ${context.getString(R.string.km_h)}"
            }
        }
    }

    fun checkAndSetPressure(
        context: Context,
        preferences: SharePreferences,
        value: String
    ): String {
        val pressureInMbar = value.toDouble()
        return when (preferences.getString(
            Constants.PRESSURE_UNIT,
            context.getString(R.string.mbar)
        )) {
            context.getString(R.string.bar) -> {
                val pressureInBar = pressureInMbar * 0.001
                "$pressureInBar ${context.getString(R.string.bar)}"
            }

            context.getString(R.string.psi) -> {
                val pressureInPsi = pressureInMbar * 0.0145038
                "$pressureInPsi ${context.getString(R.string.psi)}"
            }

            context.getString(R.string.inhg) -> {
                val pressureInInHg = pressureInMbar * 0.02953
                "$pressureInInHg ${context.getString(R.string.inhg)}"
            }

            context.getString(R.string.mmhg) -> {
                val pressureInMmHg = pressureInMbar * 0.750062
                "$pressureInMmHg ${context.getString(R.string.mmhg)}"
            }

            else -> {
                "$value ${context.getString(R.string.mbar)}"
            }
        }
    }

    fun checkAndSetVisibility(
        context: Context,
        preferences: SharePreferences,
        value: String
    ): String {
        return when (preferences.getString(
            Constants.VISIBILITY_UNIT,
            context.getString(R.string.km)
        )) {
            context.getString(R.string.mile) -> {
                val distanceInKm = value.toDouble()
                val distanceInMiles = distanceInKm * 0.621371
                "$distanceInMiles ${context.getString(R.string.mile)}"
            }

            else -> {
                "$value ${context.getString(R.string.km)}"
            }
        }
    }

    fun checkAndSetTime(
        context: Context,
        preferences: SharePreferences,
        value: String
    ): String {
        return when (preferences.getString(
            Constants.TIME_FORMAT,
            context.getString(R.string._24_hour_clock)
        )) {
            context.getString(R.string._12_hour_clock) -> {
                convertTo12HourFormat(value)
            }

            else -> {
                value
            }
        }
    }

    fun convertTo12HourFormat(time24: String): String {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Parse the input time string
        val date = inputFormat.parse(time24)

        // Format the parsed date into the 12-hour format
        return outputFormat.format(date)
    }

    fun rateApp(context: Context) {
        try {
            val rateIntent = rateIntentForUrl(context, "market://details")
            context.startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl(context, "https://play.google.com/store/apps/details")
            context.startActivity(rateIntent)
        }
    }

    private fun rateIntentForUrl(context: Context, url: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                String.format("%s?id=%s", url, context.packageName)
            )
        )
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = if (Build.VERSION.SDK_INT >= 33) {
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        }
        intent.addFlags(flags)
        return intent
    }

    fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            var shareMessage =
                "\nCheck out this amazing weather app!\n\n"
            shareMessage =
                (shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID) + "\n\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(shareIntent, "Share Via"))
        } catch (e: Exception) {
            println("Share Exception >> ${e.localizedMessage}")
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun gotoUrl(activity: Activity) {
        val uri = Uri.parse(Constants.PRIVACY_POLICY)
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun makeStatusBarTransparent(context: Activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            context.window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun makeStatusBarTransparent2(context: Activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            context.window.statusBarColor = Color.TRANSPARENT
        }

        /*WindowInsetsControllerCompat.setAppearanceLightStatusBars(true)*/
    }

    fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.setAttributes(winParams)
    }

    fun getTipsForCondition(condition: String): List<TipsModel> {
        return when (condition) {
            "Clear" -> listOf(
                TipsModel("Sun Protection", "Wear sunglasses and use sunscreen."),
                TipsModel("Stay Hydrated", "Drink plenty of water, especially if active."),
                TipsModel("Plan Activities", "Ideal for outdoor activities; enjoy nature.")
            )

            "Cloudy" -> listOf(
                TipsModel("Layer Up", "Clouds can make it feel cooler; dress in layers."),
                TipsModel("Sun Protection", "UV rays still penetrate clouds; use sunscreen."),
                TipsModel("Check Forecast", "Be prepared for possible rain.")
            )

            "Rainy" -> listOf(
                TipsModel("Waterproof Gear", "Use a waterproof jacket, boots, and umbrella."),
                TipsModel("Drive Safely", "Slow down, increase stopping distances."),
                TipsModel("Avoid Flooding", "Do not walk or drive through flooded areas."),
                TipsModel("Protect Electronics", "Use waterproof covers."),
                TipsModel("Stay Warm", "Wet conditions can lead to hypothermia.")
            )

            "Snowy" -> listOf(
                TipsModel("Drive Carefully", "Slow down and maintain a safe distance."),
                TipsModel("Shovel Safely", "Take breaks, push snow instead of lifting."),
                TipsModel("Use Salt/Sand", "Prevent slips on icy surfaces."),
                TipsModel("Wear Proper Footwear", "Boots with good traction."),
                TipsModel("Check Updates", "Stay informed about weather conditions.")
            )

            "Windy" -> listOf(
                TipsModel("Secure Items", "Tie down or bring in loose objects."),
                TipsModel("Avoid Trees/Power Lines", "Stay away from potential hazards."),
                TipsModel("Drive Cautiously", "Maintain control of your vehicle."),
                TipsModel("Close Windows/Doors", "Prevent wind damage."),
                TipsModel("Stay Indoors", "Avoid flying debris.")
            )

            "Foggy" -> listOf(
                TipsModel("Use Fog Lights", "If available, avoid high beams."),
                TipsModel("Drive Slowly", "Maintain safe distances."),
                TipsModel("Stay in Lane", "Use road markings."),
                TipsModel("Listen for Traffic", "Open windows slightly."),
                TipsModel("Clear Windshield", "Use wipers and defrosters.")
            )

            "Smoke" -> listOf(
                TipsModel("Limit Outdoor Activity", "Stay indoors if air quality is poor."),
                TipsModel("Use Masks", "Wear N95 masks to filter out smoke particles."),
                TipsModel("Air Purifiers", "Use air purifiers indoors to maintain air quality."),
                TipsModel("Keep Windows Closed", "Prevent smoke from entering."),
                TipsModel("Monitor Air Quality", "Check local air quality updates.")
            )

            else -> listOf(
                TipsModel("No Tips", "No tips available for this condition.")
            )
        }
    }

    fun showUnitsDialog(activity: Activity) {
        val dialog = Dialog(activity, R.style.SheetDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = UnitsDialogBinding.inflate(LayoutInflater.from(activity))
        val lay = binding.root
        dialog.setContentView(lay)

        val preferences = SharePreferences(activity)

        if (preferences.getBoolean(Constants.IS_CELCIUS, defValue = true)) {
            binding.tempUnitTv.setText(activity.getString(R.string.celsius))
            binding.celsius.setImageResource(R.drawable.selected)
        } else {
            binding.tempUnitTv.setText(activity.getString(R.string.fahrenheit))
            binding.fahrenheit.setImageResource(R.drawable.selected)
        }

        binding.windUnitTv.setText(
            preferences.getString(
                Constants.WIND_UNIT,
                activity.getString(R.string.km_h)
            )
        )
        binding.rainUnitTv.setText(
            preferences.getString(
                Constants.RAIN_UNIT,
                activity.getString(R.string.mm)
            )
        )
        binding.pressureUnitTv.setText(
            preferences.getString(
                Constants.PRESSURE_UNIT,
                activity.getString(R.string.mbar)
            )
        )
        binding.visibilityUnitTv.setText(
            preferences.getString(
                Constants.VISIBILITY_UNIT,
                activity.getString(R.string.km)
            )
        )
        binding.timeUnitTv.setText(
            preferences.getString(
                Constants.TIME_FORMAT,
                activity.getString(R.string._24_hour_clock)
            )
        )
        binding.dateUnitTv.setText(
            preferences.getString(
                Constants.DATE_FORMAT,
                activity.getString(R.string.dd_mm_yyyy)
            )
        )

        // Wind

        when (preferences.getString(Constants.WIND_UNIT, activity.getString(R.string.km_h))) {
            activity.getString(R.string.km_h) -> {
                binding.wind1.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.mph) -> {
                binding.wind2.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.m_s) -> {
                binding.wind3.setImageResource(R.drawable.selected)
            }
        }

        // Rain

        when (preferences.getString(Constants.RAIN_UNIT, activity.getString(R.string.mm))) {
            activity.getString(R.string.mm) -> {
                binding.rain1.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.inch) -> {
                binding.rain2.setImageResource(R.drawable.selected)
            }
        }

        // Pressure

        when (preferences.getString(Constants.PRESSURE_UNIT, activity.getString(R.string.mbar))) {
            activity.getString(R.string.mbar) -> {
                binding.pressure1.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.bar) -> {
                binding.pressure2.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.psi) -> {
                binding.pressure3.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.inhg) -> {
                binding.pressure4.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.mmhg) -> {
                binding.pressure5.setImageResource(R.drawable.selected)
            }
        }

        // Visibility

        when (preferences.getString(Constants.VISIBILITY_UNIT, activity.getString(R.string.km))) {
            activity.getString(R.string.km) -> {
                binding.visibility1.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.mile) -> {
                binding.visibility2.setImageResource(R.drawable.selected)
            }
        }

        // Time

        when (preferences.getString(
            Constants.TIME_FORMAT,
            activity.getString(R.string._24_hour_clock)
        )) {
            activity.getString(R.string._24_hour_clock) -> {
                binding.time1.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string._12_hour_clock) -> {
                binding.time2.setImageResource(R.drawable.selected)
            }
        }

        // Date

        when (preferences.getString(
            Constants.DATE_FORMAT,
            activity.getString(R.string.dd_mm_yyyy)
        )) {
            activity.getString(R.string.dd_mm_yyyy) -> {
                binding.date1.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.mm_dd_yyyy) -> {
                binding.date2.setImageResource(R.drawable.selected)
            }

            activity.getString(R.string.yyyy_mm_dd) -> {
                binding.date3.setImageResource(R.drawable.selected)
            }
        }

        binding.tempUnitTv.setOnClickListener {
            handleUnitDialogLayout(binding.mainLayout, binding.tempLayout)
        }

        binding.windUnitTv.setOnClickListener {
            handleUnitDialogLayout(binding.mainLayout, binding.windLayout)
        }

        binding.rainUnitTv.setOnClickListener {
            handleUnitDialogLayout(binding.mainLayout, binding.rainLayout)
        }

        binding.pressureUnitTv.setOnClickListener {
            handleUnitDialogLayout(binding.mainLayout, binding.pressureLayout)
        }

        binding.visibilityUnitTv.setOnClickListener {
            handleUnitDialogLayout(binding.mainLayout, binding.visibilityLayout)
        }

        binding.timeUnitTv.setOnClickListener {
            handleUnitDialogLayout(binding.mainLayout, binding.timeLayout)
        }

        binding.dateUnitTv.setOnClickListener {
            handleUnitDialogLayout(binding.mainLayout, binding.dateLayout)
        }

        // Temperature

        binding.celsius.setOnClickListener {
            binding.fahrenheit.setImageResource(R.drawable.unselected)
            binding.celsius.setImageResource(R.drawable.selected)
            preferences.putBoolean(Constants.IS_CELCIUS, true)
            binding.tempUnitTv.setText(activity.getString(R.string.celsius))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.tempLayout, binding.mainLayout)
            }, 500)
        }

        binding.fahrenheit.setOnClickListener {
            binding.celsius.setImageResource(R.drawable.unselected)
            binding.fahrenheit.setImageResource(R.drawable.selected)
            preferences.putBoolean(Constants.IS_CELCIUS, false)
            binding.tempUnitTv.setText(activity.getString(R.string.fahrenheit))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.tempLayout, binding.mainLayout)
            }, 500)
        }

        //Wind

        binding.wind1.setOnClickListener {
            binding.wind2.setImageResource(R.drawable.unselected)
            binding.wind3.setImageResource(R.drawable.unselected)
            binding.wind1.setImageResource(R.drawable.selected)
            preferences.putString(Constants.WIND_UNIT, activity.getString(R.string.km_h))
            binding.windUnitTv.setText(activity.getString(R.string.km_h))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.windLayout, binding.mainLayout)
            }, 500)
        }

        binding.wind2.setOnClickListener {
            binding.wind1.setImageResource(R.drawable.unselected)
            binding.wind3.setImageResource(R.drawable.unselected)
            binding.wind2.setImageResource(R.drawable.selected)
            preferences.putString(Constants.WIND_UNIT, activity.getString(R.string.mph))
            binding.windUnitTv.setText(activity.getString(R.string.mph))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.windLayout, binding.mainLayout)
            }, 500)
        }

        binding.wind3.setOnClickListener {
            binding.wind1.setImageResource(R.drawable.unselected)
            binding.wind2.setImageResource(R.drawable.unselected)
            binding.wind3.setImageResource(R.drawable.selected)
            preferences.putString(Constants.WIND_UNIT, activity.getString(R.string.m_s))
            binding.windUnitTv.setText(activity.getString(R.string.m_s))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.windLayout, binding.mainLayout)
            }, 500)
        }

        //Rain

        binding.rain1.setOnClickListener {
            binding.rain2.setImageResource(R.drawable.unselected)
            binding.rain1.setImageResource(R.drawable.selected)
            preferences.putString(Constants.RAIN_UNIT, activity.getString(R.string.mm))
            binding.rainUnitTv.setText(activity.getString(R.string.mm))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.rainLayout, binding.mainLayout)
            }, 500)
        }

        binding.rain2.setOnClickListener {
            binding.rain1.setImageResource(R.drawable.unselected)
            binding.rain2.setImageResource(R.drawable.selected)
            preferences.putString(Constants.RAIN_UNIT, activity.getString(R.string.inch))
            binding.rainUnitTv.setText(activity.getString(R.string.inch))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.rainLayout, binding.mainLayout)
            }, 500)
        }

        //Pressure

        binding.pressure1.setOnClickListener {
            binding.pressure2.setImageResource(R.drawable.unselected)
            binding.pressure3.setImageResource(R.drawable.unselected)
            binding.pressure4.setImageResource(R.drawable.unselected)
            binding.pressure5.setImageResource(R.drawable.unselected)
            binding.pressure1.setImageResource(R.drawable.selected)
            preferences.putString(Constants.PRESSURE_UNIT, activity.getString(R.string.mbar))
            binding.pressureUnitTv.setText(activity.getString(R.string.mbar))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.pressureLayout, binding.mainLayout)
            }, 500)
        }

        binding.pressure2.setOnClickListener {
            binding.pressure1.setImageResource(R.drawable.unselected)
            binding.pressure3.setImageResource(R.drawable.unselected)
            binding.pressure4.setImageResource(R.drawable.unselected)
            binding.pressure5.setImageResource(R.drawable.unselected)
            binding.pressure2.setImageResource(R.drawable.selected)
            preferences.putString(Constants.PRESSURE_UNIT, activity.getString(R.string.bar))
            binding.pressureUnitTv.setText(activity.getString(R.string.bar))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.pressureLayout, binding.mainLayout)
            }, 500)
        }

        binding.pressure3.setOnClickListener {
            binding.pressure2.setImageResource(R.drawable.unselected)
            binding.pressure1.setImageResource(R.drawable.unselected)
            binding.pressure4.setImageResource(R.drawable.unselected)
            binding.pressure5.setImageResource(R.drawable.unselected)
            binding.pressure3.setImageResource(R.drawable.selected)
            preferences.putString(Constants.PRESSURE_UNIT, activity.getString(R.string.psi))
            binding.pressureUnitTv.setText(activity.getString(R.string.psi))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.pressureLayout, binding.mainLayout)
            }, 500)
        }

        binding.pressure4.setOnClickListener {
            binding.pressure2.setImageResource(R.drawable.unselected)
            binding.pressure3.setImageResource(R.drawable.unselected)
            binding.pressure1.setImageResource(R.drawable.unselected)
            binding.pressure5.setImageResource(R.drawable.unselected)
            binding.pressure4.setImageResource(R.drawable.selected)
            preferences.putString(Constants.PRESSURE_UNIT, activity.getString(R.string.inhg))
            binding.pressureUnitTv.setText(activity.getString(R.string.inhg))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.pressureLayout, binding.mainLayout)
            }, 500)
        }

        binding.pressure5.setOnClickListener {
            binding.pressure2.setImageResource(R.drawable.unselected)
            binding.pressure3.setImageResource(R.drawable.unselected)
            binding.pressure4.setImageResource(R.drawable.unselected)
            binding.pressure1.setImageResource(R.drawable.unselected)
            binding.pressure5.setImageResource(R.drawable.selected)
            preferences.putString(Constants.PRESSURE_UNIT, activity.getString(R.string.mmhg))
            binding.pressureUnitTv.setText(activity.getString(R.string.mmhg))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.pressureLayout, binding.mainLayout)
            }, 500)
        }

        //Visibility

        binding.visibility1.setOnClickListener {
            binding.visibility2.setImageResource(R.drawable.unselected)
            binding.visibility1.setImageResource(R.drawable.selected)
            preferences.putString(Constants.VISIBILITY_UNIT, activity.getString(R.string.km))
            binding.visibilityUnitTv.setText(activity.getString(R.string.km))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.visibilityLayout, binding.mainLayout)
            }, 500)
        }

        binding.visibility2.setOnClickListener {
            binding.visibility1.setImageResource(R.drawable.unselected)
            binding.visibility2.setImageResource(R.drawable.selected)
            preferences.putString(Constants.VISIBILITY_UNIT, activity.getString(R.string.mile))
            binding.visibilityUnitTv.setText(activity.getString(R.string.mile))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.visibilityLayout, binding.mainLayout)
            }, 500)
        }

        //Time

        binding.time1.setOnClickListener {
            binding.time2.setImageResource(R.drawable.unselected)
            binding.time1.setImageResource(R.drawable.selected)
            preferences.putString(
                Constants.TIME_FORMAT,
                activity.getString(R.string._24_hour_clock)
            )
            binding.timeUnitTv.setText(activity.getString(R.string._24_hour_clock))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.timeLayout, binding.mainLayout)
            }, 500)
        }

        binding.time2.setOnClickListener {
            binding.time1.setImageResource(R.drawable.unselected)
            binding.time2.setImageResource(R.drawable.selected)
            preferences.putString(
                Constants.TIME_FORMAT,
                activity.getString(R.string._12_hour_clock)
            )
            binding.timeUnitTv.setText(activity.getString(R.string._12_hour_clock))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.timeLayout, binding.mainLayout)
            }, 500)
        }

        //Date

        binding.date1.setOnClickListener {
            binding.date3.setImageResource(R.drawable.unselected)
            binding.date2.setImageResource(R.drawable.unselected)
            binding.date1.setImageResource(R.drawable.selected)
            preferences.putString(Constants.DATE_FORMAT, activity.getString(R.string.dd_mm_yyyy))
            binding.dateUnitTv.setText(activity.getString(R.string.dd_mm_yyyy))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.dateLayout, binding.mainLayout)
            }, 500)
        }

        binding.date2.setOnClickListener {
            binding.date3.setImageResource(R.drawable.unselected)
            binding.date1.setImageResource(R.drawable.unselected)
            binding.date2.setImageResource(R.drawable.selected)
            preferences.putString(Constants.DATE_FORMAT, activity.getString(R.string.mm_dd_yyyy))
            binding.dateUnitTv.setText(activity.getString(R.string.mm_dd_yyyy))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.dateLayout, binding.mainLayout)
            }, 500)
        }

        binding.date3.setOnClickListener {
            binding.date1.setImageResource(R.drawable.unselected)
            binding.date2.setImageResource(R.drawable.unselected)
            binding.date3.setImageResource(R.drawable.selected)
            preferences.putString(Constants.DATE_FORMAT, activity.getString(R.string.yyyy_mm_dd))
            binding.dateUnitTv.setText(activity.getString(R.string.yyyy_mm_dd))
            Handler(Looper.getMainLooper()).postDelayed({
                handleUnitDialogLayout(binding.dateLayout, binding.mainLayout)
            }, 500)
        }

        binding.okButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    fun handleUnitDialogLayout(makeInvisible: LinearLayout, makeVisible: LinearLayout) {
        makeInvisible.visibility = View.GONE
        makeVisible.visibility = View.VISIBLE
    }

    fun nextActivity(activity: Activity, className: Class<*>?) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className))
        activity.overridePendingTransition(0, 0)
//        }
    }

    fun nextActivity(activity: Activity, className: Class<*>?, key: String?, value: String?) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className).putExtra(key, value))
        activity.overridePendingTransition(0, 0)
//        }
    }

    fun nextActivity(activity: Activity, className: Class<*>?, key: String?, value: Boolean) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className).putExtra(key, value))
        activity.overridePendingTransition(0, 0)
//        }
    }

    fun nextActivity(activity: Activity, className: Class<*>?, key: String?, value: Int) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className).putExtra(key, value))
        activity.overridePendingTransition(0, 0)
//        }
    }

    fun nextActivity(
        activity: Activity,
        className: Class<*>?,
        key1: String?,
        value1: String?,
        key2: String?,
        value2: Boolean
    ) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(
            Intent(activity, className).putExtra(key1, value1).putExtra(key2, value2)
        )
        activity.overridePendingTransition(0, 0)
//        }
    }

    fun nextFinishActivity(activity: Activity, className: Class<*>?) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className))
        activity.finish()
        activity.overridePendingTransition(0, 0)
//        }
    }

    fun nextFinishActivity(activity: Activity, className: Class<*>?, key: String?, value: String?) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className).putExtra(key, value))
        activity.finish()
        activity.overridePendingTransition(0, 0)
//        }
    }

    fun nextFinishActivity(activity: Activity, className: Class<*>?, key: String?, value: Boolean) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className).putExtra(key, value))
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }
//    }

    fun nextFinishActivity(activity: Activity, className: Class<*>?, key: String?, value: Int) {
//        AdUtils.showInterstitialAd(activity) { isLoaded: Boolean ->
        activity.startActivity(Intent(activity, className).putExtra(key, value))
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }
//    }
}

