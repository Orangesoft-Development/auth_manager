package co.orangesoft.authmanager.firebase_auth

import android.util.Log
import retrofit2.Response
import java.lang.Exception
import kotlin.reflect.KCallable

class RequestWatcher<T> {
    private lateinit var onSuccess: suspend (T) -> Unit
    private var onError: ((Throwable)-> Unit)? = null

    fun onSuccess(listener: suspend (T) -> Unit){
        onSuccess = listener
    }

    fun onError(listener: ((Throwable)-> Unit)? = null){
        onError = listener
    }

    suspend operator fun invoke(result: T) {
        onSuccess.invoke(result)
    }

    operator fun invoke(result: Throwable) {
        onError?.invoke(result)
    }
}


suspend fun <T> KCallable<Response<T>>.parseResponse(vararg args: Any?, watcher: RequestWatcher<T>.()-> Unit) {

    val watch = RequestWatcher<T>().apply(watcher)
    try {
        val result = this.call(args)
        if (result.isSuccessful)
            watch(result.body()!!)
        else {
            val exception = Throwable(result.errorBody()?.string() ?: "Error execute")
            Log.e(this.name, "Error execute request", exception)
            watch(exception)
        }
    } catch (e: Exception){
        Log.e(this.name, "Error execute request", e)
        watch(e)
    }
}