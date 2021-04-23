package co.orangesoft.authmanager.firebase_auth.user

import by.orangesoft.auth.firebase.FirebaseProfile
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.ProfileService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws

class UserControllerImpl(
    firebaseInstance: FirebaseAuth,
    private val profileService: ProfileService
) : FirebaseUserController(firebaseInstance), CoroutineScope {

    companion object {
        const val TAG = "UserControllerImpl"
    }

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    @Throws(Throwable::class)
    override suspend fun saveChanges() {
        (profile as? Profile)?.let { profile ->
            profileService.patchProfile(getAccessToken(), profile).apply {
                val newProfile = body()
                if (isSuccessful && newProfile != null) {
                    super.updateAccount(newProfile)
                }
            }
        }
    }

    @Throws(Throwable::class)
    override suspend fun updateAvatar(file: File) {
        (profile as? Profile)?.let { profile ->
            profileService.postProfileAvatar(getAccessToken(), file.asRequestBody("image/*".toMediaTypeOrNull()))
            super.updateAvatar(file)
        }
    }

    @Throws(Throwable::class)
    override suspend fun reload() {
        profileService.getProfile(getAccessToken()).apply {
            val newProfile = body()
            if (isSuccessful && newProfile != null) {
                super.updateAccount(newProfile)
            }
        }
    }

    @Throws(Throwable::class)
    override suspend fun updateAccount(profile: FirebaseProfile) {
        (profile as? Profile)?.let {
            profileService.patchProfile(getAccessToken(), it).apply {
                val newProfile = body()
                if (isSuccessful && newProfile != null) {
                    super.updateAccount(newProfile)
                }
            }
        }
    }
}