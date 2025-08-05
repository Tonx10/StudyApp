package com.example.studyapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.viewmodel.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyAppTheme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    viewModel.loadData()
                }

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
    val xp by viewModel.xp.collectAsState()
    val level by viewModel.level.collectAsState()
    val tasks by viewModel.taskList.collectAsState()

    val animatedProgress = animateFloatAsState(
        targetValue = xp / 200f,
        label = "XP Progress"
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Welcome Lisa!", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Level $level - XP: $xp / 200")
        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { animatedProgress.value },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Today's Tasks:")

        if (tasks.isEmpty()) {
            Text("No tasks yet", modifier = Modifier.padding(top = 8.dp))
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = Calendar.getInstance().time

            for (task in tasks) {
                val dueDate = dateFormat.parse(task.dueDate)
                val diffInMillis = dueDate.time - today.time
                val daysLeft = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${task.name} (${task.type})")
                        Text("Due: ${task.dueDate} • $daysLeft days left • XP: ${task.xpReward}", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.finishTask(task) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "Finish Task")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Finish")
                    }
                }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var taskName by remember { viewModel.taskName }
    var taskDue by remember { viewModel.taskDue }
    var selectedTaskType by remember { viewModel.selectedTaskType }
    var expanded by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            taskDue = dateFormatter.format(selectedDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Add New Task",
                fontSize = 24.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
            Text(if (taskDue.isEmpty()) "Select Due Date" else "Due: $taskDue")
        }

        val taskTypes = listOf("Chore", "Homework", "Study for Test", "Group Project")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedTaskType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Task Type") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                taskTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedTaskType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.addTask(taskName, taskDue, selectedTaskType)
                onBack()
            },
            enabled = taskName.isNotBlank() && taskDue.isNotBlank() && selectedTaskType.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}


@Composable
fun PomodoroScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val time by viewModel.formatTime().collectAsState(initial = "25:00")
    val isRunning by viewModel.isRunning.collectAsState()

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

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}