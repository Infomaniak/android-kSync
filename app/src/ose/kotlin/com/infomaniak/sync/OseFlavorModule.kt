/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync

import com.infomaniak.sync.ui.AboutActivity
import com.infomaniak.sync.ui.AccountsDrawerHandler
import com.infomaniak.sync.ui.OpenSourceLicenseInfoProvider
import com.infomaniak.sync.ui.OseAccountsDrawerHandler
import com.infomaniak.sync.ui.intro.IntroPageFactory
import com.infomaniak.sync.ui.setup.LoginTypesProvider
import com.infomaniak.sync.ui.setup.StandardLoginTypesProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

interface OseFlavorModules {

    @Module
    @InstallIn(ActivityComponent::class)
    interface ForActivities {
        @Binds
        fun accountsDrawerHandler(impl: OseAccountsDrawerHandler): AccountsDrawerHandler

        @Binds
        fun loginTypesProvider(impl: StandardLoginTypesProvider): LoginTypesProvider
    }

    @Module
    @InstallIn(ViewModelComponent::class)
    interface ForViewModels {
        @Binds
        fun appLicenseInfoProvider(impl: OpenSourceLicenseInfoProvider): AboutActivity.AppLicenseInfoProvider

        @Binds
        fun loginTypesProvider(impl: StandardLoginTypesProvider): LoginTypesProvider
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface Global {
        @Binds
        fun introPageFactory(impl: OseIntroPageFactory): IntroPageFactory
    }

}