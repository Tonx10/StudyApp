package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.ui.theme.StudyAppTheme
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()
        setContent {
            StudyAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController, viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(viewModel, onNavigate = { navController.navigate(it) })
        }
        composable("create") {
            CreateTaskScreen(viewModel, onBack = { navController.popBackStack() })
        }
        composable("pomodoro") {
            PomodoroScreen(viewModel, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun DashboardScreen(viewModel: MainViewModel, onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Welcome Lisa!", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Level ${viewModel.level.value} - XP: ${viewModel.xp.value} / 200")
        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { viewModel.xp.value / 200f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Today's Tasks:")
        for (task in viewModel.taskList) {
            Text("- $task")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onNavigate("create") }) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate("pomodoro") }) {
            Text("Go to Pomodoro")
        }
    }
}

@Composable
fun CreateTaskScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Add New Task", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.taskName.value,
            onValueChange = { viewModel.taskName.value = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val date = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                viewModel.taskDue.value = date
            }, year, month, day
        )

        OutlinedTextField(
            value = viewModel.taskDue.value,
            onValueChange = {}, // disable manual input
            label = { Text("Due Date") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    datePickerDialog.show()
                },
            enabled = false, // prevent keyboard input
            readOnly = true // also avoid input focus
        )


        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.addTask()
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun PomodoroScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val time = viewModel.formatTime()
    val isRunning = viewModel.isRunning.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pomodoro Timer", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(time, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.startTimer() },
            enabled = !isRunning,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Timer")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.resetTimer() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}