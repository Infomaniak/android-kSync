/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync

import com.infomaniak.sync.ui.intro.BatteryOptimizationsPage
import com.infomaniak.sync.ui.intro.IntroPageFactory
import com.infomaniak.sync.ui.intro.OpenSourcePage
import com.infomaniak.sync.ui.intro.PermissionsIntroPage
import com.infomaniak.sync.ui.intro.TasksIntroPage
import com.infomaniak.sync.ui.intro.WelcomePage
import javax.inject.Inject

class OseIntroPageFactory @Inject constructor(
    batteryOptimizationsPage: BatteryOptimizationsPage,
    openSourcePage: OpenSourcePage,
    permissionsIntroPage: PermissionsIntroPage,
    tasksIntroPage: TasksIntroPage
): IntroPageFactory {

    override val introPages = arrayOf(
        WelcomePage(),
        tasksIntroPage,
        permissionsIntroPage,
        batteryOptimizationsPage,
        openSourcePage
    )

}