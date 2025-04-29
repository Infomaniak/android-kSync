/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.infomaniak.sync.sync.SyncAdapterService
import dagger.hilt.android.testing.HiltTestApplication

@Suppress("unused")
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader, name: String, context: Context): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)

        // MockK requirements
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            throw AssertionError("MockK requires Android P [https://mockk.io/ANDROID.html]")

        // disable sync adapters
        SyncAdapterService.syncActive.set(false)
    }

}