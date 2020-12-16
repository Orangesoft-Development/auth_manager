package by.orangesoft.auth.firebase

import by.orangesoft.auth.AuthListener
import by.orangesoft.auth.BaseAuthManager

open abstract class FirebaseAuthManager(credManager: FirebaseCredentialsManager) : BaseAuthManager<FirebaseUserController, FirebaseCredentialsManager>(credManager) {

    override suspend fun logout(listener: AuthListener<FirebaseUserController>?) {
        authListener = listener
        credentialsManager.logout(currentUser.value)
    }

    override suspend fun deleteUser(listener: AuthListener<FirebaseUserController>?) {
        authListener = listener
        credentialsManager.deleteUser(currentUser.value)
    }
}

