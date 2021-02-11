package co.orangesoft.authmanager.auth.email

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.AuthCredential
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.IBaseCredentialController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.request_body.EmailCredentialRequestBody
import co.orangesoft.authmanager.auth.PrefsHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class SimpleEmailCredentialController(private val appContext: Context,
                                      private val authService: AuthService,
                                      emailAuthCredential: EmailAuthCredential
) : IBaseCredentialController, CoroutineScope {

    private var flow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)

    private val prefsHelper by lazy { PrefsHelper(appContext) }

    override val credential: AuthCredential = emailAuthCredential

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    @Suppress("UNCHECKED_CAST")
    override fun addCredential(): Flow<CredentialResult> {
        if (credential is EmailAuthCredential) {
            launch {
                Log.e("TAG","Controller thred:${Thread.currentThread()}")
                //authService.createEmailToken(EmailCredentialRequestBody(credential.email, credential.password, prefsHelper.getProfile()?.id)
                authService.fakeRequest().apply {
                    val resultToken = if (isSuccessful) body() ?: "" else ""
                    prefsHelper.saveToken("FAKE_TOKEN ${body()?.javaClass}")
                    prefsHelper.addCredential(credential)
                    flow.tryEmit(CredentialResult(credential, prefsHelper.getToken()))
                }
            }
        }

        return flow.asSharedFlow()
    }

    override fun removeCredential(): Job {
        return launch {
            prefsHelper.removeCredential(credential)
        }
    }

    override fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>) {}

    override fun onActivityResult(code: Int, data: Intent?) {}
}