package co.orangesoft.authmanager.credential

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.CredentialController
import by.orangesoft.auth.credentials.CredentialListener
import by.orangesoft.auth.credentials.CredentialResult
import co.orangesoft.authmanager.api.ApiCredentials
import co.orangesoft.authmanager.api.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PhoneCredentialController(private val authService: AuthService, phone: PhoneCredential): CredentialController(phone), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO }
    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun addCredential(listener: CredentialListener) {
        launch {
            try {
                val prevUser: FirebaseUser? = authInstance.currentUser?.let { if(it.providerData.size > 1) it else null }
                val result = authService.createPhoneToken(ApiCredentials.Phone.fromPhoneCredential(prevUser?.uid, method as PhoneCredential))

                authInstance.signInWithCustomToken(result.custom_token)
                    .addOnSuccessListener {
                        it.user?.getIdToken(true)
                            ?.addOnSuccessListener { listener(CredentialResult(method, it.token!!)) }
                            ?.addOnFailureListener { listener(it) }
                            ?: listener(Throwable("Firebase user not created"))
                    }
                    .addOnFailureListener { listener(it) }
            } catch (e: Exception) {
                listener(e)
            }
        }
    }

    override fun removeCredential(listener: CredentialListener) {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == method.providerId
        }?.let { provider ->
            authInstance.currentUser?.unlink(provider.providerId)
                ?.addOnSuccessListener { listener(method) }
                ?.addOnFailureListener { listener(it) }
        } ?: listener(method)
    }

    override fun createProvider(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {}

    override fun onActivityResult(code: Int, data: Intent?) {}

}