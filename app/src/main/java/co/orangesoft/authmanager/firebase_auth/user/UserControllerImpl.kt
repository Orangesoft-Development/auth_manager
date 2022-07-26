package co.orangesoft.authmanager.firebase_auth.user

import android.accounts.Account
import android.accounts.AccountManager
import by.orangesoft.auth.credentials.BaseAuthCredential
import by.orangesoft.auth.firebase.FirebaseProfile
import by.orangesoft.auth.firebase.FirebaseUserController
import by.orangesoft.auth.user.ITokenController
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_ACCESS_TOKEN
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_AVATAR_URL
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_BIRTHDAY
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_CITY
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_COUNTRY
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_ID
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import kotlin.jvm.Throws

/**
 * Custom implementation of FirebaseUserController with custom profile saved in the account manager
 *
 * @param firebaseInstance FirebaseAuth
 * @param accountManager Provides access to a centralized registry of the user's online accounts
 * @param account Value type that represents an Account in the AccountManager
 * @param profileService Profile service api
 *
 * @see FirebaseAuth
 * @see FirebaseUserController
 * @see AccountManager
 * @see Account
 * @see ProfileService
 *
 */
class UserControllerImpl(
    firebaseInstance: FirebaseAuth,
    private val accountManager: AccountManager,
    val account: Account,
    private val profileService: ProfileService
) : FirebaseUserController(firebaseInstance) {

    override val profile: Profile by lazy {
        Profile(
            id = accountManager.getUserData(account, ACCOUNT_ID),
            name = if (account.name == AccountManagerConst.SPEC_SYMBOL) null else account.name,
            avatarUrl = accountManager.getUserData(account, ACCOUNT_AVATAR_URL),
            birthday = accountManager.getUserData(account, ACCOUNT_BIRTHDAY),
            country = accountManager.getUserData(account, ACCOUNT_COUNTRY),
            city = accountManager.getUserData(account, ACCOUNT_CITY)
        )
    }

    @Throws(Throwable::class)
    override suspend fun reload() {
        profileService.getProfile(getAccessToken()).apply {
            updateProfile(this)
        }
        super.reload()
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
        accountManager.setAuthToken(account, ACCOUNT_ACCESS_TOKEN, accessToken)
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