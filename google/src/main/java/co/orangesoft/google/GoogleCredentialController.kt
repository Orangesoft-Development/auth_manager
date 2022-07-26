package co.orangesoft.google

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

/**
 * Credential controller using FirebaseAuth with google provider
 *
 * @see FirebaseAuthCredential.Google
 * @see FirebaseAuth
 *
 */
class GoogleCredentialController(method: FirebaseAuthCredential.Google) :
    BaseFirebaseCredentialController(method) {

    private fun googleSingInClient(
        context: Context,
        requestProfile: Boolean = true
    ): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        if (requestProfile) {
            options
                .requestProfile()
                .requestEmail()
                .requestIdToken((authCredential as FirebaseAuthCredential.Google).clientId)
        }
        return GoogleSignIn.getClient(context, options.build())
    }

    override fun clearCredInfo(context: Context) {
        googleSingInClient(context, false).revokeAccess()
    }

    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {
        Log.i("!!!", "clientId: ${(authCredential as FirebaseAuthCredential.Google).clientId}")
        activityLauncher.launch(googleSingInClient(activity).signInIntent)
    }

    override fun onActivityResult(code: Int, data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data).apply {
            addOnSuccessListener { account ->
                emitAuthTask(GoogleAuthProvider.getCredential(account.idToken, null))
            }
            addOnFailureListener {
                onError(
                    "Error add credential ${authCredential.providerId}",
                    it
                )
            }
        }
    }
}