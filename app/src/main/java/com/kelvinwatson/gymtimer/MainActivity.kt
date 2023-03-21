package com.kelvinwatson.gymtimer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelvinwatson.gymtimer.ui.theme.GymTimerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GymTimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    GymTimerScreen(viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.reset()
    }
}

@Composable
fun GymTimerScreen(gymTimerViewModel: GymTimerViewModel) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = "Gym Timer", style = MaterialTheme.typography.headlineLarge)

        Text(text = gymTimerViewModel.blinkMessage.collectAsState(initial = "").value)

        Text(
            text = gymTimerViewModel.currentTimeRemainingDisplay.collectAsState().value,
            style = MaterialTheme.typography.displayLarge
        )

        // FIXME: The two below buttons should be one button with two states (pause and resume).

        Button(onClick = {
            gymTimerViewModel.pause()
        }) {
            Text("Pause")
        }

        Button(onClick = {
            gymTimerViewModel.resume()
        }) {
            Text("Resume")
        }

        Button(
            enabled = gymTimerViewModel.allowNewCountDown,
            onClick = {
                gymTimerViewModel.countDown(10_000L)
            }) {
            Text("10 seconds")
        }

        Button(
            enabled = gymTimerViewModel.allowNewCountDown,
            onClick = {
                gymTimerViewModel.countDown(15_000L)
            }) {
            Text("15 seconds")
        }

        Button(
            enabled = gymTimerViewModel.allowNewCountDown,
            onClick = {
                gymTimerViewModel.countDown(30_000L)
            }) {
            Text("30 seconds")
        }

        Button(
            enabled = gymTimerViewModel.allowNewCountDown,
            onClick = {
                gymTimerViewModel.countDown(60_000L)
            }) {
            Text("60 seconds")
        }

        Button(
            enabled = gymTimerViewModel.allowNewCountDown,
            onClick = {
                gymTimerViewModel.countDown(90_000L)
            }) {
            Text("90 seconds")
        }

        Button(
            enabled = gymTimerViewModel.allowNewCountDown,
            onClick = {
                gymTimerViewModel.reset()
            }) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(gymTimerViewModel: GymTimerViewModel = viewModel()) {
    GymTimerTheme {
        GymTimerScreen(gymTimerViewModel)
    }
}