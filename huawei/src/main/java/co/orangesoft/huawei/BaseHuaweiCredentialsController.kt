package co.orangesoft.huawei

import android.content.Context
import androidx.fragment.app.FragmentActivity
import by.orangesoft.auth.credentials.CredentialResult
import by.orangesoft.auth.credentials.IBaseCredentialController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.coroutines.CoroutineContext

abstract class BaseHuaweiCredentialsController(override val authCredential: HuaweiAuthCredential) :
    IBaseCredentialController,
    CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()
    private var credResultFlow: MutableSharedFlow<CredentialResult> = MutableSharedFlow(1, 1)

    override fun addCredential(): Flow<CredentialResult> {
        TODO("Not yet implemented")
    }

    override fun removeCredential(): Flow<CredentialResult> {
        TODO("Not yet implemented")
    }

    override fun clearCredInfo(context: Context) {
        super.clearCredInfo(context)
    }

    override fun setActivity(activity: FragmentActivity) {
        super.setActivity(activity)
    }

    override fun onError() {
        super.onError()
    }


}