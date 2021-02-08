package co.orangesoft.authmanager.auth

import android.content.Context
import by.orangesoft.auth.credentials.*
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.auth.email.EmailAuthCredential
import co.orangesoft.authmanager.auth.email.SimpleEmailCredentialController
import co.orangesoft.authmanager.auth.phone.SimplePhoneAuthCredential
import co.orangesoft.authmanager.auth.phone.SimplePhoneCredentialController
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.jvm.Throws

@InternalCoroutinesApi
class SimpleCredentialManager(private val appContext: Context,
                              private val authService: AuthService,
                              private val profileService: ProfileService,
                              parentJob: Job? = null) : BaseCredentialsManager<SimpleUserController>(parentJob) {

    private fun getCurrentUser(): SimpleUserController = SimpleUserController(appContext, profileService)

    @Throws(Throwable::class)
    override suspend fun onLogged(credentialResult: CredentialResult): SimpleUserController {
        //authService::login.parseResponse(credentialResult)
        return getCurrentUser().apply { updateAccount(profile) }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: SimpleUserController) {
        authService.addCreds(user.accessToken, credentialResult.credential.providerId)
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: SimpleUserController) {
        authService.removeCreds(user.accessToken, credential.providerId.replace(".com", ""))
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CredBuilder(credential)

    @Throws(Throwable::class)
    override suspend fun logout(user: SimpleUserController) {
        authService.logout(user.accessToken).apply {
            //TODO maybe should clear prefs
        }
    }

    @Throws(Throwable::class)
    override suspend fun deleteUser(user: SimpleUserController) {
        authService.delete(user.accessToken).apply {
            //TODO maybe should clear prefs
        }
    }

    override fun removeCredential(credential: IBaseCredential, user: SimpleUserController): Flow<SimpleUserController> =
        flow {
            if (!user.credentials.value.let { creds -> creds.firstOrNull { it == credential } != null && creds.size > 1 }) {
                throw NoSuchElementException("Cannot remove method $credential")
            }

            getBuilder(credential).build().removeCredential().apply {
                emit(user)
            }
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
