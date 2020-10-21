package co.orangesoft.authmanager.api

import co.orangesoft.authmanager.api.request.UpdateProfileRequest
import co.orangesoft.authmanager.api.response.ProfileResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {
    @GET( "/account")
    suspend fun getProfile(@Header(AuthService.AUTH_HEADER) accessToken: String): Response<ProfileResponse>

    @PATCH( "/account")
    suspend fun patchProfile(@Header(AuthService.AUTH_HEADER) accessToken: String, @Body body: UpdateProfileRequest): Response<ProfileResponse>

    @POST("/account/avatar")
    suspend fun postProfileAvatar(@Header(AuthService.AUTH_HEADER) accessToken: String, @Body body: RequestBody): Response<ProfileResponse>
}