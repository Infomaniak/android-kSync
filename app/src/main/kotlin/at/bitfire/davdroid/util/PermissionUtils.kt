/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import at.bitfire.davdroid.BuildConfig
import at.bitfire.davdroid.R
import at.bitfire.davdroid.log.Logger
import at.bitfire.davdroid.ui.NotificationUtils
import at.bitfire.davdroid.ui.NotificationUtils.notifyIfPossible
import at.bitfire.davdroid.ui.PermissionsActivity

object PermissionUtils {

    /** There's an undocumented intent that is sent when the battery optimization whitelist changes. */
    const val ACTION_POWER_SAVE_WHITELIST_CHANGED = "android.os.action.POWER_SAVE_WHITELIST_CHANGED"

    val CONTACT_PERMISSIONS = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )
    val CALENDAR_PERMISSIONS = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    /**
     * Checks whether at least one of the given permissions is granted.
     *
     * @param context context to check
     * @param permissions array of permissions to check
     *
     * @return whether at least one of [permissions] is granted
     */
    fun haveAnyPermission(context: Context, permissions: Array<String>) =
            permissions.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    /**
     * Checks whether all given permissions are granted.
     *
     * @param context context to check
     * @param permissions array of permissions to check
     *
     * @return whether all [permissions] are granted
     */
    fun havePermissions(context: Context, permissions: Array<String>) =
            permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    /**
     * Shows a notification about missing permissions.
     *
     * @param context notification context
     * @param intent will be set as content Intent; if null, an Intent to launch PermissionsActivity will be used
     */
    fun notifyPermissions(context: Context, intent: Intent?) {
        val contentIntent = intent ?: Intent(context, PermissionsActivity::class.java)
        val notify = NotificationUtils.newBuilder(context, NotificationUtils.CHANNEL_SYNC_ERRORS)
                .setSmallIcon(R.drawable.ic_sync_problem_notify)
                .setContentTitle(context.getString(R.string.sync_error_permissions))
                .setContentText(context.getString(R.string.sync_error_permissions_text))
                .setContentIntent(PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setAutoCancel(true)
                .build()
        NotificationManagerCompat.from(context)
                .notifyIfPossible(NotificationUtils.NOTIFY_PERMISSIONS, notify)
    }

    fun showAppSettings(context: Context) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", BuildConfig.APPLICATION_ID, null))
        if (intent.resolveActivity(context.packageManager) != null)
            context.startActivity(intent)
        else
            Logger.log.warning("App settings Intent not resolvable")
    }

}