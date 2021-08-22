package co.orangesoft.authmanager.auth

import android.content.Context
import android.util.Log
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.auth.email.EmailAuthCredential
import co.orangesoft.authmanager.auth.email.SimpleEmailCredentialController
import co.orangesoft.authmanager.auth.phone.SimplePhoneAuthCredential
import co.orangesoft.authmanager.auth.phone.SimplePhoneCredentialController
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlin.jvm.Throws

@InternalCoroutinesApi
class SimpleCredentialManager(private val appContext: Context,
                              private val authService: AuthService,
                              private val profileService: ProfileService,
                              parentJob: Job? = null) : BaseCredentialsManager<SimpleUserController>(parentJob) {

    private fun getCurrentUser(): SimpleUserController = SimpleUserController(appContext, profileService)

    @Throws(Throwable::class)
    override suspend fun onLogged(credentialResult: CredentialResult): SimpleUserController {
        authService.login(credentialResult)
        return getCurrentUser().apply { updateProfileAccount(profile).launchIn(this@SimpleCredentialManager) }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: SimpleUserController) {
        authService.addCreds(user.getAccessToken(), credentialResult.providerId)
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: SimpleUserController) {
        authService.removeCreds(user.getAccessToken(), credential.providerId.replace(".com", ""))
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CredBuilder(credential)

    @Throws(Throwable::class)
    override suspend fun logout(user: SimpleUserController) {
        authService.logout(user.getAccessToken())
    }

    @Throws(Throwable::class)
    override suspend fun deleteUser(user: SimpleUserController) {
        authService.delete(user.getAccessToken())
    }

    open inner class CredBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder(credential) {

        override fun createCredential(): IBaseCredentialController =
            when (credential) {
                is SimplePhoneAuthCredential -> SimplePhoneCredentialController(appContext, authService, credential)
                is EmailAuthCredential -> SimpleEmailCredentialController(appContext, authService, credential)
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
    }
}
