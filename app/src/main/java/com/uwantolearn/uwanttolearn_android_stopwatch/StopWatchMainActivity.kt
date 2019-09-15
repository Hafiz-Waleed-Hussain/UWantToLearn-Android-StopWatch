package com.uwantolearn.uwanttolearn_android_stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class StopWatchMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.clicks().map { true }
            .subscribe { println(it) }
        resetButton.clicks().map { false }
            .subscribe { println(it) }
    }

    fun timerObservable(): Observable<String> = Observable.interval(0, 1, TimeUnit.MILLISECONDS)
        .takeWhile { it <= 3600L }
        .map { seconds -> "${seconds / 60} : ${seconds % 60}" }
}
