package by.orangesoft.auth.credentials

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow

/**
 * The IBaseCredentialController defines the methods of working with a specific credential controller
 *
 */

interface IBaseCredentialController : CredentialControllerResultListener {

    /**
     * Current authorization credential type
     * @see BaseAuthCredential
     */
    val authCredential: BaseAuthCredential

    /**
     * Add a new user authorization method
     * @return Data stream with the result of the operation
     *
     * @see CredentialResult
     * @see UnlinkCredentialResult
     *
     */
    fun addCredential(): Flow<CredentialResult>

    /**
     * Remove current authorization method of an authorized user
     * @return Data stream with the result of the operation
     *
     * @see CredentialResult
     * @see UnlinkCredentialResult
     */
    fun removeCredential(): Flow<CredentialResult>

    /**
     * Clearing saved user credentials to prevent auto-login
     * @param context
     */
    fun clearCredInfo(context: Context) {}

    /**
     * Sets the activity for working with ActivityResultLauncher
     * @param activity FragmentActivity
     *
     * @see ActivityResultLauncher
     */
    fun setActivity(activity: FragmentActivity) {}

    /**
     * Handling errors that occur when working with the credential controller
     */
    fun onError() {}
}

/**
 * The CredentialControllerResultListener provides of the Activity and ActivityResultLauncher,
 * manages the callbacks into the credential controller from an Activity's onActivityResult() method.
 * @see ComponentCallbackActivity
 * @see ActivityResultLauncher
 */

interface CredentialControllerResultListener {

    /**
     * The method that should be called when the context is ready to call ActivityResultLauncher methods
     * @param activity FragmentActivity
     * @param activityLauncher A launcher for a executing an ActivityResultContract.
     */
    fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {}

    /**
     * The method that should be called from the Activity's or Fragment's onActivityResult method
     * @param code The result code that's received by the Activity or Fragment.
     * @param data The result data that's received by the Activity or Fragment.
     */
    fun onActivityResult(code: Int, data: Intent?) {}

}