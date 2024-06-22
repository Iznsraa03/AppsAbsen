package com.example.appabsen

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class AbsensiForm : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var cameraLayout: LinearLayout
    private lateinit var imgView: ImageView
    private lateinit var editTextNama: EditText
    private lateinit var editTextNim: EditText
    private lateinit var editTextTanggal: EditText
    private lateinit var checkBoxHadir: CheckBox
    private lateinit var checkBoxAlfa: CheckBox
    private lateinit var checkBoxIzin: CheckBox
    private lateinit var editTextKeterangan: EditText
    private lateinit var buttonSelesai: Button
    private lateinit var bitmap: Bitmap
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    private lateinit var subject: String
    private lateinit var time: String
    private lateinit var instructor: String

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_STORAGE_PERMISSION = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.absensi_form)

        subject = intent.getStringExtra("matkul")?: ""
        time = intent.getStringExtra("masuk")?: ""
        instructor = intent.getStringExtra("dosen")?: ""

        // Initialize views
        backButton = findViewById(R.id.backButton)
        cameraLayout = findViewById(R.id.cameraLayout)
        imgView = findViewById(R.id.cameraIcon)
        editTextNama = findViewById(R.id.editTextNama)
        editTextNim = findViewById(R.id.editTextNim)
        editTextTanggal = findViewById(R.id.editTextTanggal)
        checkBoxHadir = findViewById(R.id.checkBoxHadir)
        checkBoxAlfa = findViewById(R.id.checkBoxAlfa)
        checkBoxIzin = findViewById(R.id.checkBoxIzin)
        editTextKeterangan = findViewById(R.id.editTextKeterangan)
        buttonSelesai = findViewById(R.id.buttonSelesai)

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().reference

        // Initialize Firestore instance
        firestore = FirebaseFirestore.getInstance()

        // Back button click listener
        backButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        // Camera click listener
        cameraLayout.setOnClickListener {
            checkCameraPermission()
        }

        // Date picker for Tanggal
        editTextTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    editTextTanggal.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Single checkbox selection logic
        checkBoxHadir.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxAlfa.isChecked = false
                checkBoxIzin.isChecked = false
            }
        }

        checkBoxAlfa.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxHadir.isChecked = false
                checkBoxIzin.isChecked = false
            }
        }

        checkBoxIzin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxHadir.isChecked = false
                checkBoxAlfa.isChecked = false
            }
        }

        // Submit button click listener
        buttonSelesai.setOnClickListener {
            val nama = editTextNama.text.toString()
            val nim = editTextNim.text.toString()
            val tanggal = editTextTanggal.text.toString()
            val keterangan = editTextKeterangan.text.toString()

            if (nama.isEmpty() || nim.isEmpty() || tanggal.isEmpty() || (!checkBoxHadir.isChecked &&!checkBoxAlfa.isChecked &&!checkBoxIzin.isChecked)) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val status = when {
                checkBoxHadir.isChecked -> "Hadir"
                checkBoxAlfa.isChecked -> "Alfa"
                checkBoxIzin.isChecked -> "Izin"
                else -> ""
            }

            // Upload the image to Firebase Storage
            uploadImageToFirebase(bitmap) { imageUrl ->
                // Save the data to Firestore
                saveDataToFirestore(subject, time, instructor, imageUrl, nama, nim, tanggal, status, keterangan)
            }
            finish()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager)!= null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required to use camera", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Lanjutkan dengan operasi yang membutuhkan izin penyimpanan
                    // Misalnya, menyimpan atau mengakses file
                } else {
                    Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            imgView.setImageBitmap(bitmap)
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap, callback: (String) -> Unit) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val fileName = "images/${UUID.randomUUID()}.jpg"
        val imageRef = storageReference.child(fileName)

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }
    }

    private fun saveDataToFirestore(subject: String, time: String, instructor: String, imageUrl: String, nama: String, nim: String, tanggal: String, status: String, keterangan: String) {
        val riwayatAbsenRef = firestore.collection("riwayatAbsensi")
        val absenData = hashMapOf(
            "matkul" to subject,
            "masuk" to time,
            "dosen" to instructor,
            "imageUrl" to imageUrl,
            "nama" to nama,
            "nim" to nim,
            "tanggal" to tanggal,
            "status" to status,
            "keterangan" to keterangan
        )
        riwayatAbsenRef.add(absenData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}