package com.example.mindflex

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Sign_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        val editTextFirstName: EditText = findViewById(R.id.editTextFirstName)
        val editTextLastName: EditText = findViewById(R.id.editTextLastName)
        val editTextEmail2: EditText = findViewById(R.id.editTextEmail2)
        val editTextPassword2: EditText = findViewById(R.id.editTextPassword2)
        val editTextConfirmPassword: EditText = findViewById(R.id.editTextConfirmPassword)

        val backToLogin = findViewById<Button>(R.id.backToLoginBtn)
        val signupBtn: Button = findViewById(R.id.Signupbtn)

        signupBtn?.setOnClickListener {
            val firstName = editTextFirstName?.text?.toString()?.trim() ?: ""
            val lastName = editTextLastName?.text?.toString()?.trim() ?: ""
            val email = editTextEmail2?.text?.toString()?.trim() ?: ""
            val password = editTextPassword2?.text?.toString() ?: ""
            val confirmPassword = editTextConfirmPassword?.text?.toString() ?: ""

            // Basic validation (first/last name optional - remove if you don't need them)
            if (firstName.isEmpty()) {
                editTextFirstName?.error = getString(R.string.first_name_cannot_be_empty)
                return@setOnClickListener
            }
            if (lastName.isEmpty()) {
                editTextLastName?.error = getString(R.string.last_name_cannot_be_empty)
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                editTextEmail2?.error = getString(R.string.email_cannot_be_empty2)
                return@setOnClickListener
            } else if (!isValidEmail(email)) {
                editTextEmail2?.error = getString(R.string.invalid_email_format2)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword2?.error = getString(R.string.password_cannot_be_empty2)
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                editTextConfirmPassword?.error = getString(R.string.please_confirm_your_password2)
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                editTextConfirmPassword?.error = getString(R.string.passwords_do_not_match2)
                return@setOnClickListener
            }

            // Disable button to avoid double taps
            signupBtn.isEnabled = false

            // Create user with Firebase Auth (no Firestore)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    signupBtn.isEnabled = true
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign up successful! Please log in.", Toast.LENGTH_SHORT).show()
                        // Navigate back to login (MainActivity)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        val msg = task.exception?.localizedMessage ?: "Sign up failed"
                        Toast.makeText(this, "Sign up failed: $msg", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    signupBtn.isEnabled = true
                    Toast.makeText(this, "Sign-up error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }

        // Back to login

        backToLogin?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()  }
    }


    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }
    }
