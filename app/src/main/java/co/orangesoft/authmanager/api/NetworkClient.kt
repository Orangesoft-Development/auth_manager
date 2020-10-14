package co.orangesoft.authmanager.api

import android.util.Log
import androidx.lifecycle.LiveData
import co.orangesoft.authmanager.user.SVBaseUserController
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun provideOkHttp(interceptors: List<Interceptor>): OkHttpClient {
    val builder = OkHttpClient.Builder()
    interceptors.forEach { builder.addInterceptor(it) }

    builder.addInterceptor(
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("OkHttp", message)
                }
            }).apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
    return builder.build()
}

fun provideAuthenticator(clientManager: Interceptor, baseUrl: String, userController: LiveData<SVBaseUserController>): co.orangesoft.authmanager.TokenManager {
    return co.orangesoft.authmanager.TokenManager(
        clientManager,
        userController,
        baseUrl
    )
}

fun provideMoshi(): Moshi {
    return Moshi.Builder()
        .add(AuthCredentialsAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()
}

class AuthCredentialsAdapter {

    private val defaultMoshi = Moshi.Builder().build()

    @FromJson
    fun fromJson(map: Map<String, @JvmSuppressWildcards Any>): ApiCredentials? {
        val itemJson = defaultMoshi.adapter(Map::class.java).toJson(map)
        return when (map["method"] as String) {
            "phone"    -> defaultMoshi.adapter(ApiCredentials.Phone::class.java).fromJson(itemJson)
            "google"   -> defaultMoshi.adapter(ApiCredentials.Social.Google::class.java).fromJson(itemJson)
            "facebook" -> defaultMoshi.adapter(ApiCredentials.Social.Facebook::class.java).fromJson(itemJson)
            "apple"    -> defaultMoshi.adapter(ApiCredentials.Social.Apple::class.java).fromJson(itemJson)
            "firebase" -> {
                when(map["social"] as String){
                    "google"   -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Google::class.java).fromJson(itemJson)
                    "facebook" -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Facebook::class.java).fromJson(itemJson)
                    "apple"    -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Apple::class.java).fromJson(itemJson)
                    "phone"    -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Phone::class.java).fromJson(itemJson)
                    else       -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Anonimus::class.java).fromJson(itemJson)
                }
            }
            else       -> defaultMoshi.adapter(ApiCredentials::class.java).fromJson(itemJson)
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: ApiCredentials) {

        val result = when (value) {
            is ApiCredentials.Phone                -> defaultMoshi.adapter(ApiCredentials.Phone::class.java).toJsonValue(value)
            is ApiCredentials.Social.Google        -> defaultMoshi.adapter(ApiCredentials.Social.Google::class.java).toJsonValue(value)
            is ApiCredentials.Social.Facebook      -> defaultMoshi.adapter(ApiCredentials.Social.Facebook::class.java).toJsonValue(value)
            is ApiCredentials.Social.Apple         -> defaultMoshi.adapter(ApiCredentials.Social.Apple::class.java).toJsonValue(value)
            is ApiCredentials.ApiFirebase.Google   -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Google::class.java).toJsonValue(value)
            is ApiCredentials.ApiFirebase.Facebook -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Facebook::class.java).toJsonValue(value)
            is ApiCredentials.ApiFirebase.Apple    -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Apple::class.java).toJsonValue(value)
            is ApiCredentials.ApiFirebase.Anonimus -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Anonimus::class.java).toJsonValue(value)
            is ApiCredentials.ApiFirebase.Phone    -> defaultMoshi.adapter(ApiCredentials.ApiFirebase.Phone::class.java).toJsonValue(value)
            else                                   -> defaultMoshi.adapter(ApiCredentials::class.java).toJsonValue(value)
        } as (Map<String, Any>)

        val adapter = defaultMoshi.adapter(Object::class.java)

        writer.beginObject()?.apply {
            result.keys.forEach {
                name(it)
                adapter.toJson(writer, result.getValue(it) as Object)
            }
            endObject()
        }
    }
}

fun provideAuthService(baseUrl: String, okHttpClient: OkHttpClient): AuthService {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
        .client(okHttpClient)
        .build()
        .create(AuthService::class.java)
}

internal fun provideProfileService(baseUrl: String, okHttpClient: OkHttpClient): ProfileService {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
        .client(okHttpClient)
        .build()
        .create(ProfileService::class.java)
}