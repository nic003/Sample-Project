package com.exam.kotlin.sample.repository

import com.exam.kotlin.sample.BuildConfig
import com.exam.kotlin.sample.base.NetworkHandler
import com.exam.kotlin.sample.exemption.Failure
import com.exam.kotlin.sample.extension.Response
import com.google.gson.JsonObject
import retrofit2.Call
import javax.inject.Inject

/**
 * Created by msaycon on 09,Nov,2019
 */
class RemoteData
@Inject constructor(private val networkHandler: NetworkHandler, private val service: DataService) :
    DataRepository {

    override fun forecast(lat: String, long: String): Response<Failure, JsonObject> {
        return when (networkHandler.isConnected) {
            true -> request(
                service.currentForecast(lat, long, BuildConfig.WEATHER_API_KEY),
                { it },
                JsonObject()
            )
            false, null -> Response.Left(Failure.NetworkConnection)
        }
    }

    private fun <T, R> request(
        call: Call<T>,
        transform: (T) -> R,
        default: T
    ): Response<Failure, R> {
        return try {
            val response = call.execute()
            when (response.isSuccessful) {
                true -> Response.Right(transform((response.body() ?: default)))
                false -> Response.Left(Failure.ServerError)
            }
        } catch (exception: Throwable) {
            Response.Left(Failure.ServerError)
        }
    }
}