package com.example.studyapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.studyapp.MainViewModel
import kotlinx.coroutines.flow.first
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "user_prefs")

object PreferencesKeys {
    val XP = intPreferencesKey("xp")
    val LEVEL = intPreferencesKey("level")
    val TASKS = stringPreferencesKey("tasks")
}

data class SerializableTask(
    val name: String,
    val dueDate: String,
    val type: String,
    val xpReward: Int
)


object DataStoreHelper {

    suspend fun saveXp(context: Context, xp: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.XP] = xp
        }
    }

    suspend fun saveLevel(context: Context, level: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LEVEL] = level
        }
    }

    suspend fun saveTasks(context: Context, tasks: List<MainViewModel.Task>) {
        val serializableTasks = tasks.map {
            SerializableTask(it.name, it.dueDate, it.type, it.xpReward)
        }
        val json = Gson().toJson(serializableTasks)
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.TASKS] = json
        }
    }

    fun readXp(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { prefs ->
                prefs[PreferencesKeys.XP] ?: 0
            }
    }

    fun readLevel(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { prefs ->
                prefs[PreferencesKeys.LEVEL] ?: 1
            }
    }

    suspend fun readTasks(context: Context): List<MainViewModel.Task> {
        val prefs = context.dataStore.data.first()
        val json = prefs[PreferencesKeys.TASKS] ?: "[]"
        return try {
            val type = object : TypeToken<List<SerializableTask>>() {}.type
            Gson().fromJson<List<SerializableTask>>(json, type).map {
                MainViewModel.Task(it.name, it.dueDate, it.type, it.xpReward)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
