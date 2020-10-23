package co.orangesoft.authmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.orangesoft.auth.BaseAuthManager
import by.orangesoft.auth.credentials.BaseCredentialsManager
import by.orangesoft.auth.credentials.firebase.FirebaseCredentialsManager
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.provideAuthService
import co.orangesoft.authmanager.api.provideOkHttp
import co.orangesoft.authmanager.api.provideProfileService
import co.orangesoft.authmanager.user.Profile

class AuthManager private constructor(credManager: BaseCredentialsManager<FirebaseUserController<Profile>, FirebaseCredentialsManager.FirebaseCredential>)
    : BaseAuthManager<FirebaseUserController<Profile>, FirebaseCredentialsManager.FirebaseCredential>(credManager) {

    enum class UserStatus {
        REGISTERED,
        UNREGISTERED
    }

    companion object {
        fun getInstance(): AuthManager {
            return AuthManager(
                CredentialManager(
                    provideAuthService("http://github.com", provideOkHttp(arrayListOf())),
                    provideProfileService("http://github.com", provideOkHttp(arrayListOf()))
                )
            )
        }
    }

    val userStatus: LiveData<UserStatus> by lazy {
        MutableLiveData<UserStatus>().apply { postValue(
            UserStatus.UNREGISTERED
        ) }
    }


    init {
        currentUser.value
        userCredentials.observeForever { creds ->
            if(creds.isEmpty() || (creds.size == 1 && creds.first().providerId == "firebase"))
                (userStatus as MutableLiveData).postValue(UserStatus.UNREGISTERED)
            else
                (userStatus as MutableLiveData).postValue(UserStatus.REGISTERED)
        }
    }
}