/***************************************************************************************************
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 **************************************************************************************************/

package at.bitfire.davdroid.ui.intro

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.bitfire.davdroid.R
import at.bitfire.davdroid.settings.SettingsManager
import at.bitfire.davdroid.ui.TasksFragment
import at.bitfire.davdroid.util.TaskUtils
import javax.inject.Inject

class TasksIntroFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.intro_tasks, container, false)


    class Factory @Inject constructor(
        val settingsManager: SettingsManager
    ): IntroFragmentFactory {

        override fun getOrder(context: Context): Int {
            return if (!TaskUtils.isAvailable(context) && settingsManager.getBooleanOrNull(TasksFragment.Model.HINT_OPENTASKS_NOT_INSTALLED) != false)
                10
            else
                IntroFragmentFactory.DONT_SHOW
        }

        override fun create() = TasksIntroFragment()

    }

}
