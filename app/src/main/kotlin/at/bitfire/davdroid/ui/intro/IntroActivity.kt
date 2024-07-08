/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui.intro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import at.bitfire.davdroid.ui.AppTheme
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import at.bitfire.davdroid.R
import at.bitfire.davdroid.log.Logger
import com.github.appintro.AppIntro2
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@OptIn(ExperimentalFoundationApi::class)
class IntroActivity : AppCompatActivity() {

    val model by viewModels<IntroModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pages = model.pages

        setContent {
            AppTheme {
                val scope = rememberCoroutineScope()
                val pagerState = rememberPagerState { pages.size }

                BackHandler {
                    if (pagerState.settledPage == 0) {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    } else scope.launch {
                        pagerState.animateScrollToPage(pagerState.settledPage - 1)
                    }
                }

        setIndicatorColor(ContextCompat.getColor(this, R.color.primaryColor), ContextCompat.getColor(this, R.color.grey700)) // kSync
//        setBarColor(ResourcesCompat.getColor(resources, R.color.primaryDarkColor, null))
        isSkipButtonEnabled = false

        onBackPressedDispatcher.addCallback(this) {
            if (currentSlide == 0) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                goToPreviousSlide()
            }
        }
    }


    /**
     * For launching the [IntroActivity]. Result is `true` when the user cancelled the intro.
     */
    object Contract: ActivityResultContract<Unit?, Boolean>() {
        override fun createIntent(context: Context, input: Unit?): Intent =
            Intent(context, IntroActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            return resultCode == Activity.RESULT_CANCELED
        }
    }

}