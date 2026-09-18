package com.example.vitallog.data

import com.example.vitallog.data.remote.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo

object AuthManager {

    private val supabase = SupabaseClientProvider.client

    suspend fun signIn(email: String, password: String) {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }

    suspend fun signUp(email: String, password: String) {
        try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            throw Exception("Sign up failed: ${e.message}")
        }
    }

    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            throw Exception("Sign out failed: ${e.message}")
        }
    }

    /** Creates a Supabase Auth identity without collecting or validating credentials. */
    suspend fun signInAnonymously() {
        try {
            if (supabase.auth.currentUserOrNull() == null) {
                supabase.auth.signInAnonymously()
            }
        } catch (e: Exception) {
            throw Exception("Could not start Supabase session: ${e.message}")
        }
    }

    suspend fun resetPassword(email: String) {
        try {
            supabase.auth.resetPasswordForEmail(email)
        } catch (e: Exception) {
            throw Exception("Could not send reset email: ${e.message}")
        }
    }

    fun getCurrentUser(): UserInfo? {
        return supabase.auth.currentUserOrNull()
    }

    fun isSignedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}
