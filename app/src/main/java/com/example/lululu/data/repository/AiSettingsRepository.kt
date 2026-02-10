package com.example.lululu.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.aiSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "ai_settings"
)

/**
 * AI设置仓库
 * 使用 DataStore 持久化 API Key 和 Base URL
 */
class AiSettingsRepository(private val context: Context) {

    companion object {
        private val API_KEY = stringPreferencesKey("api_key")
        private val BASE_URL = stringPreferencesKey("base_url")
        private val MODEL_NAME = stringPreferencesKey("model_name")

        const val DEFAULT_BASE_URL = "https://api.openai.com/"
        const val DEFAULT_MODEL = "gpt-3.5-turbo"
    }

    val apiKeyFlow: Flow<String> = context.aiSettingsDataStore.data.map { prefs ->
        prefs[API_KEY] ?: ""
    }

    val baseUrlFlow: Flow<String> = context.aiSettingsDataStore.data.map { prefs ->
        prefs[BASE_URL] ?: DEFAULT_BASE_URL
    }

    val modelNameFlow: Flow<String> = context.aiSettingsDataStore.data.map { prefs ->
        prefs[MODEL_NAME] ?: DEFAULT_MODEL
    }

    suspend fun getApiKey(): String {
        return context.aiSettingsDataStore.data.first()[API_KEY] ?: ""
    }

    suspend fun getBaseUrl(): String {
        return context.aiSettingsDataStore.data.first()[BASE_URL] ?: DEFAULT_BASE_URL
    }

    suspend fun getModelName(): String {
        return context.aiSettingsDataStore.data.first()[MODEL_NAME] ?: DEFAULT_MODEL
    }

    suspend fun saveApiKey(key: String) {
        context.aiSettingsDataStore.edit { prefs ->
            prefs[API_KEY] = key
        }
    }

    suspend fun saveBaseUrl(url: String) {
        context.aiSettingsDataStore.edit { prefs ->
            prefs[BASE_URL] = url.ifBlank { DEFAULT_BASE_URL }
        }
    }

    suspend fun saveModelName(model: String) {
        context.aiSettingsDataStore.edit { prefs ->
            prefs[MODEL_NAME] = model.ifBlank { DEFAULT_MODEL }
        }
    }

    suspend fun saveAll(apiKey: String, baseUrl: String, modelName: String) {
        context.aiSettingsDataStore.edit { prefs ->
            prefs[API_KEY] = apiKey
            prefs[BASE_URL] = baseUrl.ifBlank { DEFAULT_BASE_URL }
            prefs[MODEL_NAME] = modelName.ifBlank { DEFAULT_MODEL }
        }
    }

    suspend fun isConfigured(): Boolean {
        return getApiKey().isNotBlank()
    }
}
