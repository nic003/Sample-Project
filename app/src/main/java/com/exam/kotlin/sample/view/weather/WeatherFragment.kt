package com.exam.kotlin.sample.view.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.StringRes
import com.exam.kotlin.sample.R
import com.exam.kotlin.sample.base.BaseFragment
import com.exam.kotlin.sample.base.NetworkHandler
import com.exam.kotlin.sample.data.DataParams
import com.exam.kotlin.sample.data.LocationInfo
import com.exam.kotlin.sample.exemption.Failure
import com.exam.kotlin.sample.extension.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_weather.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


/**
 * Created by msaycon on 09,Nov,2019
 */
class WeatherFragment : BaseFragment() {

    @Inject
    lateinit var weatherViewModel: WeatherViewModel

    @Inject
    lateinit var networkHandler: NetworkHandler

    private lateinit var rxPermissions: RxPermissions

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun layoutId() = R.layout.fragment_weather

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        setToolBarTitle(getString(R.string.weather_forecast))

        rxPermissions = RxPermissions(this)
        rxPermissions.setLogging(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

        weatherViewModel = viewModel(viewModelFactory) {
            observe(forecast, ::handleForecast)
            failure(failure, ::handleFailure)
        }
    }

    override fun onResume() {
        super.onResume()
        requestLocationPermission()
    }

    @SuppressLint("CheckResult")
    private fun requestLocationPermission() {
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { granted ->
                if (granted) {

                    val locationServices =
                        activity?.getSystemService(LOCATION_SERVICE) as LocationManager

                    val locationEnable =
                        locationServices.isProviderEnabled(LocationManager.GPS_PROVIDER)

                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val locationInfo = LocationInfo(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )

                            weatherViewModel.loadForecast(locationInfo)
                        } else {
                            when (networkHandler.isConnected) {
                                true -> {
                                    if (!locationEnable) {
                                        notifyWithAction(
                                            R.string.failure_location_settings,
                                            R.string.open_settings,
                                            action = {
                                                this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                            })
                                    }
                                }
                                false, null -> handleFailure(Failure.NetworkConnection)
                            }
                        }
                    }

                } else {
                    notifyWithAction(
                        R.string.failure_location_error,
                        R.string.open_settings,
                        action = {
                            this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        })
                }
            }
    }

    private fun handleForecast(forecast: JsonObject?) {
        val weather = forecast?.getAsJsonArray(DataParams.WEATHER)
        val current = weather?.get(0)?.asJsonObject

        val main = forecast?.getAsJsonObject(DataParams.MAIN)
        val wind = forecast?.getAsJsonObject(DataParams.WIND)

        weather_icon.loadFromUrl(
            getString(
                R.string.weather_icon,
                current?.get(DataParams.ICON)?.asString
            )
        )

        city_name.text =
            getString(R.string.city_name, forecast?.get(DataParams.CITY_NAME)?.asString)
        weather_main.text = getString(R.string.main, current?.get(DataParams.MAIN)?.asString)
        weather_desc.text = getString(R.string.description, current?.get(DataParams.DESC)?.asString)

        weather_temp.text =
            getString(R.string.temp, main?.get(DataParams.TEMP)?.asDouble?.toString())
        weather_pressure.text =
            getString(R.string.pressure, main?.get(DataParams.PRESSURE)?.asInt?.toString())
        weather_humidity.text =
            getString(R.string.humidity, main?.get(DataParams.HUMIDITY)?.asInt?.toString())
        weather_tempMin.text =
            getString(R.string.temp_min, main?.get(DataParams.TEMP_MIN)?.asDouble?.toString())
        weather_tempMax.text =
            getString(R.string.temp_max, main?.get(DataParams.TEMP_MAX)?.asDouble?.toString())

        wind_speed.text =
            getString(R.string.wind_speed, wind?.get(DataParams.WIND_SPEED)?.asDouble?.toString())
        wind_deg.text =
            getString(R.string.wind_deg, wind?.get(DataParams.WIND_DEG)?.asInt?.toString())

        val dayTime = forecast?.get(DataParams.DAY_TIME)?.asLong

        val dateFormat = SimpleDateFormat("E, dd MMM yyyy hh:mm a", Locale.getDefault())

        if (dayTime != null) {
            val date = Date(dayTime * 1000)
            day_time.text = getString(R.string.day_time, dateFormat.format(date))
        }

        val system = forecast?.getAsJsonObject(DataParams.SYS)

        country.text = getString(R.string.country, system?.get(DataParams.COUNTRY)?.asString)

        val sunriseTime = system?.get(DataParams.SUNRISE)?.asLong
        val sunsetTime = system?.get(DataParams.SUNSET)?.asLong

        if (sunriseTime != null) {
            val date = Date(sunriseTime * 1000)
            sunrise.text = getString(R.string.sunrise, dateFormat.format(date))
        }

        if (sunsetTime != null) {
            val date = Date(sunsetTime * 1000)
            sunset.text = getString(R.string.sunset, dateFormat.format(date))
        }

    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> renderFailure(R.string.failure_network_connection)
            is Failure.ServerError -> renderFailure(R.string.failure_server_error)
            is Failure.PermissionError -> renderFailure(R.string.failure_location_error)
        }
    }

    private fun renderFailure(@StringRes message: Int) {
        notify(message)
    }
}