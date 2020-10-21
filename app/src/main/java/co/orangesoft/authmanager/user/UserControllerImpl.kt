package co.orangesoft.authmanager.user

import android.net.Uri
import android.util.Log
import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.ProfileService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class UserControllerImpl(
    private val profileService: ProfileService,
    private val firebaseInstance: FirebaseAuth
): UserController, CoroutineScope {

    private val TAG = "UserControllerImpl"
    override val coroutineContext: CoroutineContext = Dispatchers.IO


    override val profile: Profile? by lazy {
        firebaseInstance.currentUser?.let {
            Profile(it.uid, it.displayName, it.phoneNumber)
        }
    }

    //TODO ask how get settings, get with profile? and where should stored
    override val settings: Settings by lazy {
        Settings("customSetting1", "customSetting2")
    }

    override suspend fun update() {
        getAccessToken {
            try {
                val response = profileService.patchProfile(
                    it,
                    UpdateProfileRequest(profile?.name, profile?.birthday)
                )

                if (response.isSuccessful) {
                    updateAccount()
                } else {
                    Log.e(TAG, response.message())
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
        }
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        val body = file.asRequestBody("image/*".toMediaTypeOrNull())
        profile?.avatarUrl = file.absolutePath

        getAccessToken {
            try {
                val response = profileService.postProfileAvatar(it, body)

                if (response.isSuccessful) {
                    profile?.avatarUrl = response.body()?.avatarUrl
                    updateAccount()
                    listener(null)
                } else {
                    val errorMsg = response.message()
                    Log.e(TAG, errorMsg)
                    profile?.avatarUrl = null
                    listener(Throwable(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                profile?.avatarUrl = null
                listener(e)
            }
        }
    }

    private fun updateAccount() {
        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder().also {
                it.displayName  =  profile?.name
                it.photoUri     =  Uri.parse(profile?.avatarUrl ?: "")
            }.build()).addOnSuccessListener {
                firebaseInstance.updateCurrentUser(this)
            }
        }
    }

    override suspend fun refresh() {
        getAccessToken { token ->
            val response = profileService.getProfile(token)

            response.body()?.apply {
                profile?.let {
                    it.name = name
                    it.phoneNumber = phoneNumber
                    it.avatarUrl = avatarUrl
                    it.birthday = birthday
                }
                updateAccount()
            }
        }
    }

    override suspend fun getAccessToken(listener: suspend (String) -> Unit) {
        firebaseInstance.currentUser?.getIdToken(true)?.addOnCompleteListener {
            launch {
                if  (it.isSuccessful) {
                    listener.invoke(it.result?.token ?: "")
                } else {
                    Log.e(TAG, "Cannot get access token")
                }
            }
        }
    }
}