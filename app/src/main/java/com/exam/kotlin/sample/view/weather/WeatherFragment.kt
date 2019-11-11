package com.exam.kotlin.sample.view.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.StringRes
import com.exam.kotlin.sample.R
import com.exam.kotlin.sample.base.BaseFragment
import com.exam.kotlin.sample.data.DataParams
import com.exam.kotlin.sample.data.LocationInfo
import com.exam.kotlin.sample.exemption.Failure
import com.exam.kotlin.sample.extension.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_weather.*
import javax.inject.Inject


/**
 * Created by msaycon on 09,Nov,2019
 */
class WeatherFragment : BaseFragment() {

    @Inject
    lateinit var weatherViewModel: WeatherViewModel

    lateinit var rxPermissions: RxPermissions

    lateinit var fusedLocationClient: FusedLocationProviderClient

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
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val locationInfo = LocationInfo(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )

                            weatherViewModel.loadForecast(locationInfo)
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
        val cityName = forecast?.get(DataParams.CITY_NAME)?.asString

        val weather = forecast?.getAsJsonArray(DataParams.WEATHER)

        val current = weather?.get(0)?.asJsonObject

        val description = current?.get(DataParams.DESC)?.asString
        val icon = current?.get(DataParams.ICON)?.asString

        city_name.text = cityName
        weather_desc.text = description

        weather_icon.loadFromUrl(getString(R.string.weather_icon, icon))
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