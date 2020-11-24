package co.orangesoft.authmanager.firebase_auth.user

import android.net.Uri
import android.util.Log
import by.orangesoft.auth.credentials.firebase.FirebaseProfile
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
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
    firebaseInstance: FirebaseAuth,
    private val profileService: ProfileService
) : FirebaseUserController(firebaseInstance), CoroutineScope {

    override var profile: FirebaseProfile? =
        currentUser?.let {
            Profile(
                it.uid,
                it.providerId,
                it.displayName,
                it.phoneNumber,
                it.photoUrl.toString()
            )
        }

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override suspend fun update() {
        (profile as? Profile)?.let {
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
        (profile as? Profile)?.let {profile ->
            val body = file.asRequestBody("image/*".toMediaTypeOrNull())
            profile.avatarUrl = file.absolutePath

            try {
                val response = profileService.postProfileAvatar(getAccessToken(), body)

                if (response.isSuccessful) {
                    profile.avatarUrl = response.body()?.avatarUrl
                    updateAccount()
                    listener(null)
                } else {
                    profile.avatarUrl = null
                    listener(Throwable(response.message()))
                }
            } catch (e: Exception) {
                profile.avatarUrl = null
                listener(e)
            }
        }
    }

    override suspend fun refresh() {
        try {
            val response = profileService.getProfile(getAccessToken())

            if (response.isSuccessful) {
                updateAccount(response.body())
            } else {
                Log.e(TAG, response.message())
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    override fun updateAccount(firebaseProfile: FirebaseProfile?) {
        firebaseProfile?.let {
            this.profile = it
        }

        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder().also {
                (profile as? Profile)?.let { profile ->
                    it.displayName = profile.name
                    it.photoUri = Uri.parse(profile.avatarUrl ?: "")
                }
            }.build()).addOnSuccessListener {
                firebaseInstance.updateCurrentUser(this)
            }.addOnFailureListener { Log.e(TAG, "Unable update firebase profile", it) }
        }
    }

    companion object {
        private const val TAG = "UserControllerImpl"
    }
}