/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.sync.groups

import at.bitfire.vcard4android.Contact

interface ContactGroupStrategy {

    fun beforeUploadDirty()
    fun verifyContactBeforeSaving(contact: Contact)
    fun postProcess()

}