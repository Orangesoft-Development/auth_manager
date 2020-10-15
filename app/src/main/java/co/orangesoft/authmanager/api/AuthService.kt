package co.orangesoft.authmanager.api

import by.orangesoft.auth.credentials.ApiCredentials
import by.orangesoft.auth.credentials.AuthMethod
import co.orangesoft.authmanager.api.response.ApiProfile
import co.orangesoft.authmanager.api.request.LoginRequest
import co.orangesoft.authmanager.api.request.SendSmsRequest
import co.orangesoft.authmanager.api.response.CustomTokenResponse
import co.orangesoft.authmanager.api.response.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthService {

    @POST("/auth/send-sms")
    fun sendSmsCode(@Body body: SendSmsRequest): Call<ResponseBody>

    @POST("/firebase/custom-token")
    suspend fun createPhoneToken(@Body body: ApiCredentials.Phone): CustomTokenResponse

    @POST("/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("/auth/logout")
    suspend fun logout(@Header("Authorization") accessToken: String): Response<Unit>

    @DELETE("/account")
    suspend fun delete(@Header("Authorization") accessToken: String): Response<Unit>

    @POST("/account/auth-credentials")
    suspend fun addCreds(@Header("Authorization") accessToken: String, @Body credentials: ApiCredentials): ApiProfile

    @DELETE("/account/auth-credentials/{method}")
    suspend fun removeCreds(@Header("Authorization") accessToken: String, @Path(value = "method") @AuthMethod method: String): ApiProfile

}