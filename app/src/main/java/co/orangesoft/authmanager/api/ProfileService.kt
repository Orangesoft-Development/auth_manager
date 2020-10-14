package co.orangesoft.authmanager.api

import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.response.ApiProfile
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileService {

    @GET( "/account")
    fun getProfile(@Header(AUTH_HEADER) accessToken: String): Call<ApiProfile>

    @PATCH( "/account")
    fun patchProfile(@Header(AUTH_HEADER) accessToken: String, @Body body: UpdateProfileRequest): Call<ApiProfile>

    @POST("/account/avatar")
    fun postProfileAvatar(@Header(AUTH_HEADER) accessToken: String, @Body body: RequestBody): Call<ApiProfile>

    companion object {
        const val AUTH_HEADER = "Authorization"
    }
}