/***************************************************************************************************
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 **************************************************************************************************/

package at.bitfire.davdroid.ui.account

import android.content.Intent
import android.view.*
import androidx.fragment.app.FragmentManager
import at.bitfire.davdroid.R
import at.bitfire.davdroid.databinding.AccountCarddavItemBinding
import at.bitfire.davdroid.db.Collection
import at.bitfire.davdroid.settings.Settings
import at.bitfire.davdroid.settings.SettingsManager
import at.bitfire.davdroid.util.PermissionUtils
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class AddressBooksFragment: CollectionsFragment() {

    override val noCollectionsStringId = R.string.account_no_address_books

    private val menuProvider = object : CollectionsMenuProvider() {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.carddav_actions, menu)
        }

        override fun onPrepareMenu(menu: Menu) {
            super.onPrepareMenu(menu)
            menu.findItem(R.id.create_address_book).isVisible =
                model.hasWriteableCollections.value?.isNotEmpty() ?: false
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            if (super.onMenuItemSelected(menuItem))
                return true

            if (menuItem.itemId == R.id.create_address_book) {
                val intent = Intent(requireActivity(), CreateAddressBookActivity::class.java)
                intent.putExtra(CreateAddressBookActivity.EXTRA_ACCOUNT, accountModel.account)
                startActivity(intent)
                return true
            }

            return false
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().addMenuProvider(menuProvider)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().removeMenuProvider(menuProvider)
    }

    override fun checkPermissions() {
        if (PermissionUtils.havePermissions(requireActivity(), PermissionUtils.CONTACT_PERMISSIONS))
            binding.permissionsCard.visibility = View.GONE
        else {
            binding.permissionsText.setText(R.string.account_missing_permissions)
            binding.permissionsCard.visibility = View.VISIBLE
        }
    }

    override fun createAdapter() = AddressBookAdapter(accountModel, parentFragmentManager)


    class AddressBookViewHolder(
        parent: ViewGroup,
        accountModel: AccountActivity.Model,
        val fragmentManager: FragmentManager,
    ): CollectionViewHolder<AccountCarddavItemBinding>(parent, AccountCarddavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), accountModel) {

        @EntryPoint
        @InstallIn(SingletonComponent::class)
        interface AddressBookViewHolderEntryPoint {
            fun settingsManager(): SettingsManager
        }

        private val settings = EntryPointAccessors.fromApplication(parent.context, AddressBookViewHolderEntryPoint::class.java).settingsManager()
        private val forceReadOnlyAddressBooks = settings.getBoolean(Settings.FORCE_READ_ONLY_ADDRESSBOOKS) // managed restriction

        override fun bindTo(item: Collection) {
            binding.sync.isChecked = item.sync
            binding.title.text = item.title()

            if (item.description.isNullOrBlank())
                binding.description.visibility = View.GONE
            else {
                binding.description.text = item.description
                binding.description.visibility = View.VISIBLE
            }

            binding.readOnly.visibility = if (item.readOnly() || forceReadOnlyAddressBooks) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                accountModel.toggleSync(item)
            }
            binding.actionOverflow.setOnClickListener(CollectionPopupListener(accountModel, item, fragmentManager, forceReadOnlyAddressBooks))
        }
    }

    class AddressBookAdapter(
        accountModel: AccountActivity.Model,
        val fragmentManager: FragmentManager
    ): CollectionAdapter(accountModel) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            AddressBookViewHolder(parent, accountModel, fragmentManager)

    }

}