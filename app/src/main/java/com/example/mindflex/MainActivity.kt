package com.example.mindflex

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()


        // find views
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val loginBtn = findViewById<Button?>(R.id.loginbtn)
        val createBtn = findViewById<Button?>(R.id.createBtn) // change to createBtn if your XML uses that id

        Log.d(TAG, "views -> email:${editTextEmail != null} pwd:${editTextPassword != null} login:${loginBtn != null} signup:${createBtn!= null}")



        // Login Button
        val loginbtn: Button = findViewById(R.id.loginbtn)
        loginbtn.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            // Validate fields
            if (email.isEmpty()) {
                editTextEmail.error = "Email cannot be empty"
                return@setOnClickListener
            }

            // Check for "@" symbol explicitly
            if (!email.contains("@")) {
                editTextEmail.error = "Email must contain '@'"
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                editTextEmail.error = "Invalid email format"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }

            // Firebase sign in (auth only, no Firestore)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    loginBtn.isEnabled = true
                    if (task.isSuccessful) {
                        // Signed in successfully
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        // Navigate to Dashboard (use class name declared in your manifest)
                        val intent = Intent(this, DashBoard::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val msg = task.exception?.localizedMessage ?: "Authentication failed"
                        Toast.makeText(this, "Login failed: $msg", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "signIn failed", task.exception)
                    }
                }
                .addOnFailureListener { e ->
                    loginBtn.isEnabled = true
                    Toast.makeText(this, "Sign-in error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "signInWithEmailAndPassword failure", e)
                }
        }

        // Sign-up button opens the SignUp activity
        createBtn?.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
        }
// Function to validate email format
private fun MainActivity.isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
