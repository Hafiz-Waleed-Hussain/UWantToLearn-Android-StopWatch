package com.uwantolearn.uwanttolearn_android_stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class StopWatchMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

fun main() {
    Observable.interval(0, 1, TimeUnit.MILLISECONDS)
        .takeWhile { it <= 3600L }
        .map { seconds -> "${seconds / 60} : ${seconds % 60}" }
        .subscribe(::println)

    while (true);
}