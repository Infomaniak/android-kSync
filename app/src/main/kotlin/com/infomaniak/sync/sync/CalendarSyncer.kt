/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.sync

import android.accounts.Account
import android.content.ContentProviderClient
import com.infomaniak.sync.db.Collection
import com.infomaniak.sync.db.Service
import com.infomaniak.sync.resource.LocalCalendar
import com.infomaniak.sync.resource.LocalCalendarStore
import com.infomaniak.sync.settings.AccountSettings
import at.bitfire.ical4android.AndroidCalendar
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Sync logic for calendars
 */
class CalendarSyncer @AssistedInject constructor(
    @Assisted account: Account,
    @Assisted extras: Array<String>,
    @Assisted syncResult: SyncResult,
    calendarStore: LocalCalendarStore,
    private val accountSettingsFactory: AccountSettings.Factory,
    private val calendarSyncManagerFactory: CalendarSyncManager.Factory
): Syncer<LocalCalendarStore, LocalCalendar>(account, extras, syncResult) {

    @AssistedFactory
    interface Factory {
        fun create(account: Account, extras: Array<String>, syncResult: SyncResult): CalendarSyncer
    }

    override val dataStore = calendarStore

    override val serviceType: String
        get() = Service.TYPE_CALDAV


    override fun prepare(provider: ContentProviderClient): Boolean {
        // Update colors
        val accountSettings = accountSettingsFactory.create(account)
        if (accountSettings.getEventColors())
            AndroidCalendar.insertColors(provider, account)
        else
            AndroidCalendar.removeColors(provider, account)
        return true
    }

    override fun getDbSyncCollections(serviceId: Long): List<Collection> =
        collectionRepository.getSyncCalendars(serviceId)

    override fun syncCollection(provider: ContentProviderClient, localCollection: LocalCalendar, remoteCollection: Collection) {
        logger.info("Synchronizing calendar #${localCollection.id}, DB Collection ID: ${localCollection.dbCollectionId}, URL: ${localCollection.name}")

        val syncManager = calendarSyncManagerFactory.calendarSyncManager(
            account,
            extras,
            httpClient.value,
            dataStore.authority,
            syncResult,
            localCollection,
            remoteCollection
        )
        syncManager.performSync()
    }

}