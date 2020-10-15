package co.orangesoft.authmanager.user

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_AUTHTOKEN
import android.net.Uri
import android.os.Bundle
import android.util.Log
import co.orangesoft.authmanager.TokenManager
import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.api.response.ApiProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserControllerImpl(
    private val accountManager: AccountManager,
    val account: Account,
    private val profileService: ProfileService,
    private val firebaseInstance: FirebaseAuth
): UserController {

    private val TAG = "UserControllerImpl"

    private var _accessToken: String = ""
    private var _refreshToken: String = ""

    override val profile: Profile by lazy {
        accountManager.getAuthToken(account, "access", Bundle.EMPTY, false, { _accessToken = it.result.getString(KEY_AUTHTOKEN) }, null)
        accountManager.getAuthToken(account, "refresh", Bundle.EMPTY, false, { _refreshToken = it.result.getString(KEY_AUTHTOKEN) }, null)

        Profile(
            accountManager.getUserData(account, "id"),
            account.name,
            accountManager.getUserData(account, "avatarUrl"),
            accountManager.getUserData(account, "birthday")
        )
    }

    override val settings: Settings by lazy {
        Settings(
            accountManager.getUserData(account, "customSetting1"),
            accountManager.getUserData(account, "customSetting2")
        )
    }

    override fun update() {
        profileService.patchProfile(getAuthHeader(), UpdateProfileRequest(profile.name, profile.birthday))
            .enqueue(object: Callback<ApiProfile> {
                override fun onResponse(call: Call<ApiProfile>, response: Response<ApiProfile>) {
                    if (response.isSuccessful) {
                        updateAccount()
                    } else {
                        Log.e(TAG, response.errorBody()?.string())
                    }
                }

                override fun onFailure(call: Call<ApiProfile>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    override fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        val body = file.asRequestBody("image/*".toMediaTypeOrNull())
        profile.avatarUrl = file.absolutePath
        profileService.postProfileAvatar(getAuthHeader(), body).enqueue(object : Callback<ApiProfile> {
            override fun onResponse(call: Call<ApiProfile>, response: Response<ApiProfile>) {
                if (response.isSuccessful) {
                    profile.avatarUrl = response.body()?.avatarUrl
                    updateAccount()
                    listener(null)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, error)
                    profile.avatarUrl = null
                    listener(Throwable(error))
                }
            }

            override fun onFailure(call: Call<ApiProfile>, t: Throwable) {
                t.printStackTrace()
                profile.avatarUrl = null
                listener(t)
            }
        })
    }

    private fun updateAccount(){
        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder().also {
                it.displayName  =  profile.name
                it.photoUri     =  Uri.parse(profile.avatarUrl ?: "")
            }.build()).addOnSuccessListener {
                firebaseInstance.updateCurrentUser(this)
            }
        }

        if(profile.name != account.name)
            accountManager.renameAccount(account, profile.name, null, null)

        accountManager.setUserData(account, "avatarUrl", profile.avatarUrl)
        accountManager.setUserData(account, "birthday", profile.birthday)
    }

    override fun refresh() {
        profileService.getProfile(getAuthHeader()).enqueue(object: Callback<ApiProfile>{
            override fun onResponse(call: Call<ApiProfile>, response: Response<ApiProfile>) {
                response.body()?.apply {
                    profile.name = name
                    profile.avatarUrl = avatarUrl
                    profile.birthday = birthday
                    updateAccount()
                }
            }
            override fun onFailure(call: Call<ApiProfile>, t: Throwable) {}
        })
    }


    override fun getAccessToken(): String {
        if(_accessToken.isBlank())
            _accessToken = accountManager.getAuthToken(account, "access", Bundle.EMPTY, false, null, null).result?.getString(KEY_AUTHTOKEN) ?: ""

        return _accessToken
    }

    override fun getRefreshToken(): String {
        if(_refreshToken.isBlank())
            _refreshToken = accountManager.getAuthToken(account, "refresh", Bundle.EMPTY, false, null, null).result?.getString(KEY_AUTHTOKEN) ?: ""

        return _refreshToken
    }

    override fun updateAccessToken(token: String) {
        _accessToken = token
        accountManager.setAuthToken(account, "access", _accessToken)
    }

    override fun updateRefreshToken(token: String) {
        _refreshToken = token
        accountManager.setAuthToken(account, "refresh", _refreshToken)
    }

    private fun getAuthHeader(): String {
        return "${TokenManager.BEARER} ${getAccessToken()}"
    }
}