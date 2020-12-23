package co.orangesoft.authmanager.api

import by.orangesoft.auth.credentials.CredentialResult
import co.orangesoft.authmanager.firebase_auth.user.Profile
import co.orangesoft.authmanager.phone_auth.user.PhoneProfile
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

    @POST("/auth/custom-token")
    suspend fun createPhoneToken(@Query("phone") phone: String, @Query("code") code: String): Response<String>


    //TODO Profile and PhoneProfile should extends the same class?

    @POST("/auth/login")
    suspend fun phoneLogin(@Query("phone") phone: String, @Query("password") password: String): Response<PhoneProfile>

    @POST("/account/auth-credentials")
    suspend fun addPhoneCreds(@Header("Authorization") accessToken: String, @Query("methodId") methodId: String?): Response<PhoneProfile>

    @DELETE("/account/auth-credentials/{method}")
    suspend fun removePhoneCreds(@Header("Authorization") accessToken: String, @Path(value = "methodId") methodId: String?): Response<PhoneProfile>

}