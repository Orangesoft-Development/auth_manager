package co.orangesoft.huawei

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.credential.base.BaseHuaweiCredentialsController

class HuaweiPhoneCredentialController(private val phoneAuthCredential: HuaweiAuthCredential.Phone) :
    BaseHuaweiCredentialsController(phoneAuthCredential) {



    override fun onActivityResult(code: Int, data: Intent?) {}
    override fun onProviderCreated(
        activity: FragmentActivity,
        activityLauncher: ActivityResultLauncher<Intent>
    ) {
    }
}