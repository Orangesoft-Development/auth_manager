package co.orangesoft.authmanager.auth

import android.content.Context
import by.orangesoft.auth.credentials.*
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.auth.email.EmailAuthCredential
import co.orangesoft.authmanager.auth.email.SimpleEmailCredentialController
import co.orangesoft.authmanager.auth.phone.SimplePhoneAuthCredential
import co.orangesoft.authmanager.auth.phone.SimplePhoneCredentialController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import java.lang.Exception
import kotlin.jvm.Throws

@InternalCoroutinesApi
class SimpleCredentialManager(private val appContext: Context,
                              private val authService: AuthService,
                              private val profileService: ProfileService,
                              parentJob: Job? = null) : BaseCredentialsManager<SimpleUserController>(parentJob) {

    override fun getCurrentUser(): SimpleUserController = SimpleUserController(appContext, profileService)

    @Throws(Throwable::class)
    override suspend fun onLogged(credentialResult: CredentialResult): SimpleUserController {
        authService.login(credentialResult)
        return getCurrentUser().apply {
            updateProfileAccount(profile)
                .catch { it.printStackTrace() }
                .launchIn(this@SimpleCredentialManager)
        }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: SimpleUserController) {
        try {
            authService.addCreds(user.getAccessToken(), credentialResult.providerId)
            user.reloadProfile().flowOn(Dispatchers.IO).catch { it.printStackTrace() }.collect()
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: SimpleUserController) {
        try {
            authService.removeCreds(user.getAccessToken(), credential.providerId.replace(".com", ""))
            user.reloadProfile().flowOn(Dispatchers.IO).catch { it.printStackTrace() }.collect()
        } catch (ex: Exception) { ex.printStackTrace() }
    }

    override suspend fun onUserLogout(user: SimpleUserController): SimpleUserController {
        authService.logout(user.getAccessToken())
        return getCurrentUser()
    }

    override suspend fun onUserDelete(user: SimpleUserController): SimpleUserController {
        authService.delete(user.getAccessToken())
        return getCurrentUser()
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CredBuilder(credential)

    open inner class CredBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder(credential) {

        override fun createCredential(): IBaseCredentialController =
            when (val simpleCred = credential) {
                is SimplePhoneAuthCredential -> SimplePhoneCredentialController(appContext, authService, simpleCred)
                is EmailAuthCredential -> SimpleEmailCredentialController(appContext, authService, simpleCred)
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
    }

}
