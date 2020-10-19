package co.orangesoft.authmanager.api

import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.response.ApiProfile
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileService {

    @GET( "/account")
    suspend fun getProfile(@Header(AUTH_HEADER) accessToken: String): ApiProfile

    @PATCH( "/account")
    suspend fun patchProfile(@Header(AUTH_HEADER) accessToken: String, @Body body: UpdateProfileRequest): ApiProfile

    @POST("/account/avatar")
    suspend fun postProfileAvatar(@Header(AUTH_HEADER) accessToken: String, @Body body: RequestBody): ApiProfile

    companion object {
        const val AUTH_HEADER = "Authorization"
    }
}