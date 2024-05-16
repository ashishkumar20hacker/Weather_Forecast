package com.natureweather.sound.temperature.Extras

import org.jsoup.select.Elements


class AppInterfaces {
    interface SearchWeatherInterface {
        fun getWeatherDetails(scrapedElementsList: Elements?)
    }
    interface SearchTimeInterface {
        fun getTimeDetails(time: String)
    }
}

