package com.example.mindflex

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mindflex.R.id.main
//import com.example.mindflex.SUPABASE_KEY
//import com.example.mindflex.SUPABASE_URL
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit



class noteeditor : AppCompatActivity() {

    private val TAG = "Noteeditor"
    private lateinit var etNoteTitle: EditText
    private lateinit var etNoteContent: EditText

    // Activity result launcher for picking an image
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { insertImageUri(it) }
    }

    // OkHttp client for Supabase REST calls
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.noteeditor)


        etNoteTitle = findViewById(R.id.etNoteTitle)
        etNoteContent = findViewById(R.id.etNoteContent)

        val btnAttachImage: ImageButton = findViewById(R.id.btnAttachImage)
        btnAttachImage.setOnClickListener { pickImage.launch("image/*") }

        val btnConvertToTask: Button = findViewById(R.id.btnConvertToTask)
        btnConvertToTask.setOnClickListener {
            // Placeholder — implement convert-to-task logic here
            Toast.makeText(this, "Convert to task clicked", Toast.LENGTH_SHORT).show()
        }

        val btnSave: Button = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            saveNote(btnSave)
        }

        // Edge-to-edge padding using WindowInsetsCompat (no API 30 direct call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Insert image URI (as text placeholder) at the cursor position
    private fun insertImageUri(uri: Uri) {
        val pos = etNoteContent.selectionStart.coerceAtLeast(0)
        etNoteContent.text.insert(pos, "\n[Image: $uri]\n")
        Toast.makeText(this, "Image inserted", Toast.LENGTH_SHORT).show()
    }

    // Save note to Supabase via REST API using BuildConfig keys
    private fun saveNote(saveButton: Button) {
        val title = etNoteTitle.text?.toString()?.trim() ?: ""
        val content = etNoteContent.text?.toString()?.trim() ?: ""

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Please add a title or content", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a user id if using Firebase Auth (optional)
        val uid = try {
            FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        } catch (t: Throwable) {
            "anonymous"
        }

        // Prepare JSON body for one row
        val json = JSONObject().apply {
            put("user_id", uid)
            put("title", title)
            put("content", content)
        }

        // Build URL and headers from BuildConfig (ensure you injected these via Gradle)
        val baseUrl = BuildConfig.SUPABASE_URL.trimEnd('/')
        val url = "$baseUrl/rest/v1/notes"
        val apiKey = BuildConfig.SUPABASE_KEY

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, json.toString())

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", apiKey)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=representation")
            .post(body)
            .build()

        // Disable the button while saving
        saveButton.isEnabled = false
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    saveButton.isEnabled = true
                    Toast.makeText(this@noteeditor, "Save failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Save failed: ${e.localizedMessage}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val bodyStr = response.body?.string()
                    runOnUiThread {
                        saveButton.isEnabled = true
                        if (!response.isSuccessful) {
                            Toast.makeText(this@noteeditor, "Save failed: HTTP ${response.code}\n$bodyStr", Toast.LENGTH_LONG).show()
                            Log.e(TAG, "Save failed: HTTP ${response.code}\n$bodyStr")
                            return@runOnUiThread
                        }
                        Toast.makeText(this@noteeditor, "Note saved", Toast.LENGTH_SHORT).show()
                        // clear UI
                        etNoteTitle.setText("")
                        etNoteContent.setText("")
                    }
                }
            }
        })
    }
}
