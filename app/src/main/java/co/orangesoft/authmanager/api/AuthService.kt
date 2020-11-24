package co.orangesoft.authmanager.api

import by.orangesoft.auth.credentials.CredentialResult
import co.orangesoft.authmanager.firebase_auth.user.Profile
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("/auth/login")
    suspend fun login(@Body credentialResult: CredentialResult): Response<Profile>

    @POST("/auth/logout")
    suspend fun logout(@Header("Authorization") accessToken: String): Response<Unit>

    @DELETE("/account")
    suspend fun delete(@Header("Authorization") accessToken: String): Response<Unit>

    @POST("/account/auth-credentials")
    suspend fun addCreds(@Header("Authorization") accessToken: String, @Query("methodId") methodId: String?): Response<Profile>

    @DELETE("/account/auth-credentials/{method}")
    suspend fun removeCreds(@Header("Authorization") accessToken: String, @Path(value = "methodId") methodId: String?): Response<Profile>
}