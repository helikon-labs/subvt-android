package io.helikon.subvt.app

import android.app.Application
import io.helikon.subvt.BuildConfig
import timber.log.Timber

class SubVTApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
