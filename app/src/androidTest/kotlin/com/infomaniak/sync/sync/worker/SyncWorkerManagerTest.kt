/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.sync.worker

import android.accounts.Account
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import com.infomaniak.sync.TestUtils
import com.infomaniak.sync.TestUtils.workScheduledOrRunning
import com.infomaniak.sync.sync.SyncDataType
import com.infomaniak.sync.sync.account.TestAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SyncWorkerManagerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var syncWorkerManager: SyncWorkerManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    lateinit var account: Account

    @Before
    fun setUp() {
        hiltRule.inject()
        TestUtils.setUpWorkManager(context, workerFactory)

        account = TestAccount.create()
    }

    @After
    fun tearDown() {
        TestAccount.remove(account)
    }


    // one-time sync workers

    @Test
    fun testEnqueueOneTime() {
        val workerName = OneTimeSyncWorker.workerName(account, SyncDataType.EVENTS)
        assertFalse(TestUtils.workScheduledOrRunningOrSuccessful(context, workerName))

        val returnedName = syncWorkerManager.enqueueOneTime(account, SyncDataType.EVENTS)
        assertEquals(workerName, returnedName)
        assertTrue(TestUtils.workScheduledOrRunningOrSuccessful(context, workerName))
    }


    // periodic sync workers

    @Test
    fun enablePeriodic() {
        syncWorkerManager.enablePeriodic(account, SyncDataType.EVENTS, 60, false).result.get()

        val workerName = PeriodicSyncWorker.workerName(account, SyncDataType.EVENTS)
        assertTrue(workScheduledOrRunning(context, workerName))
    }

    @Test
    fun disablePeriodic() {
        syncWorkerManager.enablePeriodic(account, SyncDataType.EVENTS, 60, false).result.get()
        syncWorkerManager.disablePeriodic(account, SyncDataType.EVENTS).result.get()

        val workerName = PeriodicSyncWorker.workerName(account, SyncDataType.EVENTS)
        assertFalse(workScheduledOrRunning(context, workerName))
    }

}