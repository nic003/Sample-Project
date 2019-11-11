package com.exam.kotlin.sample.view.weather

import androidx.lifecycle.MutableLiveData
import com.exam.kotlin.sample.base.BaseViewModel
import com.exam.kotlin.sample.data.LocationInfo
import com.google.gson.JsonObject
import javax.inject.Inject

/**
 * Created by msaycon on 09,Nov,2019
 */
class WeatherViewModel
@Inject constructor(private val getWeatherForecast: GetWeatherForecast) : BaseViewModel() {
    var forecast: MutableLiveData<JsonObject> = MutableLiveData()

    fun loadForecast(locationInfo: LocationInfo) = getWeatherForecast(locationInfo) { it.response(::handleFailure, ::handleForecast) }

    private fun handleForecast(forecast: JsonObject) {
        this.forecast.value = forecast
    }
}