package com.exam.kotlin.sample.repository

import com.exam.kotlin.sample.exemption.Failure
import com.exam.kotlin.sample.extension.Response
import com.google.gson.JsonObject

/**
 * Created by msaycon on 06,Oct,2019
 */
interface DataRepository {
    fun forecast(lat:String, long:String): Response<Failure, JsonObject>
}