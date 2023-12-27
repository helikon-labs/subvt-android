package io.helikon.subvt.data.repository

import android.app.Application
import io.helikon.subvt.data.model.app.User
import io.helikon.subvt.data.service.AppService

class UserRepository(private val application: Application) {
    private lateinit var appService: AppService

    private fun init() {
        if (!::appService.isInitialized) {
            appService = AppService(
                application,
                "https://api.kusama.subvt.io:17901/"
            )
        }
    }

    suspend fun createUser(): Result<User?> {
        init()
        return appService.createUser()
    }
}