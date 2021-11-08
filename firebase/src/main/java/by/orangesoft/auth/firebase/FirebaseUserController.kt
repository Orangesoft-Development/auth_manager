package by.orangesoft.auth.firebase

import android.net.Uri
import by.orangesoft.auth.firebase.credential.FirebaseCredentialResult
import by.orangesoft.auth.firebase.credential.getCredentials
import by.orangesoft.auth.user.BaseUserController
import by.orangesoft.auth.user.ITokenController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.jvm.Throws

open class FirebaseUserController(protected val firebaseInstance: FirebaseAuth) : BaseUserController<FirebaseProfile>(), ITokenController {

    companion object {
        const val TAG = "FirebaseUserController"
    }

    override val profile: FirebaseProfile
        get() = firebaseInstance.getProfile()

    private val _credentials: MutableStateFlow<Collection<FirebaseCredentialResult>> by lazy {
        MutableStateFlow(firebaseInstance.getCredentials())
    }

    override val credentials: StateFlow<Collection<FirebaseCredentialResult>> by lazy {
        _credentials.asStateFlow()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getAccessToken(): String {
        var token = ""
        firebaseInstance.currentUser?.let {
            runBlocking {
                token = it.getIdToken(false).await().token ?: ""
            }
        }

        return token
    }

    override suspend fun setAccessToken(accessToken: String) {
        //do nothing for firebase credentials
    }

    fun reloadCredentials() {
        _credentials.value = firebaseInstance.getCredentials()
    }

    @Throws(Throwable::class)
    override suspend fun updateAccount(profile: FirebaseProfile) {
        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder()
                    .setPhotoUri(profile.photoUrl?.let { photoUrl -> Uri.parse(photoUrl) } ?: Uri.EMPTY)
                    .setDisplayName(profile.displayName)
                    .build())
                 .await()
        }
    }

    @Throws(Throwable::class)
    override suspend fun updateAvatar(file: File) {
        firebaseInstance.currentUser?.apply {
            updateProfile(UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.fromFile(file))
                .build())
                .await()
        }
    }

    @Throws(Throwable::class)
    override suspend fun reload() {
        firebaseInstance.currentUser?.let {
            it.reload().await()
            _credentials.value = firebaseInstance.getCredentials()
        }
    }

    private fun FirebaseAuth.getProfile(): FirebaseProfile =
            currentUser!!.let {
                FirebaseProfile(it.uid,
                        it.providerId,
                        it.displayName,
                        it.phoneNumber,
                        it.photoUrl?.toString(),
                        it.email)
            }

}
