package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseHelper {
    private const val TAG = "FirebaseHelper"

    fun isFirebaseAvailable(context: Context): Boolean {
        return try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                FirebaseApp.initializeApp(context) != null
            } else {
                true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase is not initialized: ${e.message}")
            false
        }
    }

    fun getAuth(context: Context): FirebaseAuth? {
        return if (isFirebaseAvailable(context)) {
            try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseAuth unavailable: ${e.message}")
                null
            }
        } else {
            null
        }
    }

    fun getFirestore(context: Context): FirebaseFirestore? {
        return if (isFirebaseAvailable(context)) {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseFirestore unavailable: ${e.message}")
                null
            }
        } else {
            null
        }
    }
}
