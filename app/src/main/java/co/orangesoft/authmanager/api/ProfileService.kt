package co.orangesoft.authmanager.api

import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.response.ProfileResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {
    @GET( "/account")
    suspend fun getProfile(@Header("Authorization") accessToken: String): Response<ProfileResponse>

    @PATCH( "/account")
    suspend fun patchProfile(@Header("Authorization") accessToken: String, @Body body: UpdateProfileRequest): Response<ProfileResponse>

    @POST("/account/avatar")
    suspend fun postProfileAvatar(@Header("Authorization") accessToken: String, @Body body: RequestBody): Response<ProfileResponse>
}