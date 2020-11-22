package by.orangesoft.auth.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

internal object UserProvider {
    val currentUser: LiveData<out BaseUserController<*>> = MutableLiveData()
}