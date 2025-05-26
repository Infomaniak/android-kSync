/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui.intro

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.bitfire.davdroid.R
import at.bitfire.davdroid.h6
import at.bitfire.davdroid.subtitle
import at.bitfire.davdroid.ui.AppTheme
import at.bitfire.davdroid.ui.composable.KSyncTitle

class WelcomePageInfomaniak : IntroPage() {

    override val customTopInsets: Boolean = true

    override fun getShowPolicy() = ShowPolicy.SHOW_ONLY_WITH_OTHERS

    @Composable
    override fun ComposePage() {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ContentLandscape()
        else
            ContentPortrait()
    }

    @Composable
    private fun ContentPortrait() {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_round),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(80.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            KSyncTitle(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.infomaniak_intro_welcome_title),
                style = h6,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.infomaniak_intro_welcome_subtitle),
                style = subtitle,
                textAlign = TextAlign.Center,
            )
        }
    }

    @Composable
    @Preview(
        device = "id:3.7in WVGA (Nexus One)",
        showSystemUi = true
    )
    fun Preview_ContentPortrait_Light() {
        AppTheme(darkTheme = false) {
            ContentPortrait()
        }
    }

    @Composable
    @Preview(
        device = "id:3.7in WVGA (Nexus One)",
        showSystemUi = true
    )
    fun Preview_ContentPortrait_Dark() {
        AppTheme(darkTheme = true) {
            ContentPortrait()
        }
    }


    @Preview(
        showSystemUi = true,
        device = "id:medium_tablet"
    )
    @Composable
    private fun ContentLandscape() {
        AppTheme {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(0.5f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_round),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(64.dp),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    KSyncTitle(modifier = Modifier.fillMaxWidth())
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .weight(0.5f),
                ) {
                    Text(
                        text = stringResource(R.string.infomaniak_intro_welcome_title),
                        style = h6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(R.string.infomaniak_intro_welcome_subtitle),
                        style = subtitle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
