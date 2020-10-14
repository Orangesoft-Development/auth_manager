package co.orangesoft.authmanager

import android.accounts.Account
import android.accounts.AccountManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.credentials.CredentialController
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.*
import co.orangesoft.authmanager.api.ApiCredentials
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.request.LoginRequest
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.api.response.ApiProfile
import co.orangesoft.authmanager.credential.PhoneCredential
import co.orangesoft.authmanager.credential.PhoneCredentialController
import co.orangesoft.authmanager.user.SVBaseUserController
import co.orangesoft.authmanager.user.SVUnregisteredUserController
import co.orangesoft.authmanager.user.SVUserController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch
import java.lang.Exception

internal class SVCredentialManager(
    private val accountManager: AccountManager,
    private val authService: AuthService,
    private val profileService: ProfileService,
    private val accountType: String,
    private val accountPassword: String = ""
): FirebaseCredentialsManager<SVBaseUserController>() {

    override fun getLoggedUser(): SVBaseUserController? =
        accountManager.getAccountsByType(accountType).firstOrNull { account ->
            accountManager.getUserData(account, "firebaseUid") == firebaseInstance.currentUser?.uid
        }?.let {
            SVUserController(accountManager, it, profileService, firebaseInstance)
        } ?: firebaseInstance.currentUser?.let { user ->
                var name = user.displayName ?:
                           user.providerData.firstOrNull { it.displayName?.isNotBlank() == true } ?.displayName ?:
                           user.providerData.firstOrNull { it.phoneNumber?.isNotBlank() == true || it.email?.isNotBlank() == true }.let { it?.phoneNumber ?: it?.email }

                if(name.isNullOrBlank())
                    name = "Unknown"

                val account = Account(name, accountType).also {
                    accountManager.addAccountExplicitly(it, accountPassword, Bundle().apply {
                        putString("firebaseUid", user.uid)
                        putString("id", user.uid)
                        putString("avatarUrl", user.photoUrl.toString())
                    })
                }
                SVUserController(accountManager, account, profileService, firebaseInstance)
            } ?: SVUnregisteredUserController()

    override suspend fun onLogged(credentialResult: CredentialResult): SVBaseUserController {
        try {
            val profile = authService.login(LoginRequest(ApiCredentials.fromCredentialResult(credentialResult)))
            updateFirebaseUser(firebaseInstance.currentUser!!, profile.profile)

            val profileName = profile.profile.name ?: profile.profile.credentials.firstOrNull { it.name.isNotBlank() }?.name ?: firebaseInstance.currentUser!!.displayName!!
            Account(profileName, accountType).also {
                accountManager.addAccountExplicitly(it, accountPassword, profile.toBundle(firebaseInstance.currentUser?.uid))
                accountManager.setAuthToken(it, "refresh", profile.tokens.refreshToken)
                accountManager.setAuthToken(it, "access", profile.tokens.accessToken)
            }

            return createUserController(credentialResult, firebaseInstance)
        } catch (e: Exception){
            firebaseInstance.signOut()
            throw e
        }
    }

    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: SVBaseUserController) {
        val profile = authService.addCreds("Bearer ${user.getAccessToken()}", ApiCredentials.fromCredentialResult(credentialResult))
        (credentials as MutableLiveData).postValue(getCredentials())
    }

    override suspend fun onCredentialRemoved(credential: FirebaseCredential, user: SVBaseUserController) {
        val profile = authService.removeCreds("Bearer ${user.getAccessToken()}", credential.providerId.replace(".com",""))
        (credentials as MutableLiveData).postValue(getCredentials())
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun logout(user: SVBaseUserController) {
        launch {
            try { authService.logout("Bearer ${user.getAccessToken()}") } catch (e: Exception) {}
            firebaseInstance.signOut()
            if(user is SVUserController)
                accountManager.removeAccountExplicitly(user.account)

            listener!!.invoke(getLoggedUser()!!)
            (credentials as MutableLiveData).postValue(getCredentials())
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun deleteUser(user: SVBaseUserController) {

        launch {
            try {
                if(user is SVUserController) {
                    authService.delete("Bearer ${user.getAccessToken()}")
                    accountManager.removeAccountExplicitly(user.account)
                }

                firebaseInstance.currentUser?.apply {
                    firebaseInstance.signOut()
                    delete()
                }
                (credentials as MutableLiveData).postValue(getCredentials())
                listener!!.invoke(getLoggedUser()!!)
            } catch (e: Exception) {
                listener!!.invoke(e)
            }
        }
    }

    private fun updateFirebaseUser(user: FirebaseUser, apiUser: ApiProfile){
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .apply {
                    displayName = apiUser.name
                    photoUri = Uri.parse(apiUser.avatarUrl ?: "")
                }.build()
        ).addOnFailureListener { Log.e(TAG, "Unable update firebase profile", it) }

        (credentials as MutableLiveData).postValue(getCredentials())
    }

    override fun createUserController(credentialResult: CredentialResult, firebaseInstance: FirebaseAuth): SVBaseUserController = getLoggedUser()!!


    override fun getBuilder(method: AuthMethod): Builder =
        SVCredBuilder(
            authService,
            method
        )
    override fun getBuilder(credential: FirebaseCredential): Builder =
        SVCredBuilder(
            authService,
            when (credential.providerId) {
                PhoneCredential("", "").providerId -> PhoneCredential("", "")
                Firebase.Apple.providerId -> Firebase.Apple
                Firebase.Facebook.providerId -> Firebase.Facebook
                Firebase.Google("").providerId -> Firebase.Google("")
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        )

    class SVCredBuilder(val authService: AuthService, method: AuthMethod): FirebaseCredentialsManager.CredBuilder(method) {

        override fun createCredential(method: AuthMethod): CredentialController =
            when (method) {
                is PhoneCredential  -> PhoneCredentialController(authService, method)
                else                -> super.createCredential(method)
            }

    }
}