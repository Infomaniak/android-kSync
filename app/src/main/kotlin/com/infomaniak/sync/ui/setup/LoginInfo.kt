/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.ui.setup

import com.infomaniak.sync.db.Credentials
import at.bitfire.vcard4android.GroupMethod
import java.net.URI

data class LoginInfo(
    val baseUri: URI? = null,
    val credentials: Credentials? = null,

    val suggestedAccountName: String? = null,

    /** group method that should be pre-selected */
    val suggestedGroupMethod: GroupMethod = GroupMethod.GROUP_VCARDS
)