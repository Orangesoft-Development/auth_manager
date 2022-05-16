package by.orangesoft.auth.credentials

import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class CredentialFragment : Fragment() {

    companion object {
        const val TAG = "CredentialFragment"
        fun getInstance(credResultListener: CredentialControllerResultListener): CredentialFragment =
            CredentialFragment().apply { credentialControllerResultListener = credResultListener }
    }

    private lateinit var credentialControllerResultListener: CredentialControllerResultListener

    private val credentialResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { componentActivityResult(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (::credentialControllerResultListener.isInitialized) {
            credentialControllerResultListener.onProviderCreated(requireActivity(), credentialResultLauncher)
            (activity as? ComponentCallbackActivity)?.setActivityResultCallback { componentActivityResult(it) }
        }
    }

    private fun componentActivityResult(result: ActivityResult) {
        if (::credentialControllerResultListener.isInitialized) {
            credentialControllerResultListener.onActivityResult(result.resultCode, result.data)
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }
}