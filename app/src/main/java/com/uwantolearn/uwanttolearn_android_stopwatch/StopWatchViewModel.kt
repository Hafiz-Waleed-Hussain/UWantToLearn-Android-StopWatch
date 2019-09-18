package com.uwantolearn.uwanttolearn_android_stopwatch

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StopWatchViewModel {

    private val publishSubject = PublishSubject.create<StopWatch>()
    private val pauseStateSubject = BehaviorSubject.create<Long>()
    private val resumeStateSubject = BehaviorSubject.createDefault(0L)


    fun provide(observable: Observable<StopWatch>): Disposable =
        observable.subscribe(publishSubject::onNext)


    fun uiState(): Observable<StopWatchUIState> =
        publishSubject.switchMap {
            when (it) {
                StopWatch.Idle -> {
                    Observable.just(StopWatchUIState())
                        .doOnNext { pauseStateSubject.onNext(it.seconds) }
                        .doOnNext { resumeStateSubject.onNext(it.seconds) }
                }
                StopWatch.Running -> {
                    timerObservable()
                        .doOnNext(pauseStateSubject::onNext)
                        .map { sec -> StopWatchUIState(sec, StopWatch.Running) }
                }
                StopWatch.Pause -> {
                    resumeStateSubject.onNext(pauseStateSubject.value!!)
                    Observable.just(
                        StopWatchUIState(
                            pauseStateSubject.value!!,
                            state = StopWatch.Pause
                        )
                    )

                }
            }
        }.hide()
            .observeOn(AndroidSchedulers.mainThread())

    private fun timerObservable() =
        Observable.interval(0L, 1L, TimeUnit.SECONDS)
            .map { it + resumeStateSubject.value!! }
            .takeWhile { it <= MAXIMUM_STOP_WATCH_LIMIT }
}