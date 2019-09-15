package com.uwantolearn.uwanttolearn_android_stopwatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

private const val MAXIMUM_STOP_WATCH_LIMIT = 3600L
private const val NUMBER_OF_SECONDS_IN_ONE_MINUTE = 60

class StopWatchMainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private val displayInitialState by lazy { resources.getString(R.string._0_0) }
    private val clickEmitterSubject = PublishSubject.create<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener { clickEmitterSubject.onNext(true) }
        resetButton.setOnClickListener { clickEmitterSubject.onNext(false) }


        clickEmitterSubject
            .doOnNext(::buttonStateManager)
            .switchMap {
                if (it) timerObservable()
                else Observable.just(displayInitialState)
            }.subscribe(display::setText)
            .let(disposable::add)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

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

