/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.app.Application
import androidx.compose.animation.core.AnimationConstants
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 6 digits; 00h 00m 00s
internal const val MAX_LENGTH = 2 * 3

internal const val TIMER_AT_ZERO = "00:00:00"
class CountdownViewModel(app: Application) : AndroidViewModel(app) {

    private var countingJob: Job? = null

    private val _keyboardIsVisible = MutableStateFlow(true)
    val keyboardIsVisible: StateFlow<Boolean> get() = _keyboardIsVisible

    private val _input = MutableStateFlow("")
    private val hoursMinsSecs =
        _input
            .map { rawValue -> rawValue.padStringWithZeros(MAX_LENGTH) }
            .map { paddedString ->
                val hours = paddedString.substring(0, 2)
                val mins = paddedString.substring(2, 4)
                val secs = paddedString.substring(4, 6)
                Triple(hours.toInt(), mins.toInt(), secs.toInt())
            }

    private val _countingDownTextLabel = MutableStateFlow(TIMER_AT_ZERO)
    val countingDownTextLabel: StateFlow<String> get() = _countingDownTextLabel

    private val _countDownProgresses = MutableStateFlow(Triple(1f, 1f, 1f))
    // Containing hours,  mins and secs progress
    val countDownProgresses: StateFlow<Triple<Float, Float, Float>> get() = _countDownProgresses

    val input: Flow<Triple<String, String, String>> =
        hoursMinsSecs.map { (hours, mins, secs) ->
            Triple("${hours}h", "${mins}m", "${secs}s")
        }

    fun typeNumber(num: Int) {
        if (_input.value.length == MAX_LENGTH) return
        if (num == 0 && _input.value.isEmpty()) return
        _input.value = _input.value + num.toShort()
    }

    fun delete() {
        val string = _input.value
        if (string.isEmpty()) return
        _input.value = string.subSequence(0 until string.lastIndex).toString()
    }

    fun startCountDown() {
        _keyboardIsVisible.value = false

        countingJob = viewModelScope.launch(Dispatchers.Default) {
            val (targetHours, targetMins, targetSeconds) = hoursMinsSecs.first()
            delay((AnimationConstants.DefaultDurationMillis * 4.5).toLong())
            var now = System.currentTimeMillis()
            val targetTime = (
                now +
                    targetHours.fromHoursToMillis() +
                    targetMins.fromMinutesToMillis() +
                    targetSeconds.fromSecondsToMillis()
                )

            do {
                now = System.currentTimeMillis().coerceAtMost(targetTime)
                var millisDiff = targetTime - now
                val missingHours = millisDiff / (60 * 60 * 1000L)
                millisDiff -= missingHours * (60 * 60 * 1000L)
                val missingMins = millisDiff / (60 * 1000L)
                millisDiff -= missingMins * (60 * 1000)
                val missingSecs = millisDiff / 1000
                millisDiff -= missingSecs * (1000)
                val missingMillis = millisDiff

                _countingDownTextLabel.value = "${missingHours.toZeroPaddedString(2)}:${missingMins.toZeroPaddedString(2)}:${missingSecs.toZeroPaddedString(2)}"

                _countDownProgresses.value = Triple(
                    safelyDivideAndCoerce(missingMins, 60L),
                    safelyDivideAndCoerce(missingSecs, 60L),
                    safelyDivideAndCoerce(missingMillis, 1000L)
                )
            } while (now < targetTime && isActive)

            withContext(Dispatchers.Main) {
                stopCountDown()
            }
        }
    }

    fun stopCountDown() {
        _keyboardIsVisible.value = true
        countingJob?.cancel()
        _countingDownTextLabel.value = TIMER_AT_ZERO
        _countDownProgresses.value = Triple(1f, 1f, 1f)
    }

    private fun safelyDivideAndCoerce(first: Long, second: Long): Float {
        if (second == 0L) return 0f
        return (first.toDouble() / second.toDouble()).toFloat().coerceIn(0f..1f)
    }

    private fun Int.fromHoursToMillis() = this * 60 * 60 * 1000L
    private fun Int.fromMinutesToMillis() = this * 60 * 1000L
    private fun Int.fromSecondsToMillis() = this * 1000L

    private fun Long.toZeroPaddedString(desiredLength: Int): String = run {
        toString().padStringWithZeros(desiredLength)
    }
    private fun String.padStringWithZeros(desiredLength: Int): String = run {
        if (length < desiredLength) {
            val diff = desiredLength - length
            val pad = "0".repeat(diff)
            "$pad$this" // Pad it with Zeros
        } else {
            this
        }
    }
}
