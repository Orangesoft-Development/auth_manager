package by.orangesoft.auth.firebase

import android.net.Uri
import android.util.Log
import by.orangesoft.auth.firebase.credential.FirebaseCredential
import by.orangesoft.auth.user.IBaseUserController
import by.orangesoft.auth.user.ITokenController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.internal.notify
import okhttp3.internal.wait
import java.io.File

open class FirebaseUserController(protected val firebaseInstance: FirebaseAuth) : IBaseUserController<FirebaseProfile>, ITokenController {

    companion object {
        private const val TAG = "FirebaseUserController"
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
            val task = firebaseInstance.currentUser!!.getIdToken(false)
            if(task.isComplete){
                field = task.result?.token ?: ""
                return field
            }

            task.addOnCompleteListener {
                        if  (it.isSuccessful)
                            field = it.result?.token ?: ""
                        else
                            Log.e(TAG, "Cannot get access token", it.exception)

                        notify()
                    }

            wait()
            return field
        }
        set(value) {
            firebaseInstance.getAccessToken(true).addOnCompleteListener {
                firebaseInstance.currentUser!!.getIdToken(true).addOnCompleteListener {
                    if  (it.isSuccessful)
                        field = it.result?.token ?: ""
                    else
                        Log.e(TAG, "Cannot get access token", it.exception)

                    notify()
                }
            }
            wait()
        }


    fun updateCredentials() {
        _credentials.value = firebaseInstance.getCredentials()
    }

    override suspend fun saveChanges(onError: ((Throwable) -> Unit)?) {
        firebaseInstance.updateCurrentUser(firebaseInstance.currentUser!!).addOnCompleteListener {
            if(!it.isSuccessful)
                onError?.invoke(it.exception ?: Throwable("Error account save changes"))
            else
                _credentials.value = firebaseInstance.getCredentials()
            notify()
        }
        wait()
    }

    override suspend fun updateAvatar(file: File, onError: ((Throwable) -> Unit)?) {
        firebaseInstance.currentUser!!
                .updateProfile(UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.fromFile(file)).build())
                .addOnCompleteListener {
                    if(!it.isSuccessful)
                        onError?.invoke(it.exception ?: Throwable("Error account update avatar"))
                    notify()
                }
        wait()
    }

    override suspend fun updateAccount(profile: FirebaseProfile, onError: ((Throwable) -> Unit)?) {
        firebaseInstance.currentUser!!
                .updateProfile(UserProfileChangeRequest.Builder()
                        .setPhotoUri(profile.photoUrl?.let { Uri.parse(it) } ?: Uri.EMPTY)
                        .setDisplayName(profile.displayName)
                        .build())
                .addOnCompleteListener {
                    if(!it.isSuccessful)
                        onError?.invoke(it.exception ?: Throwable("Error update account"))
                    notify()
                }
        wait()
    }

    override suspend fun reload(onError: ((Throwable) -> Unit)?) {
        firebaseInstance.currentUser!!.reload().addOnCompleteListener {
            if(!it.isSuccessful)
                onError?.invoke(it.exception ?: Throwable("Error reload account"))
            else
                _credentials.value = firebaseInstance.getCredentials()
            notify()
        }
        wait()
    }

    private fun FirebaseAuth.getProfile(): FirebaseProfile =
            currentUser!!.let {
                FirebaseProfile(it.uid,
                        it.providerId,
                        it.displayName,
                        it.phoneNumber,
                        it.photoUrl.toString(),
                        it.email)
            }

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
