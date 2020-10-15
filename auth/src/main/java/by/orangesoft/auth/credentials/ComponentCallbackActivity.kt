package by.orangesoft.auth.credentials

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher

interface ComponentCallbackActivity {

    fun setActivityResultCallback(callback: ActivityResultCallback<ActivityResult>)
    fun startActivityForResult(intent: Intent, requestCode: Int, callback: (ActivityResult) -> Unit)
}