package com.exam.kotlin.sample.di

import com.exam.kotlin.sample.ProjectApplication
import com.exam.kotlin.sample.di.viewmodel.ViewModelModule
import com.exam.kotlin.sample.view.weather.WeatherFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Created by msaycon on 01,Oct,2019
 */

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(application: ProjectApplication)
    fun inject(weatherFragment: WeatherFragment)
}
