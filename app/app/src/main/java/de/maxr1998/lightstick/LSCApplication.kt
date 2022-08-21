package de.maxr1998.lightstick

import android.app.Application
import timber.log.Timber

class LSCApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}