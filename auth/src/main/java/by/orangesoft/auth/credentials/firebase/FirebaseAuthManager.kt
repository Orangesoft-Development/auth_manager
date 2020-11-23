package by.orangesoft.auth.credentials.firebase

import by.orangesoft.auth.BaseAuthManager
import by.orangesoft.auth.user.BaseProfile

open class FirebaseAuthManager<P: BaseProfile>(credManager: FirebaseCredentialsManager<P>) : BaseAuthManager<FirebaseUserController<P>>(credManager)