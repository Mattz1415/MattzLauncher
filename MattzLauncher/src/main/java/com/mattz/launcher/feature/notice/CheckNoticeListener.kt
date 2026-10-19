package com.mattz.launcher.feature.notice

fun interface CheckNoticeListener {
    fun onSuccessful(noticeInfo: NoticeInfo?)
}