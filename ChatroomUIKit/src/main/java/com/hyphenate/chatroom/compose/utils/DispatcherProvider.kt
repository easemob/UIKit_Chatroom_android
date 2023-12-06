package com.hyphenate.chatroom.compose.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

/**
 * Coroutine dispatchers used internally by Stream libraries. Should always be used
 * instead of directly using [Dispatchers] or creating new dispatchers.
 *
 * Can be modified using [set] and [reset] for testing purposes.
 */
object DispatcherProvider {

    /**
     * Represents the Main coroutine dispatcher, tied to the UI thread.
     */
    var Main: CoroutineDispatcher = Dispatchers.Main
        internal set

    /**
     * Represents the Immediate coroutine dispatcher, which is usually tied to the UI thread.
     *
     * Useful for some cases where the UI updates require immediate execution, without dispatching the update events.
     */
    val Immediate: CoroutineDispatcher
        get() {
            val mainDispatcher = Main

            return if (mainDispatcher is MainCoroutineDispatcher) {
                mainDispatcher.immediate
            } else {
                mainDispatcher
            }
        }

    /**
     * Represents the IO coroutine dispatcher, which is usually tied to background work.
     */
    var IO: CoroutineDispatcher = Dispatchers.IO
        internal set

    /**
     * Overrides the main (UI thread) and IO dispatcher. For testing purposes only.
     */
    fun set(mainDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) {
        Main = mainDispatcher
        IO = ioDispatcher
    }

    /**
     * Resets the dispatchers to their default values. For testing purposes only.
     */
    fun reset() {
        Main = Dispatchers.Main
        IO = Dispatchers.IO
    }
}
