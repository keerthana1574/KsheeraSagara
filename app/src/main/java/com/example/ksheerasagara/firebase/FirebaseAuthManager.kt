package com.example.ksheerasagara.firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthManager(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun isSignedIn(): Boolean = auth.currentUser != null

    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun createAccount(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userMap = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users")
                        .document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Failed to save user data")
                        }
                } else {
                    onFailure(task.exception?.message ?: "Account creation failed")
                }
            }
    }

    fun getUserName(callback: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback("User")
            return
        }
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                if (!name.isNullOrEmpty()) {
                    callback(name)
                } else {
                    callback(auth.currentUser?.email?.substringBefore("@") ?: "User")
                }
            }
            .addOnFailureListener {
                callback(auth.currentUser?.email?.substringBefore("@") ?: "User")
            }
    }

    fun signOut() {
        auth.signOut()
    }
}