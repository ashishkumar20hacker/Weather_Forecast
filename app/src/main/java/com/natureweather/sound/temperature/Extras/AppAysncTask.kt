package com.natureweather.sound.temperature.Extras

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object AppAsyncTask {
    private const val TAG = "AppAysncTask"
    private const val Weather = "https://weather.com/en-IN/weather/today/l/"
    private const val HourlyWeather = "https://weather.com/en-IN/weather/hourbyhour/l/"
    private const val TenDaysWeather = "https://weather.com/en-IN/weather/tenday/l/"
    const val CurrentTime = "https://time.is/?q="

    class SearchWeather(
        var activity: Activity,
        searchWeatherInterface: AppInterfaces.SearchWeatherInterface,
        search: String
    ) :
        AsyncTask<Any?, Any?, Any?>() {
        var searchWeatherInterface: AppInterfaces.SearchWeatherInterface
        var search: String
        var selectedDiv = Elements()

        init {
            this.searchWeatherInterface = searchWeatherInterface
            this.search = search
        }

        override fun doInBackground(objects: Array<Any?>): Any? {
            try {
                val document = Jsoup.connect(Weather + search).get()
                selectedDiv = document.getElementById("MainContent")!!.allElements
                //                System.out.println("image link>>>>>>>>> " + selectedDiv);
            } catch (e: IOException) {
                println("scrapping crashed" + e.message)
            }
            return ""
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(o: Any?) {
            super.onPostExecute(o)
            searchWeatherInterface.getWeatherDetails(selectedDiv)
        }
    }

    class SearchWeatherPercipitation(
        var activity: Activity,
        searchWeatherInterface: AppInterfaces.SearchWeatherInterface,
        search: String
    ) :
        AsyncTask<Any?, Any?, Any?>() {
        var searchWeatherInterface: AppInterfaces.SearchWeatherInterface
        var search: String
        var selectedDiv = Elements()

        init {
            this.searchWeatherInterface = searchWeatherInterface
            this.search = search
        }

        override fun doInBackground(objects: Array<Any?>): Any? {
            try {
                val document = Jsoup.connect(Weather + search).get()
                //                selectedDiv = document.getElementById("MainContent").getAllElements().select("div#Slideshow--Slideshow--1YsiZ");
                selectedDiv =
                    document.getElementById("MainContent")!!.allElements.select("div#WxuTodayMapCard-main-632099a6-18f7-4023-9f96-2c44f2246787")
                //                selectedDiv = document.getElementById("MainContent").getAllElements();
            } catch (e: IOException) {
                println("scrapping crashed" + e.message)
            }
            return ""
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(o: Any?) {
            super.onPostExecute(o)
            searchWeatherInterface.getWeatherDetails(selectedDiv)
        }
    }

    class HourSearchWeather(
        searchWeatherInterface: AppInterfaces.SearchWeatherInterface,
        search: String
    ) :
        AsyncTask<Any?, Any?, Any?>() {
        var searchWeatherInterface: AppInterfaces.SearchWeatherInterface
        var search: String
        var selectedDiv = Elements()

        init {
            this.searchWeatherInterface = searchWeatherInterface
            this.search = search
        }

        override fun doInBackground(objects: Array<Any?>): Any? {
            try {
                val document = Jsoup.connect(HourlyWeather + search).get()
                selectedDiv =
                    document.getElementsByClass("DaypartDetails--DetailSummaryContent--1-r0i Disclosure--SummaryDefault--2XBO9")
            } catch (e: IOException) {
                println("scrapping crashed" + e.message)
            }
            return ""
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(o: Any?) {
            super.onPostExecute(o)
            searchWeatherInterface.getWeatherDetails(selectedDiv)
        }
    }

    class TenDaySearchWeather(
        searchWeatherInterface: AppInterfaces.SearchWeatherInterface,
        search: String
    ) :
        AsyncTask<Any?, Any?, Any?>() {
        var searchWeatherInterface: AppInterfaces.SearchWeatherInterface
        var search: String
        var selectedDiv = Elements()

        init {
            this.searchWeatherInterface = searchWeatherInterface
            this.search = search
        }

        override fun doInBackground(objects: Array<Any?>): Any? {
            try {
                val document = Jsoup.connect(TenDaysWeather + search).get()
                selectedDiv =
                    document.getElementsByClass("DetailsSummary--DetailsSummary--1DqhO DetailsSummary--fadeOnOpen--KnNyF")
                //                System.out.println("mainContent>>>>>>>>> " + selectedDiv);
            } catch (e: IOException) {
                println("scrapping crashed" + e.message)
            }
            return ""
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(o: Any?) {
            super.onPostExecute(o)
            searchWeatherInterface.getWeatherDetails(selectedDiv)
        }
    }
}