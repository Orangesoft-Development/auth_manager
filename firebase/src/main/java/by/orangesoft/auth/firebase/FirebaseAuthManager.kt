package by.orangesoft.auth.firebase

import by.orangesoft.auth.BaseAuthManager
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
abstract class FirebaseAuthManager(credManager: FirebaseCredentialsManager, parentJob: Job? = null) : BaseAuthManager<FirebaseUserController, FirebaseCredentialsManager>(credManager, parentJob) {

    fun signInAnonymously() {
        launch {
            credentialsManager.signInAnonymously()
                .onEach {
                    user.value = it
                }
                .launchIn(this)
        }
    }

    override suspend fun logout() {
        credentialsManager.logout(currentUser.value)
    }

    override suspend fun deleteUser() {
        credentialsManager.deleteUser(currentUser.value)
    }
}

