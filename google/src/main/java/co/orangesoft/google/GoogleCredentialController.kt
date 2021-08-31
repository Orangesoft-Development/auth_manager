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

class GoogleCredentialController(method: FirebaseAuthCredential.Google): BaseFirebaseCredentialController(method) {

    private fun googleSingInClient(context: Context, requestProfile: Boolean = true): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        if (requestProfile) {
            options.requestProfile()
                .requestIdToken((authCredential as FirebaseAuthCredential.Google).clientId)
        }
        return GoogleSignIn.getClient(context, options.build())
    }

    override fun signOut(context: Context) { googleSingInClient(context, false).signOut() }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {
        Log.e("!!!", "clientId: ${(authCredential as FirebaseAuthCredential.Google).clientId}")
        activityLauncher.launch(googleSingInClient(activity).signInIntent)
    }

    override fun onActivityResult(code: Int, data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data).apply {
            addOnSuccessListener { account ->
                emitAuthTask(GoogleAuthProvider.getCredential(account.idToken, null))
            }
            addOnFailureListener { onError("Error add credential ${authCredential.providerId}", it) }
        }
    }
}