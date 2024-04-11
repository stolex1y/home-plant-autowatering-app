package ru.filimonov.hpa.ui.auth.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import ru.filimonov.hpa.BuildConfig
import ru.filimonov.hpa.R
import ru.filimonov.hpa.ui.auth.AuthViewModel
import ru.filimonov.hpa.ui.common.navigation.Destination
import timber.log.Timber

@Composable
fun SignInScreen(
    onSignInFailure: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = viewModel) {
        viewModel.init()
    }
    val state: AuthViewModel.State by viewModel.state.collectAsState()
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestProfile()
        .requestEmail()
        .requestIdToken(BuildConfig.FIREBASE_AUTH_CLIENT_ID)
        .build()
    val signInLauncher =
        rememberLauncherForActivityResult(contract = GoogleSignInActivityResultContract()) { authRes ->
            authRes.onSuccess {
                Timber.d("Successful signed in")
                viewModel.onSuccessSignedIn(GoogleAuthProvider.getCredential(it.idToken, null))
            }.onFailure {
                Timber.e(it, "Sign in error")
                onSignInFailure()
            }
        }
    when (state) {
        AuthViewModel.State.Expired, AuthViewModel.State.SignedOut -> {
            LaunchedEffect(signInLauncher) {
                signInLauncher.launch(signInOptions)
            }
            viewModel.onStartSigningIn()
        }

        AuthViewModel.State.SigningIn -> Loading()
        AuthViewModel.State.SignedIn -> {}
        AuthViewModel.State.Loading -> {}
    }
}

@Composable
private fun Loading() {
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading progress",
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Loader(modifier = Modifier.size(120.dp), progress = progress)
        Text(stringResource(id = R.string.signing_in))
    }
}

@Composable
private fun Loader(modifier: Modifier = Modifier, progress: Float) {
    CircularProgressIndicator(
        progress = { progress },
        color = Color.DarkGray,
        strokeWidth = 20.dp,
        modifier = modifier,
    )
}

object SignInScreenDestination : Destination {
    override val path: Destination.Path = Destination.Path() / "auth" / "sign-in"
}

@Composable
@Preview(showBackground = true)
private fun LoadingPreview() {
    Loading()
}
