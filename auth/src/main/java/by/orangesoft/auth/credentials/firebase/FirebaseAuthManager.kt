package by.orangesoft.auth.credentials.firebase

import by.orangesoft.auth.BaseAuthManager
import by.orangesoft.auth.credentials.BaseCredentialsManager

abstract class FirebaseAuthManager<T: FirebaseUserController<*>>(credManager: BaseCredentialsManager<T>) : BaseAuthManager<T>(credManager)