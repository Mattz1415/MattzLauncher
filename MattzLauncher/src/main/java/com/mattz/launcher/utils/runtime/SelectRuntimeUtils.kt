package com.mattz.launcher.utils.runtime

import android.content.Context
import com.mattz.launcher.setting.AllSettings
import com.mattz.launcher.task.TaskExecutors
import com.mattz.launcher.ui.dialog.SelectRuntimeDialog

class SelectRuntimeUtils {
    companion object {
        @JvmStatic
        fun selectRuntime(context: Context, dialogTitle: String?, selectedListener: RuntimeSelectedListener) {
            TaskExecutors.runInUIThread {
                when (AllSettings.selectRuntimeMode.getValue()) {
                    "ask_me" -> SelectRuntimeDialog(context, selectedListener).apply {
                        dialogTitle?.let { setTitleText(it) }
                    }.show()
                    "default" -> selectedListener.onSelected(AllSettings.defaultRuntime.getValue().takeIf { it.isNotEmpty() })
                    "auto" -> selectedListener.onSelected(null)
                    else -> {}
                }
            }
        }
    }
}