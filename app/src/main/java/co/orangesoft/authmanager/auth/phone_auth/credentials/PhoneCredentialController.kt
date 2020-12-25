package co.orangesoft.authmanager.auth.phone_auth.credentials

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.CredentialListener
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.auth.parseResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PhoneCredentialController(private val authService: AuthService,
                                phoneAuthCredential: PhoneAuthCredential) : IBaseCredentialController, CoroutineScope {

    override val credential: PhoneAuthCredential = phoneAuthCredential

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO }

    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun addCredential(listener: CredentialListener) {
        launch {

            val prevUser: FirebaseUser? = authInstance.currentUser?.let { if(it.providerData.size > 1) it else null }
            authService::createPhoneToken.parseResponse(prevUser?.uid, credential.phone, credential.code) {
                onSuccess {
                    authInstance.signInWithCustomToken(it)
                        .addOnSuccessListener {
                            it.user?.getIdToken(true)?.addOnSuccessListener {
                                    listener(CredentialResult(credential, it.token ?: ""))
                                }?.addOnFailureListener { listener(it) }
                                ?: listener(Throwable("Firebase user not created"))
                        }
                        .addOnFailureListener { listener(it) }
                }

                onError {
                    listener(it)
                }
            }
        }
    }

    override fun removeCredential(listener: CredentialListener) {
        authInstance.currentUser?.providerData?.firstOrNull {
            it.providerId == credential.providerId
        }?.let { provider ->
            authInstance.currentUser?.unlink(provider.providerId)
                ?.addOnSuccessListener { listener(credential) }
                ?.addOnFailureListener { listener(it) }
        } ?: listener(credential)
    }

    override fun onActivityResult(code: Int, data: Intent?) {}

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {}
}