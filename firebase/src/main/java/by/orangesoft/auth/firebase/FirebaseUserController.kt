package by.orangesoft.auth.firebase

import android.net.Uri
import by.orangesoft.auth.firebase.credential.FirebaseCredential
import by.orangesoft.auth.user.IBaseUserController
import by.orangesoft.auth.user.ITokenController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.File
import java.lang.Exception

open class FirebaseUserController(protected val firebaseInstance: FirebaseAuth) : IBaseUserController<FirebaseProfile>, ITokenController {

    companion object {
        const val TAG = "FirebaseUserController"
    }

    override val profile: FirebaseProfile
        get() = firebaseInstance.getProfile()

    private val _credentials: MutableStateFlow<Set<FirebaseCredential>> by lazy {
        MutableStateFlow(firebaseInstance.getCredentials())
    }

    override val credentials: StateFlow<Set<FirebaseCredential>> by lazy {
        _credentials.asStateFlow()
    }

    override var accessToken: String = ""
        get() {
            firebaseInstance.currentUser?.let {
                runBlocking {
                    field = it.getIdToken(false).await().token ?: ""
                }
            }
            return field
        }

    fun updateCredentials() {
        _credentials.value = firebaseInstance.getCredentials()
    }

    override suspend fun saveChanges(onError: ((Throwable) -> Unit)?) {
        firebaseInstance.currentUser?.let {
            try {
                firebaseInstance.updateCurrentUser(it).await()
                _credentials.value = firebaseInstance.getCredentials()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    override suspend fun updateAvatar(file: File, onError: ((Throwable) -> Unit)?) {
        firebaseInstance.currentUser?.let {
            try {
                it.updateProfile(UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.fromFile(file))
                    .build())
                    .await()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    override suspend fun updateAccount(profile: FirebaseProfile, onError: ((Throwable) -> Unit)?) {
        firebaseInstance.currentUser?.let {
            try {
                it.updateProfile(UserProfileChangeRequest.Builder()
                    .setPhotoUri(profile.photoUrl?.let { photoUrl -> Uri.parse(photoUrl) } ?: Uri.EMPTY)
                    .setDisplayName(profile.displayName)
                    .build())
                    .await()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    override suspend fun reload(onError: ((Throwable) -> Unit)?) {
        firebaseInstance.currentUser?.let {
            try {
                it.reload().await()
                _credentials.value = firebaseInstance.getCredentials()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    private fun FirebaseAuth.getProfile(): FirebaseProfile =
            currentUser?.let {
                FirebaseProfile(it.uid,
                        it.providerId,
                        it.displayName,
                        it.phoneNumber,
                        it.photoUrl.toString(),
                        it.email)
            } ?: profile

    private fun FirebaseAuth.getCredentials(): Set<FirebaseCredential> =
            currentUser?.providerData?.mapNotNull {
                if (it.providerId != "firebase")
                    FirebaseCredential(
                            it.uid,
                            it.providerId,
                            it.displayName ?: "",
                            it.photoUrl?.path ?: "",
                            it.email ?: "",
                            it.phoneNumber ?: ""
                    )
                else
                    null
            }?.toSet() ?: HashSet()
}
