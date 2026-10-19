package com.mattz.launcher.task

fun interface OnTaskEndedListener<V> {
    @Throws(Throwable::class)
    fun onEnded(result: V?)
}