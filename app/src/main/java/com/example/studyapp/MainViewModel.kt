package com.example.studyapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {
    val taskName = mutableStateOf("")
    val taskDue = mutableStateOf("")
    val xp = mutableStateOf(150)
    val level = mutableStateOf(3)
    val taskList = mutableStateListOf(
        "Finish Assignment",
        "Review Lecture Notes",
        "Group Project Meeting"
    )

    fun addTask() {
        val name = taskName.value.trim()
        val due = taskDue.value.trim()
        if (name.isNotEmpty()) {
            val item = if (due.isNotEmpty()) "$name (Due: $due)" else name
            taskList.add(item)

            // Simple XP logic
            xp.value += 10
            if (xp.value >= 200) {
                xp.value -= 200
                level.value += 1
            }

            taskName.value = ""
            taskDue.value = ""
        }
    }

    private val _timeLeft = mutableStateOf(25 * 60) // 25 mins
    val timeLeft: State<Int> = _timeLeft

    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> = _isRunning

    private var timerJob: Job? = null

    fun startTimer() {
        if (_isRunning.value) return

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
        _timeLeft.value = 25 * 60
        _isRunning.value = false
    }

    fun formatTime(): String {
        val minutes = _timeLeft.value / 60
        val seconds = _timeLeft.value % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}