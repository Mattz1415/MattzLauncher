package com.mattz.launcher.ui.fragment.download.addon

import com.mattz.launcher.R
import com.mattz.launcher.feature.mod.modloader.FabricLikeUtils

class DownloadFabricFragment : DownloadFabricLikeFragment(FabricLikeUtils.FABRIC_UTILS, R.drawable.ic_fabric) {
    companion object {
        const val TAG: String = "DownloadFabricFragment"
    }
}