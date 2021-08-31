package by.orangesoft.auth

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AuthListener<T: BaseUserController<*>>(private var lifecycleOwner: LifecycleOwner? = null, override val coroutineContext: CoroutineContext = Dispatchers.Main): CoroutineScope,
    LifecycleObserver {

    constructor(unit: AuthListener<T>.()-> Unit):this() {
        apply(unit)
    }

    constructor(lifecycleOwner: LifecycleOwner, unit: AuthListener<T>.()-> Unit):this(lifecycleOwner) {
        apply(unit)
    }

    constructor(coroutineContext: CoroutineContext, unit: AuthListener<T>.()-> Unit):this(null, coroutineContext) {
        apply(unit)
    }

    private var onSuccess: ((T) -> Unit)? = null
    private var onException: ((Throwable) -> Unit)? = null

    init {
        lifecycleOwner?.lifecycle?.apply {
           addObserver(this@AuthListener)
        }
    }

    fun onAuthSuccess(listener: (T) -> Unit) {
        onSuccess = listener
    }

    fun onAuthException(listener: (Throwable) -> Unit) {
        onException = listener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected fun onDestroy(source: LifecycleOwner){
        source.lifecycle.removeObserver(this)
        lifecycleOwner = null
        synchronized(this) {
            onSuccess = null
            onException = null
        }
    }

    operator fun invoke(result: T) {
        launch {
            synchronized(this@AuthListener) {
                onSuccess?.invoke(result)
            }
        }
    }

    operator fun invoke(result: Throwable) {
        launch {
            synchronized(this@AuthListener) {
                onException?.invoke(result)
            }
        }
    }
}