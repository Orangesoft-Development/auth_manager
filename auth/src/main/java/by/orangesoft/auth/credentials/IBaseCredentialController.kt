package by.orangesoft.auth.credentials

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

interface IBaseCredentialController {

    val authCredential: BaseAuthCredential

    fun addCredential(): Flow<CredentialResult>

    fun removeCredential(): Job

    fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>)

    fun onActivityResult(code: Int, data: Intent?)

    fun clearCredInfo(context: Context) { }

    fun setActivity(activity: FragmentActivity) {
        if(activity is ComponentCallbackActivity)
            activity.setActivityResultCallback(ActivityResultCallback { onActivityResult(it.resultCode, it.data) })

        GlobalScope.launch(Dispatchers.Main) {
            val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> onActivityResult(result.resultCode, result.data) }
            onProviderCreated(activity, launcher)
        }
    }
}