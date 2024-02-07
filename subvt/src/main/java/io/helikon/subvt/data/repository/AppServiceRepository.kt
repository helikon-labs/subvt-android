package io.helikon.subvt.data.repository

import android.content.Context
import io.helikon.subvt.BuildConfig
import io.helikon.subvt.data.model.app.Network
import io.helikon.subvt.data.model.app.User
import io.helikon.subvt.data.service.AppService
import kotlinx.collections.immutable.ImmutableList
import javax.inject.Inject

class AppServiceRepository
    @Inject
    constructor(private val context: Context) {
        private lateinit var appService: AppService

        private fun init() {
            if (!::appService.isInitialized) {
                appService =
                    AppService(
                        context,
                        "https://${BuildConfig.API_HOST}:${BuildConfig.APP_SERVICE_PORT}/",
                    )
            }
        }

        suspend fun createUser(): Result<User?> {
            init()
            return appService.createUser()
        }

        suspend fun getNetworks(): Result<ImmutableList<Network>?> {
            init()
            return appService.getNetworks()
        }
    }
