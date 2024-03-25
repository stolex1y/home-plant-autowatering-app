package ru.filimonov.hpa.ui.auth.signout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import ru.filimonov.hpa.ui.auth.AuthViewModel
import ru.filimonov.hpa.ui.common.navigation.Destination
import ru.filimonov.hpa.ui.devices.DevicesScreenDestination

@Composable
fun SignOutScreen(
    onSignOut: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestProfile()
        .requestEmail()
//            .requestIdToken(BuildConfig.FIREBASE_AUTH_CLIENT_ID)
        .requestIdToken("958297732854-78en75e5o6s6svqeoj7cormo804kbomc.apps.googleusercontent.com")
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, signInOptions)
    googleSignInClient.signOut()
    LaunchedEffect(key1 = viewModel) {
        viewModel.signOut()
        onSignOut()
    }
}

fun NavGraphBuilder.addSignOutScreen(
    onSignOut: () -> Unit,
) {
    composable(
        route = SignOutScreenDestination.path.raw,
    ) {
        SignOutScreen(
            onSignOut = onSignOut,
        )
    }
}

object SignOutScreenDestination : Destination {
    override val path: Destination.Path = Destination.Path() / "auth" / "sign-out"
}
