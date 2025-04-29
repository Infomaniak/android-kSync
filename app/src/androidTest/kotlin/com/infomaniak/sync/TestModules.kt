/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync

import com.infomaniak.sync.push.PushRegistrationWorkerManager
import com.infomaniak.sync.repository.DavCollectionRepository
import com.infomaniak.sync.startup.StartupPlugin
import com.infomaniak.sync.startup.TasksAppWatcher
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dagger.multibindings.Multibinds

// remove PushRegistrationWorkerModule from Android tests
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PushRegistrationWorkerManager.PushRegistrationWorkerModule::class]
)
abstract class TestPushRegistrationWorkerModule {
    // provides empty set of listeners
    @Multibinds
    abstract fun empty(): Set<DavCollectionRepository.OnChangeListener>
}

// remove TasksAppWatcherModule from Android tests
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [TasksAppWatcher.TasksAppWatcherModule::class]
)
abstract class TestTasksAppWatcherModuleModule {
    // provides empty set of plugins
    @Multibinds
    abstract fun empty(): Set<StartupPlugin>
}