package com.uwantolearn.uwanttolearn_android_stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class StopWatchMainActivity : AppCompatActivity() {

    private val clickEmitterSubject = PublishSubject.create<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener { clickEmitterSubject.onNext(true) }
        resetButton.setOnClickListener { clickEmitterSubject.onNext(false) }
    }

    fun timerObservable(): Observable<String> = Observable.interval(0, 1, TimeUnit.MILLISECONDS)
        .takeWhile { it <= 3600L }
        .map { seconds -> "${seconds / 60} : ${seconds % 60}" }
}

