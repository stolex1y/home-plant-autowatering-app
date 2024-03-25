package ru.filimonov.hpa.ui.auth.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignInActivityResultContract :
    ActivityResultContract<GoogleSignInOptions, Result<GoogleSignInAccount>>() {
    override fun createIntent(context: Context, input: GoogleSignInOptions): Intent {
        val googleSignInClient = GoogleSignIn.getClient(context, input)
        return googleSignInClient.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result<GoogleSignInAccount> {
        if (resultCode != Activity.RESULT_OK || intent == null) {
            return Result.failure(NotSuccessfulActivityResult(resultCode = resultCode))
        }
        val signInResult = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return if (signInResult.isSuccessful) {
            Result.success(signInResult.result)
        } else {
            Result.failure(signInResult.exception!!)
        }
    }

}

class NotSuccessfulActivityResult(
    val resultCode: Int
) : Throwable("Got not OK result code from started activity. Result code: $resultCode")
