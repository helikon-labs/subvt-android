package io.helikon.subvt.ui.screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.helikon.subvt.data.service.AppService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IntroductionViewModel(application: Application) : AndroidViewModel(application) {
    private val appService = AppService(
        application,
        "https://api.kusama.subvt.io:17901/"
    )

    fun createUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = appService.createUser()
            Log.i("", "result is ${response.isSuccess}")
            Log.i("", "public key is ${response.getOrNull()?.publicKeyHex}")
        }
    }
}