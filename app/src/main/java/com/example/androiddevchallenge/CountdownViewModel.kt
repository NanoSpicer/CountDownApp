package com.example.androiddevchallenge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private val _countDownProgresses = MutableStateFlow(Triple(0f, 0f, 0f))
    // Containing hours,  mins and secs progress
    val countDownProgresses: StateFlow<Triple<Float, Float, Float>> get() = _countDownProgresses

    val input: Flow<Triple<String, String, String>> =
        hoursMinsSecs.map { (hours, mins, secs) ->
            Triple("${hours}h", "${mins}m", "${secs}s")
        }

    fun typeNumber(num: Int) {
        if(_input.value.length == MAX_LENGTH) return
        if(num == 0 && _input.value.isEmpty()) return
        _input.value = _input.value + num.toShort()
    }

    fun delete() {
        val string = _input.value
        if(string.isEmpty()) return
        _input.value = string.subSequence(0 until string.lastIndex).toString()
    }

    fun startCountDown() {
        _keyboardIsVisible.value = false

        countingJob = viewModelScope.launch(Dispatchers.Default) {
            val (hours, mins, secs) = hoursMinsSecs.first()
            var now = System.currentTimeMillis()
            val targetTime = now + hours.fromHoursToMillis() + mins.fromMinutesToMillis() + secs.fromSecondsToMillis()
            do {
                now = System.currentTimeMillis().coerceAtMost(targetTime)
                var millisDiff = targetTime - now
                val missingHours = millisDiff / (60*60*1000)
                millisDiff -= missingHours*(60*60*1000)
                val missingMins = millisDiff / (60*1000)
                millisDiff -= missingMins*(60*1000)
                val missingSecs = millisDiff / 1000
                millisDiff -= missingSecs*(1000)
                val modSecs = millisDiff

                _countingDownTextLabel.value = "${missingHours.toZeroPaddedString(2)}:${missingMins.toZeroPaddedString(2)}:${missingSecs.toZeroPaddedString(2)}"


                _countDownProgresses.value = Triple(
                    0f,
                    0f,
                    (modSecs.toFloat()/1000L).coerceIn(0f..1f)
                )

            } while(now < targetTime && isActive)

            withContext(Dispatchers.Main) {
                stopCountDown()
            }
        }
    }

    fun stopCountDown() {
        _keyboardIsVisible.value = true
        countingJob?.cancel()
        _countingDownTextLabel.value = TIMER_AT_ZERO
    }


    private fun Int.fromHoursToMillis() = this * 60*60*1000L
    private fun Int.fromMinutesToMillis() = this * 60*1000L
    private fun Int.fromSecondsToMillis() = this * 1000L

    private fun Long.toZeroPaddedString(desiredLength: Int): String = run {
        toString().padStringWithZeros(desiredLength)
    }
    private fun String.padStringWithZeros(desiredLength: Int): String = run {
        if(length < desiredLength) {
            val diff = desiredLength - length
            val pad = "0".repeat(diff)
            "$pad$this" // Pad it with Zeros
        } else {
            this
        }
    }


}