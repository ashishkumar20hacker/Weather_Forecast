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
    const val CurrentTime = "https://time.is/"



    class SearchCurrentTime(
        searchTimeInterface: AppInterfaces.SearchTimeInterface,
        search: String
    ) :
        AsyncTask<Any?, Any?, Any?>() {
        var searchTimeInterface: AppInterfaces.SearchTimeInterface
        var search: String
        var selectedDiv = Elements()
        var formattedTime: String = ""

        init {
            this.searchTimeInterface = searchTimeInterface
            this.search = search
        }

        override fun doInBackground(objects: Array<Any?>): Any? {
            try {
                val document = Jsoup.connect(CurrentTime + search).get()
                selectedDiv = document.select("time[id=clock]")
                val string1 = selectedDiv.select("span[id=bcdigit1]").text()
                val string2 = selectedDiv.select("span[id=bcdigit2]").text()
                val string3 = selectedDiv.select("span[class=sep]").text()
                val string4 = selectedDiv.select("span[id=bcdigit3]").text()
                val string5 = selectedDiv.select("span[id=bcdigit4]").text()
                formattedTime = "$string1$string2$string3$string4$string5"
                /*if (selectedDiv != null) {
                    val inputTime = selectedDiv.text()

                    if (inputTime.isNotEmpty()) {
                        val df = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                        df.timeZone = TimeZone.getTimeZone("UTC")
                        val date = df.parse(inputTime)

                        if (date != null) {
                            df.timeZone = TimeZone.getDefault()
                            formattedTime = df.format(date)
                        } else {
                            Log.e(TAG, "Failed to parse date: inputTime=$inputTime")
                        }
                    } else {
                        Log.e(TAG, "Empty inputTime")
                    }
                } else {
                    Log.e(TAG, "Failed to find selectedDiv")
                }*/
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
            searchTimeInterface.getTimeDetails(formattedTime)
        }
    }

    fun getCurrentTimeOfLocation(search: String) {
        val service: ExecutorService = Executors.newSingleThreadExecutor()
        service.execute {
            var selectedDiv = Elements()
            val document = Jsoup.connect(CurrentTime + search).get()
            selectedDiv = document.select("time[id=clock]")
            val string1 = selectedDiv.select("span[id=bcdigit1]").text()
            val string2 = selectedDiv.select("span[id=bcdigit2]").text()
            val string3 = selectedDiv.select("span[class=sep]").text()
            val string4 = selectedDiv.select("span[id=bcdigit3]").text()
            val string5 = selectedDiv.select("span[id=bcdigit4]").text()
            var formattedTime = "$string1$string2$string3$string4$string5"
        }
    }

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
                val document = Jsoup.connect(HourlyWeather + search).get()
                selectedDiv =
                    document.getElementsByClass("DetailsSummary--DetailsSummary--1DqhO DetailsSummary--hourlyDetailsSummary--2xM-L")
                //                System.out.println("hourmainContent>>>>>>>>> " + selectedDiv);
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