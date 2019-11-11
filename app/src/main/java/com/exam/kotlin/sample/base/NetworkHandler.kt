package com.exam.kotlin.sample.base

import android.content.Context
import com.exam.kotlin.sample.extension.networkInfo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by msaycon on 01,Oct,2019
 */
@Singleton
class NetworkHandler
@Inject constructor(private val context: Context) {
    val isConnected get() = context.networkInfo?.isConnected
}