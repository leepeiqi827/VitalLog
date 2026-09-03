package com.example.vitallog.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

    private const val SUPABASE_URL = "https://ofdpikldpwrishojvmui.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9mZHBpa2xkcHdyaXNob2p2bXVpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODgzOTI1MTcsImV4cCI6MjEwMzk2ODUxN30.246Qxw4jgSWIIkHoiSWgntQfs4uXRHKJJ4eM3GQ2Jn4"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
 
