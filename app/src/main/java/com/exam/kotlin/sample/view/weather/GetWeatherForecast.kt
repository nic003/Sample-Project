package com.exam.kotlin.sample.view.weather

import com.exam.kotlin.sample.data.LocationInfo
import com.exam.kotlin.sample.exemption.Failure
import com.exam.kotlin.sample.extension.Interactor
import com.exam.kotlin.sample.extension.Response
import com.exam.kotlin.sample.repository.DataRepository
import com.google.gson.JsonObject
import javax.inject.Inject

/**
 * Created by msaycon on 09,Nov,2019
 */
class GetWeatherForecast
@Inject constructor(private val dataRepository: DataRepository) :
    Interactor<JsonObject, LocationInfo>() {

    override suspend fun run(params: LocationInfo): Response<Failure, JsonObject> {
        return dataRepository.forecast(params.lat, params.long)
    }
}