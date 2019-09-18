package com.uwantolearn.uwanttolearn_android_stopwatch

data class StopWatchUIState(
    val seconds: Long = 0L,
    val state: StopWatch = StopWatch.Idle
)


