package co.orangesoft.authmanager.user

import android.net.Uri
import android.util.Log
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.ProfileService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class UserControllerImpl(
    firebaseInstance: FirebaseAuth,
    private val profileService: ProfileService
) : FirebaseUserController<Profile>(firebaseInstance), CoroutineScope {

    private val TAG = "UserControllerImpl"

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override var profile: Profile? = firebaseInstance.currentUser?.let {
        Profile(it.uid, it.displayName, it.phoneNumber)
    }

    override suspend fun update() {
        profile?.let {
            try {
                val response = profileService.patchProfile(getAccessToken(), it)

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

        try {
            val response = profileService.postProfileAvatar(getAccessToken(), body)

            if (response.isSuccessful) {
                profile?.avatarUrl = response.body()?.avatarUrl
                updateAccount()
                listener(null)
            } else {
                profile?.avatarUrl = null
                listener(Throwable(response.message()))
            }
        } catch (e: Exception) {
            profile?.avatarUrl = null
            listener(e)
        }
    }

    override suspend fun refresh() {
        try {
            val response = profileService.getProfile(getAccessToken())

            if (response.isSuccessful) {
                profile = response.body()
                updateAccount()
            } else {
                Log.e(TAG, response.message())
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    private fun updateAccount() {
        updateAccount {
            it.displayName = profile?.name
            it.photoUri = Uri.parse(profile?.avatarUrl ?: "")
        }
    }
}