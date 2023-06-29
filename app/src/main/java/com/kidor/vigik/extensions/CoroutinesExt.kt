package com.kidor.vigik.extensions

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Launches a list of jobs asynchronously and wait for all of them to finish.
 *
 * @param blocks The jobs to launch.
 */
suspend fun awaitAll(vararg blocks: suspend () -> Unit) = coroutineScope {
    blocks.forEach {
        launch { it() }
    }
}
