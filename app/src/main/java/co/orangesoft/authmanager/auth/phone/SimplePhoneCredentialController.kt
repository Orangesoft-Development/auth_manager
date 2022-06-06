package co.orangesoft.authmanager.auth.phone

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialController
import by.orangesoft.auth.credentials.UnlinkCredentialResult
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.request_body.PhoneCredentialRequestBody
import co.orangesoft.authmanager.auth.PrefsHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext

class SimplePhoneCredentialController(private val appContext: Context,
                                      private val authService: AuthService,
                                      simplePhoneAuthCredential: SimplePhoneAuthCredential
) : IBaseCredentialController, CoroutineScope {

    private var flow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)

    private val prefsHelper by lazy { PrefsHelper(appContext) }

    override val authCredential: BaseAuthCredential = simplePhoneAuthCredential

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override fun addCredential(): Flow<CredentialResult> {
        if (authCredential is SimplePhoneAuthCredential) {
            launch {
                authService.createPhoneToken(PhoneCredentialRequestBody(authCredential.phone, authCredential.code, prefsHelper.getProfile()?.id))
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