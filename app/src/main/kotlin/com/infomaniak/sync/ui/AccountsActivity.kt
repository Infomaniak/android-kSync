/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.sync.ui.account.AccountActivity
import com.infomaniak.sync.ui.intro.IntroActivity
import com.infomaniak.sync.ui.setup.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AccountsActivity: AppCompatActivity() {

    @Inject
    lateinit var accountsDrawerHandler: AccountsDrawerHandler

    private val introActivityLauncher = registerForActivityResult(IntroActivity.Contract) { cancelled ->
        if (cancelled)
            finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // handle "Sync all" intent from launcher shortcut
        val syncAccounts = intent.action == Intent.ACTION_SYNC

        setContent {
            AccountsScreen(
                initialSyncAccounts = syncAccounts,
                onShowAppIntro = {
                    introActivityLauncher.launch(null)
                },
                accountsDrawerHandler = accountsDrawerHandler,
                onAddAccount = {
                    startActivity(Intent(this, LoginActivity::class.java))
                },
                onShowAccount = { account ->
                    val intent = Intent(this, AccountActivity::class.java)
                    intent.putExtra(AccountActivity.EXTRA_ACCOUNT, account)
                    startActivity(intent)
                },
                onManagePermissions = {
                    startActivity(Intent(this, PermissionsActivity::class.java))
                }
            )
        }
    }

}