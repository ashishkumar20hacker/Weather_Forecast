package com.natureweather.sound.temperature.Extras

import android.app.Activity
import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException


object AppAsyncTask {
    private const val Weather = "https://weather.com/en-IN/weather/today/l/"
    private const val HourlyWeather = "https://weather.com/en-IN/weather/hourbyhour/l/"
    private const val TenDaysWeather = "https://weather.com/en-IN/weather/tenday/l/"

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
