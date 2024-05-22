package com.natureweather.sound.temperature.Extras

import kotlinx.coroutines.asCoroutineDispatcher
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.util.concurrent.Executors

object DataFetcher {

    private const val WEATHER_BASE_URL = "https://weather.com/en-IN/weather/"
    const val CURRENT_TIME_URL = "https://time.is/?q="


    private enum class WeatherDataType {
        GENERAL,
        HOURLY,
        TEN_DAY,
        PRECIPITATION
    }

    private fun fetchWeatherData(
        url: String,
        type: WeatherDataType,
        search: String,
        callback: (Elements?) -> Unit
    ) {
        try {
            val document = Jsoup.connect("$url$search").get()
            val elements = when (type) {
                WeatherDataType.GENERAL -> document.getElementById("MainContent")!!.allElements
                WeatherDataType.HOURLY -> document.getElementsByClass("DaypartDetails--DetailSummaryContent--1-r0i Disclosure--SummaryDefault--2XBO9")
                WeatherDataType.TEN_DAY -> document.getElementsByClass("DetailsSummary--DetailsSummary--1DqhO DetailsSummary--fadeOnOpen--KnNyF")
                WeatherDataType.PRECIPITATION -> document.getElementById("MainContent")!!.allElements.select(
                    "div[class=\n" +
                            "              Slideshow--slide--4GN6B\n" +
                            "              Slideshow--showme--3qmfi\n" +
                            "            ]"
                )
            }
            callback.invoke(elements)
        } catch (e: IOException) {
            e.printStackTrace()
            callback.invoke(null)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            callback.invoke(null)
        }
    }

    fun searchWeather(search: String, callback: (Elements?) -> Unit) {
        fetchWeatherData(WEATHER_BASE_URL + "today/l/", WeatherDataType.GENERAL, search, callback)
    }

    fun searchHourlyWeather(search: String, callback: (Elements?) -> Unit) {
        fetchWeatherData(
            WEATHER_BASE_URL + "hourbyhour/l/",
            WeatherDataType.HOURLY,
            search,
            callback
        )
    }

    fun searchTenDayWeather(search: String, callback: (Elements?) -> Unit) {
        fetchWeatherData(WEATHER_BASE_URL + "tenday/l/", WeatherDataType.TEN_DAY, search, callback)
    }

    fun searchPrecipitationWeather(search: String, callback: (Elements?) -> Unit) {
        fetchWeatherData(
            WEATHER_BASE_URL + "today/l/",
            WeatherDataType.PRECIPITATION,
            search,
            callback
        )
    }
}


/*object DataFetcher {

    private const val WEATHER_BASE_URL = "https://weather.com/en-IN/weather/"
    private const val CURRENT_TIME_URL = "https://www.google.com/search?q="

    private val coroutineDispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

    fun searchCurrentTime(search: String, callback: (String) -> Unit) {
        GlobalScope.launch(coroutineDispatcher) {
            try {
//                val document = Jsoup.connect(CURRENT_TIME_URL + search + "+current+time").get()
//                val selectedDiv = document.selectFirst("div.gsrt.vk_bk.FzvWSb.YwPhnf")
//                val inputTime = selectedDiv.text()
//                val formattedTime =
//                callback.invoke(formattedTime)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun searchWeatherData(url: String, type: Int, search: String, callback: (Elements?) -> Unit) {
        GlobalScope.launch(coroutineDispatcher) {
            try {
                val document = Jsoup.connect("$url$search").get()
                when(type) {
                    1 -> callback.invoke(document.getElementsByClass("DaypartDetails--DetailSummaryContent--1-r0i Disclosure--SummaryDefault--2XBO9"))
                    2 -> callback.invoke(document.getElementsByClass("DetailsSummary--DetailsSummary--1DqhO DetailsSummary--fadeOnOpen--KnNyF"))
                    3 -> callback.invoke(document.getElementById("MainContent")!!.allElements.select("div#WxuTodayMapCard-main-632099a6-18f7-4023-9f96-2c44f2246787"))
                    else -> callback.invoke(document.select("#MainContent"))
                }

            } catch (e: IOException) {
                e.printStackTrace()
                callback.invoke(null)
            }
        }
    }

    fun searchWeather(search: String, callback: (Elements?) -> Unit) {
        searchWeatherData(WEATHER_BASE_URL + "today/l/", 0, search, callback)
    }

    fun searchHourlyWeather(search: String, callback: (Elements?) -> Unit) {
        searchWeatherData(WEATHER_BASE_URL + "hourbyhour/l/", 1, search, callback)
    }

    fun searchTenDayWeather(search: String, callback: (Elements?) -> Unit) {
        searchWeatherData(WEATHER_BASE_URL + "tenday/l/", 2, search, callback)
    }

    fun searchPercipitationWeather(search: String, callback: (Elements?) -> Unit) {
        searchWeatherData(WEATHER_BASE_URL + "today/l/", 3, search, callback)
    }

    fun finalize() {
        coroutineDispatcher.close()
    }
}*/
