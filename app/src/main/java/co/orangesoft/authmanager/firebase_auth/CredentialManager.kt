package co.orangesoft.authmanager.firebase_auth

import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialsManager
import by.orangesoft.auth.firebase.credential.Firebase
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.user.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job

@InternalCoroutinesApi
internal class CredentialManager(
    private val authService: AuthService,
    private val profileService: ProfileService,
    parentJob: Job? = null
): FirebaseCredentialsManager(parentJob) {

    override fun getCurrentUser(): FirebaseUserController =
        firebaseInstance.currentUser?.let {
            UserControllerImpl(firebaseInstance, profileService)
        } ?: UnregisteredUserControllerImpl(firebaseInstance)

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController {
        authService::login.parseResponse(credentialResult)
        return onLogged(credentialResult).apply { updateAccount(profile) }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
        authService::addCreds.parseResponse(user.accessToken, credentialResult.credential.providerId)
        user.reloadCredentials()
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: FirebaseUserController) {
        authService::removeCreds.parseResponse(user.accessToken, credential.providerId.replace(".com", ""))
        user.reloadCredentials()
    }

    override suspend fun logout(user: FirebaseUserController) {
        authService::logout.parseResponse(user.accessToken)
        super.logout(user)
    }

    override suspend fun deleteUser(user: FirebaseUserController) {
        authService::delete.parseResponse(user.accessToken)
        super.deleteUser(user)
    }

    override fun getBuilder(credential: IBaseCredential): IBaseCredentialsManager.Builder =
        CredBuilder(
            when (credential.providerId) {
                Firebase.Apple.providerId -> Firebase.Apple
                Firebase.Facebook.providerId -> Firebase.Facebook
                Firebase.Google("").providerId -> Firebase.Google("")
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        )
}