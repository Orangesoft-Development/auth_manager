package co.orangesoft.authmanager.firebase_auth.user

import android.accounts.Account
import android.accounts.AccountManager
import by.orangesoft.auth.firebase.FirebaseProfile
import by.orangesoft.auth.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.ProfileService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import kotlin.jvm.Throws

class UserControllerImpl(
    firebaseInstance: FirebaseAuth,
    private val accountManager: AccountManager,
    val account: Account,
    private val profileService: ProfileService
) : FirebaseUserController(firebaseInstance) {

    override val profile: Profile by lazy {
        Profile(
            id = accountManager.getUserData(account, "id"),
            name = if (account.name == "*") null else account.name,
            avatarUrl = accountManager.getUserData(account, "avatarUrl"),
            birthday = accountManager.getUserData(account, "birthday"),
            country = accountManager.getUserData(account, "country"),
            city = accountManager.getUserData(account, "city")
        )
    }

    @Throws(Throwable::class)
    override suspend fun reload() {
        profileService.getProfile(getAccessToken()).apply {
            updateProfile(this)
        }
    }

    @Throws(Throwable::class)
    override suspend fun updateAvatar(file: File) {
        profileService.postProfileAvatar(
            getAccessToken(),
            file.asRequestBody("image/*".toMediaTypeOrNull())
        ).apply {
            updateProfile(this)
        }
    }


    @Throws(Throwable::class)
    override suspend fun updateAccount(profile: FirebaseProfile) {
        if (profile is Profile) {
            profileService.patchProfile(getAccessToken(), profile).apply {
                updateProfile(this)
            }
        }
    }

    override suspend fun setAccessToken(accessToken: String) {
        super.setAccessToken(accessToken)
        accountManager.setAuthToken(account, "access", accessToken)
    }

    private suspend fun updateProfile(response: Response<Profile>) {
        val newProfile = response.body()
        if (response.isSuccessful && newProfile != null) {
            profile.apply {
                name = newProfile.name
                avatarUrl = newProfile.avatarUrl
                birthday = newProfile.birthday
                country = newProfile.country
                city = newProfile.city
                avatarUrl = newProfile.avatarUrl
            }
            super.updateAccount(newProfile)
        } else {
            throw Exception("Updating profile failed")
        }
    }
}