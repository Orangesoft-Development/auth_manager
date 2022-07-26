package co.orangesoft.authmanager.firebase_auth.phone_auth

import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.firebase.credential.FirebaseAuthCredential
import by.orangesoft.auth.firebase.credential.controllers.BaseFirebaseCredentialController
import co.orangesoft.authmanager.api.AuthService
import co.orangesoft.authmanager.api.request_body.PhoneCredentialRequestBody
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

/**
 * Credential controller using FirebaseAuth with phone provider
 *
 * @param authService Authorization api
 * @param phoneAuthCredential Type of authorization credential
 * @see FirebaseAuthCredential.Phone
 * @see FirebaseAuth
 *
 */

class PhoneCredentialController(
    private val authService: AuthService,
    phoneAuthCredential: PhoneAuthCredential
) : BaseFirebaseCredentialController(phoneAuthCredential), CoroutineScope {

    override val authCredential: PhoneAuthCredential = phoneAuthCredential

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO }

    override fun addCredential(): Flow<CredentialResult> {
        runBlocking {
            val prevUser: FirebaseUser? =
                authInstance.currentUser?.let { if (it.providerData.size > 1) it else null }
            authService.createPhoneToken(
                PhoneCredentialRequestBody(
                    authCredential.phone,
                    authCredential.code,
                    prevUser?.uid
                )
            ).apply {
                authInstance.signInWithCustomToken(body() ?: "").await()
            }
        }

        return super.addCredential()
    }

}