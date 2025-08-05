package com.example.studyapp

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

// DataStore keys
val Context.dataStore by preferencesDataStore(name = "user_prefs")

// Preferences Keys object
object PreferencesKeys {
    val XP = intPreferencesKey("xp")
    val LEVEL = intPreferencesKey("level")
    val TASKS = stringPreferencesKey("tasks")
}

// A serializable data class to save to DataStore
@Serializable
data class SerializableTask(
    val name: String,
    val dueDate: String,
    val type: String,
    val xpReward: Int
)

object DataManager {
    suspend fun saveXP(context: Context, xp: Int) {
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

    suspend fun loadXP(context: Context): Int {
        val prefs = context.dataStore.data.first()
        return prefs[PreferencesKeys.XP] ?: 0
    }

    suspend fun loadLevel(context: Context): Int {
        val prefs = context.dataStore.data.first()
        return prefs[PreferencesKeys.LEVEL] ?: 1
    }

    suspend fun loadTasks(context: Context): List<MainViewModel.Task> {
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
