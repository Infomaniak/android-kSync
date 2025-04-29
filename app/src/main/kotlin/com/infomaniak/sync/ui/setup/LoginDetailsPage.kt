/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package com.infomaniak.sync.ui.setup

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginDetailsPage(
    snackbarHostState: SnackbarHostState,
    model: LoginScreenModel = viewModel()
) {
    val uiState = model.loginDetailsUiState
    uiState.loginType.LoginScreen(
        snackbarHostState = snackbarHostState,
        initialLoginInfo = uiState.loginInfo,
        onLogin = { loginInfo ->
            model.updateLoginInfo(loginInfo)
            model.navToNextPage()
        }
    )
}