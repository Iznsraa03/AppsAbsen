package com.example.appabsen

import Course
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Adapter(private val courseList: List<Course>) : RecyclerView.Adapter<Adapter.CourseViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.tvCourseCode.text = course.courseCode
        holder.tvTime.text = course.time
        holder.tvInstructor.text = course.instructor

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(course.imageUrl)
            .placeholder(R.drawable.background) // Placeholder image jika gambar belum selesai dimuat
            .error(R.drawable.background) // Gambar yang akan ditampilkan jika ada error dalam memuat gambar
            .into(holder.ivCourseImage)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseCode: TextView = itemView.findViewById(R.id.tvCourseCode)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvInstructor: TextView = itemView.findViewById(R.id.tvInstructor)
        val ivCourseImage: ImageView = itemView.findViewById(R.id.ivCourseImage)
    }
}

