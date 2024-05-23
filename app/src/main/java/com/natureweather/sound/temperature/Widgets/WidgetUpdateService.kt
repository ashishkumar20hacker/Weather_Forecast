package com.natureweather.sound.temperature.Widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.JobIntentService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.natureweather.sound.temperature.Extras.Constants
import com.natureweather.sound.temperature.Extras.DataFetcher
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WidgetUpdateService : JobIntentService() {

    companion object {
        private const val JOB_ID = 1000

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, WidgetUpdateService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val context = applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)

        val widgetOneIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, WidgetOneProvider::class.java)
        )
        for (appWidgetId in widgetOneIds) {
            updateAppWidgetOne(context, appWidgetManager, appWidgetId)
        }

        val widgetTwoIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, WidgetTwoProvider::class.java)
        )
        for (appWidgetId in widgetTwoIds) {
            updateAppWidgetTwo(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidgetOne(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_one)
        val preferences = SharePreferences(context)
        val location = preferences.getString(Constants.SELECTED_ADDRESS, "")
        val latlong: String = Utils.convertAddress(context, location)

        remoteViews.setTextViewText(R.id.cityName, location)

        loadGifIntoWidget(context, remoteViews, appWidgetManager, appWidgetId, R.drawable.sun_splash)
        CoroutineScope(Dispatchers.IO).launch {
            val elements = fetchWeatherData(latlong)
            elements?.let {
                val temperature = it.select("span[class=CurrentConditions--tempValue--MHmYY]").text()
                val condition = it.select("div[class=CurrentConditions--phraseValue--mZC_p]").text()
                val maxmin = it.select("div[class=CurrentConditions--tempHiLoValue--3T1DG]").text()
                val max = "Max.: ${Utils.checkAndSetTemperature(preferences, maxmin.substring(4, 7))}"
                val min = "Min.: ${Utils.checkAndSetTemperature(preferences, maxmin.takeLast(3))}"

                withContext(Dispatchers.Main) {
                    remoteViews.setTextViewText(R.id.temperature, Utils.checkAndSetTemperature(preferences, temperature))
                    remoteViews.setTextViewText(R.id.condition_tv, condition)
                    remoteViews.setTextViewText(R.id.maxTemp, max)
                    remoteViews.setTextViewText(R.id.minTemp, min)

                    Utils.getConditionImage(condition)?.let { it1 ->
                        loadImageIntoWidget(context, remoteViews, appWidgetManager, appWidgetId,
                            it1
                        )
                    }

                    remoteViews.setViewVisibility(R.id.gif_loading_ll, View.GONE)
                    remoteViews.setViewVisibility(R.id.main,View.VISIBLE)

                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                }
            } ?: run {
                println("Something went wrong!!")
            }
        }
    }

    private fun updateAppWidgetTwo(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_two)
        val preferences = SharePreferences(context)
        val location = preferences.getString(Constants.SELECTED_ADDRESS, "")
        val latlong: String = Utils.convertAddress(context, location)

        loadGifIntoWidget(context, remoteViews, appWidgetManager, appWidgetId, R.drawable.sun_splash)
        CoroutineScope(Dispatchers.IO).launch {
            val elements = fetchWeatherData(latlong)
            elements?.let {
                val temperature = it.select("span[class=CurrentConditions--tempValue--MHmYY]").text()
                val condition = it.select("div[class=CurrentConditions--phraseValue--mZC_p]").text()
                val maxmin = it.select("div[class=CurrentConditions--tempHiLoValue--3T1DG]").text()
                val min_max = "${Utils.checkAndSetTemperature(preferences, maxmin.substring(4, 7))}/${Utils.checkAndSetTemperature(preferences, maxmin.takeLast(3))}"


                withContext(Dispatchers.Main) {
                    remoteViews.setTextViewText(R.id.temperature, Utils.checkAndSetTemperature(preferences, temperature))
                    remoteViews.setTextViewText(R.id.min_max, min_max)
                    remoteViews.setTextViewText(R.id.cityName, location)
                    val currentDate = Calendar.getInstance().time
                    val dateFormat = SimpleDateFormat("EEEE, MMMM, dd", Locale.ENGLISH)
                    val formattedDate = dateFormat.format(currentDate)
                    remoteViews.setTextViewText(R.id.date, formattedDate)

                    Utils.getConditionImage(condition)?.let { it1 ->
                        loadImageIntoWidget(context, remoteViews, appWidgetManager, appWidgetId,
                            it1
                        )
                    }

                    remoteViews.setViewVisibility(R.id.gif_loading_ll, View.GONE)
                    remoteViews.setViewVisibility(R.id.main,View.VISIBLE)

                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                }
            } ?: run {
                println("Something went wrong!!")
            }
        }
    }

    private suspend fun fetchWeatherData(latlong: String): Elements? {
        return withContext(Dispatchers.IO) {
            try {
                val result = CompletableDeferred<Elements?>()
                DataFetcher.searchWeather(latlong) { elements ->
                    result.complete(elements)
                }
                result.await()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun loadImageIntoWidget(
        context: Context,
        remoteViews: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        iconUrl: Int
    ) {
        Glide.with(context)
            .asBitmap()
            .load(iconUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        remoteViews.setImageViewBitmap(R.id.condition_iv, resource)
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle placeholder if necessary
                }
            })
    }

    private fun loadGifIntoWidget(
        context: Context,
        remoteViews: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        iconUrl: Int
    ) {
        Glide.with(context)
            .asBitmap()
            .load(iconUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    remoteViews.setImageViewBitmap(R.id.gif_loading, resource)
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle placeholder if necessary
                }
            })
    }
}
