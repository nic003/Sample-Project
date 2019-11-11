package com.exam.kotlin.sample.repository

import com.exam.kotlin.sample.api.DataApi
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by msaycon on 06,Oct,2019
 */
@Singleton
class DataService
@Inject constructor(retrofit: Retrofit) : DataApi {

    private val dataApi by lazy { retrofit.create(DataApi::class.java) }

    override fun currentForecast(lat: String, long: String, apiKey: String): Call<JsonObject> {
       return dataApi.currentForecast(lat, long, apiKey)
    }
}