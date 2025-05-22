/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui.intro

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import at.bitfire.davdroid.BuildConfig
import at.bitfire.davdroid.R
import at.bitfire.davdroid.h6
import at.bitfire.davdroid.ui.AppTheme

@Composable
fun BatteryOptimizationsPageContentInfomaniak(
    model: BatteryOptimizationsPageModel = viewModel()
) {
    val ignoreBatteryOptimizationsResultLauncher = rememberLauncherForActivityResult(
        BatteryOptimizationsPage.IgnoreBatteryOptimizationsContract
    ) {
        model.checkBatteryOptimizations()
    }

    val hintBatteryOptimizations by model.hintBatteryOptimizations.collectAsStateWithLifecycle(false)
    val uiState = model.uiState
    LaunchedEffect(uiState) {
        if (uiState.shouldBeExempted && !uiState.isExempted)
            ignoreBatteryOptimizationsResultLauncher.launch(BuildConfig.APPLICATION_ID)
    }

    val hintAutostartPermission by model.hintAutostartPermission.collectAsStateWithLifecycle(false)
    BatteryOptimizationsPageContentInfomaniak(
        dontShowBattery = hintBatteryOptimizations == false,
        isExempted = uiState.isExempted,
        shouldBeExempted = uiState.shouldBeExempted,
        onChangeShouldBeExempted = model::updateShouldBeExempted,
        dontShowAutostart = hintAutostartPermission == false,
        onChangeDontShowAutostart = model::updateHintAutostartPermission,
        manufacturerWarning = BatteryOptimizationsPageModel.manufacturerWarning
    )
}

@Composable
fun BatteryOptimizationsPageContentInfomaniak(
    dontShowBattery: Boolean,
    isExempted: Boolean,
    shouldBeExempted: Boolean,
    onChangeShouldBeExempted: (Boolean) -> Unit = {},
    dontShowAutostart: Boolean,
    onChangeDontShowAutostart: (Boolean) -> Unit = {},
    manufacturerWarning: Boolean
) {
    LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.ic_ksync_battery_optimizations),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(80.dp),
        )

        Text(
            text = stringResource(R.string.infomaniak_startup_battery_optimization),
            style = h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        ElevatedCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.infomaniak_fragment_battery_heading),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = shouldBeExempted,
                        onCheckedChange = {
                            // Only accept click events if not whitelisted
                            if (!isExempted) {
                                onChangeShouldBeExempted(it)
                            }
                        },
                        enabled = !dontShowBattery
                    )
                }
                Text(
                    text = stringResource(
                        R.string.infomaniak_startup_battery_optimization_message,
                        stringResource(R.string.app_name)
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
        if (manufacturerWarning) {
            AnimatedVisibility(visible = shouldBeExempted) {
                ElevatedCard(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.intro_autostart_title,
                                Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = stringResource(R.string.infomaniak_fragment_battery_autostart_text),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = dontShowAutostart,
                                onCheckedChange = { onChangeDontShowAutostart(dontShowAutostart) }
                            )
                            Text(
                                text = stringResource(R.string.infomaniak_fragment_battery_autostart_dontshow),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .clickable { onChangeDontShowAutostart(dontShowAutostart) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BatteryOptimizationsContentInfomaniak_Preview() {
    AppTheme {
        BatteryOptimizationsPageContentInfomaniak(
            dontShowBattery = true,
            isExempted = false,
            shouldBeExempted = true,
            dontShowAutostart = false,
            manufacturerWarning = true
        )
    }
}
