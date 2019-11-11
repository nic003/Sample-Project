package com.exam.kotlin.sample.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by msaycon on 06,Oct,2019
 */
interface DataApi {
    companion object {
        private const val FORECAST = "weather"
    }

    @GET(FORECAST)
    fun currentForecast(@Query("lat") lat: String, @Query("lon") long: String, @Query("APPID") apiKey: String): Call<JsonObject>
}