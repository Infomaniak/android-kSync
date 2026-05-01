/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui.intro

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import at.bitfire.davdroid.settings.SettingsManager
import at.bitfire.davdroid.sync.TasksAppManager
import at.bitfire.davdroid.ui.TasksCardInfomaniak
import at.bitfire.davdroid.ui.TasksModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TasksIntroPage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val tasksAppManager: TasksAppManager
): IntroPage() {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context) //kSync

    override fun getShowPolicy(): ShowPolicy {
        val hasTasksIntroPageBeenDisplayedOnce = sharedPreferences.getBoolean(TASK_INTRO_PAGE_KEY, false) //kSync

        return if (tasksAppManager.currentProvider() != null || settingsManager.getBooleanOrNull(TasksModel.HINT_OPENTASKS_NOT_INSTALLED) == false || hasTasksIntroPageBeenDisplayedOnce)
                ShowPolicy.DONT_SHOW
            else
                ShowPolicy.SHOW_ALWAYS
    }

    @Composable
    override fun ComposePage() {
        sharedPreferences.edit { putBoolean(TASK_INTRO_PAGE_KEY, true) } //kSync
        TasksCardInfomaniak() // kSync
    }

}

private const val TASK_INTRO_PAGE_KEY = "hasTasksIntroPageBeenDisplayedOnce" //kSync
