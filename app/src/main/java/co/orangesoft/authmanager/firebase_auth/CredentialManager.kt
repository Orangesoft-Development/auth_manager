package co.orangesoft.authmanager.firebase_auth

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import by.orangesoft.auth.credentials.*
import by.orangesoft.auth.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.user.*
import co.orangesoft.authmanager.firebase_auth.phone_auth.PhoneAuthCredential
import co.orangesoft.authmanager.firebase_auth.phone_auth.PhoneCredentialController
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_AVATAR_URL
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_FIREBASE_UID
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_ID
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.SPEC_SYMBOL
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn

@InternalCoroutinesApi
internal class CredentialManager(
    private val accountManager: AccountManager,
    private val accountType: String,
    private val accountPassword: String = "",
    private val authService: AuthService,
    private val profileService: ProfileService,
    appContext: Context,
    parentJob: Job? = null
): FirebaseCredentialsManager(appContext, parentJob) {

    override fun getCurrentUser(): FirebaseUserController {
        return accountManager.getAccountsByType(accountType).firstOrNull { account ->
            accountManager.getUserData(account, ACCOUNT_FIREBASE_UID) == firebaseInstance.currentUser?.uid
        }?.let {
            UserControllerImpl(firebaseInstance, accountManager, it, profileService)
        } ?: firebaseInstance.currentUser?.let { user ->
            val name = if (user.displayName.isNullOrEmpty()) "*" else user.displayName

            val account = Account(name, accountType).also {
                accountManager.addAccountExplicitly(it, accountPassword, Bundle().apply {
                    putString(ACCOUNT_ID, user.uid)
                    putString(ACCOUNT_FIREBASE_UID, user.uid)
                    putString(ACCOUNT_AVATAR_URL, user.photoUrl?.toString() ?: "")
                })
            }
            UserControllerImpl(firebaseInstance, accountManager, account, profileService)
        } ?: UnregisteredUserControllerImpl(firebaseInstance)
    }

    override suspend fun onLogged(credentialResult: CredentialResult): FirebaseUserController {
        val loginResponse = authService.login(credentialResult).body()
        return super.onLogged(credentialResult).apply {
            loginResponse?.let { loginResponse ->
                val profileName = profile.displayName ?: SPEC_SYMBOL
                Account(profileName, accountType).also {
                    accountManager.addAccountExplicitly(it, accountPassword, loginResponse.toBundle(firebaseInstance.currentUser?.uid))
                }
            }
            updateProfileAccount(profile).launchIn(this@CredentialManager)
        }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: FirebaseUserController) {
        authService.addCreds(user.getAccessToken(), credentialResult.providerId)
        user.reloadCredentials()
    }

    override suspend fun onCredentialRemoved(credential: IBaseCredential, user: FirebaseUserController) {
        authService.removeCreds(user.getAccessToken(), credential.providerId.replace(".com", ""))
        user.reloadCredentials()
    }

    override suspend fun onUserLogout(user: FirebaseUserController): FirebaseUserController {
        authService.logout(user.getAccessToken())
        if(user is UserControllerImpl)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(user.account)
            }
        return super.onUserLogout(user)
    }

    override suspend fun onUserDelete(user: FirebaseUserController): FirebaseUserController {
        authService.delete(user.getAccessToken())
        return super.onUserDelete(user)
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