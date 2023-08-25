package com.kidor.vigik.car.screens.common

/**
 * Possible state of
 */
enum class CarDataScreenState {
    /**
     * When the screen starts and no data where collected.
     */
    NOT_INITIALIZED,

    /**
     * When some data are collected and ready to be displayed.
     */
    DATA_AVAILABLE,

    /**
     * When en error occurred when collecting data.
     */
    ERROR
}
