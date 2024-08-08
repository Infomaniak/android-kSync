/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.provider.CalendarContract
import androidx.annotation.WorkerThread
import at.bitfire.davdroid.InvalidAccountException
import at.bitfire.davdroid.R
import at.bitfire.davdroid.db.AppDatabase
import at.bitfire.davdroid.db.Service
import at.bitfire.davdroid.log.Logger
import at.bitfire.davdroid.settings.AccountSettings
import at.bitfire.davdroid.settings.Settings
import at.bitfire.davdroid.settings.SettingsManager
import at.bitfire.davdroid.util.PermissionUtils
import at.bitfire.davdroid.util.TaskUtils
import at.bitfire.ical4android.TaskProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Utility methods related to synchronization management (authorities, workers etc.)
 */
object SyncUtils {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SyncUtilsEntryPoint {
        fun appDatabase(): AppDatabase
        fun settingsManager(): SettingsManager
    }

    /**
     * Returns a list of all available sync authorities:
     *
     *   1. calendar authority
     *   2. address books authority
     *   3. current tasks authority (if available)
     *
     * Checking the availability of authorities may be relatively expensive, so the
     * result should be cached for the current operation.
     *
     * @return list of available sync authorities for main accounts
     */
    fun syncAuthorities(context: Context): List<String> {
        val result = mutableListOf(
            CalendarContract.AUTHORITY,
            context.getString(R.string.address_books_authority)
        )
        TaskUtils.currentProvider(context)?.let { taskProvider ->
            result += taskProvider.authority
        }
        return result
    }

    // task sync utils

    @WorkerThread
    fun updateTaskSync(context: Context) {
        val tasksProvider = TaskUtils.currentProvider(context)
        Logger.log.info("App launched or other package (un)installed; current tasks provider = $tasksProvider")

        var permissionsRequired = false     // whether additional permissions are required
        val currentProvider by lazy {       // only this provider shall be enabled (null to disable all providers)
            TaskUtils.currentProvider(context)
        }

        // check all accounts and (de)activate task provider(s) if a CalDAV service is defined
        val db = EntryPointAccessors.fromApplication(context, SyncUtilsEntryPoint::class.java).appDatabase()
        val accountManager = AccountManager.get(context)
        for (account in accountManager.getAccountsByType(context.getString(R.string.account_type))) {
            val hasCalDAV = db.serviceDao().getByAccountAndType(account.name, Service.TYPE_CALDAV) != null
            for (providerName in TaskProvider.ProviderName.entries) {
                val isSyncable = ContentResolver.getIsSyncable(account, providerName.authority)     // may be -1 (unknown state)
                val shallBeSyncable = hasCalDAV && providerName == currentProvider
                if ((shallBeSyncable && isSyncable != 1) || (!shallBeSyncable && isSyncable != 0)) {
                    // enable/disable sync
                    setSyncableFromSettings(context, account, providerName.authority, shallBeSyncable)

                    // if sync has just been enabled: check whether additional permissions are required
                    if (shallBeSyncable && !PermissionUtils.havePermissions(context, providerName.permissions))
                        permissionsRequired = true
                }
            }
        }

        if (permissionsRequired) {
            Logger.log.warning("Tasks synchronization is now enabled for at least one account, but permissions are not granted")
            PermissionUtils.notifyPermissions(context, null)
        }
    }

    private fun setSyncableFromSettings(context: Context, account: Account, authority: String, syncable: Boolean) {
        val settingsManager by lazy { EntryPointAccessors.fromApplication(context, SyncUtilsEntryPoint::class.java).settingsManager() }
        if (syncable) {
            Logger.log.info("Enabling $authority sync for $account")
            ContentResolver.setIsSyncable(account, authority, 1)
            try {
                val settings = AccountSettings(context, account)
                val interval = settings.getTasksSyncInterval() ?: settingsManager.getLong(Settings.DEFAULT_SYNC_INTERVAL)
                settings.setSyncInterval(authority, interval)
            } catch (e: InvalidAccountException) {
                // account has already been removed
            }
        } else {
            Logger.log.info("Disabling $authority sync for $account")
            ContentResolver.setIsSyncable(account, authority, 0)
        }
    }
}