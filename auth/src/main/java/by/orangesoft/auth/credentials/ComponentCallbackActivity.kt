package by.orangesoft.auth.credentials

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback

interface ComponentCallbackActivity {

    fun setActivityResultCallback(callback: ActivityResultCallback<ActivityResult>)

}