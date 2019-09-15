package com.uwantolearn.uwanttolearn_android_stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

private const val MAXIMUM_STOP_WATCH_LIMIT = 3600L
private const val NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60

class StopWatchMainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private val displayInitialState by lazy { resources.getString(R.string._0_0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mergeClicks().switchMap {
            if (it) timerObservable()
            else Observable.just(displayInitialState)
        }.subscribe(display::setText)
            .let(disposable::add)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun mergeClicks(): Observable<Boolean> =
        listOf(startButton.clicks().map { true }, resetButton.clicks().map { false })
            .merge()
            .doOnNext(::buttonStateManager)


    private fun timerObservable(): Observable<String> =
        Observable.interval(0, 1, TimeUnit.SECONDS)
            .takeWhile { it <= MAXIMUM_STOP_WATCH_LIMIT }
            .map(timeFormatter)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { buttonStateManager(false) }

    private val timeFormatter: (Long) -> String =
        { secs ->
            if (secs == MAXIMUM_STOP_WATCH_LIMIT) displayInitialState
            else "${secs / NUMBER_OF_SECONDS_IN_ONE_MINUTE} : ${secs % NUMBER_OF_SECONDS_IN_ONE_MINUTE}"
        }

    private fun buttonStateManager(boolean: Boolean) {
        startButton.isEnabled = !boolean
        resetButton.isEnabled = boolean
    }
}