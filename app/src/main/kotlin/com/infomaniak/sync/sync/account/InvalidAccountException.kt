/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.sync.account

import android.accounts.Account

/**
 * Thrown when an account is invalid (usually because it doesn't exist anymore).
 */
class InvalidAccountException(account: Account): Exception("Invalid account: $account")