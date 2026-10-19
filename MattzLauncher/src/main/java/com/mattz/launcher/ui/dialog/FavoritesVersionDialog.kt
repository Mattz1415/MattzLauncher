package com.mattz.launcher.ui.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mattz.launcher.R
import com.mattz.launcher.feature.version.favorites.FavoritesVersionUtils
import com.mattz.launcher.task.Task
import com.mattz.launcher.task.TaskExecutors
import com.mattz.launcher.ui.subassembly.version.FavoritesVersionAdapter

class FavoritesVersionDialog(
    context: Context,
    private val versionName: String,
    private val favoritesChanged: () -> Unit
) : AbstractSelectDialog(context) {
    private val mFavoritesAdapter = FavoritesVersionAdapter(versionName)

    override fun initDialog(recyclerView: RecyclerView) {
        setTitleText(R.string.version_manager_favorites_dialog_title)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mFavoritesAdapter
    }

    override fun dismiss() {
        super.dismiss()
        Task.runTask {
            FavoritesVersionUtils.updateVersionFolders(versionName, mFavoritesAdapter.getSelectedCategorySet())
        }.ended(TaskExecutors.getAndroidUI()) {
            favoritesChanged()
        }.execute()
    }
}
