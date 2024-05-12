package ru.filimonov.hpa.ui.auth.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import ru.filimonov.hpa.BuildConfig
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.auth.AuthViewModel
import ru.filimonov.hpa.ui.theme.HpaTheme
import ru.filimonov.hpa.widgets.HpaLoading
import ru.filimonov.hpa.widgets.HpaScaffold
import ru.filimonov.hpa.widgets.HpaSnackbarHost
import ru.filimonov.hpa.widgets.SnackbarState
import timber.log.Timber

@Composable
fun SignInScreen(
    onSignInFailure: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = viewModel) {
        viewModel.init()
    }

    val state: AuthViewModel.UiState by viewModel.state.collectAsStateWithLifecycle()
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestProfile()
        .requestEmail()
        .requestIdToken(BuildConfig.FIREBASE_AUTH_CLIENT_ID)
        .build()
    val snackbarState = SnackbarState.rememberSnackbarState()
    val signInLauncher =
        rememberLauncherForActivityResult(contract = GoogleSignInActivityResultContract()) { authRes ->
            authRes.onSuccess {
                Timber.d("Successful signed in")
                viewModel.authenticate(GoogleAuthProvider.getCredential(it.idToken, null))
            }.onFailure {
                Timber.e(it, "Sign in error")
                onSignInFailure()
            }
        }
    HpaScaffold(
        snackbarHost = { HpaSnackbarHost(snackbarState) }
    ) {
        when (val stateValue = state) {
            AuthViewModel.UiState.Expired, AuthViewModel.UiState.SignedOut -> {
                LaunchedEffect(signInLauncher) {
                    signInLauncher.launch(signInOptions)
                }
                viewModel.onStartSigningIn()
            }

            AuthViewModel.UiState.SigningIn -> Loading()
            is AuthViewModel.UiState.SignedIn -> {
                SignedIn(name = stateValue.userAccount.name)
            }

            AuthViewModel.UiState.Loading -> {}

            is AuthViewModel.UiState.Error -> {
                snackbarState.showSnackbar(
                    messageRes = stateValue.msg,
                    onDismiss = { onSignInFailure() })
            }
        }
    }
}

@Composable
private fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HpaLoading()
        Text(stringResource(id = R.string.signing_in))
    }
}

@Composable
private fun SignedIn(
    name: String?
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (name == null) {
            Text(stringResource(id = R.string.welcome))
        } else {
            Text(stringResource(id = R.string.welcome_with_name, name))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LoadingPreview() {
    HpaTheme {
        Loading()
    }
}
