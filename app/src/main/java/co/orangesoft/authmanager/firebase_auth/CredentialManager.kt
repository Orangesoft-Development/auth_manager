package co.orangesoft.authmanager.firebase_auth

import android.content.Context
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.user.*
import co.orangesoft.authmanager.firebase_auth.phone_auth.PhoneAuthCredential
import co.orangesoft.authmanager.firebase_auth.phone_auth.PhoneCredentialController
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job

@InternalCoroutinesApi
internal class CredentialManager(
    private val authService: AuthService,
    private val profileService: ProfileService,
    appContext: Context,
    parentJob: Job? = null
): FirebaseCredentialsManager(appContext, parentJob) {

    override fun getCurrentUser(): FirebaseUserController =
        firebaseInstance.currentUser?.let {
            UserControllerImpl(firebaseInstance, profileService)
        } ?: UnregisteredUserControllerImpl(firebaseInstance)

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController {
        authService.login(credentialResult)
        return super.onLogged(credentialResult).apply { updateAccount(profile) }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
        authService.addCreds(user.getAccessToken(), credentialResult.credential.providerId)
        user.reloadCredentials()
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: FirebaseUserController) {
        authService.removeCreds(user.getAccessToken(), credential.providerId.replace(".com", ""))
        user.reloadCredentials()
    }

    override suspend fun logout(user: FirebaseUserController) {
        authService.logout(user.getAccessToken())
        super.logout(user)
    }

    override suspend fun deleteUser(user: FirebaseUserController) {
        authService.delete(user.getAccessToken())
        super.deleteUser(user)
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder = CustomCredBuilder(credential)

    inner class CustomCredBuilder(credential: IBaseCredential): CredBuilder(credential) {

        override fun createCredential(): IBaseCredentialController =
            when (credential) {
                is PhoneAuthCredential -> PhoneCredentialController(authService, credential)
                else -> super.createCredential()
            }
    }
}