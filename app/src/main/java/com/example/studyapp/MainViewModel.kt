package com.example.studyapp

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.datastore.DataStoreHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class MainViewModel(private val context: Context) : ViewModel() {

    data class Task(
        val name: String,
        val dueDate: String,
        val type: String,
        val xpReward: Int
    )
    val xp = MutableStateFlow(0)
    val level = MutableStateFlow(1)

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList.asStateFlow()

    var taskName = mutableStateOf("")
    var taskDue = mutableStateOf("")
    var selectedTaskType = mutableStateOf("Homework")

    val taskTypeXp = mapOf(
        "Chore" to 10,
        "Homework" to 20,
        "Study for Test" to 35,
        "Group Project" to 50,
        "Reading" to 30,
        "Exercise" to 15,
        "Household" to 15,
        "Planning" to 10,
        "Creative Project" to 45,
        "Personal Development" to 30,
        "Errands" to 10,
        "Finances" to 25,
        "Health" to 20,
        "Social" to 5
    )

    private val _timeLeft = MutableStateFlow(25 * 60)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    private var initialTimerDuration = 25 * 60
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            DataStoreHelper.readXp(context).collect { newXp -> xp.value = newXp }
        }
        viewModelScope.launch {
            DataStoreHelper.readLevel(context).collect { newLevel -> level.value = newLevel }
        }
    }

    fun addTask(name: String, due: String, type: String) {
        val xpValue = taskTypeXp[type] ?: 10
        if (name.isNotBlank() && due.isNotBlank()) {
            val newTask = Task(name, due, type, xpValue)
            val updatedTasks = _taskList.value.toMutableList().apply { add(newTask) }
            _taskList.value = updatedTasks
            saveData()
            taskName.value = ""
            taskDue.value = ""
            selectedTaskType.value = "Homework"
        }
    }

    fun finishTask(task: Task) {
        val updatedTasks = _taskList.value.toMutableList().apply { remove(task) }
        _taskList.value = updatedTasks
        gainXp(task.xpReward)
        saveData()
    }

    private fun gainXp(amount: Int) {
        val newXp = xp.value + amount
        if (newXp >= 200) {
            level.value += 1
            xp.value = newXp - 200
        } else {
            xp.value = newXp
        }
        saveData()
    }

    fun startTimer(totalSeconds: Int) {
        if (_isRunning.value) return

        initialTimerDuration = totalSeconds
        _timeLeft.value = totalSeconds
        _isRunning.value = true

        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1
            }
            _isRunning.value = false
        }
    }


    fun resetTimer() {
        timerJob?.cancel()
        _timeLeft.value = initialTimerDuration
        _isRunning.value = false
    }


    fun formatTime(): Flow<String> {
        return timeLeft.map { timeInSeconds ->
            val minutes = timeInSeconds / 60
            val seconds = timeInSeconds % 60
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _taskList.value = DataStoreHelper.readTasks(context)
        }
    }

    private fun saveData() {
        viewModelScope.launch {
            DataStoreHelper.saveXp(context, xp.value)
            DataStoreHelper.saveLevel(context, level.value)
            DataStoreHelper.saveTasks(context, _taskList.value)
        }
    }
}
