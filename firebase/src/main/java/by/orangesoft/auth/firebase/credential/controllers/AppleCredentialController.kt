package by.orangesoft.auth.firebase.credential.controllers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.Firebase
import com.google.firebase.auth.OAuthProvider
import java.util.*

class AppleCredentialController: BaseFirebaseCredentialController(Firebase.Apple) {

    private val appleSingInClient: OAuthProvider by lazy {
        OAuthProvider.newBuilder(credential.providerId).apply {
            addCustomParameter("locale", Locale.getDefault().language)
            scopes = listOf("email", "name")
        }.build()
    }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {
        activityCallback = authInstance.currentUser?.let { currentUser ->
            if(!currentUser.isAnonymous && currentUser.providerData.size > 1)
                currentUser.startActivityForLinkWithProvider(activity, appleSingInClient)
            else null
        } ?: authInstance.startActivityForSignInWithProvider(activity, appleSingInClient)
    }

    override fun onActivityResult(code: Int, data: Intent?) {}
}