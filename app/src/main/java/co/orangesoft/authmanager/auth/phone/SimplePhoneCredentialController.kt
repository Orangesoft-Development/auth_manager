package co.orangesoft.authmanager.auth.phone

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.IBaseCredentialController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.request_body.PhoneCredentialRequestBody
import co.orangesoft.authmanager.auth.PrefsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class SimplePhoneCredentialController(private val appContext: Context,
                                      private val authService: AuthService,
                                      simplePhoneAuthCredential: SimplePhoneAuthCredential
) : IBaseCredentialController, CoroutineScope {

    private lateinit var flow: MutableSharedFlow<*>

    private val prefsHelper by lazy { PrefsHelper(appContext) }

    override val credential: AuthCredential = simplePhoneAuthCredential

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    @Suppress("UNCHECKED_CAST")
    override fun addCredential(): Flow<CredentialResult> {
        flow = MutableSharedFlow<CredentialResult>(1, 1)
        if (credential is SimplePhoneAuthCredential) {
            launch {
                //authService.createEmailToken(EmailCredentialRequestBody(credential.email, credential.password, prefsHelper.getProfile()?.id)
                authService.fakeRequest().apply {
                    val resultToken = if (isSuccessful) body() ?: "" else ""
                    prefsHelper.saveToken("FAKE_TOKEN ${body()?.javaClass}")
                    prefsHelper.addCredential(credential)
                    (flow as MutableSharedFlow<CredentialResult>).tryEmit(CredentialResult(credential, prefsHelper.getToken()))
                }
            }
        }

        return (flow as MutableSharedFlow<CredentialResult>).asSharedFlow()
    }

    override fun removeCredential(): Collection<IBaseCredential> {
        prefsHelper.removeCredential(credential)
        return prefsHelper.getCredentials()
    }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {}

    override fun onActivityResult(code: Int, data: Intent?) {}
}