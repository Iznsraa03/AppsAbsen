package com.example.appabsen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RiwayatAdapter(private val absensiList: List<RAbsensi>) : RecyclerView.Adapter<RiwayatAdapter.AbsensiViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsensiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return AbsensiViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbsensiViewHolder, position: Int) {
        val absensi = absensiList[position]
        holder.textViewNama.text = absensi.nama
        holder.textViewNim.text = absensi.nim
        holder.textViewMataKuliah.text = absensi.mataKuliah
        holder.textViewTanggal.text = absensi.tanggal

        // Load image using Glide or any other image loading library
        Glide.with(holder.itemView.context)
            .load(absensi.imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return absensiList.size
    }

    class AbsensiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNama: TextView = itemView.findViewById(R.id.textNama)
        val textViewNim: TextView = itemView.findViewById(R.id.textNim)
        val textViewMataKuliah: TextView = itemView.findViewById(R.id.textMataKuliah)
        val textViewTanggal: TextView = itemView.findViewById(R.id.textTglAbsen)
        val imageView: ImageView = itemView.findViewById(R.id.Rimg)
    }
}
