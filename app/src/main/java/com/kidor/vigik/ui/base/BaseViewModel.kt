package com.kidor.vigik.ui.base

interface BaseViewModel<T> {

    fun setView(view: T)

    fun onStart() {
        throw NotImplementedError()
    }

    fun onStop() {
        throw NotImplementedError()
    }
}