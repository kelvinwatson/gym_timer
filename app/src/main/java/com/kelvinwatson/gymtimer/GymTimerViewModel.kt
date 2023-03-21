package com.kelvinwatson.gymtimer

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * State holder for [CountDownTimer].
 */
class GymTimerViewModel(app: Application) : AndroidViewModel(app) {

    private val timesUpNotificationHelper = OverTimeNotificationHelper(app)

    private var countDownTimer: CountDownTimer? = null
    private var countDownInProgressInternal = MutableStateFlow(false)
    private var blinkJob: Job? = null

    private val currentTimeRemainingMillis = MutableStateFlow(0L)
    private val currentTimeRemainingDisplayInternal = MutableStateFlow("00:00:00")
    val currentTimeRemainingDisplay: StateFlow<String>
        get() = currentTimeRemainingDisplayInternal

    private val blinkMessageInternal = MutableStateFlow("")
    val blinkMessage: StateFlow<String>
        get() = blinkMessageInternal

    val allowNewCountDown :Boolean
        get() = !countDownInProgressInternal.value

    private fun Long.toDisplayTime(): String = String.format(
        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60, // 60 min per hour
        TimeUnit.MILLISECONDS.toSeconds(this) % 60 // 60 sec per min
    )

    /**
     * Start a new countdown.
     */
    fun countDown(millis: Long) {
        reset()

        countDownTimer = object : CountDownTimer(millis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                countDownInProgressInternal.value = true
                currentTimeRemainingMillis.value = millisUntilFinished
                currentTimeRemainingDisplayInternal.value = millisUntilFinished.toDisplayTime()
            }

            override fun onFinish() {
                countDownInProgressInternal.value = false
                blinkAndNotify()
            }
        }.start()
    }

    /**
     * Pauses the current timer. Internally, this actually cancels the current timer.
     */
    fun pause() {
        countDownTimer?.cancel()
    }

    /**
     * Restore timer and overtime text.
     */
    fun reset() {
        blinkJob?.cancel()
        blinkMessageInternal.value = ""
        countDownTimer?.cancel()
        currentTimeRemainingDisplayInternal.value = "00:00:00"
        countDownInProgressInternal.value = false
    }

    /**
     * Creates and starts a new timer with the remaining time.
     */
    fun resume() {
        countDownTimer = object : CountDownTimer(currentTimeRemainingMillis.value, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownInProgressInternal.value = true
                currentTimeRemainingMillis.value = millisUntilFinished
                currentTimeRemainingDisplayInternal.value = millisUntilFinished.toDisplayTime()
            }

            override fun onFinish() {
                countDownInProgressInternal.value = false
                blinkAndNotify()
            }
        }.start()
    }

    private fun blinkAndNotify() {
        blinkJob = viewModelScope.launch {
            var k = 0
            while (isActive) {
                timesUpNotificationHelper.onOverTime(k)
                blinkMessageInternal.value = "Times up. ${k}sec over time!"
                k++
                delay(1_000L)
            }
        }
    }
}