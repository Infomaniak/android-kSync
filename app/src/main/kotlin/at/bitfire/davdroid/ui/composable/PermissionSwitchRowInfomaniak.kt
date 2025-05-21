/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui.composable

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.bitfire.davdroid.R
import at.bitfire.davdroid.ui.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun PermissionSwitchRowInfomaniak(
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    text: String,
    allPermissionsGranted: Boolean,
    summaryWhenGranted: String? = null,
    summaryWhenNotGranted: String? = null,
    fontWeight: FontWeight = FontWeight.Normal,
    onLaunchRequest: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconRes?.let { resId ->
            Icon(
                painter = painterResource(resId),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = fontWeight,
                style = MaterialTheme.typography.bodyLarge
            )

            if (summaryWhenGranted != null && summaryWhenNotGranted != null) {
                Text(
                    text = if (allPermissionsGranted) summaryWhenGranted else summaryWhenNotGranted,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Switch(
            checked = allPermissionsGranted,
            thumbContent = if (allPermissionsGranted) {
                {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            } else null,
            onCheckedChange = { checked ->
                if (checked) {
                    onLaunchRequest()
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionSwitchRowInfomaniak(
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    text: String,
    summaryWhenGranted: String? = null,
    summaryWhenNotGranted: String? = null,
    permissions: List<String>,
    fontWeight: FontWeight = FontWeight.Normal
) {
    if (LocalInspectionMode.current) {
        // preview
        PermissionSwitchRowInfomaniak(
            iconRes = iconRes,
            text = text,
            fontWeight = fontWeight,
            allPermissionsGranted = false,
            summaryWhenGranted = summaryWhenGranted,
            summaryWhenNotGranted = summaryWhenNotGranted,
            onLaunchRequest = {},
            modifier = modifier
        )
        return
    }

    val state = rememberMultiplePermissionsState(permissions = permissions.toList())
    PermissionSwitchRowInfomaniak(
        iconRes = iconRes,
        text = text,
        fontWeight = fontWeight,
        allPermissionsGranted = state.allPermissionsGranted,
        summaryWhenGranted = summaryWhenGranted,
        summaryWhenNotGranted = summaryWhenNotGranted,
        onLaunchRequest = state::launchMultiplePermissionRequest,
        modifier = modifier
    )
}

@Preview
@Composable
fun PermissionSwitchRowInfomaniak_Preview_NotGranted() {
    AppTheme {
        PermissionSwitchRowInfomaniak(
            iconRes = R.drawable.ic_ksync_contacts,
            text = "Contacts",
            allPermissionsGranted = false,
            summaryWhenGranted = "Granted",
            summaryWhenNotGranted = "Not granted",
            onLaunchRequest = {}
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PermissionSwitchRowInfomaniak_Preview_Granted() {
    AppTheme {
        Surface {
            PermissionSwitchRowInfomaniak(
                iconRes = R.drawable.ic_ksync_contacts,
                text = "Contacts",
                allPermissionsGranted = true,
                summaryWhenGranted = "Granted",
                summaryWhenNotGranted = "Not granted",
                onLaunchRequest = {}
            )
        }
    }
}
