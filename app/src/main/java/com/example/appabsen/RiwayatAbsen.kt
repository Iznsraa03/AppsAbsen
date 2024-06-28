package com.example.appabsen

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RiwayatAbsen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var absensiAdapter: RiwayatAdapter
    private val absensiList = mutableListOf<RAbsensi>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.riwayat_absen)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { onBackPressed() }

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        absensiAdapter = RiwayatAdapter(absensiList) // Assuming RiwayatAdapter takes absensiList as parameter
        recyclerView.adapter = absensiAdapter

        // Fetch data from Firestore
        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        db.collection("riwayatAbsensi")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nama = document.getString("nama") ?: ""
                    val nim = document.getString("nim") ?: ""
                    val mataKuliah = document.getString("matkul") ?: ""
                    val tanggal = document.getString("tanggal") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val status = document.getString("status")?:""


                    val absensi = RAbsensi(nama, nim, mataKuliah, tanggal, imageUrl, status)
                    absensiList.add(absensi)
                }
                absensiAdapter.notifyDataSetChanged() // Notify adapter that data set has changed
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
