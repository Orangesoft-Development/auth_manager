package co.orangesoft.authmanager.auth

import android.content.Context
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredential
import by.orangesoft.auth.user.IBaseUserController
import by.orangesoft.auth.user.ITokenController
import co.orangesoft.authmanager.api.ProfileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.CoroutineContext

class SimpleUserController(private val appContext: Context? = null,
                           private val profileService: ProfileService? = null) : IBaseUserController<SimpleProfile>,
     ITokenController, CoroutineScope {

    companion object {
        const val TAG = "SimpleUserControllerImpl"
    }

    private val prefsHelper by lazy { PrefsHelper(appContext) }

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private val _credentials: MutableStateFlow<Collection<CredentialResult>> by lazy {
        MutableStateFlow(prefsHelper.getCredentials())
    }

    override suspend fun getAccessToken(): String {
        return prefsHelper.getToken()
    }

    override suspend fun setAccessToken(accessToken: String) {
        prefsHelper.saveToken(accessToken)
    }

    override val credentials: StateFlow<Collection<CredentialResult>> by lazy {
        _credentials.asStateFlow()
    }

    override val profile: SimpleProfile = prefsHelper.getProfile() ?: SimpleProfile("")

    override suspend fun reload() {
        profileService?.let {
            it.getSimpleProfile(getAccessToken()).apply {
                val profile = body()
                if (isSuccessful && profile != null) {
                    prefsHelper.saveProfile(profile)
                }
            }
        }
    }

    override suspend fun updateAvatar(file: File) {
        (profile as? SimpleProfile)?.let { profile ->
            profileService?.postSimpleProfileAvatar(getAccessToken(), file.asRequestBody("image/*".toMediaTypeOrNull()))
                ?.apply {
                    val simpleProfile = body()
                    if (isSuccessful && simpleProfile != null) {
                        prefsHelper.saveProfile(simpleProfile)
                    }
                }
        }
    }

    override suspend fun updateAccount(profile: SimpleProfile) {
        (profile as? SimpleProfile)?.let {
            profileService?.patchSimpleProfile(getAccessToken(), it).apply {
                prefsHelper.saveProfile(SimpleProfile(""))
            }
        }
    }
}