package com.example.androiddevchallenge.ui

import android.content.Context
import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.*
import com.example.androiddevchallenge.R

class HMSViewModal(numHours: Int, numMinutes: Int, numSeconds: Int) {
    var numHours: Int by mutableStateOf(numHours)
    var numMinutes: Int by mutableStateOf(numMinutes)
    var numSeconds: Int by mutableStateOf(numSeconds)
}

/*
class HMSMViewModal(numHours: Int, numMinutes: Int, numSeconds: Int) :
    HMSViewModal(numHours, numMinutes, numSeconds) {
    var numMilliseconds by mutableStateOf(numHours)
}
*/

@Composable
fun CountdownTimer() {
    var state by rememberSaveable { mutableStateOf(CountdownTimerState.STOPPED) }
    var countDownTimer: CountDownTimer? by remember { mutableStateOf(null) }
    val totalTimeViewModal = remember/*Saveable (TODO)*/ { HMSViewModal(0, 0, 0) }

    var remainingTimeMillis: Long? by rememberSaveable { mutableStateOf(null) }

    fun timerDone() {
        state = CountdownTimerState.STOPPED
        countDownTimer = null
        remainingTimeMillis = null
    }

    val context = LocalContext.current
    when (state) {
        CountdownTimerState.STOPPED -> TimerLayout({
            Row {
                // hours
                TimeNumberTextField(totalTimeViewModal.numHours.toString(),
                    { it.toIntOrNull()?.let { if (it >= 0) totalTimeViewModal.numHours = it } })
                Text(":")
                // minutes
                TimeNumberTextField(totalTimeViewModal.numMinutes.toAtLease2DigitString(),
                    {
                        it.toIntOrNull()?.let {
                            if (isValidMinutesOrSeconds(it)) totalTimeViewModal.numMinutes = it
                        }
                    })
                Text(":")
                // seconds
                TimeNumberTextField(totalTimeViewModal.numSeconds.toAtLease2DigitString(),
                    {
                        it.toIntOrNull()?.let {
                            if (isValidMinutesOrSeconds(it)) totalTimeViewModal.numSeconds = it
                        }
                    })
            }
        }, {
            Row {
                // start
                Button(onClick = {
                    state = CountdownTimerState.STARTED
                    countDownTimer = startCountDownTimer(
                        context,
                        hmsToMillis(
                            totalTimeViewModal.numHours,
                            totalTimeViewModal.numMinutes,
                            totalTimeViewModal.numSeconds
                        ), { remainingTimeMillis = it },
                        ::timerDone
                    )
                }) {
                    Icon(Icons.Filled.PlayCircle, stringResource(R.string.start))
                }

                // clear
                Button(onClick = {
                    totalTimeViewModal.numHours = 0
                    totalTimeViewModal.numMinutes = 0
                    totalTimeViewModal.numSeconds = 0
                }) {
                    Icon(Icons.Filled.Clear, stringResource(R.string.clear))
                }
            }
        })
        CountdownTimerState.STARTED, CountdownTimerState.PAUSED -> {
            val remainingTimeHmsm = millisToHmsm(remainingTimeMillis ?: 0) // TODO
            TimerLayout(
                {
                    Row {
                        Text(remainingTimeHmsm.numHours.toString())
                        Text(":")
                        Text(remainingTimeHmsm.numMinutes.toAtLease2DigitString())
                        Text(":")
                        Text(remainingTimeHmsm.numSeconds.toAtLease2DigitString())
                        Text(".")
                        // milliseconds, not editable
                        // Should be accurate when paused.
                        Text(remainingTimeHmsm.numMilliseconds.toAtLeaseNDigitString(3))
                    }
                }, {
                    Row {
                        when (state) {
                            CountdownTimerState.STARTED ->
                                // pause
                                Button(onClick = {
                                    state = CountdownTimerState.PAUSED
                                    countDownTimer!!.cancel()
                                    countDownTimer = null
                                }) {
                                    Icon(Icons.Filled.PauseCircle, stringResource(R.string.pause))
                                }
                            CountdownTimerState.PAUSED ->
                                Button(onClick = {
                                    state = CountdownTimerState.STARTED
                                    countDownTimer =
                                        startCountDownTimer(context, remainingTimeMillis!!, {
                                            remainingTimeMillis = it
                                        }, ::timerDone)
                                }) {
                                    Icon(Icons.Filled.PlayCircle, stringResource(R.string.resume))
                                }
                            else -> throw IllegalArgumentException(state.toString())
                        }
                        // stop
                        Button(onClick = {
                            state = CountdownTimerState.STOPPED
                            countDownTimer!!.cancel()
                            countDownTimer = null
                            remainingTimeMillis = null
                        }) {
                            Icon(Icons.Filled.StopCircle, stringResource(R.string.stop))
                        }
                    }
                })

        }
    }
}

@Composable
fun TimeNumberTextField(
    value: String,
    onValueChange: (String) -> Unit
) =
    OutlinedTextField(value, onValueChange, Modifier.width(64.dp))

@Composable
fun TimerLayout(timeText: @Composable () -> Unit, buttons: @Composable () -> Unit) =
    Column {
        timeText()
        buttons()
    }

fun startCountDownTimer(
    context: Context,
    millis: Long,
    onTick: (Long) -> Unit,
    onFinishMore: () -> Unit
): CountDownTimer {
    return object : CountDownTimer(
        millis,
        1L
    ) {
        override fun onTick(millisUntilFinished: Long) {
            onTick(millisUntilFinished)
        }

        override fun onFinish() {
            Toast.makeText(context, R.string.timer_done, Toast.LENGTH_LONG).show()
            /*AlertDialog(
                onDismissRequest = {},
                title = { stringResource(R.string.timer_done) },
                buttons = { Text(stringResource(android.R.string.ok)) }
            )*/
            onFinishMore()
        }
    }.start()
}