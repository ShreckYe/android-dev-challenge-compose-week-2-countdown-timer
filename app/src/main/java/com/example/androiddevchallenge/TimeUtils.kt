package com.example.androiddevchallenge

fun Int.toAtLeaseNDigitString(numDigits: Int) =
    toString().padStart(numDigits, '0')

fun Int.toAtLease2DigitString() =
    toAtLeaseNDigitString(2)

fun isValidMinutesOrSeconds(timeNumber: Int) =
    timeNumber in 0 until 60

/*
@ExperimentalTime
fun hmsToMillis(numHours: Int, numMinutes: Int, numSeconds: Int) =
    (numHours.hours + numMinutes.minutes + numSeconds.seconds).toLongMilliseconds()
*/

fun hmsToMillis(numHours: Int, numMinutes: Int, numSeconds: Int) =
    ((numHours * 60 + numMinutes) * 60 + numSeconds) * 1000L

data class HoursMinutesSecondsMilliseconds(
    val numHours: Int, val numMinutes: Int, val numSeconds: Int, val numMilliseconds: Int
)

fun millisToHmsm(millis: Long): HoursMinutesSecondsMilliseconds {
    val numMilliseconds = (millis % 1000).toInt()
    val numAllInSeconds = millis / 1000
    val numSeconds = (numAllInSeconds % 60).toInt()
    val numAllInMinutes = numAllInSeconds / 60
    val numMinutes = (numAllInMinutes % 60).toInt()
    val numHours = (numAllInMinutes / 60).toInt()
    return HoursMinutesSecondsMilliseconds(numHours, numMinutes, numSeconds, numMilliseconds)
}