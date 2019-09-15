package com.uwantolearn.uwanttolearn_android_stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class StopWatchMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mergeClicks()
            .switchMap {
                if (it) timerObservable()
                else Observable.empty()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(display::setText)
    }

    private fun mergeClicks(): Observable<Boolean> = Observable.merge(
        listOf(
            startButton.clicks().map { true },
            resetButton.clicks().map { false }
        )
    ).doOnNext {
        startButton.isEnabled = !it
        resetButton.isEnabled = it
    }


    private fun timerObservable(): Observable<String> =
        Observable.interval(0, 1, TimeUnit.SECONDS)
            .takeWhile { it <= 3600L }
            .map { seconds -> "${seconds / 60} : ${seconds % 60}" }
}
