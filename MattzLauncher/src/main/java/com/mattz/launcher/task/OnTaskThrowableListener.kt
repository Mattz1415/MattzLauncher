package com.mattz.launcher.task

fun interface OnTaskThrowableListener {
    fun onThrowable(throwable: Throwable)
}