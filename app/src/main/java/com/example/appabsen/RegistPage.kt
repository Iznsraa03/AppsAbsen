package com.example.appabsen

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegistPage : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextNama: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignUp: Button
    private lateinit var backButton: ImageButton

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regist_page)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextNama = findViewById(R.id.editTextNama)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        buttonSignUp.setOnClickListener {
            validateInputs()
        }
    }

    private fun validateInputs() {
        val email = editTextEmail.text.toString().trim()
        val nama = editTextNama.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            editTextEmail.error = "Email harus diisi"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Email tidak valid"
            return
        }
        if (TextUtils.isEmpty(nama)) {
            editTextNama.error = "Nama harus diisi"
            return
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.error = "Password harus diisi"
            return
        }
        if (password.length < 6) {
            editTextPassword.error = "Password harus lebih dari 6 karakter"
            return
        }

        // Save the user to Firestore
        saveUserToFirestore(email, nama, password)
    }

    private fun saveUserToFirestore(email: String, nama: String, password: String) {
        val user = hashMapOf(
            "email" to email,
            "nama" to nama,
            "pass" to password
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Sign Up berhasil", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
