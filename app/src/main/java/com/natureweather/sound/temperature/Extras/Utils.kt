package com.natureweather.sound.temperature.Extras

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.natureweather.sound.temperature.BuildConfig
import com.natureweather.sound.temperature.R


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
                    latlong = "$lat,$lng".toString()
                    println(lat)
                    println(lng)
                    println(latlong)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } // end catch
        } // end if
        return latlong
    }

    fun convertToFarenhiet(value: String): String {
        val inputInDouble = value.toDouble()
        val outputInFahrenheit = inputInDouble * 9 / 5 + 32
        return  /*String.valueOf(outputInFahrenheit)*/outputInFahrenheit.toString()
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

