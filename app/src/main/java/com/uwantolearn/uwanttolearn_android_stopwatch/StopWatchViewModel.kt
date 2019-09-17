package com.uwantolearn.uwanttolearn_android_stopwatch

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StopWatchViewModel {

    private val publishSubject = PublishSubject.create<StopWatch>()

    fun provide(observable: Observable<StopWatch>): Disposable =
        observable.subscribe(publishSubject::onNext)


    fun uiState(): Observable<StopWatchUIState> =
        publishSubject.switchMap {
            when (it) {
                StopWatch.Idle -> {
                    Observable.just(StopWatchUIState())
                }
                StopWatch.Running -> {
                    timerObservable()
                        .map { sec -> StopWatchUIState(sec, StopWatch.Running) }
                }
                StopWatch.Pause -> {
                    TODO()
                }
            }
        }.hide()
            .observeOn(AndroidSchedulers.mainThread())

    private fun timerObservable() =
        Observable.interval(0L, 1L, TimeUnit.SECONDS)
            .takeWhile { it <= MAXIMUM_STOP_WATCH_LIMIT }
}