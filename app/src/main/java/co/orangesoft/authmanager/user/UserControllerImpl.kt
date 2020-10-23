package co.orangesoft.authmanager.user

import android.net.Uri
import android.util.Log
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.ProfileService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class UserControllerImpl(
    private val profileService: ProfileService,
    firebaseInstance: FirebaseAuth
) : FirebaseUserController<Profile>(firebaseInstance), CoroutineScope {

    private val TAG = "UserControllerImpl"
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override val profile: Profile? by lazy {
        firebaseInstance.currentUser?.let {
            Profile(it.uid, it.displayName, it.phoneNumber)
        }
    }

    //TODO ask how get settings, get with profile? and should stored in prefs
    override val settings: Settings by lazy {
        Settings("customSetting1", "customSetting2")
    }

    override suspend fun update() {
        try {
            val response = profileService.patchProfile(
                getAccessToken(),
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

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        val body = file.asRequestBody("image/*".toMediaTypeOrNull())
        profile?.avatarUrl = file.absolutePath

        try {
            val response = profileService.postProfileAvatar(getAccessToken(), body)

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

    private fun updateAccount() {
        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder().also {
                it.displayName = profile?.name
                it.photoUri = Uri.parse(profile?.avatarUrl ?: "")
            }.build()).addOnSuccessListener {
                firebaseInstance.updateCurrentUser(this)
            }
        }
    }

    override suspend fun refresh() {
        val response = profileService.getProfile(getAccessToken())

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