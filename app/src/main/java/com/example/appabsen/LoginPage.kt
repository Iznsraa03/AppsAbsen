package com.example.appabsen

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginPage : AppCompatActivity() {

    private lateinit var editTextNama: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignIn: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate;/(savedInstanceState)

        setContentView(R.layout.login_page)

        editTextNama = findViewById(R.id.editTextNama)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonSignIn = findViewById(R.id.buttonSignIn)

        buttonLogin.setOnClickListener {
            validateInputs()
        }

        buttonSignIn.setOnClickListener {
            Toast.makeText(this, "Sign In clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegistPage::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs() {
        val nama = editTextNama.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (TextUtils.isEmpty(nama)) {
            editTextNama.error = "Nama harus diisi"
            return
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.error = "Password harus diisi"
            return
        }

        // Query Firestore to validate user credentials
        db.collection("users")
            .whereEqualTo("nama", nama)
            .whereEqualTo("pass", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Nama atau password salah", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
