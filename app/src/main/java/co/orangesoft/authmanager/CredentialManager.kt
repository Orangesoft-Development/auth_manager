package co.orangesoft.authmanager

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.firebase.*
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.request.LoginRequest
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.api.response.ApiProfile
import co.orangesoft.authmanager.user.UnregisteredUserControllerImpl
import co.orangesoft.authmanager.user.UserController
import co.orangesoft.authmanager.user.UserControllerImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch
import java.lang.Exception

internal class CredentialManager(
    private val authService: AuthService,
    private val profileService: ProfileService
): FirebaseCredentialsManager<UserController>() {

    override fun getLoggedUser(): UserController? =
        firebaseInstance.currentUser?.let { user ->
                var name = user.displayName ?:
                           user.providerData.firstOrNull { it.displayName?.isNotBlank() == true } ?.displayName ?:
                           user.providerData.firstOrNull { it.phoneNumber?.isNotBlank() == true || it.email?.isNotBlank() == true }.let { it?.phoneNumber ?: it?.email }

                if (name.isNullOrBlank())
                    name = "Unknown"

                UserControllerImpl(profileService, firebaseInstance)
            } ?: UnregisteredUserControllerImpl()

    override suspend fun onLogged(credentialResult: CredentialResult): UserController {
        try {
            val profile = authService.login(LoginRequest())
            updateFirebaseUser(firebaseInstance.currentUser!!, profile.profile)

            return createUserController(credentialResult, firebaseInstance)
        } catch (e: Exception){
            firebaseInstance.signOut()
            throw e
        }
    }

    //TODO getAccessToken suspend?
    override suspend fun onCredentialAdded(credentialResult: CredentialResult, user: UserController) {
        user.getAccessToken {
            val profile = authService.addCreds(it, credentialResult.method.providerId)
            (credentials as MutableLiveData).postValue(getCredentials())
        }
    }

    override suspend fun onCredentialRemoved(credential: FirebaseCredential, user: UserController) {
      user.getAccessToken {
          val profile = authService.removeCreds(it, credential.providerId.replace(".com",""))
          (credentials as MutableLiveData).postValue(getCredentials())
      }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun logout(user: UserController) {
        launch {
            try {
                //TODO accessToken thread?
                //authService.logout("Bearer ${user.getAccessToken()}")
            } catch (e: Exception) {}
            firebaseInstance.signOut()

            listener!!.invoke(getLoggedUser()!!)
            (credentials as MutableLiveData).postValue(getCredentials())
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun deleteUser(user: UserController) {

        launch {
            try {
                if(user is UserControllerImpl) {
                    user.getAccessToken {
                        authService.delete(it)
                    }
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

    override fun createUserController(credentialResult: CredentialResult, firebaseInstance: FirebaseAuth): UserController = getLoggedUser()!!

    override fun getBuilder(method: AuthMethod): Builder =
        CredBuilder(method)

    override fun getBuilder(credential: FirebaseCredential): Builder =
        CredBuilder(
            when (credential.providerId) {
                Firebase.Apple.providerId -> Firebase.Apple
                Firebase.Facebook.providerId -> Firebase.Facebook
                Firebase.Google("").providerId -> Firebase.Google("")
                else -> throw UnsupportedOperationException("Method $credential is not supported")
            }
        )


}