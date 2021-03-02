package by.orangesoft.auth.firebase

import by.orangesoft.auth.BaseAuthManager
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job

@InternalCoroutinesApi
abstract class FirebaseAuthManager(credManager: FirebaseCredentialsManager, parentJob: Job? = null) : BaseAuthManager<FirebaseUserController, FirebaseCredentialsManager>(credManager, parentJob) {

    override suspend fun logout() {
        credentialsManager.logout(currentUser.value)
    }

    override suspend fun deleteUser() {
        credentialsManager.deleteUser(currentUser.value)
    }
}

