package co.orangesoft.authmanager.auth

import android.content.Context
import android.content.SharedPreferences
import by.orangesoft.auth.credentials.AuthCredential
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import androidx.preference.PreferenceManager;


class PrefsHelper(private val appContext: Context?) {

    companion object {
        const val TOKEN_PREF = "token_pref"
        const val CREDENTIALS_PREF = "credentials_pref"
        const val PROFILE_PREF = "profile_pref"
    }

    private val gson by lazy { Gson() }

    fun saveToken(token: String) {
        appContext?.let {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(it)
            val edit: SharedPreferences.Editor = sharedPreferences.edit()
            edit.putString(TOKEN_PREF, token)
            edit.apply()
        }
    }

    fun getToken(): String {
        return appContext?.let {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(it)
            sharedPreferences.getString(TOKEN_PREF, "") ?: ""
        } ?: ""
    }

    fun addCredential(credential: AuthCredential) {
        appContext?.let {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(it)
            val credentials = getCredentials().toMutableList()

            if (!credentials.any { it.providerId == credential.providerId }) {
                credentials.add(credential)
                val edit: SharedPreferences.Editor = sharedPreferences.edit()
                val jsonCredentials = gson.toJson(credentials)
                edit.putString(CREDENTIALS_PREF, jsonCredentials)
                edit.apply()
            }
        }
    }

    fun removeCredential(credential: AuthCredential) {
        appContext?.let { context ->
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val credentials = getCredentials().toMutableList()

            credentials.firstOrNull { it.providerId == credential.providerId }?.let {
                credentials.remove(it)
                val edit: SharedPreferences.Editor = sharedPreferences.edit()
                val jsonCredentials = gson.toJson(credentials)
                edit.putString(CREDENTIALS_PREF, jsonCredentials)
                edit.apply()
            }
        }
    }

    fun getCredentials(): List<AuthCredential> {
        return appContext?.let {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(it)
            sharedPreferences.getString(CREDENTIALS_PREF, "")

            var arrayItems: List<AuthCredential> = listOf()
            val serializedObject = sharedPreferences.getString(CREDENTIALS_PREF, null)
            if (serializedObject != null) {
                val type: Type = object : TypeToken<List<AuthCredential?>?>() {}.type
                arrayItems = gson.fromJson(serializedObject, type)
            }
            arrayItems
        } ?: listOf()
    }

    fun saveProfile(profile: SimpleProfile) {
        appContext?.let {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(it)
            val edit: SharedPreferences.Editor = sharedPreferences.edit()

            val jsonProfile = gson.toJson(profile)
            edit.putString(PROFILE_PREF, jsonProfile)
            edit.apply()
        }
    }

    fun getProfile(): SimpleProfile? {
        appContext?.let {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
            val jsonProfile = sharedPreferences.getString(PROFILE_PREF, "")

            return if (jsonProfile?.isNotEmpty() == true)
                gson.fromJson(jsonProfile, SimpleProfile::class.java)
            else null
        } ?: return null
    }
}