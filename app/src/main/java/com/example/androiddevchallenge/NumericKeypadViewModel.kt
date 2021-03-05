package com.example.androiddevchallenge

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

// 6 digits; 00h 00m 00s
internal const val MAX_LENGTH = 2 * 3



class NumericKeypadViewModel : ViewModel() {

    private val _input = MutableStateFlow("")
    val input: Flow<String> =
        _input
            .map { rawValue ->
                if(rawValue.length < MAX_LENGTH) {
                    val diff = MAX_LENGTH - rawValue.length
                    val pad = "0".repeat(diff)
                    "$pad$rawValue" // Padd it with Zeros
                } else {
                    rawValue
                }
            }
            .map { paddedString ->
                val hours = paddedString.substring(0, 2)
                val mins = paddedString.substring(2, 4)
                val secs = paddedString.substring(4, 6)
                Triple(hours, mins, secs)
            }
            .map { (h, m, s) -> "${h}h ${m}m ${s}s"}


    fun typeNumber(num: Int) {
        if(_input.value.length == MAX_LENGTH) return
        _input.value = _input.value + num.toShort()
    }

    fun delete() {
        val string = _input.value
        if(string.isEmpty()) return
        _input.value = string.subSequence(0 until string.lastIndex).toString()
    }

    fun startCountDown() {
        println(":)")
    }


}