package co.orangesoft.authmanager.phone_auth.user

import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.user.IBaseUserController
import by.orangesoft.auth.user.ITokenController
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.parseResponse
import co.orangesoft.authmanager.phone_auth.credentials.PhoneCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.CoroutineContext

open class PhoneUserController(private val profileService: ProfileService? = null) : IBaseUserController<PhoneProfile>, ITokenController, CoroutineScope {

    override val profile: PhoneProfile by lazy {
        PhoneProfile("")
    }

    private val _credentials: MutableStateFlow<Set<PhoneCredential>> by lazy {
        MutableStateFlow(setOf())
    }

    override val credentials: StateFlow<Set<IBaseCredential>>by lazy {
        _credentials.asStateFlow()
    }

    override var accessToken: String = ""


    companion object {
        const val TAG = "PhoneUserController"
    }

    override val coroutineContext: CoroutineContext = Dispatchers.IO


    override suspend fun reload(onError: ((Throwable) -> Unit)?) {
        profileService!!::getProfile.parseResponse(accessToken) {
            onSuccess {  }
            onError(onError)
        }
    }

    override suspend fun updateAvatar(file: File, onError: ((Throwable) -> Unit)?) {
        (profile as? PhoneProfile)?.let { profile ->
            profileService!!::postProfileAvatar.parseResponse(accessToken, file.asRequestBody("image/*".toMediaTypeOrNull())){
                onSuccess { }
                onError(onError)
            }
        }
    }

    override suspend fun updateAccount(profile: PhoneProfile, onError: ((Throwable) -> Unit)?) {
        (profile as? PhoneProfile)?.let {
            profileService!!::patchProfile.parseResponse(accessToken, it){
                onSuccess { result ->  }
                onError(onError)
            }
        }
    }

    override suspend fun saveChanges(onError: ((Throwable) -> Unit)?) {
        (profile as? PhoneProfile)?.let { profile ->
            profileService!!::patchProfile.parseResponse(accessToken, profile) {
                onSuccess {

                }
                onError(onError)
            }
        }
    }

    fun updateCredentials() {
        //TODO what should do here
        //_credentials.value = firebaseInstance.getCredentials()
    }
}