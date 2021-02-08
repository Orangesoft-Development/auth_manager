package by.orangesoft.auth.credentials

import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

interface IBaseCredentialController {

    val credential: AuthCredential

    fun addCredential(): Flow<CredentialResult>

    fun removeCredential(): Collection<IBaseCredential>

    fun onProviderCreated(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>)

    fun onActivityResult(code: Int, data: Intent?)

    fun setActivity(activity: FragmentActivity) {
        if(activity is ComponentCallbackActivity)
            activity.setActivityResultCallback(ActivityResultCallback { onActivityResult(it.resultCode, it.data) })

        GlobalScope.launch(Dispatchers.Main) {
            val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> onActivityResult(result.resultCode, result.data) }
            onProviderCreated(activity, launcher)
        }
    }
}