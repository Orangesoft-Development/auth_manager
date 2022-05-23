package co.orangesoft.apple

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import com.google.firebase.auth.OAuthProvider
import java.util.*

class AppleCredentialController : BaseFirebaseCredentialController(FirebaseAuthCredential.Apple) {

    private val appleSingInClient: OAuthProvider by lazy {
        OAuthProvider.newBuilder(authCredential.providerId).apply {
            addCustomParameter("locale", Locale.getDefault().language)
            scopes = listOf("email", "name")
        }.build()
    }

    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {
        authTaskFlow.tryEmit(authInstance.currentUser?.let { currentUser ->
            val authTask = if (!currentUser.isAnonymous && currentUser.providerData.size > 1)
                currentUser.startActivityForLinkWithProvider(activity, appleSingInClient) else null
            authTask
        } ?: authInstance.startActivityForSignInWithProvider(activity, appleSingInClient))
    }

    override fun onActivityResult(code: Int, data: Intent?) {}
}