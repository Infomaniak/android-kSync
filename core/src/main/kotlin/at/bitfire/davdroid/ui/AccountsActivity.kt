/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.login.InfomaniakLogin
import at.bitfire.davdroid.ui.account.AccountActivity
import at.bitfire.davdroid.ui.intro.IntroActivity
import at.bitfire.davdroid.ui.setup.LoginActivity
import at.bitfire.davdroid.util.getInfomaniakLogin
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

    private val infomaniakLogin: InfomaniakLogin by lazy { getInfomaniakLogin() }

    private val webViewLoginResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            with(result) {
                if (resultCode == RESULT_OK) {
                    val code = data?.extras?.getString(InfomaniakLogin.CODE_TAG)
                    if (code.isNullOrBlank()) {
                        val translatedError = data?.extras?.getString(InfomaniakLogin.ERROR_TRANSLATED_TAG)
                        Toast.makeText(this@AccountsActivity, translatedError, Toast.LENGTH_LONG).show()
                    } else {
                        Intent(this@AccountsActivity, LoginActivity::class.java).apply {
                            putExtra("code", code)
                            startActivity(this)
                        }
                    }
                }
            }
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
                    infomaniakLogin.startWebViewLogin(webViewLoginResultLauncher)
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
