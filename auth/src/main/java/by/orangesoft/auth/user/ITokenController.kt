package by.orangesoft.auth.user

interface ITokenController {
    suspend fun getAccessToken(): String

    suspend fun setAccessToken(accessToken: String)
}