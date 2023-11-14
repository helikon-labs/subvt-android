package io.helikon.subvt.data.repository

import android.app.Application
import io.helikon.subvt.data.model.app.User
import io.helikon.subvt.data.service.AppService

class UserRepository(application: Application) {
    private val appService = AppService(
        application,
        "https://api.kusama.subvt.io:17901/"
    )

    suspend fun createUser(): Result<User?> {
        return appService.createUser()
    }
}