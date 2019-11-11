package com.exam.kotlin.sample.view

import com.exam.kotlin.sample.base.BaseActivity
import com.exam.kotlin.sample.view.weather.WeatherFragment

/**
 * Created by msaycon on 09,Nov,2019
 */
class MainActivity : BaseActivity(){

    override fun fragment() = WeatherFragment()
}