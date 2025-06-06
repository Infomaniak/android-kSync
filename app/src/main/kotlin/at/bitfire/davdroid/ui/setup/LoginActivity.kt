/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui.setup

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.bitfire.davdroid.db.Credentials
import at.bitfire.davdroid.KSyncConstants.PASSWORD_API_URL
import at.bitfire.davdroid.KSyncConstants.PROFILE_API_URL
import at.bitfire.davdroid.KSyncConstants.SYNC_INFOMANIAK
import at.bitfire.davdroid.model.InfomaniakPassword
import at.bitfire.davdroid.model.InfomaniakUser
import at.bitfire.davdroid.ui.AppTheme
import at.bitfire.davdroid.ui.account.AccountActivity
import at.bitfire.davdroid.ui.composable.ProgressBar
import at.bitfire.davdroid.util.getInfomaniakLogin
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.lib.login.InfomaniakLogin.TokenResult.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.Logger
import javax.inject.Inject

/**
 * Activity to initially connect to a server and create an account.
 * Fields for server/user data can be pre-filled with extras in the Intent.
 */
@AndroidEntryPoint
class LoginActivity @Inject constructor() : AppCompatActivity() {

    @Inject
    lateinit var loginTypesProvider: LoginTypesProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (initialLoginType, skipLoginTypePage) = loginTypesProvider.intentToInitialLoginType(intent)

