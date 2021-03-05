package com.example.androiddevchallenge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

// 6 digits; 00h 00m 00s
internal const val MAX_LENGTH = 2 * 3



class CountdownViewModel(app: Application) : AndroidViewModel(app) {

    private val _keyboardIsVisible = MutableStateFlow(true)
    val keyboardIsVisible: StateFlow<Boolean> get() = _keyboardIsVisible

    private val _input = MutableStateFlow("")
    val input: Flow<Triple<String, String, String>> =
        _input
            .map { rawValue -> rawValue.padStringWithZeros(MAX_LENGTH) }
            .map { paddedString ->
                val hours = paddedString.substring(0, 2)
                val mins = paddedString.substring(2, 4)
                val secs = paddedString.substring(4, 6)
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
    }

    fun stopCountDown() {
        _keyboardIsVisible.value = true
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