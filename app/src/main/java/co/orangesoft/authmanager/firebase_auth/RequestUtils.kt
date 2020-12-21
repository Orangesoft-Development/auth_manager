package co.orangesoft.authmanager.firebase_auth

import android.util.Log
import retrofit2.Response
import java.lang.Exception
import kotlin.jvm.Throws
import kotlin.reflect.KCallable

@Throws(Throwable::class)
suspend fun <T> KCallable<Response<T>>.parseResponse(vararg args: Any?): T =
    try {
        val result = this.call(args)
        val resultBody = result.body()
        if (result.isSuccessful && resultBody != null) {
            resultBody
        } else {
            val exception = Throwable(result.errorBody()?.toString() ?: "Error execute")
            Log.e(this.name, "Error execute request", exception)
            throw exception
        }
    } catch (e: Exception){
        Log.e(this.name, "Error execute request", e)
        throw e
    }