        setContent {
            //region kSync
            val loginInfoState = produceState<LoginInfo?>(initialValue = null) {
                withContext(Dispatchers.Default) {
                    value = loginInfoFromIntentInfomaniak(intent, getInfomaniakLogin())
                }
            }
            val loginInfo = loginInfoState.value
            //endregion

            if (loginInfo == null) { // kSync
                AppTheme { // kSync
                    ProgressBar(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) // kSync
                } // kSync
            } else { // kSync
                LoginScreen( // kSync
                    initialLoginType = initialLoginType,
                    skipLoginTypePage = skipLoginTypePage,
                    initialLoginInfo = loginInfo, // kSync
                    onNavUp = { onSupportNavigateUp() },
                    onFinish = { newAccount ->
                        finish()

                        if (newAccount != null) {
                            val intent = Intent(this, AccountActivity::class.java)
                            intent.putExtra(AccountActivity.EXTRA_ACCOUNT, newAccount)
                            startActivity(intent)
                        }
                    }
                )
            }
        }
    }

    companion object {

        /**
         * When set, "login by URL" will be activated by default, and the URL field will be set to this value.
         * When not set, "login by email" will be activated by default.
         */
        const val EXTRA_URL = "url"

        /**
         * When set, and {@link #EXTRA_PASSWORD} is set too, the user name field will be set to this value.
         * When set, and {@link #EXTRA_URL} is not set, the email address field will be set to this value.
         */
        const val EXTRA_USERNAME = "username"

        /**
         * When set, the password field will be set to this value.
         */
        const val EXTRA_PASSWORD = "password"

        /**
         * When set, Nextcloud Login Flow will be used.
         */
        const val EXTRA_LOGIN_FLOW = "loginFlow"

        /**
         * Extracts login information from given intent, validates it and returns it in [LoginInfo].
         *
         * @param intent Contains base url, username and password.
         * @return Extracted login info. Contains null values if given info is invalid.
         */
        fun loginInfoFromIntent(intent: Intent): LoginInfo {
            var givenUri: String? = null
            var givenUsername: String? = null
            var givenPassword: String? = null

            // extract URI or email and optionally username/password from Intent data
            val logger = Logger.getGlobal()
            intent.data?.normalizeScheme()?.let { uri ->
                val realScheme = when (uri.scheme) {
                    // replace caldav[s]:// and carddav[s]:// with http[s]://
                    "caldav", "carddav" -> "http"
                    "caldavs", "carddavs", "davx5" -> "https"

                    // keep these
                    "http", "https", "mailto" -> uri.scheme

                    // unknown scheme
                    else -> null
                }

                when (realScheme) {
                    "http", "https" -> {
                        // extract user info
                        uri.userInfo?.split(':')?.let { userInfo ->
                            givenUsername = userInfo.getOrNull(0)
                            givenPassword = userInfo.getOrNull(1)
                        }

                        // use real scheme, drop user info and fragment
                        givenUri = try {
                            URI(realScheme, null, uri.host, uri.port, uri.path, uri.query, null).toString()
                        } catch (_: URISyntaxException) {
                            logger.warning("Couldn't construct URI from login Intent data: $uri")
                            null
                        }
                    }

                    "mailto" ->
                        givenUsername = uri.schemeSpecificPart
                }
            }

            if (givenUri == null)
                givenUri = intent.getStringExtra(EXTRA_URL)

            // always prefer username/password from the extras
            if (intent.hasExtra(EXTRA_USERNAME))
                givenUsername = intent.getStringExtra(EXTRA_USERNAME)
            if (intent.hasExtra(EXTRA_PASSWORD))
                givenPassword = intent.getStringExtra(EXTRA_PASSWORD)

            return LoginInfo(
                baseUri = try {
                    URI(givenUri)
                } catch (_: Exception) {
                    null
                },
                credentials = Credentials(
                    username = givenUsername,
                    password = givenPassword
                )
            )
        }

        //region kSync
        /**
         * Extracts login information from given intent, validates it and returns it in [LoginInfo].
         *
         * @param intent Contains base url, username and password.
         * @param infomaniakLogin Contains all the necessary information and functions to connect to Infomaniak servers.
         * @return Extracted login info. Contains null values if given info is invalid.
         */
        suspend fun loginInfoFromIntentInfomaniak(intent: Intent, infomaniakLogin: InfomaniakLogin): LoginInfo {
            var givenUri: String? = null
            var givenUsername: String? = null
            var givenPassword: String? = null

            // extract URI or email and optionally username/password from Intent data
            val logger = Logger.getGlobal()
            intent.data?.normalizeScheme()?.let { uri ->
                val realScheme = when (uri.scheme) {
                    // replace caldav[s]:// and carddav[s]:// with http[s]://
                    "caldav", "carddav" -> "http"
                    "caldavs", "carddavs", "davx5" -> "https"

                    // keep these
                    "http", "https", "mailto" -> uri.scheme

                    // unknown scheme
                    else -> null
                }

                when (realScheme) {
                    "http", "https" -> {
                        // extract user info
                        uri.userInfo?.split(':')?.let { userInfo ->
                            givenUsername = userInfo.getOrNull(0)
                            givenPassword = userInfo.getOrNull(1)
                        }

                        // use real scheme, drop user info and fragment
                        givenUri = try {
                            URI(realScheme, null, uri.host, uri.port, uri.path, uri.query, null).toString()
                        } catch (_: URISyntaxException) {
                            logger.warning("Couldn't construct URI from login Intent data: $uri")
                            null
                        }
                    }

                    "mailto" ->
                        givenUsername = uri.schemeSpecificPart
                }
            }

            if (givenUri == null)
                givenUri = intent.getStringExtra(EXTRA_URL)

            // always prefer username/password from the extras
            if (intent.hasExtra(EXTRA_USERNAME))
                givenUsername = intent.getStringExtra(EXTRA_USERNAME)
            if (intent.hasExtra(EXTRA_PASSWORD))
                givenPassword = intent.getStringExtra(EXTRA_PASSWORD)

            return LoginInfo(
                baseUri = URI(SYNC_INFOMANIAK),
                credentials = intent.getStringExtra("code")?.let { code -> getCredentials(code, infomaniakLogin) },
            )
        }

        private suspend fun getCredentials(code: String, infomaniakLogin: InfomaniakLogin): Credentials? {
            try {
                val okHttpClient = OkHttpClient.Builder().build()
                val gson = Gson()
                val apiToken = getApiToken(code, infomaniakLogin, okHttpClient) ?: return null
                val infomaniakUser = getInfomaniakUser(apiToken, okHttpClient, gson) ?: return null
                val infomaniakPassword = getInfomaniakPassword(apiToken, okHttpClient, gson) ?: return null

                val credentials = Credentials(infomaniakUser.login, infomaniakPassword.password)
                infomaniakLogin.deleteToken(okHttpClient, apiToken)

                return credentials
            } catch (exception: Exception) {
                exception.printStackTrace()
                return null
            }
        }

        private suspend fun getApiToken(code: String, infomaniakLogin: InfomaniakLogin, okHttpClient: OkHttpClient): ApiToken? {
            val tokenResult = infomaniakLogin.getToken(okHttpClient, code)
            return when (tokenResult) {
                is Success -> tokenResult.apiToken
                else -> null
            }
        }

        private fun getInfomaniakUser(apiToken: ApiToken, okHttpClient: OkHttpClient, gson: Gson): InfomaniakUser? {

            val request = Request.Builder()
                .url(PROFILE_API_URL)
                .header("Authorization", "Bearer ${apiToken.accessToken}")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            return if (response.isSuccessful) {
                val body = response.body?.string() ?: return null
                val jsonObject = JsonParser.parseString(body).asJsonObject.getAsJsonObject("data")
                gson.fromJson(jsonObject, InfomaniakUser::class.java)
            } else {
                null
            }
        }

        private fun getInfomaniakPassword(apiToken: ApiToken, okHttpClient: OkHttpClient, gson: Gson): InfomaniakPassword? {

            val formatter = SimpleDateFormat("EEEE MMM d yyyy HH:mm:ss", Locale.getDefault())

            val formBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "Infomaniak Sync - ${formatter.format(Date())}")

            val request = Request.Builder()
                .url(PASSWORD_API_URL)
                .header("Authorization", "Bearer ${apiToken.accessToken}")
                .post(formBuilder.build())
                .build()

            val response = okHttpClient.newCall(request).execute()

            return if (response.isSuccessful) {
                val body = response.body?.string() ?: return null
                val jsonObject = JsonParser.parseString(body).asJsonObject.getAsJsonObject("data")
                gson.fromJson(jsonObject, InfomaniakPassword::class.java)
            } else {
                null
            }
        }
        //endregion
    }
}
