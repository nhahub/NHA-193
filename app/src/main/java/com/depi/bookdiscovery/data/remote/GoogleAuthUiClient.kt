package com.depi.bookdiscovery.data.remote

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.depi.bookdiscovery.BuildConfig

/**
 * [GoogleAuthClient]
 * A client responsible for handling the Google Sign-In flow using the new AndroidX Credential Manager API.
 * This class abstracts the complexity of fetching the Google ID Token required for Firebase authentication.
 *
 * @param context The application context required to initialize the Credential Manager.
 */
class GoogleAuthClient(
    private val context: Context
) {

    private val credentialManager = CredentialManager.create(context)
    val clientId = BuildConfig.MY_CLIENT_ID

    /**
     * Initiates the Google Sign-In flow and retrieves the ID Token upon successful authentication.
     *
     * @param activity The host Activity, required by the Credential Manager for UI interaction.
     * @return The Google ID Token string if sign-in is successful and the token is retrieved, otherwise null.
     */
    suspend fun signIn(activity: Activity): String? {

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = activity
        )

        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
            return googleCred.idToken
        }

        return null
    }
}