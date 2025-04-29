/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.ui.account

import com.infomaniak.sync.db.HomeSet
import com.infomaniak.sync.db.Service
import com.infomaniak.sync.repository.DavHomeSetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetBindableHomeSetsFromServiceUseCase @Inject constructor(
    val homeSetRepository: DavHomeSetRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(serviceFlow: Flow<Service?>): Flow<List<HomeSet>> =
        serviceFlow.flatMapLatest { service ->
            if (service == null)
                flowOf(emptyList())
            else
                homeSetRepository.getBindableByServiceFlow(service.id)
        }

}