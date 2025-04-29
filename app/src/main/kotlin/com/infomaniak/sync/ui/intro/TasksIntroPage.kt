/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.ui.intro

import androidx.compose.runtime.Composable
import com.infomaniak.sync.settings.SettingsManager
import com.infomaniak.sync.sync.TasksAppManager
import com.infomaniak.sync.ui.TasksCard
import com.infomaniak.sync.ui.TasksModel
import javax.inject.Inject

class TasksIntroPage @Inject constructor(
    private val settingsManager: SettingsManager,
    private val tasksAppManager: TasksAppManager
): IntroPage() {

    override fun getShowPolicy(): ShowPolicy {
        return if (tasksAppManager.currentProvider() != null || settingsManager.getBooleanOrNull(TasksModel.HINT_OPENTASKS_NOT_INSTALLED) == false)
                ShowPolicy.DONT_SHOW
            else
                ShowPolicy.SHOW_ALWAYS
    }

    @Composable
    override fun ComposePage() {
        TasksCard()
    }

}