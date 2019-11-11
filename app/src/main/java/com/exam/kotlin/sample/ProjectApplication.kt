package com.exam.kotlin.sample

import android.app.Application
import com.exam.kotlin.sample.di.AppComponent
import com.exam.kotlin.sample.di.AppModule
import com.exam.kotlin.sample.di.DaggerAppComponent

/**
 * Created by msaycon on 01,Oct,2019
 */

class ProjectApplication : Application() {

    val appComponent: AppComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }
}