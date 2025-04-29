/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.util

import com.google.common.base.Joiner
import com.google.common.base.Strings

fun String?.trimToNull() = Strings.emptyToNull(this?.trim())

fun String.withTrailingSlash() =
    if (this.endsWith('/'))
        this
    else
        "$this/"