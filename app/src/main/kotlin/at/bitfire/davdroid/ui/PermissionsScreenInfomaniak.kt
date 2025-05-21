/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import at.bitfire.davdroid.BuildConfig
import at.bitfire.davdroid.R
import at.bitfire.davdroid.h6
import at.bitfire.davdroid.subtitle
import at.bitfire.davdroid.ui.composable.PermissionSwitchRowInfomaniak
import at.bitfire.davdroid.util.PermissionUtils
import at.bitfire.ical4android.TaskProvider
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Used when "Manage permissions" is selected in the settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreenInfomaniak(
    onNavigateUp: () -> Unit
) {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_settings_security_app_permissions)) },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateUp
                        ) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.navigate_up))
                        }
                    }
                )
            }
        ) { paddingValues ->
            PermissionsScreenInfomaniak(modifier = Modifier.padding(paddingValues))
        }
    }
}

/**
 * Used by [PermissionsScreenInfomaniak] and directly embedded in [at.bitfire.davdroid.ui.intro.PermissionsIntroPage].
 */
@Composable
fun PermissionsScreenInfomaniak(
    modifier: Modifier = Modifier,
    model: PermissionsModel = viewModel()
) {
    // check permissions when the lifecycle owner (for instance Activity) is resumed
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                model.checkPermissions()
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current
    PermissionsScreenInfomaniak(
        keepPermissions = model.needKeepPermissions,
        onKeepPermissionsRequested = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intent = Intent(
                    Intent.ACTION_AUTO_REVOKE_PERMISSIONS,
                    Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                )
                try {
                    context.startActivity(intent)
                    Toast.makeText(context, R.string.permissions_autoreset_instruction, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Logger.getGlobal().log(Level.WARNING, "Couldn't start Keep Permissions activity", e)
                }
            }
        },
        jtxAvailable = model.jtxAvailable,
        modifier = modifier
    )
}


@Composable
fun PermissionsScreenInfomaniak(
    keepPermissions: Boolean?,
    onKeepPermissionsRequested: () -> Unit,
    jtxAvailable: Boolean?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.ic_ksync_permissions),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(80.dp),
        )

        Text(
            text = stringResource(R.string.permissions_title),
            style = h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.permissions_text, stringResource(R.string.app_name)),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        ElevatedCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                if (keepPermissions != null) {
                    PermissionSwitchRowInfomaniak(
                        text = stringResource(R.string.permissions_autoreset_title),
                        summaryWhenGranted = stringResource(R.string.permissions_autoreset_status_on),
                        summaryWhenNotGranted = stringResource(R.string.permissions_autoreset_status_off),
                        allPermissionsGranted = keepPermissions,
                        onLaunchRequest = onKeepPermissionsRequested,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                val allPermissions = mutableListOf<String>()
                allPermissions.addAll(PermissionUtils.CONTACT_PERMISSIONS)
                allPermissions.addAll(PermissionUtils.CALENDAR_PERMISSIONS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    allPermissions += Manifest.permission.POST_NOTIFICATIONS
                if (jtxAvailable == true)
                    allPermissions.addAll(TaskProvider.PERMISSIONS_JTX)
                PermissionSwitchRowInfomaniak(
                    text = stringResource(R.string.permissions_all_title),
                    permissions = allPermissions,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    PermissionSwitchRowInfomaniak(
                        iconRes = R.drawable.ic_ksync_notifications,
                        text = stringResource(R.string.permissions_notification_title),

                        permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                PermissionSwitchRowInfomaniak(
                    iconRes = R.drawable.ic_ksync_calendar,
                    text = stringResource(R.string.permissions_calendar_title),
                    permissions = PermissionUtils.CALENDAR_PERMISSIONS.toList(),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                PermissionSwitchRowInfomaniak(
                    iconRes = R.drawable.ic_ksync_contacts,
                    text = stringResource(R.string.permissions_contacts_title),
                    permissions = PermissionUtils.CONTACT_PERMISSIONS.toList(),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (jtxAvailable == true)
                    PermissionSwitchRowInfomaniak(
                        iconRes = R.drawable.ic_ksync_task,
                        text = stringResource(R.string.permissions_jtx_title),
                        permissions = TaskProvider.PERMISSIONS_JTX.toList(),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                Text(
                    text = stringResource(R.string.permissions_app_settings_hint),
                    style = subtitle,
                    modifier = Modifier.padding(top = 24.dp)
                )

                val context = LocalContext.current
                OutlinedButton(
                    modifier = Modifier.padding(vertical = 8.dp),
                    onClick = { PermissionUtils.showAppSettings(context) }
                ) {
                    Text(stringResource(R.string.permissions_app_settings))
                }
            }
        }
    }
}

@Composable
@Preview
fun PermissionsCardInfomaniak_Preview() {
    AppTheme {
        PermissionsScreenInfomaniak(
            keepPermissions = true,
            onKeepPermissionsRequested = {},
            jtxAvailable = true
        )
    }
}
