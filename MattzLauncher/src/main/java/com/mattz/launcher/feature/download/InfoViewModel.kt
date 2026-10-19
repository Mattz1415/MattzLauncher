package com.mattz.launcher.feature.download

import androidx.lifecycle.ViewModel
import com.mattz.launcher.feature.download.item.InfoItem
import com.mattz.launcher.feature.download.platform.AbstractPlatformHelper

class InfoViewModel : ViewModel() {
    var platformHelper: AbstractPlatformHelper? = null
    var infoItem: InfoItem? = null
}