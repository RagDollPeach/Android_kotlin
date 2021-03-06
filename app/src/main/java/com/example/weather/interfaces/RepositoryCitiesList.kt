package com.example.weather.interfaces

import com.example.weather.domain.Weather
import com.example.weather.utils.Location

fun interface RepositoryCitiesList {
    fun getCitiesList(location: Location): List<Weather>
}