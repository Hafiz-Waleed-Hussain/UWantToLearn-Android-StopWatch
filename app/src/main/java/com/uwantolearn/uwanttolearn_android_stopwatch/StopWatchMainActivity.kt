package com.uwantolearn.uwanttolearn_android_stopwatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_main.*


class StopWatchMainActivity : AppCompatActivity() {

    // first = StartButton, second = PauseButton, third = ResetButton
    private val IDLE_BUTTON_STATE = Triple(true, false, false)
    private val RUNNING_BUTTON_STATE = Triple(false, true, true)
    private val PAUSE_BUTTON_STATE = Triple(true, false, true)
    private val displayInitialState by lazy { resources.getString(R.string._0_0) }
    private val serialDisposableForProvide = SerialDisposable()
    private val serialDisposableForUiState = SerialDisposable()

    private val viewModel = StopWatchViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mergeClicks()
            .let(viewModel::provide)
            .let(serialDisposableForProvide::set)

        viewModel.uiState()
            .subscribe(::render)
            .let(serialDisposableForUiState::set)
    }

    override fun onDestroy() {
        serialDisposableForProvide.dispose()
        serialDisposableForUiState.dispose()
        super.onDestroy()
    }

    private fun mergeClicks(): Observable<StopWatch> =
        listOf(
            startButton.clicks().map { StopWatch.Running },
            pauseButton.clicks().map { StopWatch.Pause },
            resetButton.clicks().map { StopWatch.Idle })
            .merge()


    private fun render(uiState: StopWatchUIState) {
        when (uiState.state) {
            StopWatch.Idle -> IDLE_BUTTON_STATE
            StopWatch.Running -> RUNNING_BUTTON_STATE
            StopWatch.Pause -> PAUSE_BUTTON_STATE
        }.let(::buttonStateManager)
        timeFormatter(uiState.seconds).let(display::setText)
    }

    private val timeFormatter: (Long) -> String =
        { secs ->
            if (secs == MAXIMUM_STOP_WATCH_LIMIT || secs == 0L) displayInitialState
            else "${secs / NUMBER_OF_SECONDS_IN_ONE_MINUTE} : ${secs % NUMBER_OF_SECONDS_IN_ONE_MINUTE}"
        }

    private fun buttonStateManager(triple: Triple<Boolean, Boolean, Boolean>) {
        val (start, pause, reset) = triple
        startButton.isEnabled = start
        pauseButton.isEnabled = pause
        resetButton.isEnabled = reset
    }
}

