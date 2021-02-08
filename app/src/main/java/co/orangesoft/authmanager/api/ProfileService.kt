package co.orangesoft.authmanager.api

import co.orangesoft.authmanager.auth.SimpleProfile
import co.orangesoft.authmanager.firebase_auth.user.Profile
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {
    @GET( "account")
    suspend fun getProfile(@Header("Authorization") accessToken: String): Response<Profile>

    @PATCH( "account")
    suspend fun patchProfile(@Header("Authorization") accessToken: String, @Body body: Profile): Response<Profile>

    @POST("account/avatar")
    suspend fun postProfileAvatar(@Header("Authorization") accessToken: String, @Body body: RequestBody): Response<Profile>

    @GET( "account")
    suspend fun getSimpleProfile(@Header("Authorization") accessToken: String): Response<SimpleProfile>

    @PATCH( "account")
    suspend fun patchSimpleProfile(@Header("Authorization") accessToken: String, @Body body: SimpleProfile): Response<SimpleProfile>

    @POST("account/avatar")
    suspend fun postSimpleProfileAvatar(@Header("Authorization") accessToken: String, @Body body: RequestBody): Response<SimpleProfile>
}