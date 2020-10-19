package co.orangesoft.authmanager.user

import android.net.Uri
import android.util.Log
import by.orangesoft.auth.user.BaseUserController
import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.ProfileService
import co.orangesoft.authmanager.api.response.ApiProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
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

    //TODO ask how get settings, get with profile?
    override val settings: Settings by lazy {
        Settings("customSetting1", "customSetting2")
    }

    override suspend fun update() {
        getAccessToken {
            profileService.patchProfile(it, UpdateProfileRequest(profile?.name, profile?.birthday))
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
    }

    override suspend fun updateAvatar(file: File, listener: (Throwable?) -> Unit) {
        val body = file.asRequestBody("image/*".toMediaTypeOrNull())
        profile?.avatarUrl = file.absolutePath

        getAccessToken {
            profileService.postProfileAvatar(it, body).enqueue(object : Callback<ApiProfile> {
                override fun onResponse(call: Call<ApiProfile>, response: Response<ApiProfile>) {
                    if (response.isSuccessful) {
                        profile?.avatarUrl = response.body()?.avatarUrl
                        updateAccount()
                        listener(null)
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e(TAG, error)
                        profile?.avatarUrl = null
                        listener(Throwable(error))
                    }
                }

                override fun onFailure(call: Call<ApiProfile>, t: Throwable) {
                    t.printStackTrace()
                    profile?.avatarUrl = null
                    listener(t)
                }
            })
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
            profileService.getProfile(token).enqueue(object: Callback<ApiProfile>{
                override fun onResponse(call: Call<ApiProfile>, response: Response<ApiProfile>) {
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
                override fun onFailure(call: Call<ApiProfile>, t: Throwable) {}
            })
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