/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.resource.contactrow

import android.content.ContentValues
import android.provider.ContactsContract.CommonDataKinds.GroupMembership
import com.infomaniak.sync.resource.LocalContact
import com.infomaniak.sync.util.trimToNull
import at.bitfire.vcard4android.Contact
import at.bitfire.vcard4android.GroupMethod
import at.bitfire.vcard4android.contactrow.DataRowHandler
import java.io.FileNotFoundException

class GroupMembershipHandler(val localContact: LocalContact): DataRowHandler() {

    override fun forMimeType() = GroupMembership.CONTENT_ITEM_TYPE

    override fun handle(values: ContentValues, contact: Contact) {
        super.handle(values, contact)

        val groupId = values.getAsLong(GroupMembership.GROUP_ROW_ID)
        localContact.groupMemberships += groupId

        if (localContact.addressBook.groupMethod == GroupMethod.CATEGORIES) {
            try {
                val group = localContact.addressBook.findGroupById(groupId)
                group.getContact().displayName.trimToNull()?.let { groupName ->
                    logger.fine("Adding membership in group $groupName as category")
                    contact.categories.add(groupName)
                }
            } catch (ignored: FileNotFoundException) {
                logger.warning("Contact is member in group $groupId which doesn't exist anymore")
            }
        }
    }

}