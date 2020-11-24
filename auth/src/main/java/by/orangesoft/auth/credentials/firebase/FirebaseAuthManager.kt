package by.orangesoft.auth.credentials.firebase

import by.orangesoft.auth.BaseAuthManager

open class FirebaseAuthManager(credManager: FirebaseCredentialsManager) : BaseAuthManager<FirebaseUserController>(credManager)