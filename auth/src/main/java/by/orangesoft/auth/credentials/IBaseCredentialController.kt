package by.orangesoft.auth.credentials

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow

interface IBaseCredentialController : CredentialControllerResultListener {

    val authCredential: BaseAuthCredential

    fun addCredential(): Flow<CredentialResult>

    fun removeCredential(): Flow<CredentialResult>

    fun clearCredInfo(context: Context) {}

    fun setActivity(activity: FragmentActivity) {}

    fun onError() {}
}

interface CredentialControllerResultListener {
    fun onActivityResult(code: Int, data: Intent?)
    fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    )
}