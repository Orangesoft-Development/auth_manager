package co.orangesoft.google

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class GoogleCredentialController(method: Firebase.Google): BaseFirebaseCredentialController(method) {

    private fun googleSingInClient(activity: FragmentActivity): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken((credential as Firebase.Google).clientId)
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(activity, options)
    }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {
        Log.e("!!!", "clientId: ${(credential as Firebase.Google).clientId}")
        activityLauncher.launch(googleSingInClient(activity).signInIntent)
    }

    override fun onActivityResult(code: Int, data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data).apply {
            addOnSuccessListener { account ->
                activityCallback = getAuthTask(GoogleAuthProvider.getCredential(account.idToken, null))
                getCredential()
            }
            addOnFailureListener { onError("Error add credential ${credential.providerId}", it) }
        }
    }
}