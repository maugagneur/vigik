package com.kidor.vigik.ui.base

import java.util.Timer
import java.util.TimerTask

interface BaseViewModel<T> {

    fun setView(view: T)

    fun onStart() {
        throw NotImplementedError()
    }

    fun onStop() {
        throw NotImplementedError()
    }

    /**
     * Starts the specified task.
     *
     * @param runnable The task to be executed
     */
    fun startTask(runnable: Runnable) {
        Thread(runnable).start()
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     *
     * @param runnable  The task to be scheduled
     * @param delay     The delay in ms before task is to be executed
     */
    fun startDelayedTask(runnable: Runnable, delay: Long) {
        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    runnable.run()
                }
            },
            delay
        )
    }
}