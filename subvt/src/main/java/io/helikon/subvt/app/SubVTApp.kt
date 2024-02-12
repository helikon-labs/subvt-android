package io.helikon.subvt.app

import android.app.Application
import com.google.android.filament.utils.Utils
import dagger.hilt.android.HiltAndroidApp
import io.helikon.subvt.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class SubVTApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
