/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.repository

import com.infomaniak.sync.db.AppDatabase
import com.infomaniak.sync.db.Principal
import javax.inject.Inject

class PrincipalRepository @Inject constructor(
    db: AppDatabase
) {

    private val dao = db.principalDao()

    fun get(id: Long): Principal = dao.get(id)

}