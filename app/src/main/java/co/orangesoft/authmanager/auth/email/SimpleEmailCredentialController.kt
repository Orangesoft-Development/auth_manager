package co.orangesoft.authmanager.auth.email

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialController
import by.orangesoft.auth.credentials.UnlinkCredentialResult
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.request_body.EmailCredentialRequestBody
import co.orangesoft.authmanager.auth.PrefsHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class SimpleEmailCredentialController(private val appContext: Context,
                                      private val authService: AuthService,
                                      emailAuthCredential: EmailAuthCredential
) : IBaseCredentialController, CoroutineScope {

    private var flow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)

    private val prefsHelper by lazy { PrefsHelper(appContext) }

    override val authCredential: BaseAuthCredential = emailAuthCredential

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    @Suppress("UNCHECKED_CAST")
    override fun addCredential(): Flow<CredentialResult> {
        if (authCredential is EmailAuthCredential) {
            launch {
                authService.createEmailToken(EmailCredentialRequestBody(authCredential.email, authCredential.password, prefsHelper.getProfile()?.id))
                    .apply {
                        val token = if (isSuccessful) body() ?: "" else ""
                        prefsHelper.saveToken(token)
                        prefsHelper.addCredential(authCredential)
                        flow.tryEmit(CredentialResult(authCredential.providerId, token))
                    }
            }
        }
        return flow.asSharedFlow()
    }

    override fun removeCredential() =  flow.asSharedFlow().onStart {
        prefsHelper.removeCredential(authCredential)
        flow.emit(UnlinkCredentialResult())
    }

}