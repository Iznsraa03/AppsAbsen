package com.example.appabsen

import Course
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomePage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: Adapter
    private lateinit var courseList: MutableList<Course>
    private lateinit var progressBar: ProgressBar
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        progressBar = findViewById(R.id.progressBar)

        val ivBack: ImageView = findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            val intent = Intent(this, RiwayatAbsen::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.rvCourses)
        recyclerView.layoutManager = LinearLayoutManager(this)

        courseList = mutableListOf()
        courseAdapter = Adapter(courseList)
        recyclerView.adapter = courseAdapter

        courseAdapter.setOnItemClickListener(object : Adapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val course = courseList[position]
                val intent = Intent(this@HomePage, AbsensiForm::class.java)
                intent.putExtra("matkul", course.subject)
                intent.putExtra("masuk", course.time)
                intent.putExtra("dosen", course.instructor)
                startActivity(intent)
            }
        })

        fetchCoursesFromFirestore()
    }

    private fun fetchCoursesFromFirestore() {
        progressBar.visibility = ProgressBar.VISIBLE
        db.collection("matkul")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val courseCode = document.getString("cMatkul") ?: ""
                    val subject = document.getString("matkul")?:""
                    val time = document.getString("masuk") ?: ""
                    val instructor = document.getString("dosen") ?: ""
                    val imageUrl = document.getString("img") ?: "" // Ganti "imageUrl" dengan nama field di Firestore

                    val course = Course(courseCode,subject,time, instructor, imageUrl)
                    courseList.add(course)
                }
                courseAdapter.notifyDataSetChanged()
                progressBar.visibility = ProgressBar.GONE
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = ProgressBar.GONE
            }
    }
}
