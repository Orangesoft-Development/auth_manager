package by.orangesoft.auth.credentials

import android.content.Intent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.credentials.firebase.ComponentCallbackActivity

abstract class CredentialController(val method: AuthMethod) {

    fun addCredential(listener: CredentialListener.() -> Unit) = addCredential(CredentialListener().apply(listener))
    abstract fun addCredential(listener: CredentialListener)

    fun removeCredential(listener: CredentialListener.() -> Unit) = removeCredential(CredentialListener().apply(listener))
    abstract fun removeCredential(listener: CredentialListener)

    abstract fun createProvider(activity: FragmentActivity, activityLauncher: ActivityResultLauncher<Intent>)

    abstract fun onActivityResult(code: Int, data: Intent?)

    fun setActivity(activity: FragmentActivity) {
        if(activity is ComponentCallbackActivity)
            activity.setActivityResultCallback(
                ActivityResultCallback { onActivityResult(it.resultCode, it.data) }
            )

        val launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> onActivityResult(result.resultCode, result.data) }
        createProvider(activity, launcher)
    }
}