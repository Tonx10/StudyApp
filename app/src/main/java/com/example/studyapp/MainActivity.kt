package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyapp.ui.theme.StudyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
//                DashboardScreen()
//                CreateTaskScreen()
                PomodoroScreen()
            }
        }
    }
}


@Composable
fun DashboardScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Welcome Lisa!", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Level 3 - XP: 150 / 200")
        Spacer(modifier = Modifier.height(4.dp))

        // XP progress bar (150/200 = 0.75f)
        LinearProgressIndicator(
            progress = { 0.75f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Today's Tasks:")
        Text("- Finish Assignment")
        Text("- Review Lecture Notes")
        Text("- Group Project Meeting")
    }
}



@Composable
fun CreateTaskScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Add New Task", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Task Name: _______")
        Spacer(modifier = Modifier.height(8.dp))

        Text("Due Date: ________")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { println("Task added!") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}


@Composable
fun PomodoroScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pomodoro Timer", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("25:00", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { println("Timer Started") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Timer")
        }
    }
}
