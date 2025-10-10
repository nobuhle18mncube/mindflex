package com.example.mindflex

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class notelist : AppCompatActivity() {
    private val TAG = "notelist"

    private lateinit var rvNotes: RecyclerView
    private lateinit var searchEt: EditText
    private lateinit var progress: ProgressBar
    private lateinit var tvEmpty: TextView

    private lateinit var adapter: NoteAdapter
    private var allNotes: List<Note> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notelist)

        rvNotes = findViewById(R.id.rvNotes)
        searchEt = findViewById(R.id.etSearch)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvNotes = findViewById(R.id.rvNotes)
        searchEt = findViewById(R.id.etSearch)
        progress = findViewById(R.id.progress)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = NoteAdapter(emptyList()) { note ->
            // click: open editor/preview — implement as needed
            Toast.makeText(this, "Clicked: ${note.title ?: note.id}", Toast.LENGTH_SHORT).show()
        }

        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        // Load notes
        loadNotes()
    }

    private fun filterNotes(query: String) {
        if (query.isEmpty()) {
            adapter.setData(allNotes)
            tvEmpty.visibility = if (allNotes.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            return
        }
        val filtered = allNotes.filter { n ->
            val title = n.title ?: ""
            val content = n.content ?: ""
            (title + " " + content).lowercase(Locale.getDefault()).contains(query)
        }
        adapter.setData(filtered)
        tvEmpty.visibility = if (filtered.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun loadNotes() {
        progress.visibility = android.view.View.VISIBLE
        tvEmpty.visibility = android.view.View.GONE

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val userEq = if (!uid.isNullOrEmpty()) "eq.$uid" else {
            // If user not signed in, you might want to show nothing or implement a different flow
            Log.w(TAG, "No Firebase user; using anonymous filter that returns nothing")
            // Using "is.null" will return rows where user_id IS NULL — change if not desired
            "is.null"
        }

        val call: Call<List<Note>> = SupabaseClient.api.getNotesForUser(
            select = "*",
            userEq = userEq,
            apiKey = SupabaseClient.apiKeyHeader,
            auth = SupabaseClient.authHeader
        )

        call.enqueue(object : Callback<List<Note>> {
            override fun onResponse(call: Call<List<Note>>, response: Response<List<Note>>) {
                runOnUiThread {
                    progress.visibility = android.view.View.GONE
                }
                if (!response.isSuccessful) {
                    Log.e(TAG, "Error fetching notes: ${response.code()} ${response.message()}")
                    runOnUiThread {
                        Toast.makeText(this@notelist, "Failed to load notes: ${response.code()}", Toast.LENGTH_LONG).show()
                        tvEmpty.visibility = android.view.View.VISIBLE
                    }
                    return
                }
                val notes = response.body() ?: emptyList()
                allNotes = notes
                runOnUiThread {
                    adapter.setData(notes)
                    tvEmpty.visibility = if (notes.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                }
            }

            override fun onFailure(call: Call<List<Note>>, t: Throwable) {
                Log.e(TAG, "Failed to call API", t)
                runOnUiThread {
                    progress.visibility = android.view.View.GONE
                    tvEmpty.visibility = android.view.View.VISIBLE
                    Toast.makeText(this@notelist, "Network error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}