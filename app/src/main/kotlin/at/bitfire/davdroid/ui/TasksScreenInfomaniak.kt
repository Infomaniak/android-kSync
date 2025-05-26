/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import at.bitfire.davdroid.BuildConfig
import at.bitfire.davdroid.R
import at.bitfire.ical4android.TaskProvider
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import at.bitfire.davdroid.h6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreenInfomaniak(onNavUp: () -> Unit) {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.intro_tasks_title)) },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavUp
                        ) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.navigate_up))
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                TasksCardInfomaniak()
            }
        }
    }
}

@Composable
fun TasksCardInfomaniak(
    model: TasksModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val wantToSynchronise = model.wantToSynchronise.collectAsStateWithLifecycle(false)
    val jtxInstalled = model.jtxInstalled

    TasksCardInfomaniak(
        wantToSynchronise = wantToSynchronise.value,
        onWantToSynchroniseToggled = { toggled -> model.setWantToSynchronise(toggled) },
        jtxInstalled = jtxInstalled,
        installApp = { packageName ->
            val uri = ("market://details?id=$packageName&referrer=" +
                    Uri.encode("utm_source=" + BuildConfig.APPLICATION_ID)).toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(context.packageManager) != null)
                context.startActivity(intent)
            else
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.intro_tasks_no_app_store),
                        duration = SnackbarDuration.Long
                    )
                }
        }
    )
}

@Composable
fun TasksCardInfomaniak(
    wantToSynchronise: Boolean,
    onWantToSynchroniseToggled: (Boolean) -> Unit = {},
    jtxInstalled: Boolean,
    installApp: (String) -> Unit = {},
) {
    val paddingModifier = Modifier.padding(16.dp)

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.ic_ksync_tasks),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(80.dp),
        )

        Text(
            text = stringResource(R.string.infomaniak_fragment_tasks_title),
            style = h6,
            textAlign = TextAlign.Center,
        )

        ElevatedCard (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = paddingModifier,
            ) {
                Text(
                    stringResource(R.string.infomaniak_fragment_tasks_text),
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(modifier = Modifier.weight(1.0f))

                Switch(checked = wantToSynchronise, onCheckedChange = onWantToSynchroniseToggled)
            }
        }

        AnimatedVisibility(
            visible = wantToSynchronise,
        ) {
            ElevatedCard (
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = paddingModifier,
                ) {
                    Text(
                        stringResource(R.string.infomaniak_fragment_tasks_explanation),
                        style = MaterialTheme.typography.labelLarge,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_jtxboard),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            stringResource(R.string.intro_tasks_jtx),
                            style = MaterialTheme.typography.labelLarge,
                        )

                        Spacer(modifier = Modifier.weight(1.0f))

                        if (jtxInstalled) {
                            Text(
                                stringResource(R.string.infomaniak_installed_button),
                                color = MaterialTheme.colorScheme.outline,
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle),
                                tint = Color.Unspecified,
                                contentDescription = null,
                            )
                        } else {
                            TextButton(onClick = { installApp(TaskProvider.ProviderName.JtxBoard.packageName) }) {
                                Text(stringResource(R.string.infomaniak_install_button))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TasksCardInfomaniak_Preview() {
    AppTheme {
        TasksCardInfomaniak(
            wantToSynchronise = true,
            jtxInstalled = true,
        )
    }
}
