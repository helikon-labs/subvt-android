package io.helikon.subvt.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "subvt_preferences")

class UserPreferencesRepository(context: Context) {
    private object Keys {
        val USER_CREATED = booleanPreferencesKey("user_created")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    private val dataStore = context.dataStore

    val userCreated: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.USER_CREATED] ?: false
        }

    suspend fun setUserIsCreated(userIsCreated: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_CREATED] = userIsCreated
        }
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(onboardingCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_COMPLETED] = onboardingCompleted
        }
    }
}