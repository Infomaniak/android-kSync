package at.bitfire.davdroid.util

import android.content.Context
import at.bitfire.davdroid.BuildConfig.APPLICATION_ID
import at.bitfire.davdroid.BuildConfig.CLIENT_ID
import com.infomaniak.lib.login.InfomaniakLogin

fun Context.getInfomaniakLogin() = InfomaniakLogin(context = this, appUID = APPLICATION_ID, clientID = CLIENT_ID)
