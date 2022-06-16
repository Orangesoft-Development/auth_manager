package co.orangesoft.huawei

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity

class HuaweiEmailCredentialsController(private val phoneAuthCredential: HuaweiAuthCredential.Email) :
    BaseHuaweiCredentialsController(phoneAuthCredential) {


    override fun onActivityResult(code: Int, data: Intent?) {}
    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {}
}