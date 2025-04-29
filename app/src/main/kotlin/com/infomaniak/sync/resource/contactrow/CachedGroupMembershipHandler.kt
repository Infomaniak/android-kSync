/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.resource.contactrow

import android.content.ContentValues
import com.infomaniak.sync.resource.LocalContact
import at.bitfire.vcard4android.CachedGroupMembership
import at.bitfire.vcard4android.Contact
import at.bitfire.vcard4android.GroupMethod
import at.bitfire.vcard4android.contactrow.DataRowHandler
import java.util.logging.Logger

class CachedGroupMembershipHandler(val localContact: LocalContact): DataRowHandler() {
    
    override fun forMimeType() = CachedGroupMembership.CONTENT_ITEM_TYPE

    override fun handle(values: ContentValues, contact: Contact) {
        super.handle(values, contact)

        if (localContact.addressBook.groupMethod == GroupMethod.GROUP_VCARDS)
            localContact.cachedGroupMemberships += values.getAsLong(CachedGroupMembership.GROUP_ID)
        else
            Logger.getGlobal().warning("Ignoring cached group membership for group method CATEGORIES")
    }

}