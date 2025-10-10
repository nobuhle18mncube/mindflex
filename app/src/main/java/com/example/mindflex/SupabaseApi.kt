package com.example.mindflex


import retrofit2.Call
import retrofit2.http.*

interface SupabaseApi {
    // Create note: returns created row(s)
    @Headers("Content-Type: application/json")
    @POST("notes")
    fun createNote(
        @Body note: Note,
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Prefer") prefer: String = "return=representation"
    ): Call<List<Note>>

    // Get notes for a given user: pass userEq = "eq.<uid>" and select like "*"
    @GET("notes")
    fun getNotesForUser(
        @Query("select") select: String = "*",
        @Query("user_id") userEq: String,             // e.g. "eq.<uid>"
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String
    ): Call<List<Note>>
}