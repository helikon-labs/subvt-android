package io.helikon.subvt.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "subvt_preferences")

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val userCreated: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.USER_CREATED] ?: false
        }

    suspend fun setUserCreated(userCreated: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_CREATED] = userCreated
        }
    }

    private object Keys {
        val USER_CREATED = booleanPreferencesKey("user_created")
    }
}